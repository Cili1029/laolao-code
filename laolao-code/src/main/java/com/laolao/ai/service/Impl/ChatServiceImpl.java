package com.laolao.ai.service.Impl;

import com.laolao.ai.constant.AiConstant;
import com.laolao.ai.pojo.dto.RedisMessageDTO;
import com.laolao.ai.pojo.vo.GetOldSessionVO;
import com.laolao.ai.service.ChatService;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private ChatClient chatClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MapStruct mapStruct;

    @Override
    public Result<GetOldSessionVO> getOldSession() {
        String key = AiConstant.CHAT_PREFIX + SecurityUtils.getUserId();

        if (TRUE.equals(redisTemplate.hasKey(key))) {
            // 存在
            List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);

            // 续期
            if (rawList != null && !rawList.isEmpty()) {
                redisTemplate.expire(key, AiConstant.CHAT_TTL);
            }

            List<RedisMessageDTO> messageDTOS = (rawList == null)
                    ? List.of() : rawList.stream()
                    .map(obj -> (RedisMessageDTO) obj)
                    .filter(dto -> "user".equals(dto.getType()) || "assistant".equals(dto.getType()))
                    .toList();
            GetOldSessionVO getOldSessionVO = new GetOldSessionVO();
            getOldSessionVO.setSessionId(SecurityUtils.getUserId());
            getOldSessionVO.setMessages(mapStruct.messageDTOSToMessageVOS(messageDTOS));

            return Result.success(getOldSessionVO);
        }
        return Result.success(null, null);
    }

    @Override
    public Flux<String> chat(String userInput) {
        return chatClient.prompt()
                .user(userInput)
                .advisors(a -> a
                        .param(ChatMemory.CONVERSATION_ID, SecurityUtils.getUserId() == null ? "0" : SecurityUtils.getUserId().toString())
                )
                .stream()
                .content();
    }

    @Override
    public Result<String> clear() {
        redisTemplate.delete(AiConstant.CHAT_PREFIX + SecurityUtils.getUserId());
        return Result.success("清除完成");
    }
}
