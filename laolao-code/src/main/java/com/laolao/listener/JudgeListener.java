package com.laolao.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.JudgeResult;
import com.laolao.common.result.WsResult;
import com.laolao.common.util.MapStruct;
import com.laolao.common.websocket.NotificationHandler;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.ExamIdAndJudgeRecordIdDTO;
import com.laolao.pojo.entity.JudgeMemberResult;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.pojo.vo.JudgeRecordVO;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RocketMQMessageListener(
        endpoints = "127.0.0.1:9081",
        topic = "JudgeTopic",
        consumerGroup = "judge_consumer_group",
        tag = "*", // 监听 MEMBER, ADVISOR, RELEASE
        consumptionThreadCount = 10,
        requestTimeout = 6000
)
public class JudgeListener implements RocketMQListener {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private JudgeService judgeService;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private MapStruct mapStruct;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;
    @Resource
    private NotificationHandler notificationHandler;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JudgeMemberResultMapper judgeMemberResultMapper;
    @Resource
    private QuestionMapper questionMapper;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        String tag = messageView.getTag().orElse("MEMBER");
        if ("MEMBER".equals(tag)) {
            memberJudge(messageView);
        } else if ("ADVISOR".equals(tag)) {
            advisorTestJudge(messageView);
        } else {
            // 其他标签不处理，直接消费掉
            System.out.println("未识别的消息标签：" + tag);
        }
        return ConsumeResult.SUCCESS;
    }

    private void memberJudge(MessageView messageView) {
        Integer judgeRecordId, examId;
        ExamIdAndJudgeRecordIdDTO data;
        // 获取记录json反序列化
        String json = StandardCharsets.UTF_8.decode((messageView.getBody())).toString();
        try {
            data = objectMapper.readValue(json, ExamIdAndJudgeRecordIdDTO.class);
            judgeRecordId = data.getJudgeRecordId();
            examId = data.getExamId();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
//            log.error("MQ消息格式错误，无法解析: {}", json);
            return;
        }

        System.out.println("记录 " + judgeRecordId + " 开始判题");
        // 查出记录
        JudgeRecord judgeRecord = judgeRecordMapper.selectById(judgeRecordId);
        if (judgeRecord == null) return;
        try {
            // 获取测试用例
            List<QuestionTestCase> questionTestCases = questionTestCaseMapper.selectList(
                    Wrappers.lambdaQuery(QuestionTestCase.class)
                            .eq(QuestionTestCase::getQuestionId, judgeRecord.getQuestionId()));

            // 获取这一题定的分值
            Integer score = examMapper.selectScoreByExamIdAndQuestionId(examId, judgeRecord.getQuestionId());
            JudgeResult judgeResult = judgeService.judge(judgeRecord.getAnswerCode(), questionTestCases);

            // 填写分数
            if (judgeResult.getStatus() == JudgeConstant.STATUS_AC) {
                judgeRecord.setScore(score);
            } else if (judgeResult.getStatus() == JudgeConstant.STATUS_WA) {
                judgeRecord.setScore((score * judgeResult.getPassTestCaseCount() / questionTestCases.size()));
            }

            // 填写记录表
            judgeRecord.setStdout(judgeResult.getStdout());
            judgeRecord.setStderr(judgeResult.getStderr());
            judgeRecord.setQuestionTestCaseId(judgeResult.getQuestionTestCaseId());
            judgeRecord.setStatus(judgeResult.getStatus());
            judgeRecord.setTime(judgeResult.getTime());
            judgeRecord.setMemory(judgeResult.getMemory());
            judgeRecordMapper.updateById(judgeRecord);

            // 调用rocketmq传结果
            JudgeRecordVO judgeRecordVO = mapStruct.JudgeResultToJudgeRecordVO(judgeResult);
            judgeRecordVO.setQuestionId(judgeRecord.getQuestionId());
            judgeRecordVO.setScore(judgeRecord.getScore());
            notificationHandler.sendToUser(judgeRecord.getUserId(), WsResult.of("JUDGE_RESULT", judgeRecordVO));

            // 更新最优答案
            JudgeMemberResult memberResult = JudgeMemberResult.builder()
                    .examId(examId)
                    .examRecordId(judgeRecord.getExamRecordId())
                    .userId(judgeRecord.getUserId())
                    .questionId(judgeRecord.getQuestionId())
                    .bestJudgeRecordId(judgeRecord.getId())
                    .score(judgeRecord.getScore())
                    .status(judgeRecord.getStatus())
                    .submitTime(judgeRecord.getSubmitTime())
                    .build();
            judgeMemberResultMapper.updateResult(memberResult);
        } catch (Exception e) {
            e.printStackTrace();
            // 失败了更新状态为“异常”
            judgeRecord.setStatus(JudgeConstant.STATUS_UNKNOWN);
            judgeRecordMapper.updateById(judgeRecord);
        }
    }

    private void advisorTestJudge(MessageView messageView) {
        Integer questionId;
        // 获取记录json反序列化
        String json = StandardCharsets.UTF_8.decode((messageView.getBody())).toString();
        try {
            questionId = objectMapper.readValue(json, Integer.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
//            log.error("MQ消息格式错误，无法解析: {}", json);
            return;
        }
        if (questionId == null) return;

        System.out.println("题目 " + questionId + " 开始判题");
        try {
            // 获取题目
            Question question = questionMapper.selectById(questionId);
            // 获取测试用例
            List<QuestionTestCase> questionTestCases = questionTestCaseMapper.selectList(
                    Wrappers.lambdaQuery(QuestionTestCase.class)
                            .eq(QuestionTestCase::getQuestionId, questionId));

            JudgeResult judgeResult = judgeService.judge(question.getStandardSolution(), questionTestCases);

            // 调用rocketmq传结果
            JudgeRecordVO judgeRecordVO = mapStruct.JudgeResultToJudgeRecordVO(judgeResult);
            notificationHandler.sendToUser(question.getAdvisorId(), WsResult.of("JUDGE_RESULT", judgeRecordVO));

        } catch (Exception e) {
            // 判题失败
            e.printStackTrace();
        }
    }
}