package com.laolao.ai.controller;

import com.laolao.ai.pojo.vo.GetOldSessionVO;
import com.laolao.ai.service.ChatService;
import com.laolao.common.result.Result;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai/chat")
public class ChatController {
    @Resource
    private ChatService chatService;

    /**
     * 获取旧会话
     * @return 结果信息
     */
    @GetMapping("/history")
    public Result<GetOldSessionVO> getOldSession() {
        return chatService.getOldSession();
    }

    /**
     * 聊天
     * @param userInput 用户输入的内容
     * @return 结果
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam String userInput) {
        return chatService.chat(userInput);
    }

    /**
     * 清空记忆
     * @return 结果信息
     */
    @PostMapping("/clear")
    public Result<String> clear() {
        return chatService.clear();
    }
}