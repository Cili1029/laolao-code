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
public class JudgeRecord implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 是否为本题最优记录：0-否，1-是
     */
    private Integer isBest;

    /**
     * 退出状态码（0为成功，非0为失败）
     */
    private Integer exitCode;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 学生提交的代码快照
     */
    private String answerCode;

    /**
     * 标准输出
     */
    private String stdout;

    /**
     * 错误输出（包括报错信息）
     */
    private String stderr;

    /**
     * 错误示例Id
     */
    private Integer questionTestCaseId;

    /**
     * 判题状态
     */
    private String status;

    /**
     * 执行耗时(ms)
     */
    private Integer time;

    /**
     * 内存消耗(MB)
     */
    private Integer memory;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}