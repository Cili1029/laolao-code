package com.laolao.ai.tool;

import com.laolao.ai.pojo.dto.RedisMessageDTO;
import org.springframework.ai.chat.messages.*;


public class MessageTools {
    public static RedisMessageDTO toDTO(Message message) {
        RedisMessageDTO.RedisMessageDTOBuilder builder = RedisMessageDTO.builder()
                .type(message.getMessageType().getValue())
                .content(message.getText())
                .metadata(message.getMetadata());

        if (message instanceof AssistantMessage assistantMsg) {
            builder.toolCall(assistantMsg.getToolCalls());
        } else if (message instanceof ToolResponseMessage toolMsg) {
            builder.toolResponse(toolMsg.getResponses());
        }

        return builder.build();
    }

    public static Message toEntity(RedisMessageDTO redisMessageDTO) {
        MessageType type = MessageType.fromValue(redisMessageDTO.getType());
        return switch (type) {
            case SYSTEM -> SystemMessage.builder()
                    .text(redisMessageDTO.getContent())
                    .metadata(redisMessageDTO.getMetadata())
                    .build();
            case USER -> UserMessage.builder()
                    .text(redisMessageDTO.getContent())
                    .metadata(redisMessageDTO.getMetadata())
                    .build();
            case ASSISTANT -> AssistantMessage.builder()
                    .content(redisMessageDTO.getContent())
                    .toolCalls(redisMessageDTO.getToolCall())
                    .properties(redisMessageDTO.getMetadata())
                    .build();
            case TOOL -> ToolResponseMessage.builder()
                    .responses(redisMessageDTO.getToolResponse())
                    .metadata(redisMessageDTO.getMetadata())
                    .build();
        };
    }
}
