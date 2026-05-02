package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionBankDialogTagVO implements Serializable {

    /**
     * question id
     */
    private Integer id;

    /**
     * 标签名
     */
    private String name;
}