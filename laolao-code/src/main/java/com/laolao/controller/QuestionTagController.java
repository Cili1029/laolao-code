package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.QuestionBankTagVO;
import com.laolao.service.QuestionTagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class QuestionTagController {
    @Resource
    private QuestionTagService questionTagService;

    /**
     * 获取问题tag分类
     *
     * @return 结果数据
     */
    @GetMapping()
    public Result<List<QuestionBankTagVO>> getTags() {
        return questionTagService.getTags();
    }

}
