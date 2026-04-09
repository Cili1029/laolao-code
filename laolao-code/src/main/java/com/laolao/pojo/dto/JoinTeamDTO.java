package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinTeamDTO implements Serializable {

    /**
     * 成员ID
     */
    private Integer userId;

    /**
     * 组邀请码
     */
    private String inviteCode;

    /**
     * 组ID
     */
    private Integer teamId;
}
