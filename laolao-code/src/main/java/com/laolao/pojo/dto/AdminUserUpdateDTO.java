package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 用户表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AdminUserUpdateDTO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 账号
     */
    private String username;

    /**
     * 名字
     */
    private String name;

    /**
     * 用户角色：0-管理员，1-导师，2-成员
     */
    private Integer role;
}