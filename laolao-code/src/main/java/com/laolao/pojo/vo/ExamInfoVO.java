package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamInfoVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 考试标题
     */
    private String title;

    /**
     * 考试状态（给老师看）
     */
    private Integer studentStatus;

    /**
     * 学生状态
     */
    private Integer status;

    /**
     * 考试说明
     */
    private String description;

    /**
     * 所属组
     */
    private String studyGroup;

    /**
     * 题目数量
     */
    private Integer questions;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 考生进入时间
     */
    private LocalDateTime enterTime;

    /**
     * 学生提交时间
     */
    private LocalDateTime submitTime;
}

