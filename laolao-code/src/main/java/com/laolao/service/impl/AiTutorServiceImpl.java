package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.AiReportMapper;
import com.laolao.pojo.entity.AiReport;
import com.laolao.service.AiTutorService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;

@Service
public class AiTutorServiceImpl implements AiTutorService {
    @Resource
    private ChatClient tutorClient;
    @Resource
    private AiReportMapper aiReportMapper;

    @Override
    public Flux<String> generateQuestionReport(Integer judgeRecordId) {
        // 先查库里有没有这个报告
        AiReport report = aiReportMapper.selectOne(Wrappers.<AiReport>lambdaQuery()
                .eq(AiReport::getTargetType, 1)
                .eq(AiReport::getTargetId, judgeRecordId));
        // 存在，直接返回给前端
        if (report != null) {
            // Flux.just() 会把这个字符串当作一次完整的流数据发送出去，然后自动结束
            return Flux.just(report.getContent());
        }
        // 用于收集AI吐出来的字
        StringBuilder fullReport = new StringBuilder();
        return tutorClient.prompt()
                .user(u -> u.text("请帮我生成一份分析报告。当前任务类型为：1，目标ID为：{judgeRecordId}。请先调用对应工具获取数据，然后根据类型要求输出报告。")
                        .param("judgeRecordId", judgeRecordId))
                .stream()
                .content()
                // 每当大模型吐出一个词，就把它追加到 StringBuilder 里
                .doOnNext(fullReport::append)
                // 当大模型说完了（流结束），触发存库逻辑
                .doOnComplete(() -> {
                    String finalContent = fullReport.toString()
                            .trim() //砍掉文章最开头和最末尾的空格和换行
                            .replaceAll("\\n\\s*\\n+", "\n\n"); // 正则替换中间所有的多余空行

                    // 异步线程去存数据库
                    CompletableFuture.runAsync(() -> {
                        AiReport aiReport = AiReport.builder()
                                .targetType(1)
                                .targetId(judgeRecordId)
                                .content(finalContent)
                                .build();
                        aiReportMapper.insert(aiReport);
                    });
                });
    }

    @Override
    public Flux<String> generateUserExamReport(Integer examId, Integer examRecordId) {
        // 先查库里有没有这个报告
        AiReport report = aiReportMapper.selectOne(Wrappers.<AiReport>lambdaQuery()
                .eq(AiReport::getTargetType, 2)
                .eq(AiReport::getTargetId, examId));
        // 存在，直接返回给前端
        if (report != null) {
            // Flux.just() 会把这个字符串当作一次完整的流数据发送出去，然后自动结束
            return Flux.just(report.getContent());
        }
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Flux.just("系统错误，请联系管理员！");
        }
        // 用于收集AI吐出来的字
        StringBuilder fullReport = new StringBuilder();
        return tutorClient.prompt()
                .user(u -> u.text("请帮我生成一份分析报告。当前任务类型为：2，目标ID为：{examId}，考生为：{userId}。请先调用对应工具获取数据，然后根据类型要求输出报告。")
                        .param("examId", examId).param("userId", userId))
                .stream()
                .content()
                // 每当大模型吐出一个词，就把它追加到 StringBuilder 里
                .doOnNext(fullReport::append)
                // 当大模型说完了（流结束），触发存库逻辑
                .doOnComplete(() -> {
                    String finalContent = fullReport.toString()
                            .trim() //砍掉文章最开头和最末尾的空格和换行
                            .replaceAll("\\n\\s*\\n+", "\n\n"); // 正则替换中间所有的多余空行

                    // 异步线程去存数据库
                    CompletableFuture.runAsync(() -> {
                        AiReport aiReport = AiReport.builder()
                                .targetType(2)
                                .targetId(examRecordId)
                                .content(finalContent)
                                .build();
                        aiReportMapper.insert(aiReport);
                    });
                });
    }

    @Override
    public Flux<String> generateManagerExamReport(Integer examId) {
        // 先查库里有没有这个报告
        AiReport report = aiReportMapper.selectOne(Wrappers.<AiReport>lambdaQuery()
                .eq(AiReport::getTargetType, 3)
                .eq(AiReport::getTargetId, examId));
        // 存在，直接返回给前端
        if (report != null) {
            // Flux.just() 会把这个字符串当作一次完整的流数据发送出去，然后自动结束
            return Flux.just(report.getContent());
        }
        // 用于收集AI吐出来的字
        StringBuilder fullReport = new StringBuilder();
        return tutorClient.prompt()
                .user(u -> u.text("请帮我生成一份分析报告。当前任务类型为：3，目标ID为：{examId}。请先调用对应工具获取数据，然后根据类型要求输出报告。")
                        .param("examId", examId))
                .stream()
                .content()
                // 每当大模型吐出一个词，就把它追加到 StringBuilder 里
                .doOnNext(fullReport::append)
                // 当大模型说完了（流结束），触发存库逻辑
                .doOnComplete(() -> {
                    String finalContent = fullReport.toString()
                            .trim() //砍掉文章最开头和最末尾的空格和换行
                            .replaceAll("\\n\\s*\\n+", "\n\n"); // 正则替换中间所有的多余空行

                    // 异步线程去存数据库
                    CompletableFuture.runAsync(() -> {
                        AiReport aiReport = AiReport.builder()
                                .targetType(3)
                                .targetId(examId)
                                .content(finalContent)
                                .build();
                        aiReportMapper.insert(aiReport);
                    });
                });
    }
}
