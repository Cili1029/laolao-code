package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionBankInfoVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容 (Markdown格式)
     */
    private String content;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 难度：0-简单, 1-中等, 2-困难
     */
    private Byte difficulty;

    /**
     * 时间限制 (ms)
     */
    private Integer timeLimit;

    /**
     * 内存限制 (MB)
     */
    private Integer memoryLimit;

    /**
     * 标准答案代码 (供 AI 参考)
     */
    private String standardSolution;
}