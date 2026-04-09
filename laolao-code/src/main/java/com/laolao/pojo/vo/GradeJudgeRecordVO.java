package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GradeJudgeRecordVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 考生得分
     */
    private Integer userScore;

    /**
     * 学生提交的代码快照
     */
    private String answerCode;

    /**
     * 判题状态
     */
    private Integer status;
}