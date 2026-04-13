package com.laolao.pojo.dto;

import com.laolao.pojo.entity.QuestionTestCase;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddQuestionDTO implements Serializable {
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
     * 考试中定义的分数
     */
    private Integer questionScore;

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
     * 初始化模板代码 (如 public class Main...)
     */
    private String templateCode;

    /**
     * 标准答案代码 (供 AI 参考)
     */
    private String standardSolution;

    /**
     * 父题目ID(0-祖宗模板题，非0-考试属子题快照)
     */
    private Integer parentId;

    /**
     * 测试示例
     */
    private List<QuestionTestCase> testCases;
}