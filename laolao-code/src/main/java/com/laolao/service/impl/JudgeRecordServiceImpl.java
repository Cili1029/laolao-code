package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import com.laolao.service.JudgeRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JudgeRecordServiceImpl implements JudgeRecordService {
    @Resource
    private JudgeRecordMapper judgeRecordMapper;

    @Override
    public Result<List<SimpleJudgeRecordVO>> getSimpleJudgeRecord(Integer examRecordId, Integer questionId) {
        List<SimpleJudgeRecordVO> simpleJudgeRecordVOList = judgeRecordMapper.selectSimpleSubmitRecord(examRecordId, questionId);
        return Result.success(simpleJudgeRecordVOList);
    }

    @Override
    public Result<List<JudgeResult>> getDetailJudgeRecord(Integer submitRecordId) {
        return null;
    }
}
