package com.laolao.controller;

import com.laolao.service.AiTutorService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai/report")
public class AiTutorController {

    @Resource
    private AiTutorService aiTutorService;

    /**
     * 分析错题生成报告
     *
     * @param judgeRecordId 判题记录Id
     * @return 报告
     */
    @GetMapping(value = "/question", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateQuestionReport(@RequestParam Integer judgeRecordId) {
        return aiTutorService.generateQuestionReport(judgeRecordId);
    }

    /**
     * 分析这次考试生成考生的报告
     *
     * @param examId 考试Id
     * @param examRecordId 考试记录Id
     * @return 报告
     */
    @GetMapping(value = "/exam-record", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateUserExamReport(@RequestParam Integer examId, @RequestParam Integer examRecordId) {
        return aiTutorService.generateUserExamReport(examId, examRecordId);
    }

    /**
     * 分析这次考试生成导师的报告
     *
     * @param examId 考试Id
     * @return 报告
     */
    @GetMapping(value = "/exam-report", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateManagerExamReport(@RequestParam Integer examId) {
        return aiTutorService.generateManagerExamReport(examId);
    }
}