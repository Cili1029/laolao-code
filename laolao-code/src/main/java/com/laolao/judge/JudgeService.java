package com.laolao.judge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.result.JudgeResult;
import com.laolao.pojo.entity.QuestionTestCase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.*;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExecResult {
        /**
         * 退出码
         */
        private Integer exitCode;
        /**
         * 标准输出
         */
        private String stdout;
        /**
         * 错误输出
         */
        private String stderr;
        /**
         * 消耗时间 (ms)
         */
        private Integer time;

        /**
         * 消耗内存 (MB)
         */
        private Integer memory;
    }


    /**
     * 判题服务
     *
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
            String errMessage = e.getMessage();
            return JudgeResult.builder()
                    .status(JudgeConstant.STATUS_CE)
                    .errorMessage(errMessage == null ? "" : errMessage.split("\\R", 2)[0].trim())
                    .totalCount(questionTestCases.size())
                    .build();
        }

        // 从池中取出一个容器（如果池子空了，最多等 5 秒）
        String containerId = containerQueue.poll(5, TimeUnit.SECONDS);
        if (containerId == null) throw new RuntimeException("当前无可用容器（判题繁忙）");

        try {
            // 上传代码
            uploadFile(containerId, JudgeUnits.generateMain(userCode, methodInfo), "Main.java");
            // 编译
            String compileError = compile(containerId);
            // 不为空，编译错误
            if (compileError != null) {
                return JudgeResult.builder()
                        .status(JudgeConstant.STATUS_CE)
                        .errorMessage(compileError)
                        .totalCount(questionTestCases.size())
                        .build();
            }

            // 构造输入数据（第一行为测试案例数）
            StringBuilder allInput = new StringBuilder();
            allInput.append(questionTestCases.size()).append("\n");
            for (QuestionTestCase questionTestCase : questionTestCases) {
                allInput.append(questionTestCase.getInput()).append("\n");
            }
            uploadFile(containerId, allInput.toString(), "input.txt");

            ExecResult execResult = runTestCase(containerId);

            // 判题逻辑开始
            // 时间超限（容器猝死）
            if (execResult.getExitCode() == 124) {
                return JudgeResult.builder()
                        .status(JudgeConstant.STATUS_TLE)
                        .totalCount(questionTestCases.size())
                        .build();
            }

            // 内存超限（容器猝死）
            Boolean oomKilled = dockerClient.inspectContainerCmd(containerId).exec().getState().getOOMKilled();
            if (Boolean.TRUE.equals(oomKilled)) {
                return JudgeResult.builder()
                        .status(JudgeConstant.STATUS_MLE)
                        .totalCount(questionTestCases.size())
                        .build();
            }

            // 经过超时超限，按理来说应该是可以拿得到指标
            // 填充 time, memory, stderr
            parseMetrics(execResult);
            // 兜底，这种情况估计只能是系统问题了
            if (execResult.getTime() == null || execResult.getMemory() == null) {
                return JudgeResult.builder()
                        .status(JudgeConstant.STATUS_SE)
                        .errorMessage(execResult.getStderr()) // 此时 stderr 里通常有 JVM 报错
                        .totalCount(questionTestCases.size())
                        .build();
            }

            // 解析程序输出的 JSON (包含答案错误 WA 和 运行时错误 RE)
            try {
                String stdout = execResult.getStdout().trim();
                // /usr/bin/time 会将结果追加在 stderr 的最后一行
                int lastJsonIndex = stdout.lastIndexOf("{\"status\"");
                if (lastJsonIndex == -1) {
                    // 没有找到JSON，直接返回
                    throw new RuntimeException();
                }

                String realError = stdout.substring(0, lastJsonIndex).trim();
                String jsonPart = stdout.substring(lastJsonIndex);


                JsonNode jsonRes = OBJECT_MAPPER.readTree(jsonPart);
                int status = jsonRes.get("status").asInt();
                // 运行错误 但是这个时候可能有跑通的用例（空指针，除零等等）
                if (status != 0) {
                    return JudgeResult.builder()
                            .status(JudgeConstant.STATUS_RE)
                            .errorMessage(jsonRes.get("errorMessage").asText())
                            .totalCount(questionTestCases.size())
                            .passCount(jsonRes.get("passCount").asInt())
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
                                .errorMessage(realError)
                                .totalCount(questionTestCases.size())
                                .passCount(i)
                                .failedInput(questionTestCases.get(i).getInput())
                                .failedExpect(questionTestCases.get(i).getOutput())
                                .failedActual(userOutput)
                                .build();
                    }
                }
            } catch (Exception e) {
                // 如果 JSON 解析失败，说明程序崩溃且没输出 JSON (未捕获的 RE)
                return JudgeResult.builder()
                        .status(JudgeConstant.STATUS_RE)
                        .errorMessage(execResult.getStderr()) // 保留 parseMetrics 提取的 stderr
                        .totalCount(questionTestCases.size())
                        .build();
            }

            // 全部通过
            return JudgeResult.builder()
                    .status(JudgeConstant.STATUS_AC)
                    .time(execResult.getTime())
                    .memory(execResult.getMemory())
                    .totalCount(questionTestCases.size())
                    .passCount(questionTestCases.size())
                    .build();
        } finally {
            // 清理容器
            CompletableFuture.runAsync(() -> {
                try {
                    resetAndReturnContainer(containerId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 上传字符串到容器文件
     *
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
     *
     * @param containerId 容器ID
     * @return 编译错误信息，如果成功则返回 null
     * @throws InterruptedException 报错
     */
    private String compile(String containerId) throws InterruptedException {
        StringBuilder stderr = new StringBuilder();
        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStderr(true)
                .withCmd("sh", "-c", "timeout 10s javac Main.java")
                .exec().getId();

        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame item) {
                if (item.getStreamType() == StreamType.STDERR) stderr.append(new String(item.getPayload()));
            }
        }).awaitCompletion(12, TimeUnit.SECONDS);
        Long exitCode = dockerClient.inspectExecCmd(execId).exec().getExitCodeLong();

        // 编译没挂并且编译失败
        if (exitCode != null && exitCode != 0) {
            return stderr.isEmpty() ? "未知编译错误" : stderr.toString();
        }
        return null;
    }

    /**
     * 运行用例（使用输入重定向）
     *
     * @param containerId 容器ID
     * @return 判题结果
     * @throws InterruptedException 报错
     */
    private ExecResult runTestCase(String containerId) throws InterruptedException {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        // 使用一个原子计数器或数组来记录总字节数
        final long[] totalBytes = {0};
        long MAX_BYTES = 1024 * 1024; // 限制 1MB 输出

        // 使用 < input.txt 读取输入
        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                // 使用 sh -c 来执行带重定向的命令，限制 3 秒
                .withCmd("sh", "-c", "timeout 3s /usr/bin/time -f '{\"time\":%e,\"mem\":%M}' java -Xmx128m Main < input.txt")
                .exec().getId();

        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame item) {
                byte[] payload = item.getPayload();
                if (payload == null || payload.length == 0) {
                    return;
                }

                // 检查是否超过长度限制
                if (totalBytes[0] > MAX_BYTES) {
                    return;
                }
                totalBytes[0] += payload.length;

                String content = new String(item.getPayload());
                if (item.getStreamType() == StreamType.STDOUT) {
                    stdout.append(content);
                } else if (item.getStreamType() == StreamType.STDERR) {
                    stderr.append(content);
                }
            }
        }).awaitCompletion(5, TimeUnit.SECONDS); // 这里的 5s 是保护性时间，比 timeout 略长

        Long exitCode = dockerClient.inspectExecCmd(execId).exec().getExitCodeLong();

        return ExecResult.builder()
                .exitCode(exitCode != null ? exitCode.intValue() : -1)
                .stdout(stdout.toString())
                .stderr(stderr.toString())
                .build();
    }

    /**
     * 解析时间内存
     *
     * @param raw 容器运行结果
     */
    private void parseMetrics(ExecResult raw) {
        String err = raw.getStderr();
        if (err == null || err.isEmpty()) {
            return;
        }

        // /usr/bin/time 会将结果追加在 stderr 的最后一行
        int lastJsonIndex = err.lastIndexOf("{\"time\"");
        if (lastJsonIndex == -1) {
            // 没有找到JSON，直接返回
            return;
        }

        String realError = err.substring(0, lastJsonIndex).trim();
        String jsonPart = err.substring(lastJsonIndex);

        try {
            JsonNode node = OBJECT_MAPPER.readTree(jsonPart);

            // %e 是秒，转为毫秒
            double timeInSeconds = node.get("time").asDouble();
            raw.setTime((int) Math.ceil(timeInSeconds * 1000));

            // %M 是 KB，转为 MB
            long memoryInKb = node.get("mem").asLong();
            raw.setMemory((int) (memoryInKb / 1024));

            raw.setStderr(realError);
        } catch (Exception ignored) {
        }
    }

    /**
     * 将字符串代码转换成Docker识别的Tar压缩流
     *
     * @param content  内容
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
     *
     * @param userOutput     用户答案
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
                                .withPidsLimit(50L)                 // 限制进程数
                                .withUlimits(Collections.singletonList(new Ulimit("fsize", 1024 * 1024 * 10L, 1024 * 1024 * 10L)))
                        )
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
     * 清理容器以便复用
     * @param containerId 容器Id
     */
    private void resetAndReturnContainer(String containerId) {
        try {
            // 强制杀掉可能存在的残留进程，删除临时文件
            String execId = dockerClient.execCreateCmd(containerId)
                    .withCmd("sh", "-c", "pkill -u root java; rm -rf /judger/*")
                    .exec().getId();

            dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<>()).awaitCompletion(2, TimeUnit.SECONDS);

            // 放回池子复用
            containerQueue.offer(containerId);
        } catch (Exception e) {
            // 重置失败（可能容器崩了），彻底销毁并补位
            try {
                dockerClient.removeContainerCmd(containerId).withForce(true).exec();
            } catch (Exception ignored) {
            }
            createNewContainerToPool();
        }
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