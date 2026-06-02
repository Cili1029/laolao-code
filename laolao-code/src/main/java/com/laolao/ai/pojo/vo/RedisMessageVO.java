package com.laolao.ai.pojo.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RedisMessageVO {
    /**
     * 消息类型: SYSTEM, USER, ASSISTANT, TOOL
     */
    private String type;

    /**
     * 内容
     */
    private String content;
}
