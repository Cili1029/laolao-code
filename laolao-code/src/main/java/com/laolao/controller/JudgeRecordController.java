package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import com.laolao.service.JudgeRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/submit-record")
public class JudgeRecordController {
    @Resource
    private JudgeRecordService judgeRecordService;

    @GetMapping("/simple")
    public Result<List<SimpleJudgeRecordVO>> getSimpleSubmitRecord(@RequestParam Integer examRecordId, @RequestParam Integer questionId) {
        return judgeRecordService.getSimpleJudgeRecord(examRecordId, questionId);
    }

    @GetMapping("/detail")
    public Result<List<JudgeResult>> getDetailSubmitRecord(@RequestParam Integer submitRecordId) {
        return judgeRecordService.getDetailJudgeRecord(submitRecordId);
    }
}
