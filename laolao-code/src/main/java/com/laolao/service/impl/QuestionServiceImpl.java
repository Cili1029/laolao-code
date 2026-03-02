package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.QuestionMapper;
import com.laolao.mapper.QuestionTestCaseMapper;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;
    @Resource
    private MapStruct mapStruct;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> addQuestion(AddQuestionDTO addQuestionDTO) {
        Question question = mapStruct.addQuestionDTOtoQuestion(addQuestionDTO);
        question.setAdvisorId(SecurityUtils.getUserId());
        if (question.getId() != null) {
            // 这是旧题，做更新
            questionMapper.updateById(question);
            // 测试示例直接全量删除
            questionTestCaseMapper.delete(new LambdaQueryWrapper<QuestionTestCase>()
                    .eq(QuestionTestCase::getQuestionId, question.getId()));
        } else {
            // 新题，做添加
            questionMapper.insert(question);
            // 执行完insert后，MyBatis-Plus会自动把数据库生成的自增ID赋值回question
            // 此时 question.getId() 已经有值了
        }

        // 统一插入测试用例
        List<QuestionTestCase> testCases = addQuestionDTO.getTestCases();
        if (testCases != null && !testCases.isEmpty()) {
            // 遍历一遍绑定questionId，并清空旧的用例Id
            for (QuestionTestCase testCase : testCases) {
                testCase.setQuestionId(question.getId()); // 绑定题目ID
            }
            // 批量插入测试用例
            questionTestCaseMapper.insertBatch(testCases);
        }
        return Result.success("保存成功！",question.getId());
    }
}
