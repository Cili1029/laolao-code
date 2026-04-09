package com.laolao.pojo.vo;

import com.laolao.pojo.entity.QuestionTestCase;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseExamQuestionVO implements Serializable {
    /**
     * 题目Id
     */
    private Integer id;

    /**
     * 题目值
     */
    private Integer score;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 代码
     */
    private String code;

    /**
     * 是否以通过判题(0-否, 1-是)
     */
    private Integer isValidated;

    /**
     * 测试用例
     */
    private List<QuestionTestCase> testCases;
}