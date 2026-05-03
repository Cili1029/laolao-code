package com.laolao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.QuestionBankDialogVO;
import com.laolao.pojo.vo.QuestionBankInfoVO;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Resource
    private QuestionService questionService;

    /**
     * 获取单独题目的信息（题目仓库）
     * @param questionId 题目主键
     * @return 题目数据
     */
    @GetMapping("/single")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<QuestionBankInfoVO> getSingleQuestionInfo(@RequestParam Integer questionId) {
        return questionService.getSingleQuestionInfo(questionId);
    }

    /**
     * 添加题目
     *
     * @param addQuestionDTO 添加的题目
     * @return 主键Id
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Integer> addOrUpdateQuestion(@RequestBody AddQuestionDTO addQuestionDTO) {
        return questionService.addOrUpdateQuestion(addQuestionDTO);
    }

    /**
     * 获取私人题库
     *
     * @return 主键Id
     */
    @GetMapping("/private")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Page<QuestionBankDialogVO>> getPrivateQuestions(Integer pageNum, Integer pageSize, String content) {
        return questionService.getPrivateQuestions(pageNum, pageSize, content);
    }

    /**
     * 获取公共题库
     *
     * @return 主键Id
     */
    @GetMapping("/public")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<Page<QuestionBankDialogVO>> getPublicQuestions(Integer pageNum, Integer pageSize, String content, Integer tagId, Integer isFavorite) {
        return questionService.getPublicQuestions(pageNum, pageSize, content, tagId, isFavorite);
    }

    /**
     * 收藏/取消收藏题目
     * @param questionId 题目ID
     * @return 结果信息
     */
    @PutMapping("/favorite")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<String> favorite(@RequestParam Integer questionId) {
        return questionService.favorite(questionId);
    }

    /**
     * 软删除题目
     *
     * @param questionId 题目Id
     * @return 结果信息
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<String> delete(@RequestParam Integer questionId) {
        return questionService.delete(questionId);
    }

    /**
     * 拷贝所选题
     */
    @GetMapping("/copy")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<DraftQuestionVO> copyQuestion(@RequestParam Integer questionId, @RequestParam Integer examId) {
        return questionService.copyQuestion(questionId, examId);
    }
}
