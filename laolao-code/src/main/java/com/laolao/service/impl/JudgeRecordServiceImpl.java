package com.laolao.service.impl;

import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.result.Result;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.mapper.QuestionTestCaseMapper;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import com.laolao.service.JudgeRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JudgeRecordServiceImpl implements JudgeRecordService {
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;

    @Override
    public Result<List<SimpleJudgeRecordVO>> getSimpleJudgeRecord(Integer examRecordId, Integer questionId) {
        List<SimpleJudgeRecordVO> simpleJudgeRecordVOList = judgeRecordMapper.selectSimpleJudgeRecord(examRecordId, questionId);
        return Result.success(simpleJudgeRecordVOList);
    }

    @Override
    public Result<JudgeRecordVO> getDetailJudgeRecord(Integer judgeRecordId) {
        // 获取判题结果
        JudgeRecordVO judgeRecordVO = judgeRecordMapper.selectDetailJudgeRecord(judgeRecordId);
        // 获取这次判题的示例（如果是答案错误）
        if (judgeRecordVO.getStatus() == JudgeConstant.STATUS_WA) {
            QuestionTestCase questionTestCase = questionTestCaseMapper.selectById(judgeRecordVO.getQuestionTestCaseId());
            judgeRecordVO.setQuestionTestCase(questionTestCase);
        }
        return Result.success(judgeRecordVO);
    }
}
