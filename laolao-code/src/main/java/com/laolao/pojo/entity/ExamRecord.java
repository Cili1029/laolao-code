package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试记录表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExamRecord implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 考试ID
     */
    private Integer examId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 总得分
     */
    private Integer score;

    /**
     * 状态：0-进行中，1-已提交，2-AI已批改
     */
    private Byte status;

    /**
     * 整场考试的综合评价
     */
    private String report;

    /**
     * 学生开始答题时间
     */
    private LocalDateTime startTime;

    /**
     * 学生结束答题时间
     */
    private LocalDateTime endTime;
}
