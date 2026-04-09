package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.UserReportVO;

import java.util.List;

public interface UserExamReportService {
    Result<List<ExamRecordVO>> getSimpleExamRecord();

    Result<UserReportVO> getExamReport(Integer recordId);
}
