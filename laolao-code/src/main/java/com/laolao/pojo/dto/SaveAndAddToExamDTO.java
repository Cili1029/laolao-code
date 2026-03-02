package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaveAndAddToExamDTO {
    /**
     * 考试Id
     */
    private Integer examId;

    /**
     * 题目（题库里没有的新题）
     */
    private AddQuestionDTO question;
}
