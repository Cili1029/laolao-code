package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.UserExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/exam/user")
public class UserExamController {
    @Resource
    private UserExamService userExamService;

    /**
     * 开始考试
     *
     * @param examId 考试ID
     * @return 考试记录ID
     */
    @PostMapping("/start")
    public Result<Integer> startExam(@RequestParam Integer examId) {
        return userExamService.startExam(examId);
    }

    /**
     * 获取考试题目
     *
     * @param recordId 考试记录ID
     * @return 考试题目列表
     */
    @GetMapping("/begin")
    public Result<ExamBeginVO> getExamQuestion(@RequestParam Integer recordId) {
        return userExamService.getExamQuestion(recordId);
    }

    /**
     * 提交代码判题
     *
     * @param judgeDTO 判题请求参数
     * @return 判题记录Id
     */
    @PostMapping("/judge")
    public Result<Integer> judge(@RequestBody JudgeDTO judgeDTO) {
        return userExamService.judge(judgeDTO);
    }

    /**
     * 交卷
     *
     * @param recordId 考试Id
     * @return 判题结果
     */
    @PutMapping("/submit")
    public Result<String> submit(@RequestParam Integer recordId) {
        return userExamService.submit(recordId);
    }
}
