package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 组成员关联表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StudyGroupMember implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 组ID
     */
    private Integer studyGroupId;

    /**
     * 成员ID
     */
    private Integer memberId;
}