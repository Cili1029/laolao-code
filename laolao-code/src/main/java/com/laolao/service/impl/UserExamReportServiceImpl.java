package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.UserReportVO;
import com.laolao.service.UserExamReportService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserExamReportServiceImpl implements UserExamReportService {
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;

    @Override
    public Result<List<ExamRecordVO>> getSimpleExamRecord() {
        Integer userId = SecurityUtils.getUserId();
        List<ExamRecordVO> examRecordVOList = examRecordMapper.selectSimpleExamRecord(userId);
        return Result.success(examRecordVOList);
    }

    @Override
    public Result<UserReportVO> getExamReport(Integer recordId) {
        UserReportVO userReportVO = examRecordMapper.selectUserInfoByRecordId(recordId);
        userReportVO.setJudgeRecords(judgeRecordMapper.selectUserExamReportByRecordId(userReportVO.getId()));
        return Result.success(userReportVO);
    }
}
