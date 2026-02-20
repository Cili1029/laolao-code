package com.laolao.pojo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionScore {
    /**
     * 题目
     */
    private int question;

    /**
     * 分值
     */
    private int score;
}
