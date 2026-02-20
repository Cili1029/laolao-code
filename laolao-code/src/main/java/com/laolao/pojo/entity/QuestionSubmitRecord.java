package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class QuestionSubmitRecord implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 题目状态：0-通过(AC), 1-解答错误(WA)
     */
    private Integer status;

    /**
     * 该题得分
     */
    private Integer score;

    /**
     * 学生提交的代码快照
     */
    private String answerCode;

    /**
     * 沙盒返回的原始错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时(ms)
     */
    private Integer time;

    /**
     * 内存消耗(KB)
     */
    private Integer memory;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}