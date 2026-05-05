package com.laolao.pojo.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserExamAnswerInfoVO {
    /**
     * 总分
     */
    public Integer score;

    /**
     * 考试记录Id
     */
    public Integer examRecordId;

    /**
     * 题目作答情况
     */
    public List<UserExamAnswerQuestionStatusVO> questions;
}
