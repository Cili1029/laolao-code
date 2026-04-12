package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.service.ManagerExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exam/manager")
public class ManagerExamController {
    @Resource
    private ManagerExamService managerExamService;

    @PostMapping("/cancel")
    public Result<String> cancelExam(@RequestParam Integer examId) {
       return  managerExamService.cancelExam(examId);
    }
}
