package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberExamJudgeRecordVO implements Serializable {

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
    private Integer memberScore;

    /**
     * 学生提交的代码快照
     */
    private String answerCode;

    /**
     * 标准答案代码 (供 AI 参考)
     */
    private String standardSolution;

    /**
     * 判题状态
     */
    private Integer status;

    /**
     * ai报告
     */
    private String aiReport;
}