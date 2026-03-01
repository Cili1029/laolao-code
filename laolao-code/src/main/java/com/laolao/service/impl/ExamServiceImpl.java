package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.JudgeResult;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.common.util.StudentExamStatusCalculator;
import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.mapper.QuestionTestCaseMapper;
import com.laolao.pojo.dto.CreateExamDTO;
import com.laolao.pojo.dto.JudgeDTO;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
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
    public Result<List<ExamVO>> getSimpleExam() {
        Integer userId = SecurityUtils.getUserId();
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        return Result.success(examVOList);
    }

    @Override
    public Result<ExamInfoVO> getExamInfo(Integer examId) {
        ExamInfoVO examInfoVO = examMapper.selectExamInfo(examId);
        // 如果是导师
        if (SecurityUtils.hasAuthority("ADVISOR")) {
            // 不用填写时间和学生状态，直接返回
            return Result.success(examInfoVO);
        }
        // 查询考生是否已经进入，顺便获取进入时间和交卷时间
        ExamRecord examRecord = examRecordMapper.selectStatusByExamId(examId, SecurityUtils.getUserId());
        if (examRecord != null) {
            examInfoVO.setEnterTime(examRecord.getEnterTime());
            examInfoVO.setSubmitTime(examRecord.getSubmitTime());
        }
        // 设置当前状态
        examInfoVO.setStudentStatus(StudentExamStatusCalculator.calculate(examInfoVO.getStartTime(), examInfoVO.getEndTime(),
                examInfoVO.getEnterTime(), examInfoVO.getSubmitTime()));
        return Result.success(examInfoVO);
    }

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
            JudgeResult judge = judgeService.judge(judgeDTO.getCode(), questionTestCases, score);
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

    @Override
    public Result<Integer> createExam(CreateExamDTO createExamDTO) {
        if (createExamDTO.getStartTime().isAfter(createExamDTO.getEndTime())) {
            return Result.error("开始时间应该在考试时间之前！");
        }
        Exam exam = Exam.builder()
                .title(createExamDTO.getTitle())
                .description(createExamDTO.getDescription())
                .advisorId(SecurityUtils.getUserId())
                .studyGroupId(createExamDTO.getStudyGroupId())
                .startTime(createExamDTO.getStartTime())
                .endTime(createExamDTO.getEndTime())
                .build();
        examMapper.insert(exam);
        return Result.success("创建成功", exam.getId());
    }
}
