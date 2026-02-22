package com.laolao.pojo.vo;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleJudgeRecordVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 该题得分
     */
    private Integer score;

    /**
     * 执行耗时(ms)
     */
    private Integer time;

    /**
     * 内存消耗(MB)
     */
    private Integer memory;
}