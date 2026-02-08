package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamVO;

import java.util.List;

public interface ExamService {
    Result<List<ExamVO>> getSimpleExam();
}
