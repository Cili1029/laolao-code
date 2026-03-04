package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionIdDTO implements Serializable {
    /**
     * 主键ID
     */
    private Integer questionId;
}