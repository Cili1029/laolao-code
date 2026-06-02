package com.laolao.ai.pojo.dto;

import lombok.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RedisMessageDTO {
    /**
     * 消息类型: SYSTEM, USER, ASSISTANT, TOOL
     */
    private String type;

    /**
     * 内容
     */
    private String content;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 调用工具(ASSISTANT用)
     */
    private List<AssistantMessage.ToolCall> toolCall;

    /**
     * 工具结果(TOOL用)
     */
    private List<ToolResponseMessage.ToolResponse> toolResponse;
}
