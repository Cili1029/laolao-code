package com.laolao.pojo.vo;

import com.laolao.pojo.entity.QuestionTestCase;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JudgeRecordVO {

    /**
     * 判题状态
     */
    private Integer status;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 标准输出
     */
    private String stdout;

    /**
     * 错误输出（包括报错信息）
     */
    private String stderr;

    /**
     * 提示
     */
    private String msg;

    /**
     * 错误示例Id
     */
    private Integer questionTestCaseId;

    /**
     * 错误示例（用于给前端看）
     */
    private QuestionTestCase questionTestCase;

    /**
     * 消耗时间 (ms)
     */
    private Integer time;

    /**
     * 消耗内存 (MB)
     */
    private Integer memory;
}
