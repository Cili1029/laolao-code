package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.QuestionMapper;
import com.laolao.mapper.QuestionTestCaseMapper;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.dto.QuestionIdDTO;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.QuestionBankVO;
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
    public Result<Integer> addOrUpdateQuestion(AddQuestionDTO addQuestionDTO) {
        Question question = mapStruct.addQuestionDTOtoQuestion(addQuestionDTO);
        question.setAdvisorId(SecurityUtils.getUserId());
        if (question.getId() != null) {
            // 这是旧题（可能是修改祖宗，也可能是修改子题），做更新
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
            for (QuestionTestCase testCase : testCases) {
                testCase.setQuestionId(question.getId()); // 绑定新的题目ID
            }
            // 批量插入测试用例
            questionTestCaseMapper.insertBatch(testCases);
        }

        return Result.success("保存成功！", question.getId());
    }

    @Override
    public Result<Page<QuestionBankVO>> getPrivateQuestions(Integer pageNum, Integer pageSize, String content) {
        Page<QuestionBankVO> page = new Page<>(pageNum, pageSize);
        Page<QuestionBankVO> res = questionMapper.selectPrivateBank(page, SecurityUtils.getUserId(), content);
        return Result.success(res);
    }

    @Override
    public Result<Page<QuestionBankVO>> getPublicQuestions(Integer pageNum, Integer pageSize, String content) {
        Page<QuestionBankVO> page = new Page<>(pageNum, pageSize);
        Page<QuestionBankVO> res = questionMapper.selectPublicBank(page, content);
        return Result.success(res);
    }

    @Override
    public Result<String> changeStatus(QuestionIdDTO questionIdDTO) {
        questionMapper.updateStatus(SecurityUtils.getUserId(), questionIdDTO.getQuestionId());
        return Result.success("更新状态成功！");
    }

    @Override
    public Result<String> delete(Integer questionId) {
        questionMapper.deleteQuestion(SecurityUtils.getUserId(), questionId);
        return Result.success("删除成功！");
    }

    @Override
    public Result<DraftQuestionVO> copyQuestion(Integer questionId) {
        DraftQuestionVO questionVO = questionMapper.selectQuestionById(questionId);
        questionVO.setQuestionScore(0);
        // 测试示例
        questionVO.setTestCases(questionTestCaseMapper.selectBatchByQuestionIds(List.of(questionId)));
        return Result.success(questionVO);
    }
}
