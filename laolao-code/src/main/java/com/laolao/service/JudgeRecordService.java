package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;

import java.util.List;

public interface JudgeRecordService {
    Result<List<SimpleJudgeRecordVO>> getSimpleJudgeRecord(Integer examRecordId, Integer questionId);

    Result<JudgeRecordVO> getDetailJudgeRecord(Integer judgeRecordId);
}
