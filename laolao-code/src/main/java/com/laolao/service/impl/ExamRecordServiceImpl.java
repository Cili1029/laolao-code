package com.laolao.service.impl;

import com.laolao.common.context.UserContext;
import com.laolao.common.result.Result;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.service.ExamRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamRecordServiceImpl implements ExamRecordService {
    @Resource
    private ExamRecordMapper examRecordMapper;

    @Override
    public Result<List<ExamRecordVO>> getSimpleExamRecord() {
        Integer userId = UserContext.getCurrentId();
        List<ExamRecordVO> examRecordVOList = examRecordMapper.selectSimpleExamRecord(userId);
        return Result.success(examRecordVOList);
    }
}
