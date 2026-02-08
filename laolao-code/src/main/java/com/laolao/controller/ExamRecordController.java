package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.service.ExamRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/exam-record")
public class ExamRecordController {
    @Resource
    private ExamRecordService examRecordService;

    @GetMapping
    public Result<List<ExamRecordVO>> getSimpleExamRecord() {
        return examRecordService.getSimpleExamRecord();
    }
}
