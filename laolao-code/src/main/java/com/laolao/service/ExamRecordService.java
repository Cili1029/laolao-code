package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.ExamVO;

import java.util.List;

public interface ExamRecordService {
    Result<List<ExamRecordVO>> getSimpleExamRecord();
}
