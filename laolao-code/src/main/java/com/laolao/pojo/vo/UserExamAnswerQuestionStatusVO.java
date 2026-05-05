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
public class UserExamAnswerQuestionStatusVO implements Serializable {
    /**
     * 题目
     */
    private String title;

    /**
     * 题目分数
     */
    private Integer questionScore;

    /**
     * 考生分数
     */
    private Integer userScore;
}