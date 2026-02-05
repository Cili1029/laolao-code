package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 组表 (老师创建的组)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Group implements Serializable {

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
     * 创建老师的ID
     */
    private Integer advisorId;

    /**
     * 组邀请码
     */
    private String inviteCode;
}
