package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
