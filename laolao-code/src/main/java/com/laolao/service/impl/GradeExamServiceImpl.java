package com.laolao.service.impl;

import com.laolao.common.constant.ExamConstant;
import com.laolao.common.result.Result;
import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.dto.UpdateScoreDTO;
import com.laolao.pojo.entity.Exam;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.vo.GradeJudgeRecordVO;
import com.laolao.pojo.vo.GradeMemberVO;
import com.laolao.service.GradeExamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeExamServiceImpl implements GradeExamService {
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private ExamMapper examMapper;

    @Override
    public Result<List<GradeMemberVO>> getGradeMember(Integer examId) {
        // 只获取有进入考试的考生信息
        List<GradeMemberVO> gradeMemberVOList = examRecordMapper.selectRecordIdAndMemberByExamId(examId);
        if (gradeMemberVOList == null || gradeMemberVOList.isEmpty()) {
            return Result.success(gradeMemberVOList);
        }
        // 获取第一个考生作答情况
        List<GradeJudgeRecordVO> gradeJudgeRecordVOList = judgeRecordMapper.selectGradeInfoByRecordId(gradeMemberVOList.get(0).getId());
        gradeMemberVOList.get(0).setJudgeRecords(gradeJudgeRecordVOList);
        // 放置
        return Result.success(gradeMemberVOList);
    }

    @Override
    @Transactional
    public Result<Integer> updateScore(UpdateScoreDTO updateScoreDTO) {
        // 先获取这一道题的旧分数
        Integer oldScore = judgeRecordMapper.selectScoreById(updateScoreDTO.getJudgeRecordId());
        // 计算差值
        int diffScore = updateScoreDTO.getScore() - oldScore;
        if (diffScore == 0) {
            // 没变化，不更新
            return Result.success(0);
        }
        // 先更新记录单体分数
        JudgeRecord record = JudgeRecord.builder()
                .id(updateScoreDTO.getJudgeRecordId())
                .score(updateScoreDTO.getScore())
                .build();
        judgeRecordMapper.updateById(record);
        // 更新examRecord总分数
        examRecordMapper.updateScoreByDiff(updateScoreDTO.getExamRecordId(), diffScore);
        return Result.success("更新分数成功", diffScore);
    }

    @Override
    public Result<Integer> graded(Integer examId) {
        // 以后接入rocketmq后应该先不改status，ai逐个写完报告再修改status
        // TODO
        Exam exam = Exam.builder()
                .id(examId)
                .status(ExamConstant.GRADING_COMPLETED)
                .build();
        examMapper.updateById(exam);
        return Result.success("改卷已结束");
    }
}
