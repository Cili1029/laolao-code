package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.JudgeResult;
import com.laolao.pojo.vo.SimpleSubmitRecordVO;
import com.laolao.service.SubmitRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/submit-record")
public class SubmitRecordController {
    @Resource
    private SubmitRecordService submitRecordService;

    @GetMapping("/simple")
    public Result<List<SimpleSubmitRecordVO>> getSimpleSubmitRecord(@RequestParam Integer examRecordId) {
        return submitRecordService.getSimpleSubmitRecord(examRecordId);
    }

    @GetMapping("/detail")
    public Result<List<JudgeResult>> getDetailSubmitRecord(@RequestParam Integer submitRecordId) {
        return submitRecordService.getDetailSubmitRecord(submitRecordId);
    }
}
