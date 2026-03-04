package com.laolao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.dto.QuestionIdDTO;
import com.laolao.pojo.vo.DraftQuestionVO;
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
    public Result<Page<QuestionBankVO>> getPrivateQuestions(Integer pageNum, Integer pageSize, String content) {
        return questionService.getPrivateQuestions(pageNum, pageSize, content);
    }

    /**
     * 获取公共题库
     *
     * @return 主键Id
     */
    @GetMapping("/public")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Page<QuestionBankVO>> getPublicQuestions(Integer pageNum, Integer pageSize, String content) {
        return questionService.getPublicQuestions(pageNum, pageSize, content);
    }

    /**
     * 修改题目为公共/私有
     *
     * @param questionIdDTO 题目数据
     * @return 结果信息
     */
    @PostMapping("/status")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<String> changeStatus(@RequestBody QuestionIdDTO questionIdDTO) {
        return questionService.changeStatus(questionIdDTO);
    }

    /**
     * 软删除题目
     *
     * @param questionId 题目Id
     * @return 结果信息
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<String> delete(@RequestParam Integer questionId) {
        return questionService.delete(questionId);
    }

    /**
     * 拷贝所选题
     */
    @GetMapping("/copy")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<DraftQuestionVO> copyQuestion(@RequestParam Integer questionId) {
        return questionService.copyQuestion(questionId);
    }
}
