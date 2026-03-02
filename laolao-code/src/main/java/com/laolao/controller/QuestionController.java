package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/question")
public class QuestionController {
    @Resource
    private QuestionService questionService;

    /**
     * 添加题目
     *
     * @param addQuestionDTO 添加的题目
     * @return 主键Id
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> addQuestion(@RequestBody AddQuestionDTO addQuestionDTO) {
        return questionService.addQuestion(addQuestionDTO);
    }
}
