package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 用户表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExamCompleteUserVO implements Serializable {
    /**
     * 名字
     */
    private String name;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 考试记录ID
     */
    private Integer examRecordId;
}