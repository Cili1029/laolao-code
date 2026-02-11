package com.laolao.service.impl;

import com.laolao.common.context.UserContext;
import com.laolao.common.result.Result;
import com.laolao.mapper.ExamMapper;
import com.laolao.pojo.entity.Exam;
import com.laolao.pojo.entity.ExamRecord;
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
    public Result<List<ExamQuestionVO>> getExamQuestion(Integer recordId) {
        // 获取这个记录的考试的题目Id
        Exam exam = examMapper.selectExamByRecordId(recordId);
        List<ExamQuestionVO> examQuestionVOList = examMapper.selectQuestionById(exam.getQuestions());
        return Result.success(examQuestionVOList);
    }
}
