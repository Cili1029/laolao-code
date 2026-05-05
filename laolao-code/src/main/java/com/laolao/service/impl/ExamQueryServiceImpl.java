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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamQueryServiceImpl implements ExamQueryService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private AiReportMapper aiReportMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Result<List<ExamVO>> getSimpleExam() {
        Integer userId = SecurityUtils.getUserId();
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        examVOList.forEach(examVO -> {
            ExamSummaryPermissionsVO permissions = ExamHelper.calculateSummary(examVO.getStatus());
            examVO.setSummaryPermissions(permissions);
        });
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

        ExamDetailPermissionsVO calculate = ExamHelper.calculateDetail(role, examInfoVO.getStatus(), examInfoVO.getStartTime(), examInfoVO.getEndTime(), record);
        examInfoVO.setExamPermissions(calculate);
        return Result.success(examInfoVO);
    }

    @Override
    public Result<ExamCompleteReportVO> getExamCompleteReport(Integer examId) {
        ExamCompleteReportVO examCompleteReportVO = new ExamCompleteReportVO();
        // 学生情况
        examCompleteReportVO.setUserList(examRecordMapper.selectUserJoinExamInfo(examId));

        // ai报告
        AiReport aireport = aiReportMapper.selectOne(Wrappers.<AiReport>lambdaQuery()
                .eq(AiReport::getTargetType, 3)
                .eq(AiReport::getTargetId, examId));
        if (aireport != null) {
            examCompleteReportVO.setAiReport(aireport.getContent());
        }
        return Result.success(examCompleteReportVO);
    }

    @Override
    public Result<UserExamAnswerInfoVO> getUserExamAnswerInfo(Integer examId) {
        // 获取分数和报告
        UserExamAnswerInfoVO userExamAnswerInfoVO = examRecordMapper.selectUserExamScore(examId, SecurityUtils.getUserId());
        // 获取每一题作答情况
        userExamAnswerInfoVO.setQuestions(questionMapper.selectUserExamQuestionAnswerInfo(examId, SecurityUtils.getUserId()));
        return Result.success(userExamAnswerInfoVO);
    }
}
