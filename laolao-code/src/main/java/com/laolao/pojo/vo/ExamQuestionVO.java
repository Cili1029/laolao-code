package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExamQuestionVO implements Serializable {

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
     * 难度：0-简单, 1-中等, 2-困难
     */
    private Byte difficulty;

    /**
     * 初始化模板代码 (如 public class Main...)
     */
    private String templateCode;
}