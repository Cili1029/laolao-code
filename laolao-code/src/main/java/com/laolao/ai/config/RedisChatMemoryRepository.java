package com.laolao.ai.config;

import com.laolao.ai.constant.AiConstant;
import com.laolao.ai.pojo.dto.RedisMessageDTO;
import com.laolao.ai.tool.MessageTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String key = AiConstant.CHAT_PREFIX + conversationId;
        // 获取原始数据（config配置后自动转为DTO）
        List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);

        // 续期
        if (rawList != null && !rawList.isEmpty()) {
            redisTemplate.expire(key, AiConstant.CHAT_TTL);
        }

        return rawList == null ? List.of() : rawList.stream()
                .map(obj -> (RedisMessageDTO) obj) // 需要 RedisConfig 配置 JSON 序列化
                .map(MessageTools::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        String key = AiConstant.CHAT_PREFIX + conversationId;
        redisTemplate.delete(key);

        List<RedisMessageDTO> dtos = messages.stream()
                .map(MessageTools::toDTO)
                .toList();

        if (!dtos.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, dtos.toArray());
            redisTemplate.expire(key, AiConstant.CHAT_TTL);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(AiConstant.CHAT_PREFIX + conversationId);
    }

    @Override
    public List<String> findConversationIds() {
        Set<String> keys = redisTemplate.keys(AiConstant.CHAT_PREFIX + "*");
        if (keys == null) {
            return List.of();
        }
        return keys.stream()
                .map(key -> key.replace(AiConstant.CHAT_PREFIX, ""))
                .collect(Collectors.toList());
    }
}