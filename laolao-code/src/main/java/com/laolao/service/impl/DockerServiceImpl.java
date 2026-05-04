package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.judge.JudgeService;
import com.laolao.pojo.vo.AdminJudgerInfoVO;
import com.laolao.service.DockerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DockerServiceImpl implements DockerService {
    @Resource
    JudgeService judgeService;

    @Override
    public Result<AdminJudgerInfoVO> getJudgerInfo() {
        AdminJudgerInfoVO judgeInfo = judgeService.getJudgeInfo();
        return Result.success(judgeInfo);
    }
}
