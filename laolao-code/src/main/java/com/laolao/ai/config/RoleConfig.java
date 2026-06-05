package com.laolao.ai.config;

import com.laolao.ai.tool.AiChatTools;
import com.laolao.ai.tool.RecordAiTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.laolao.ai.constant.PromptConstant.AiChatPrompt;
import static com.laolao.ai.constant.PromptConstant.RecordPrompt;

@Configuration
public class RoleConfig {
    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository redisChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(20) // 保留最近 20 条对话记录
                .build();
    }

    @Bean
    public ChatClient tutorClient(DeepSeekChatModel deepSeek, RecordAiTools recordAiTools) {
        return ChatClient.builder(deepSeek)
                .defaultSystem(RecordPrompt)
                .defaultTools(recordAiTools)
                .build();
    }
    @Bean
    public ChatClient aiChatClient(DeepSeekChatModel deepSeek, AiChatTools aiChatTools, ChatMemory chatMemory) {
        MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId("default_session")
                .build();

        return ChatClient.builder(deepSeek)
                .defaultSystem(AiChatPrompt)
                .defaultTools(aiChatTools)
                .defaultAdvisors(advisor)
                .build();
    }
}
