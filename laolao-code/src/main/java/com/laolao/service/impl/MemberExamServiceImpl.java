package com.laolao.service.impl;

import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.result.Result;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.MemberExamService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MemberExamServiceImpl implements MemberExamService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private RocketMQClientTemplate rocketMQClientTemplate;

    @Override
    public Result<Integer> startExam(Integer examId) {
        // 查看记录里是否有正在考试的记录
        Integer userId = SecurityUtils.getUserId();
        ExamRecord examRecord = examRecordMapper.selectExamRecord(userId, examId);
        if (examRecord != null && examRecord.getStatus() == 0) {
            // 考试进行中，不用创建新记录，直接返回所记录的记录Id
            return Result.success(examRecord.getId());
        }
        ExamRecord record = ExamRecord.builder()
                .examId(examId)
                .userId(userId)
                .build();
        examRecordMapper.insert(record);
        return Result.success(record.getId());
    }

    @Override
    public Result<ExamBeginVO> getExamQuestion(Integer recordId) {
        // 获取这个记录所在的考试Id和状态
        ExamRecord examRecord = examRecordMapper.selectExamByRecordId(recordId);
        if (examRecord.getStatus() != 0) {
            return Result.error("考试已结束或已交卷");
        }
        // 获取其题目以及当前学生每道题的分数
        List<ExamQuestionVO> examQuestionVOList = examMapper.selectQuestionById(examRecord.getExamId(), recordId, SecurityUtils.getUserId());
        ExamBeginVO examBeginVO = new ExamBeginVO();
        examBeginVO.setExamId(examRecord.getExamId());
        examBeginVO.setQuestions(examQuestionVOList);
        return Result.success(examBeginVO);
    }

    @Override
    public Result<Integer> judge(JudgeDTO judgeDTO) {
        // 构建代测试记录
        JudgeRecord judgeRecord = JudgeRecord.builder()
                .status(JudgeConstant.STATUS_JUDGING)
                .userId(SecurityUtils.getUserId())
                .examRecordId(judgeDTO.getRecordId())
                .questionId(judgeDTO.getQuestionId())
                .answerCode(judgeDTO.getCode())
                .build();

        judgeRecordMapper.insert(judgeRecord);

        ExamIdAndJudgeRecordIdDTO examIdAndJudgeRecordIdDTO = ExamIdAndJudgeRecordIdDTO.builder()
                .examId(judgeDTO.getExamId())
                .judgeRecordId(judgeRecord.getId())
                .build();

        // 发送队列
        rocketMQClientTemplate.convertAndSend("JudgeTopic:MEMBER", examIdAndJudgeRecordIdDTO);
        // 返回记录Id
        return Result.success(judgeRecord.getId());
    }

    @Override
    @Transactional
    public Result<String> submit(Integer recordId) {
        // 仅修改状态和交卷时间, 剩下的交卷再改
        examRecordMapper.updateStatusToSubmitted(recordId, SecurityUtils.getUserId());
        return Result.success("交卷成功，请耐心等待考试结束统一公布成绩");
    }

    @Transactional
    public void submitBatch(Integer examId) {
        // 批量交卷并算分
        examRecordMapper.batchSubmitAndCalculateScore(examId);
    }
}
