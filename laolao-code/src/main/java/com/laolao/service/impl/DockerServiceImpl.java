package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.judge.JudgeService;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.vo.AdminJudgeRecordVO;
import com.laolao.pojo.vo.AdminJudgerInfoVO;
import com.laolao.service.DockerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerServiceImpl implements DockerService {
    @Resource
    JudgeService judgeService;
    @Resource
    JudgeRecordMapper judgeRecordMapper;

    @Override
    public Result<AdminJudgerInfoVO> getJudgerInfo() {
        AdminJudgerInfoVO judgeInfo = judgeService.getJudgeInfo();
        return Result.success(judgeInfo);
    }

    @Override
    public Result<List<AdminJudgeRecordVO>> getJudgeRecord() {
        List<AdminJudgeRecordVO> adminJudgeRecordVOList =judgeRecordMapper.selectJudgeRecordWithAdmin();
        return Result.success(adminJudgeRecordVOList);
    }

    @Override
    public Result<String> adjustPoolSize(Integer newSize) {
        if (judgeService.adjustPoolSize(newSize)) {
            return Result.success("扩容成功");
        }
        return Result.error("只可扩容不可减少！");
    }
}
