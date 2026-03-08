package com.laolao.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AiTutorController {

    private final ChatClient reportClient;

    @GetMapping(value = "/report", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> report(@RequestParam Integer recordId) {
        return reportClient.prompt()
                .user(u -> u.text("帮我分析一下判题记录，ID为：{recordId}，分析完将结果存入报告表")
                        .param("recordId", recordId))
                .stream()
                .content();
    }
}