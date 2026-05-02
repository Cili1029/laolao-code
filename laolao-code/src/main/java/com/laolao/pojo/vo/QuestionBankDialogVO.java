package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionBankDialogVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 难度
     */
    private Integer difficulty;
}