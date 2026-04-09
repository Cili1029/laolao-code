package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionBankVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 作者
     */
    private String manager;

    /**
     * 是否公开
     */
    private Integer isPublic;
}