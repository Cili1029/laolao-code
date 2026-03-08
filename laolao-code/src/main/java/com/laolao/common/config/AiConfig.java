package com.laolao.common.config;

import com.laolao.aitool.RecordAiTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    /**
     * 直接注入 Spring Boot 自动装配好的 ChatClient.Builder
     */
    @Bean
    public ChatClient tutorClient(DeepSeekChatModel deepSeek, RecordAiTools recordAiTools) {
        String systemPrompt = """
                你是一个资深编程导师。你的任务是根据提供的判题记录，分析学生提交的代码报错，指出错误原因并给出修改思路。
                要求：
                1. 简明扼要，控制在四五行左右，只提供关键帮助。
                2. 因为是给学生看的，你要以导师的第一人称语气帮助学生，循循善诱。
                3. 展现出真人导师的温度，而不是冷冰冰的机器人。
                4. 各种数据的主键ID，添加到数据库情况为隐私，不可告知给学生
                """;

        return ChatClient.builder(deepSeek)
                .defaultSystem(systemPrompt)
                .defaultTools(recordAiTools)
                .build();
    }
}
