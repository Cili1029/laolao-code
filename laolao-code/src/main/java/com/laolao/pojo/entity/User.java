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
@ToString
@Builder
public class User implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 账号
     */
    private String username;

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

    // 获取权限标识
    public String getRoleName() {
        if (this.role == 0) {
            return "ADMIN";
        } else if (this.role == 1) {
            return "ADVISOR";
        } else if (this.role == 2) {
            return "MEMBER";
        } else {
            return "ERROR";
        }
    }
}