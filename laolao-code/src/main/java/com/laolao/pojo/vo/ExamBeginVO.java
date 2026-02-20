package com.laolao.pojo.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamBeginVO {
    /**
     * 考试Id
     */
    private Integer examId;

    /**
     * 考试题目
     */
    private List<ExamQuestionVO> questions;
}
