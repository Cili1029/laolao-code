package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.JudgeResult;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.MemberExamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemberExamServiceImpl implements MemberExamService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private JudgeService judgeService;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private MapStruct mapStruct;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;

    @Override
    public Result<Integer> startExam(Integer examId) {
        // 查看记录里是否有正在考试的记录
        Integer userId = SecurityUtils.getUserId();
        ExamRecord examRecord = examRecordMapper.selectExamRecord(userId, examId);
        if (examRecord != null && examRecord.getStatus() == 0) {
            // 考试进行中，不用创建新记录，直接返回所记录的记录Id
            return Result.success(examRecord.getId());
        }
        ExamRecord record = ExamRecord.builder()
                .examId(examId)
                .userId(userId)
                .build();
        examRecordMapper.insert(record);
        return Result.success(record.getId());
    }

    @Override
    public Result<ExamBeginVO> getExamQuestion(Integer recordId) {
        // 获取这个记录所在的考试Id
        Integer examId = examRecordMapper.selectExamIdByRecordId(recordId);
        // 获取其题目以及当前学生每道题的分数
        List<ExamQuestionVO> examQuestionVOList = examMapper.selectQuestionById(examId, recordId, SecurityUtils.getUserId());
        ExamBeginVO examBeginVO = new ExamBeginVO();
        examBeginVO.setExamId(examId);
        examBeginVO.setQuestions(examQuestionVOList);
        return Result.success(examBeginVO);
    }

    @Override
    public Result<JudgeRecordVO> judge(JudgeDTO judgeDTO) {
        try {
            // 获取测试用例
            List<QuestionTestCase> questionTestCases = questionTestCaseMapper.selectList(
                    Wrappers.lambdaQuery(QuestionTestCase.class)
                            .eq(QuestionTestCase::getQuestionId, judgeDTO.getQuestionId()));
            // 获取这一题定的分值
            Integer score = examMapper.selectScoreByQuestionId(judgeDTO.getExamId(), judgeDTO.getQuestionId());
            JudgeResult judge = judgeService.judge(judgeDTO.getCode(), questionTestCases);
            // 填写分数
            if (judge.getStatus() == JudgeConstant.STATUS_AC) {
                judge.setScore(score);
            } else if (judge.getStatus() == JudgeConstant.STATUS_WA) {
                judge.setScore((score * judge.getPassTestCaseCount() / questionTestCases.size()));
            } else {
                judge.setScore(0);
            }
            // 转换写入记录提交表
            JudgeRecord judgeRecord = mapStruct.JudgeResultToJudgeRecord(judge);
            judgeRecord.setExamRecordId(judgeDTO.getRecordId());
            judgeRecord.setQuestionId(judgeDTO.getQuestionId());
            judgeRecord.setUserId(SecurityUtils.getUserId());
            judgeRecord.setAnswerCode(judgeDTO.getCode());
            judgeRecordMapper.insert(judgeRecord);
            // 转VO返回
            JudgeRecordVO judgeRecordVO = mapStruct.JudgeResultToJudgeRecordVO(judge);
            return Result.success(judgeRecordVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("判题失败！练习管理员！");
        }
    }
}
