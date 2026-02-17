package com.laolao.common.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import com.laolao.pojo.entity.JudgeResult;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeService {
    private final DockerClient dockerClient;

    // 【容器池】使用阻塞队列存放已启动的容器 ID
    // 判题时从这里取，用完后销毁，再补新的，保证用户请求判题“秒开”
    private final BlockingQueue<String> containerQueue = new LinkedBlockingQueue<>(10);
    // 定义池子的大小
    private final int POOL_SIZE = 5;

    // 构造函数：Spring 自动注入配置好的 dockerClient
    public JudgeService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    /**
     * 清理旧容器，创建新容器
     * PostConstruct: Spring Bean 初始化完成后执行
     */
    @PostConstruct
    public void onStart() {
        cleanOldContainers();
        initPool();
    }

    /**
     * 清理所有带判题标签的容器
     */
    private void cleanOldContainers() {
        try {
            // 列出所有（包括运行中和已停止）带有指定标签的容器
            List<Container> containers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withLabelFilter(Collections.singletonMap(JudgeConstant.LABEL_KEY, JudgeConstant.LABEL_VALUE))
                    .exec();

            for (Container c : containers) {
                dockerClient.removeContainerCmd(c.getId()).withForce(true).exec();
            }
        } catch (Exception e) {
            System.err.println("启动清理失败: " + e.getMessage());
        }
    }

    // 初始化池子
    private void initPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            createNewContainerToPool();
        }
    }

    /**
     * 异步创建一个新容器并放入池中
     * 使用异步防止创建容器的过程卡住主线程
     */
    private void createNewContainerToPool() {
        CompletableFuture.runAsync(() -> {
            try {
                // 创建容器并进行安全配置
                CreateContainerResponse container = dockerClient.createContainerCmd("eclipse-temurin:17-jdk-alpine")
                        .withLabels(Collections.singletonMap(JudgeConstant.LABEL_KEY, JudgeConstant.LABEL_VALUE)) // 给容器打标签
                        .withNetworkDisabled(true)    // 禁用网络
                        .withWorkingDir("/app")                     // 设置容器内的工作目录
                        .withHostConfig(HostConfig.newHostConfig()
                                .withMemory(256 * 1024 * 1024L)     // 限制内存 256MB
                                .withCpuQuota(50000L)               // 限制 CPU 使用率 (0.5核)
                                .withPidsLimit(50L))                // 限制进程数
                        .withCmd("tail", "-f", "/dev/null")         // 让容器启动后保持运行
                        .exec();

                String id = container.getId();
                dockerClient.startContainerCmd(id).exec(); // 启动容器
                containerQueue.offer(id);                  // 将启动好的容器 ID 放入队列
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 判题服务
     *
     * @param userCode 用户提交的 Java 代码字符串
     */
    public JudgeResult judge(String userCode) throws Exception {
        // 从池中取出一个容器（如果池子空了，最多等 5 秒）
        String containerId = containerQueue.poll(5, TimeUnit.SECONDS);
        if (containerId == null) throw new RuntimeException("当前无可用容器（判题繁忙）");

        // 准备存放输出的容器
        StringBuilder stdoutBuilder = new StringBuilder();
        StringBuilder stderrBuilder = new StringBuilder();

        try {
            // dockerClient 要求通过 Tar 流的方式上传文件
            // 因此需要将代码字符串打包成 Tar 流，发送到容器内部
            dockerClient.copyArchiveToContainerCmd(containerId)
                    // 需要补上通用包
                    .withTarInputStream(toTarStream(JudgeConstant.commonImports + userCode, "Main.java"))
                    .withRemotePath("/app")
                    .exec();

            // 执行命令
            String execId = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true) // 获取标准输出
                    .withAttachStderr(true) // 获取错误输出
                    // 执行 sh 命令：先 javac 编译，如果成功则 java 运行
                    .withCmd("sh", "-c", "javac Main.java && /usr/bin/time -f 'TIME:%e MEM:%M' java Main")
                    .exec()
                    .getId();

            // 开始执行 Exec 指令并监听结果
            dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame item) {
                    String payload = new String(item.getPayload());
                    if (item.getStreamType() == StreamType.STDOUT) {
                        stdoutBuilder.append(payload);
                    } else if (item.getStreamType() == StreamType.STDERR) {
                        stderrBuilder.append(payload);
                    }
                }
            }).awaitCompletion(10, TimeUnit.SECONDS); // 限制运行时间 10 秒（防止死循环）

            // 获取 Exec 命令的退出码（核心新增逻辑）
            InspectExecResponse exec = dockerClient.inspectExecCmd(execId).exec();

            // 组装结果返回实体类
            return setResult(stdoutBuilder, stderrBuilder, exec.getExitCodeLong());
        } finally {
            // 容器用完即焚，创建新容器补上
            CompletableFuture.runAsync(() -> {
                try {
                    dockerClient.removeContainerCmd(containerId).withForce(true).exec();
                    createNewContainerToPool();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 将字符串代码转换成 Docker 识别的 Tar 压缩流
     */
    public static InputStream toTarStream(String content, String fileName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] contentBytes = content.getBytes();

        try (TarArchiveOutputStream taos = new TarArchiveOutputStream(bos)) {
            TarArchiveEntry entry = new TarArchiveEntry(fileName);
            entry.setSize(contentBytes.length);
            entry.setMode(0100644); // 设置 Linux 文件权限为可读

            taos.putArchiveEntry(entry);
            taos.write(contentBytes);
            taos.closeArchiveEntry();
            taos.finish();
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }

    private static JudgeResult setResult(StringBuilder stdoutBuilder, StringBuilder stderrBuilder, Long exitCodeLong) {
        JudgeResult result = new JudgeResult();

        String stdout = stdoutBuilder.toString().trim();
        String stderr = stderrBuilder.toString().trim();

        // 先存下输出，方便调试
        result.setStdout(stdout);
        result.setExitCode(exitCodeLong);

        // 从后往前查找指标，因为指标通常在最后一行
        String metricFlag = "TIME:";
        int errIndex = stderr.lastIndexOf(metricFlag);

        if (errIndex != -1) {
            // 提取指标之前的真正错误信息
            String realError = stderr.substring(0, errIndex).trim();
            if (!realError.isEmpty()) {
                result.setStderr(realError);
            }

            // 提取指标字符串 (TIME:0.10 MEM:35072)
            String metricPart = stderr.substring(errIndex);

            // 提取 TIME
            int timeEnd = metricPart.indexOf("MEM:");
            if (timeEnd != -1) {
                String timeStr = metricPart.substring(metricFlag.length(), timeEnd).trim();
                try {
                    result.setTime(Double.parseDouble(timeStr) * 1000);
                } catch (Exception ignore) {}

                // 提取 MEM
                String memStr = metricPart.substring(timeEnd + 4).trim();
                try {
                    // 如果后面还有其他杂质，可以再截取第一个空格前的数字
                    memStr = memStr.split("\\s+")[0];
                    result.setMemory(Long.parseLong(memStr) / 1024);
                } catch (Exception ignore) {}
            }
        } else {
            // 如果没找到指标，说明可能是编译阶段就挂了或者系统错误
            result.setStderr(stderr);
        }
        return result;
    }

    /**
     * 项目关闭时清理容器
     * PreDestroy: Spring Bean 销毁前执行
     */
    @PreDestroy
    public void onStop() {
        // 依次从队列取出所有容器并删除
        while (!containerQueue.isEmpty()) {
            String id = containerQueue.poll();
            if (id != null) {
                try {
                    dockerClient.removeContainerCmd(id).withForce(true).exec();
                } catch (Exception e) {
                    System.err.println("清理容器失败: " + id);
                }
            }
        }
    }
}