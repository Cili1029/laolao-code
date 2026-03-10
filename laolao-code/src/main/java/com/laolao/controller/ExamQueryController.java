package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.*;
import com.laolao.service.ExamQueryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamQueryController {
    @Resource
    private ExamQueryService examQueryService;

    /**
     * 获取考试列表
     *
     * @return 考试列表
     */
    @GetMapping
    public Result<List<ExamVO>> getSimpleExam() {
        return examQueryService.getSimpleExam();
    }

    /**
     * 获取考试详情
     *
     * @param examId 考试ID
     * @return 考试详情
     */
    @GetMapping("/info")
    public Result<ExamInfoVO> getExamInfo(@RequestParam Integer examId) {
        return examQueryService.getExamInfo(examId);
    }

    /**
     * 导师获得考试后考试数据
     *
     * @param examId 考试Id
     * @return 考试数据
     */
    @GetMapping("/complete-report")
    public Result<ExamCompleteReportVO> getExamCompleteReport(@RequestParam Integer examId) {
        return examQueryService.getExamCompleteReport(examId);
    }
}
