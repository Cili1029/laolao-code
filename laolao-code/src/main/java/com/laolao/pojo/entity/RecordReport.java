package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试题目详情报告表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RecordReport implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 关联的考试记录ID
     */
    private Integer examRecordId;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 用户ID（冗余字段，方便查询）
     */
    private Integer userId;

    /**
     * 题目状态：0-未答, 1-通过(AC), 2-解答错误(WA), 3-运行超时, 4-编译错误
     */
    private Byte status;

    /**
     * 该题得分
     */
    private Integer score;

    /**
     * 学生提交的代码快照
     */
    private String answerCode;

    /**
     * AI对这道题的代码审查和改进建议
     */
    private String aiFeedback;

    /**
     * 沙盒返回的原始错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时(ms)
     */
    private Integer timeConsumed;

    /**
     * 内存消耗(KB)
     */
    private Integer memoryConsumed;

    /**
     * 更新时间（自动更新）
     */
    private LocalDateTime updateTime;
}