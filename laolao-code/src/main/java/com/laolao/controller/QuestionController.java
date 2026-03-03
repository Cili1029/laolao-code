package com.laolao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.mapper.QuestionMapper;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.vo.QuestionBankVO;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/question")
public class QuestionController {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionMapper questionMapper;

    /**
     * 添加题目
     *
     * @param addQuestionDTO 添加的题目
     * @return 主键Id
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> addOrUpdateQuestion(@RequestBody AddQuestionDTO addQuestionDTO) {
        return questionService.addOrUpdateQuestion(addQuestionDTO);
    }

    /**
     * 获取私人题库
     *
     * @return 主键Id
     */
    @GetMapping("/private")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Page<QuestionBankVO>> getPrivateQuestions(Integer pageNum, Integer pageSize) {
        return questionService.getPrivateQuestions(pageNum, pageSize);
    }

    /**
     * 获取私人题库
     *
     * @return 主键Id
     */
    @GetMapping("/public")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Page<QuestionBankVO>> getPublicQuestions(Integer pageNum, Integer pageSize) {
        return questionService.getPublicQuestions(pageNum, pageSize);
    }


}
