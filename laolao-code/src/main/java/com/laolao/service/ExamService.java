package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.JudgeDTO;
import com.laolao.pojo.vo.ExamBeginVO;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.pojo.vo.JudgeRecordVO;

import java.util.List;

public interface ExamService {
    Result<List<ExamVO>> getSimpleExam();

    Result<ExamInfoVO> getExamInfo(Integer examId);

    Result<Integer> startExam(Integer examId);

    Result<ExamBeginVO> getExamQuestion(Integer recordId);

    Result<JudgeRecordVO> judge(JudgeDTO judgeDTO);
}
