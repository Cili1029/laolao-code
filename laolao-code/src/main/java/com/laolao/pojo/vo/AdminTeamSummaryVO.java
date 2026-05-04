package com.laolao.pojo.vo;

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
public class AdminTeamSummaryVO implements Serializable {

    /**
     * 组数量
     */
    private Integer teamCount;

    /**
     * 考试发布数量
     */
    private Integer examCount;
}