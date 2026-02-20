package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JudgeDTO {
    /**
     * 考试ID
     */
    private Integer examId;

    /**
     * 考试记录ID
     */
    private Integer recordId;

    /**
     * 题目主键
     */
    private Integer questionId;

    /**
     * 学生代码
     */
    private String code;
}
