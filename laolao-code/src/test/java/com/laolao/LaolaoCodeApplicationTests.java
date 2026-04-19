package com.laolao;

import com.laolao.mapper.QuestionTestCaseMapper;
import com.laolao.pojo.entity.QuestionTestCase;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LaolaoCodeApplicationTests {
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;

    @Test
    void contextLoads() {
        QuestionTestCase questionTestCase = questionTestCaseMapper.selectById(9);
        System.out.println(questionTestCase.getInput());
    }
}