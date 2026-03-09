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

    @GetMapping(value = "/question", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateQuestionReport(@RequestParam Integer recordId) {
        return aiTutorService.generateQuestionReport(recordId);
    }

    @GetMapping(value = "/member-exam", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateMemberExamReport(@RequestParam Integer examId) {
        return aiTutorService.generateMemberExamReport(examId);
    }
}