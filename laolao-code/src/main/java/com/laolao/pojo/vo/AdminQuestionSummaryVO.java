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
public class AdminQuestionSummaryVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer questionCount;

    /**
     * 账号
     */
    private String copyCount;
}