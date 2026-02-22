package com.laolao.service.impl;

import com.laolao.common.context.UserContext;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.mapper.QuestionMapper;
import com.laolao.pojo.dto.JudgeDTO;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.ExamBeginVO;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamQuestionVO;
import com.laolao.pojo.vo.ExamVO;
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
    private QuestionMapper questionMapper;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private MapStruct mapStruct;

    @Override
    public Result<List<ExamVO>> getSimpleExam() {
        Integer userId = UserContext.getCurrentId();
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        return Result.success(examVOList);
    }

    @Override
    public Result<ExamInfoVO> getExamInfo(Integer examId) {
        ExamInfoVO examInfoVO = examMapper.selectExamInfo(examId);
        return Result.success(examInfoVO);
    }

    @Override
    public Result<Integer> startExam(Integer examId) {
        // 查看记录里是否有正在考试的记录
        Integer userId = UserContext.getCurrentId();
        ExamRecord examRecord = examMapper.selectExamRecord(userId, examId);
        if (examRecord != null && examRecord.getStatus() == 0) {
            // 考试进行中，不用创建新记录，直接返回所记录的记录Id
            return Result.success(examRecord.getId());
        }
        ExamRecord record = ExamRecord.builder()
                .examId(examId)
                .userId(userId)
                .build();
        examMapper.insertRecord(record);
        return Result.success(record.getId());
    }

    @Override
    public Result<ExamBeginVO> getExamQuestion(Integer recordId) {
        // 获取这个记录的考试的题目Id
        Exam exam = examMapper.selectExamByRecordId(recordId);
        List<Integer> questionIdList = exam.getQuestions().stream().map(QuestionScore::getQuestion).toList();
        List<ExamQuestionVO> examQuestionVOList = examMapper.selectQuestionById(questionIdList);
        ExamBeginVO examBeginVO = new ExamBeginVO();
        examBeginVO.setExamId(exam.getId());
        examBeginVO.setQuestions(examQuestionVOList);
        return Result.success(examBeginVO);
    }

    @Override
    public Result<JudgeResult> judge(JudgeDTO judgeDTO) {
        try {
            // 获取测试用例
            Question question = questionMapper.selectTestCaseById(judgeDTO.getQuestionId());
            // 获取这一题定的分值
            Integer score = examMapper.selectScoreByQuestionId(judgeDTO.getExamId(), judgeDTO.getQuestionId());
            JudgeResult judge = judgeService.judge(judgeDTO.getCode(), question.getTestCases(), score);
            // 转换写入记录提交表
            JudgeRecord judgeRecord = mapStruct.JudgeResultToJudgeRecord(judge);
            judgeRecord.setExamRecordId(judgeDTO.getRecordId());
            judgeRecord.setQuestionId(judgeDTO.getQuestionId());
            judgeRecord.setUserId(UserContext.getCurrentId());
            judgeRecord.setAnswerCode(judgeDTO.getCode());
            judgeRecordMapper.insert(judgeRecord);
            return Result.success(judge);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("判题失败！练习管理员！");
        }
    }
}
