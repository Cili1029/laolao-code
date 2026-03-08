package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.MemberReportVO;

import java.util.List;

public interface MemberExamReportService {
    Result<List<ExamRecordVO>> getSimpleExamRecord();

    Result<MemberReportVO> getExamReport(Integer recordId);
}
