package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.JudgeDTO;
import com.laolao.pojo.dto.CreateExamDTO;
import com.laolao.pojo.vo.ExamBeginVO;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/exam")
public class ExamController {
    @Resource
    private ExamService examService;

    /**
     * 获取考试列表
     *
     * @return 考试列表
     */
    @GetMapping
    public Result<List<ExamVO>> getSimpleExam() {
        return examService.getSimpleExam();
    }

    /**
     * 获取考试详情
     *
     * @param examId 考试ID
     * @return 考试详情
     */
    @GetMapping("/info")
    public Result<ExamInfoVO> getExamInfo(@RequestParam Integer examId) {
        return examService.getExamInfo(examId);
    }

    /**
     * 开始考试
     *
     * @param examId 考试ID
     * @return 考试记录ID
     */
    @PostMapping("/start")
    public Result<Integer> startExam(@RequestParam Integer examId) {
        return examService.startExam(examId);
    }

    /**
     * 获取考试题目
     *
     * @param recordId 考试记录ID
     * @return 考试题目列表
     */
    @GetMapping("/begin")
    public Result<ExamBeginVO> getExamQuestion(@RequestParam Integer recordId) {
        return examService.getExamQuestion(recordId);
    }

    /**
     * 提交代码判题
     *
     * @param judgeDTO 判题请求参数
     * @return 判题结果
     */
    @PostMapping("/judge")
    public Result<JudgeRecordVO> judge(@RequestBody JudgeDTO judgeDTO) {
        return examService.judge(judgeDTO);
    }

    /**
     * 导师创建考试
     *
     * @param createExamDTO 考试参数
     * @return 考试Id
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> createExam(@RequestBody CreateExamDTO createExamDTO) {
        return examService.createExam(createExamDTO);
    }
}
