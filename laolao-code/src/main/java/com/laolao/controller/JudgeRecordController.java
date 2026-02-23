package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import com.laolao.service.JudgeRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/judge-record")
public class JudgeRecordController {
    @Resource
    private JudgeRecordService judgeRecordService;

    /**
     * 获取简单判题记录列表
     *
     * @param examRecordId 考试记录ID
     * @param questionId   题目ID
     * @return 判题记录列表
     */
    @GetMapping("/simple")
    public Result<List<SimpleJudgeRecordVO>> getSimpleSubmitRecord(@RequestParam Integer examRecordId, @RequestParam Integer questionId) {
        return judgeRecordService.getSimpleJudgeRecord(examRecordId, questionId);
    }

    /**
     * 获取判题记录详情
     *
     * @param judgeRecordId 判题记录ID
     * @return 判题记录详情
     */
    @GetMapping
    public Result<JudgeRecordVO> getDetailSubmitRecord(@RequestParam Integer judgeRecordId) {
        return judgeRecordService.getDetailJudgeRecord(judgeRecordId);
    }
}
