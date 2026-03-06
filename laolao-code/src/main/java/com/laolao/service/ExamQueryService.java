package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.*;

import java.util.List;

public interface ExamQueryService {
    Result<List<ExamVO>> getSimpleExam();

    Result<ExamInfoVO> getExamInfo(Integer examId);
}
