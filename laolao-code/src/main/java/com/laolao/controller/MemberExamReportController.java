package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.MemberReportVO;
import com.laolao.service.MemberExamReportService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/member-report")
public class MemberExamReportController {
    @Resource
    private MemberExamReportService memberExamReportService;

    /**
     * 获取考试报告
     * 只获取已经改完卷的
     *
     * @return 考试记录列表
     */
    @GetMapping
    public Result<List<ExamRecordVO>> getSimpleExamReport() {
        return memberExamReportService.getSimpleExamRecord();
    }

    /**
     * 获取详细报告
     *
     * @param recordId examRecord主键
     * @return 基础信息
     */
    @GetMapping("/detail")
    public Result<MemberReportVO> getExamReport(@RequestParam Integer recordId) {
        return memberExamReportService.getExamReport(recordId);
    }
}
