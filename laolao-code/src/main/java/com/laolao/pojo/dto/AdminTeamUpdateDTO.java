package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 组表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AdminTeamUpdateDTO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 组名
     */
    private String name;

    /**
     * 组描述
     */
    private String description;

    /**
     * 组邀请码
     */
    private String inviteCode;
}
