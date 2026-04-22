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
     * 用户ID
     */
    private Integer userId;

    /**
     * 关联的考试记录ID
     */
    private Integer examRecordId;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 判题状态(-1-判题中, 0-AC, 1-WA, 2-MLE, 3-TLE, 4-RE, 5-CE, 6-SE, 7-UN)
     */
    private Integer status;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 代码快照
     */
    private String answerCode;

    /**
     * 编译错误/系统异常
     */
    private String errorMessage;

    /**
     * 测试示例总数
     */
    private Integer totalCount;

    /**
     * 测试示例通过数
     */
    private Integer passCount;

    /**
     * 未通过的的测试示例输入
     */
    private String failedInput;

    /**
     * 未通过的的测试示例预期输出
     */
    private String failedExpect;

    /**
     * 未通过的的测试示例实际输出
     */
    private String failedActual;

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