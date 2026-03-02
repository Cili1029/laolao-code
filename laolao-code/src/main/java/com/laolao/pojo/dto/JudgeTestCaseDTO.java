package com.laolao.pojo.dto;

import com.laolao.pojo.entity.QuestionTestCase;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JudgeTestCaseDTO {
    /**
     * 学生代码
     */
    private String code;

    /**
     * 测试示例
     */
    private QuestionTestCase testCase;
}
