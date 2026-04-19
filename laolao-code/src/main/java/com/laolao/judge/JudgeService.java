package com.laolao.judge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.result.JudgeResult;
import com.laolao.pojo.entity.QuestionTestCase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class JudgeService {
    @Resource
    private DockerClient dockerClient;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * 判题服务
     * @param userCode          用户提交的 Java 代码字符串
     * @param questionTestCases 测试示例
     * @return 判题结果
     */
    public JudgeResult judge(String userCode, List<QuestionTestCase> questionTestCases) throws Exception {
        // 基础语法检查
        JudgeUnits.MethodInfo methodInfo;
        try {
            methodInfo = JudgeUnits.paramParsing(userCode);
        } catch (Exception e) {
            String errMsg = e.getMessage();
            return JudgeResult.builder().status(JudgeConstant.STATUS_CE).stderr(errMsg == null ? "" : errMsg.split("\\R", 2)[0].trim()).build();
        }

        // 从池中取出一个容器（如果池子空了，最多等 5 秒）
        String containerId = containerQueue.poll(5, TimeUnit.SECONDS);
        if (containerId == null) throw new RuntimeException("当前无可用容器（判题繁忙）");

        try {
            // 上传代码
            uploadFile(containerId, JudgeUnits.generateMain(userCode, methodInfo), "Main.java");
            // 编译
            String compileError = compile(containerId);
            // 为空，直接返回失败信息
            if (compileError != null) {
                return JudgeResult.compileError(compileError);
            }

            // 构造输入数据（第一行为测试案例数）
            StringBuilder allInput = new StringBuilder();
            allInput.append(questionTestCases.size()).append("\n");
            for (QuestionTestCase questionTestCase : questionTestCases) {
                allInput.append(questionTestCase.getInput()).append("\n");
            }
            uploadFile(containerId, allInput.toString(), "input.txt");
            JudgeResult response = runTestCase(containerId);

            // 判题逻辑开始
            // 内存超限
            Boolean oomKilled = dockerClient.inspectContainerCmd(containerId).exec().getState().getOOMKilled();
            if (Boolean.TRUE.equals(oomKilled)) {
                return JudgeResult.builder().status(JudgeConstant.STATUS_MLE).build();
            }

            // 时间超限
            if (response.getExitCode() == 124) {
                return JudgeResult.builder().status(JudgeConstant.STATUS_TLE).build();
            }

            // 解析程序输出的 JSON (包含答案错误 WA 和 运行时错误 RE)
            try {
                JsonNode jsonRes = OBJECT_MAPPER.readTree(response.getStdout());
                int status = jsonRes.get("status").asInt();
                // 运行错误
                if (status != 0) {
                    return JudgeResult.builder()
                            .status(JudgeConstant.STATUS_RE)
                            .stderr(jsonRes.get("errorMessage").asText())
                            .passTestCaseCount(jsonRes.get("caseIndex").asInt())
                            .build();
                }

                // 答案错误
                JsonNode outputs = jsonRes.get("output");
                for (int i = 0; i < outputs.size(); i++) {
                    String userOutput = outputs.get(i).asText();
                    String standardOutput = questionTestCases.get(i).getOutput();
                    if (!compareOutput(userOutput, standardOutput)) {
                        return JudgeResult.builder()
                                .status(JudgeConstant.STATUS_WA)
                                .passTestCaseCount(i)
                                .build();
                    }
                }
            } catch (Exception e) {
                // 如果 JSON 解析失败，说明程序崩溃且没输出 JSON (未捕获的 RE)
                return JudgeResult.builder().status(JudgeConstant.STATUS_RE).build();
            }

            // 全部通过
            return JudgeResult.builder()
                    .status(JudgeConstant.STATUS_AC)
                    .time(response.getTime())
                    .memory(response.getMemory())
                    .build();
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
     * @param containerId 容器ID
     * @param content     内容
     * @param fileName    文件名
     * @throws IOException 报错
     */
    private void uploadFile(String containerId, String content, String fileName) throws IOException {
        dockerClient.copyArchiveToContainerCmd(containerId)
                .withTarInputStream(toTarStream(content, fileName))
                .withRemotePath("/judger")
                .exec();
    }

    /**
     * 编译代码
     * @param containerId 容器ID
     * @return 编译错误信息，如果成功则返回 null
     * @throws InterruptedException 报错
     */
    private String compile(String containerId) throws InterruptedException {
        StringBuilder stderr = new StringBuilder();
        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStderr(true)
                .withCmd("javac", "Main.java")
                .exec().getId();

        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame item) {
                if (item.getStreamType() == StreamType.STDERR) stderr.append(new String(item.getPayload()));
            }
        }).awaitCompletion(5, TimeUnit.SECONDS);
        Long exitCode = dockerClient.inspectExecCmd(execId).exec().getExitCodeLong();

        // 只有退出码不为 0 且有错误信息时才判定为编译失败
        if (exitCode != null && exitCode != 0) {
            return stderr.isEmpty() ? "未知编译错误" : stderr.toString();
        }
        return null;
    }

    /**
     * 运行用例（使用输入重定向）
     * @param containerId 容器ID
     * @return 判题结果
     * @throws InterruptedException 报错
     */
    private JudgeResult runTestCase(String containerId) throws InterruptedException {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        // 使用 < input.txt 读取输入
        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                // 重点：使用 sh -c 来执行带重定向的命令，限制 3 秒
                .withCmd("sh", "-c", "timeout 3s /usr/bin/time -f '{\"time\":%e,\"mem\":%M}' java Main < input.txt")
                .exec().getId();

        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame item) {
                if (item.getStreamType() == StreamType.STDOUT) stdout.append(new String(item.getPayload()));
                else if (item.getStreamType() == StreamType.STDERR) stderr.append(new String(item.getPayload()));
            }
        }).awaitCompletion(5, TimeUnit.SECONDS); // 这里的 5s 是保护性时间，比 timeout 略长

        Long exitCode = dockerClient.inspectExecCmd(execId).exec().getExitCodeLong();

        // 解析时间和内存
        return parseMetrics(stdout.toString(), stderr.toString(), exitCode);
    }

    /**
     * 解析时间内存
     * @param out 标准输出
     * @param err 标准错误
     * @param exitCode 退出码
     * @return 判题结果
     */
    private JudgeResult parseMetrics(String out, String err, Long exitCode) {
        JudgeResult res = new JudgeResult();
        res.setStdout(out == null ? "" : out.trim());
        res.setExitCode(exitCode.intValue());

        if (err == null || err.isEmpty()) {
            return res;
        }

        String stderrStr = err.trim();
        // /usr/bin/time 会将结果追加在 stderr 的最后一行
        int lastJsonStart = stderrStr.lastIndexOf("{\"time\"");

        if (lastJsonStart != -1) {
            // 提取真正的错误信息（JSON 之前的内容）
            String realError = stderrStr.substring(0, lastJsonStart).trim();
            res.setStderr(realError);

            // 提取解析 JSON 指标
            String jsonPart = stderrStr.substring(lastJsonStart);
            try {
                JsonNode node = OBJECT_MAPPER.readTree(jsonPart);

                // %e 是秒，转为毫秒
                double timeInSeconds = node.get("time").asDouble();
                res.setTime((int) Math.ceil(timeInSeconds * 1000));

                // %M 是 KB，转为 MB
                long memoryInKb = node.get("mem").asLong();
                res.setMemory((int) (memoryInKb / 1024));

            } catch (Exception ignored) {
            }
        } else {
            res.setStderr(stderrStr);
        }
        return res;
    }

    /**
     * 将字符串代码转换成Docker识别的Tar压缩流
     * @param content 内容
     * @param fileName 文件名
     * @return tar格式的流
     * @throws IOException 报错
     */
    public static InputStream toTarStream(String content, String fileName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] contentBytes = content.getBytes();

        try (TarArchiveOutputStream tao = new TarArchiveOutputStream(bos)) {
            TarArchiveEntry entry = new TarArchiveEntry(fileName);
            entry.setSize(contentBytes.length);
            entry.setMode(33188); // 设置 Linux 文件权限为可读

            tao.putArchiveEntry(entry);
            tao.write(contentBytes);
            tao.closeArchiveEntry();
            tao.finish();
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }

    /**
     * 答案比较
     * @param userOutput 用户答案
     * @param standardOutput 标准答案
     * @return 结果
     */
    private boolean compareOutput(String userOutput, String standardOutput) {
        try {
            // 将两个字符串都解析为 JsonNode 对象
            JsonNode node1 = OBJECT_MAPPER.readTree(userOutput);
            JsonNode node2 = OBJECT_MAPPER.readTree(standardOutput);

            // JsonNode 的 equals 方法会递归比对每一个值，忽略空格和格式差异
            return node1.equals(node2);
        } catch (Exception e) {
            // 如果解析 JSON 失败，退回到字符串修剪比对
            return userOutput.trim().equals(standardOutput.trim());
        }
    }


    // ====================以下为容器相关====================

    /**
     * 判题机容器池
     */
    private final BlockingQueue<String> containerQueue = new LinkedBlockingQueue<>(10);

    /**
     * 项目启动时清理旧容器，创建新容器
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

    /**
     * 初始化池子
     */
    private void initPool() {
        int POOL_SIZE = 3;
        for (int i = 0; i < POOL_SIZE; i++) {
            createNewContainerToPool();
        }
    }

    /**
     * 判题机序号计数器
     */
    private final AtomicInteger containerIdx = new AtomicInteger(1);

    /**
     * 异步创建一个新容器并放入池中
     */
    private void createNewContainerToPool() {
        CompletableFuture.runAsync(() -> {
            try {
                String containerName = "judger-" + containerIdx.getAndIncrement();
                // 创建容器并进行安全配置
                CreateContainerResponse container = dockerClient.createContainerCmd("judger")
                        .withName(containerName)
                        .withLabels(Collections.singletonMap(JudgeConstant.LABEL_KEY, JudgeConstant.LABEL_VALUE)) // 给容器打标签
                        .withNetworkDisabled(true)    // 禁用网络
                        .withWorkingDir("/judger")                  // 设置容器内的工作目录
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