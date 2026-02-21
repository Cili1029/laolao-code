package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.SimpleSubmitRecordVO;

import java.util.List;

public interface SubmitRecordService {
    Result<List<SimpleSubmitRecordVO>> getSimpleSubmitRecord(Integer examRecordId);

    Result<List<JudgeResult>> getDetailSubmitRecord(Integer submitRecordId);
}
