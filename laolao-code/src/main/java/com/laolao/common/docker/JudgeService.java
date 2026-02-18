package com.laolao.common.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.entity.TestCase;
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
    // 判题时从这里取，用完后销毁，再补新的，保证用户请求判题秒开
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
     * 判题服务核心代码
     *
     * @param userCode 用户提交的 Java 代码字符串
     * @param testCases 示例
     * @return 判题结果
     */
    public JudgeResult judge(String userCode, List<TestCase> testCases) throws Exception {
        // 从池中取出一个容器（如果池子空了，最多等 5 秒）
        String containerId = containerQueue.poll(5, TimeUnit.SECONDS);
        if (containerId == null) throw new RuntimeException("当前无可用容器（判题繁忙）");

        try {
            // 上传代码
            uploadFile(containerId, JudgeConstant.commonImports + userCode, "Main.java");
            // 编译
            String compileError = compile(containerId);
            // 为空，直接返回失败信息
            if (compileError != null) {
                JudgeResult judgeResult = new JudgeResult();
                judgeResult.setExitCode(1L);
                judgeResult.setStderr(compileError);
                return judgeResult;
            }

            // 编译成功，开始逐个运行测试用例
            long maxMemory = 0;
            double maxTime = 0;

            for (TestCase tc : testCases) {
                // 将当前输入写入 input.txt
                uploadFile(containerId, tc.getInput(), "input.txt");

                // 执行并获取结果
                JudgeResult response = runSingleCase(containerId);

                // 判断是否超时或发生运行错误
                if (response.getExitCode() != 0) {
                    JudgeResult judgeResult = new JudgeResult();
                    judgeResult.setExitCode(1L);
                    return judgeResult;
                }
                // 比对输出结果
                boolean isPassed = tc.getOutput().trim().equals(response.getStdout().trim());
                if (!isPassed) {
//                    JudgeResult res = JudgeResult.fail("Wrong Answer", "Output mismatch at case " + i);
                    JudgeResult judgeResult = new JudgeResult();
                    judgeResult.setStdout(response.getStdout());
//                    judgeResult.setExpected(tc.getOutput());
                    return judgeResult;
                }

                // 记录最大消耗
                maxMemory = Math.max(maxMemory, response.getMemory());
                maxTime = Math.max(maxTime, response.getTime());
            }

            // 全部通过
//            return JudgeResult.success("Accepted", maxTime, maxMemory);
            JudgeResult judgeResult = new JudgeResult();
            judgeResult.setExitCode(0L);
            judgeResult.setTime(maxTime);
            judgeResult.setMemory(maxMemory);
            return judgeResult;
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
     * 上传字符串到容器文件
     */
    private void uploadFile(String containerId, String content, String fileName) throws IOException {
        dockerClient.copyArchiveToContainerCmd(containerId)
                .withTarInputStream(toTarStream(content, fileName))
                .withRemotePath("/app")
                .exec();
    }

    /**
     * 编译代码
     * @return 编译错误信息，如果成功则返回 null
     */
    private String compile(String containerId) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStderr(true)
                .withCmd("javac", "Main.java")
                .exec().getId();

        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame item) {
                if (item.getStreamType() == StreamType.STDERR) sb.append(new String(item.getPayload()));
            }
        }).awaitCompletion(5, TimeUnit.SECONDS);
        Long exitCode = dockerClient.inspectExecCmd(execId).exec().getExitCodeLong();

        // 只有退出码不为 0 且有错误信息时才判定为编译失败
        if (exitCode != null && exitCode != 0) {
            return sb.isEmpty() ? "Unknown Compilation Error" : sb.toString();
        }
        return null;
    }

    private JudgeResult parseMetrics(String out, String err, Long exitCode) {
        JudgeResult res = new JudgeResult();
        // 清理标准输出前后的空白字符
        res.setStdout(out == null ? "" : out.trim());
        res.setExitCode(exitCode);

        if (err == null || err.isEmpty()) {
            res.setStderr("");
            return res;
        }

        String stderrStr = err.trim();
        // 我们在命令中定义的标识符
        String metricFlag = "TIME:";
        int metricIndex = stderrStr.lastIndexOf(metricFlag);

        if (metricIndex != -1) {
            // 1. 提取真正的错误信息
            // 指标之前的字符串是程序运行时的运行时错误（Runtime Error）
            String realError = stderrStr.substring(0, metricIndex).trim();
            res.setStderr(realError);

            // 2. 提取指标部分，例如 "TIME:0.05 MEM:35840"
            String metricPart = stderrStr.substring(metricIndex);

            // 查找 MEM: 的位置来拆分时间和内存
            int memIndex = metricPart.indexOf("MEM:");
            if (memIndex != -1) {
                // 提取时间 (单位：秒 -> 毫秒)
                String timeStr = metricPart.substring(metricFlag.length(), memIndex).trim();
                try {
                    // /usr/bin/time %e 返回的是秒（如 0.01），转成 Double 再乘 1000 得到毫秒
                    double timeInSeconds = Double.parseDouble(timeStr);
                    res.setTime(timeInSeconds * 1000);
                } catch (Exception e) {
                    res.setTime(0.0);
                }

                // 提取内存 (单位：KB)
                String memStr = metricPart.substring(memIndex + 4).trim();
                try {
                    // Linux 下 /usr/bin/time %M 返回的是最大常驻内存，单位通常是 KB
                    // 如果你想转成 MB，可以除以 1024
                    long memoryInKb = Long.parseLong(memStr);
                    res.setMemory(memoryInKb);
                } catch (Exception e) {
                    res.setMemory(0L);
                }
            }
        } else {
            // 如果没找到 TIME: 标识，说明程序可能因为某种原因中途被系统杀死了（如 OOM Killer）
            // 或者产生了一些无法解析的系统错误，直接把全部错误存入 stderr
            res.setStderr(stderrStr);
            res.setTime(0.0);
            res.setMemory(0L);
        }

        return res;
    }

    /**
     * 运行单个用例（使用输入重定向）
     */
    private JudgeResult runSingleCase(String containerId) throws InterruptedException {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        // 使用 < input.txt 读取输入
        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                // 重点：使用 sh -c 来执行带重定向的命令
                .withCmd("sh", "-c", "/usr/bin/time -f 'TIME:%e MEM:%M' java Main < input.txt")
                .exec().getId();

        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame item) {
                String payload = new String(item.getPayload());
                if (item.getStreamType() == StreamType.STDOUT) stdout.append(payload);
                else if (item.getStreamType() == StreamType.STDERR) stderr.append(payload);
            }
        }).awaitCompletion(5, TimeUnit.SECONDS);

        Long exitCode = dockerClient.inspectExecCmd(execId).exec().getExitCodeLong();

        // 解析时间和内存
        return parseMetrics(stdout.toString(), stderr.toString(), exitCode);
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