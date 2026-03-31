package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 成员判题最终成绩汇总表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JudgeMemberResult implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 考试Id
     */
    private Integer examId;

    /**
     * 关联的考试记录ID
     */
    private Integer examRecordId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 最优判题记录ID
     */
    private Integer bestJudgeRecordId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 判题状态
     */
    private Integer status;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}