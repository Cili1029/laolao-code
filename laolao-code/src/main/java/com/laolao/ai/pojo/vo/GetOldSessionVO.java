package com.laolao.ai.pojo.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GetOldSessionVO {
    /**
     * 回话Id
     */
    private Integer sessionId;

    /**
     * 历史信息
     */
    private List<RedisMessageVO> messages;
}
