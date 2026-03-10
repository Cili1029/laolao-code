package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.MemberExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/exam/member")
public class MemberExamController {
    @Resource
    private MemberExamService memberExamService;

    /**
     * 开始考试
     *
     * @param examId 考试ID
     * @return 考试记录ID
     */
    @PostMapping("/start")
    public Result<Integer> startExam(@RequestParam Integer examId) {
        return memberExamService.startExam(examId);
    }

    /**
     * 获取考试题目
     *
     * @param recordId 考试记录ID
     * @return 考试题目列表
     */
    @GetMapping("/begin")
    public Result<ExamBeginVO> getExamQuestion(@RequestParam Integer recordId) {
        return memberExamService.getExamQuestion(recordId);
    }

    /**
     * 提交代码判题
     *
     * @param judgeDTO 判题请求参数
     * @return 判题结果
     */
    @PostMapping("/judge")
    public Result<JudgeRecordVO> judge(@RequestBody JudgeDTO judgeDTO) {
        return memberExamService.judge(judgeDTO);
    }

    /**
     * 交卷
     *
     * @param recordId 考试Id
     * @return 判题结果
     */
    @PutMapping("/submit")
    public Result<String> submit(@RequestParam Integer recordId) {
        return memberExamService.submit(recordId);
    }
}
