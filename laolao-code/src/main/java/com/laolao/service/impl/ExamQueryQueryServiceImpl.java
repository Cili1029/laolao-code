package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laolao.common.result.Result;
import com.laolao.common.util.ExamHelper;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.ExamQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamQueryQueryServiceImpl implements ExamQueryService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private AiReportMapper aiReportMapper;

    @Override
    public Result<List<ExamVO>> getSimpleExam() {
        Integer userId = SecurityUtils.getUserId();
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        return Result.success(examVOList);
    }

    @Override
    public Result<ExamInfoVO> getExamInfo(Integer examId) {
        ExamInfoVO examInfoVO = examMapper.selectExamInfo(examId);
        Integer userId = SecurityUtils.getUserId();
        Integer role = SecurityUtils.hasAuthority("MANAGER") ? 1 : 2;

        // 如果是学生
        UserExamRecord record = null;
        if (SecurityUtils.hasAuthority("USER")) {
            record = examRecordMapper.selectStatusByExamId(examId, userId);
            examInfoVO.setUserExamRecord(record);
        }

        ExamPermissionsVO calculate = ExamHelper.calculate(role, examInfoVO.getStatus(), examInfoVO.getStartTime(), examInfoVO.getEndTime(), record);
        examInfoVO.setExamPermissions(calculate);
        return Result.success(examInfoVO);
    }

    @Override
    public Result<ExamCompleteReportVO> getExamCompleteReport(Integer examId) {
        AiReport report = aiReportMapper.selectOne(Wrappers.<AiReport>lambdaQuery()
                .eq(AiReport::getTargetType, 3)
                .eq(AiReport::getTargetId, examId));
        ExamCompleteReportVO examCompleteReportVO = new ExamCompleteReportVO();
        if (report != null) {
            examCompleteReportVO.setAiReport(report.getContent());
        }
        return Result.success(examCompleteReportVO);
    }
}
