package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JudgeDTO {
    /**
     * 题目主键
     */
    private Integer id;

    /**
     * 学生代码
     */
    private String Code;
}
