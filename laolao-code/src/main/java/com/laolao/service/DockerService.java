package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.AdminJudgerInfoVO;

public interface DockerService {
    Result<AdminJudgerInfoVO> getJudgerInfo();
}
