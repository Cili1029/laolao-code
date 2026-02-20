package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.JudgeDTO;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.ExamBeginVO;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/exam")
public class ExamController {
    @Resource
    private ExamService examService;

    @GetMapping
    public Result<List<ExamVO>> getSimpleExam() {
        return examService.getSimpleExam();
    }

    @GetMapping("/info")
    public Result<ExamInfoVO> getExamInfo(@RequestParam Integer examId) {
        return examService.getExamInfo(examId);
    }

    @PostMapping("/start")
    public Result<Integer> startExam(@RequestParam Integer examId) {
        return examService.startExam(examId);
    }

    @GetMapping("/begin")
    public Result<ExamBeginVO> getExamQuestion(@RequestParam Integer recordId) {
        return examService.getExamQuestion(recordId);
    }

    @PostMapping("/judge")
    public Result<JudgeResult> judge(@RequestBody JudgeDTO judgeDTO) {
        return examService.judge(judgeDTO);
    }
}
