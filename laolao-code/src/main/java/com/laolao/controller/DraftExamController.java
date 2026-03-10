package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.CreateExamDTO;
import com.laolao.pojo.dto.JudgeTestCaseDTO;
import com.laolao.pojo.dto.SaveAndAddToExamDTO;
import com.laolao.pojo.dto.UpdateDraftDTO;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.service.DraftExamService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam/draft")
public class DraftExamController {
    @Resource
    private DraftExamService examService;
    /**
     * 导师创建考试
     *
     * @param createExamDTO 考试参数
     * @return 考试Id
     */
    @PostMapping("/create-exam")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> createExam(@RequestBody CreateExamDTO createExamDTO) {
        return examService.createExam(createExamDTO);
    }

    /**
     * 导师更新考试
     *
     * @param draftDTO 信息
     * @return 消息结果
     */
    @PutMapping("/update-exam")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> updateDraft(@RequestBody UpdateDraftDTO draftDTO) {
        return examService.updateDraft(draftDTO);
    }

    /**
     * 导师运行测试用例
     *
     * @param judgeTestCaseDTO 测试参数
     * @return 判题结果
     */
    @PostMapping("/judge")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<JudgeRecordVO> judgeTestCase(@RequestBody JudgeTestCaseDTO judgeTestCaseDTO) {
        return examService.judgeTestCase(judgeTestCaseDTO);
    }

    /**
     * 获取草稿中的题目
     *
     * @param examId 考试Id
     * @return 题目数据
     */
    @GetMapping("/get-question")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<List<DraftQuestionVO>> getQuestion(@RequestParam Integer examId) {
        return examService.getDraftQuestion(examId);
    }

    /**
     * 添加题目到考试
     *
     * @param saveAndAddToExamDTO 添加的题目
     * @return 题目Id
     */
    @PostMapping("/add-question")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> saveAndAddToExam(@RequestBody SaveAndAddToExamDTO saveAndAddToExamDTO) {
        return examService.saveAndAddToExam(saveAndAddToExamDTO);
    }

    /**
     * 从草稿中移除题目
     *
     * @param examId 考试id
     * @param questionId 题目id
     * @return 结果信息
     */
    @DeleteMapping("/remove-question")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<String> removeQuestion(@RequestParam Integer examId, @RequestParam Integer questionId) {
        return examService.removeQuestion(examId, questionId);
    }

    /**
     * 删除草稿
     *
     * @param examId 考试id
     * @return 结果信息
     */
    @DeleteMapping("/delete-draft")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<String> deleteDraft(@RequestParam Integer examId) {
        return examService.deleteDraft(examId);
    }

    /**
     * 发布考试
     *
     * @param examId 考试ID
     * @return 消息结果
     */
    @PostMapping("/release-exam")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<Integer> releaseExam(@RequestParam Integer examId) {
        return examService.releaseExam(examId);
    }
}
