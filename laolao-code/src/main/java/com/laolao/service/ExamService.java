package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.vo.*;

import java.util.List;

public interface ExamService {
    Result<List<ExamVO>> getSimpleExam();

    Result<ExamInfoVO> getExamInfo(Integer examId);

    Result<Integer> startExam(Integer examId);

    Result<ExamBeginVO> getExamQuestion(Integer recordId);

    Result<JudgeRecordVO> judge(JudgeDTO judgeDTO);

    Result<Integer> createExam(CreateExamDTO createExamDTO);

    Result<JudgeRecordVO> judgeTestCase(JudgeTestCaseDTO judgeTestCaseDTO);

    Result<Integer> saveAndAddToExam(SaveAndAddToExamDTO saveAndAddToExamDTO);

    Result<String> removeQuestion(Integer examId, Integer questionId);

    Result<List<DraftQuestionVO>> getDraftQuestion(Integer examId);
}
