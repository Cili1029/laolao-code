package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.AdminJudgeRecordVO;
import com.laolao.pojo.vo.AdminJudgerInfoVO;

import java.util.List;

public interface DockerService {
    Result<AdminJudgerInfoVO> getJudgerInfo();

    Result<List<AdminJudgeRecordVO>> getJudgeRecord();

    Result<String> adjustPoolSize(Integer newSize);
}
