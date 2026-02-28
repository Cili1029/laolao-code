package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * 考试题目分值配置表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("exam_question_config")
public class ExamQuestionConfig implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 所属考试ID
     */
    private Integer examId;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 该题在该场考试中的分值
     */
    private Integer score;

    /**
     * 题目在试卷中的显示顺序
     */
    private Integer sortOrder;
}