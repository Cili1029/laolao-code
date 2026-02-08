package com.laolao.pojo.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 题目表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Question implements Serializable {

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
     * 标签列表 [ "栈", "简单", "Java" ]
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
     * 测试用例 [ {"input": "1 2", "output": "3"}, {"input": "2 2", "output": "4"} ]
     */
    private List<Map<String, String>> testCases;

    /**
     * 初始化模板代码 (如 public class Main...)
     */
    private String templateCode;

    /**
     * 标准答案代码 (供 AI 参考)
     */
    private String standardSolution;

    /**
     * 题目解析 (由老师编写或 AI 生成)
     */
    private String explanation;

    /**
     * 创建者ID（导师ID）
     */
    private Integer advisorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（自动更新）
     */
    private LocalDateTime updateTime;
}