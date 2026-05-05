package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.AdminJudgeRecordVO;
import com.laolao.pojo.vo.AdminJudgerInfoVO;
import com.laolao.service.DockerService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docker")
@PreAuthorize("hasRole('ADMIN')")
public class DockerController {
    @Resource
    private DockerService dockerService;

    /**
     * 获取判题机信息
     *
     * @return 判题机信息
     */
    @GetMapping("/judger")
    public Result<AdminJudgerInfoVO> getJudgerInfo() {
        return dockerService.getJudgerInfo();
    }

    /**
     * 获取最近判题记录
     *
     * @return 判题记录列表
     */
    @GetMapping("/judge-record")
    public Result<List<AdminJudgeRecordVO>> getJudgeRecord() {
        return dockerService.getJudgeRecord();
    }

    /**
     * 扩容容器池
     *
     * @return 结果信息
     */
    @PostMapping("/adjust")
    public Result<String> adjustPoolSize(@RequestParam Integer newSize) {
        return dockerService.adjustPoolSize(newSize);
    }
}
