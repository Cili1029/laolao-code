package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
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
     * 考生进入时间
     */
    private LocalDateTime enterTime;

    /**
     * 学生提交时间
     */
    private LocalDateTime submitTime;
}
