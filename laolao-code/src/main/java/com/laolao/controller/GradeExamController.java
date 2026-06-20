package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.UpdateScoreDTO;
import com.laolao.pojo.vo.GradeJudgeRecordVO;
import com.laolao.pojo.vo.GradeUserVO;
import com.laolao.service.GradeExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam/grade")
public class GradeExamController {
    @Resource
    private GradeExamService gradeExamService;

    /**
     * 获取参与考试的考生
     * 自动获取第一个考生的作答数据
     *
     * @return 考生数据
     */
    @GetMapping
    public Result<List<GradeUserVO>> getGradeUser(@RequestParam Integer examId) {
        return gradeExamService.getGradeUser(examId);
    }

    /**
     * 根据记录Id获取考生的作答数据
     *
     * @return 考生数据
     */
    @GetMapping("/user-answer")
    public Result<List<GradeJudgeRecordVO>> getUserAnswer(@RequestParam Integer examRecordId) {
        return gradeExamService.getUserAnswer(examRecordId);
    }

    /**
     * 更新考生某一题的分数
     *
     * @param updateScoreDTO id和分数
     * @return 总分差值
     */
    @PutMapping("/update-score")
    public Result<Integer> updateScore(@RequestBody UpdateScoreDTO updateScoreDTO) {
        return gradeExamService.updateScore(updateScoreDTO);
    }

    /**
     * 提交改卷
     *
     * @param examId 考试Id
     * @return 结果信息
     */
    @PutMapping("/graded")
    public Result<Integer> graded(@RequestParam Integer examId) {
        return gradeExamService.graded(examId);
    }
}
