package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.vo.*;


public interface MemberExamService {

    Result<Integer> startExam(Integer examId);

    Result<ExamBeginVO> getExamQuestion(Integer recordId);

    Result<JudgeRecordVO> judge(JudgeDTO judgeDTO);
}
