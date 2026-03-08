package com.laolao.pojo.ai;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InsertAiReportContext implements Serializable {

    /**
     * 主键Id
     */
    @ToolParam(description = "放空即可，Mybatis会自增")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 导师ID
     */
    @ToolParam(description = "报告关联的类型: 1-单题判题(judge_record), 2-学生单场试卷(exam_record), 3-全班考试统测(exam)")
    private Integer targetType;

    /**
     * 状态（0-草稿，1-已发布）
     */
    @ToolParam(description = "关联的ID (judge_record_id, exam_record_id 或 exam_id)")
    private Integer targetId;

    /**
     * 所属组ID
     */
    @ToolParam(description = "AI生成的报告")
    private String content;
}

