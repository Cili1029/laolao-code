package com.laolao.pojo.vo;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleSubmitRecordVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 题目状态：0-通过(AC), 1-解答错误(WA)
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