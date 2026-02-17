package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.JudgeDTO;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamQuestionVO;
import com.laolao.pojo.vo.ExamVO;

import java.util.List;

public interface ExamService {
    Result<List<ExamVO>> getSimpleExam();

    Result<ExamInfoVO> getExamInfo(Integer examId);

    Result<Integer> startExam(Integer examId);

    Result<List<ExamQuestionVO>> getExamQuestion(Integer recordId);

    Result<JudgeResult> judge(JudgeDTO judgeDTO);
}
