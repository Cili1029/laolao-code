package com.laolao.service;

import com.laolao.common.result.Result;

public interface ManagerExamService {
    Result<String> cancelExam(Integer examId);
}
