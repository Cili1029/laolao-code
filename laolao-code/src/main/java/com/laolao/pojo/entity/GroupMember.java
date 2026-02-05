package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 班级成员关联表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GroupMember implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 组ID
     */
    private Integer groupId;

    /**
     * 成员ID
     */
    private Integer memberId;
}