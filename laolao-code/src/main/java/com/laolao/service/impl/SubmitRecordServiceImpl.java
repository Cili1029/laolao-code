package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.mapper.SubmitRecordMapper;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.SimpleSubmitRecordVO;
import com.laolao.service.SubmitRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmitRecordServiceImpl implements SubmitRecordService {
    @Resource
    private SubmitRecordMapper submitRecordMapper;

    @Override
    public Result<List<SimpleSubmitRecordVO>> getSimpleSubmitRecord(Integer examRecordId) {
        List<SimpleSubmitRecordVO> simpleSubmitRecordVOList = submitRecordMapper.selectSimpleSubmitRecord(examRecordId);
        return Result.success(simpleSubmitRecordVOList);
    }

    @Override
    public Result<List<JudgeResult>> getDetailSubmitRecord(Integer submitRecordId) {
        return null;
    }
}
