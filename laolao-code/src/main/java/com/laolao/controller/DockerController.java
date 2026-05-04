package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.AdminJudgerInfoVO;
import com.laolao.service.DockerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/docker")
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
}
