package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AiReport implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 报告关联的类型: 1-单题判题(judge_record), 2-学生单场试卷(exam_record), 3-全班考试统测(exam)
     */
    private Integer targetType;

    /**
     * 关联的ID (judge_record_id, exam_record_id 或 exam_id)
     */
    private Integer targetId;

    /**
     * AI生成的报告
     */
    private String content;
}

