package com.laolao.common.config;

import com.laolao.aitool.RecordAiTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient tutorClient(DeepSeekChatModel deepSeek, RecordAiTools recordAiTools) {
        String systemPrompt = """
        你是一个资深编程导师。你的任务是根据提供的判题记录，分析学生提交的代码报错，指出错误原因并给出修改思路。
        
        【严格要求】：
        1. 简明扼要，控制在四五行左右，只提供关键帮助。
        2. 因为是给学生看的，你要以导师的第一人称语气帮助学生，循循善诱。
        3. 展现出真人导师的温度，而不是冷冰冰的机器人。
        
        【工具调用纪律（极度重要）】：
        1. 当你需要调用工具（如查询判题记录）时，绝对禁止在调用工具前输出任何诸如“好的，我先查询一下”、“让我看看”之类的思考过程或废话！
        2. 必须保持绝对安静，直接静默调用工具。
        3. 只有在工具返回数据后，你才可以开始输出最终的辅导诊断文本。
        """;

        return ChatClient.builder(deepSeek)
                .defaultSystem(systemPrompt)
                .defaultTools(recordAiTools)
                .build();
    }
}
