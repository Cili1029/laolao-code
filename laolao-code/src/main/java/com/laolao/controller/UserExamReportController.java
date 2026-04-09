package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.UserReportVO;
import com.laolao.service.UserExamReportService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-report")
public class UserExamReportController {
    @Resource
    private UserExamReportService userExamReportService;

    /**
     * 获取考试报告
     * 只获取已经改完卷的
     *
     * @return 考试记录列表
     */
    @GetMapping
    public Result<List<ExamRecordVO>> getSimpleExamReport() {
        return userExamReportService.getSimpleExamRecord();
    }

    /**
     * 获取详细报告
     *
     * @param recordId examRecord主键
     * @return 基础信息
     */
    @GetMapping("/detail")
    public Result<UserReportVO> getExamReport(@RequestParam Integer recordId) {
        return userExamReportService.getExamReport(recordId);
    }
}
