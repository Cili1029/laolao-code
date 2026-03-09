package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    public Flux<String> generateQuestionReport(Integer recordId) {
        // 先查库里有没有这个报告
        AiReport report = aiReportMapper.selectOne(Wrappers.<AiReport>lambdaQuery()
                .eq(AiReport::getTargetType, 1)
                .eq(AiReport::getTargetId, recordId));
        // 存在，直接返回给前端
        if (report != null) {
            // Flux.just() 会把这个字符串当作一次完整的流数据发送出去，然后自动结束
            return Flux.just(report.getContent());
        }
        // 用于收集AI吐出来的字
        StringBuilder fullReport = new StringBuilder();
        return tutorClient.prompt()
                .user(u -> u.text("帮我分析一下判题记录，ID为：{recordId}")
                        .param("recordId", recordId))
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
                                .targetId(recordId)
                                .content(finalContent)
                                .build();
                        aiReportMapper.insert(aiReport);
                    });
                });
    }

    @Override
    public Flux<String> generateMemberExamReport(Integer examId) {
        return null;
    }
}
