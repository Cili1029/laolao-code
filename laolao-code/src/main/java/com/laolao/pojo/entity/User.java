package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 用户表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 邮箱：也为登录账号
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 名字
     */
    private String name;

    /**
     * 用户角色：0-导师，1-成员
     */
    private Integer role;
}