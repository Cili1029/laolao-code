package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinGroupDTO implements Serializable {

    /**
     * 成员ID
     */
    private Integer memberId;

    /**
     * 组邀请码
     */
    private String inviteCode;

    /**
     * 组ID
     */
    private Integer groupId;
}
