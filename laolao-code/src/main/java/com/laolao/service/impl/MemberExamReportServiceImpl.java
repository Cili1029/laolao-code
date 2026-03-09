package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.MemberReportVO;
import com.laolao.service.MemberExamReportService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberExamReportServiceImpl implements MemberExamReportService {
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
    public Result<MemberReportVO> getExamReport(Integer recordId) {
        MemberReportVO memberReportVO = examRecordMapper.selectMemberInfoByRecordId(recordId);
        memberReportVO.setJudgeRecords(judgeRecordMapper.selectMemberExamReportByRecordId(memberReportVO.getId()));
        return Result.success(memberReportVO);
    }
}
