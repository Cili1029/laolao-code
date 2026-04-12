package com.laolao.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laolao.common.constant.ExamConstant;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.JudgeResult;
import com.laolao.common.result.WsResult;
import com.laolao.common.util.MapStruct;
import com.laolao.common.websocket.NotificationHandler;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.ExamIdAndJudgeRecordIdDTO;
import com.laolao.pojo.entity.JudgeUserResult;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.messege.ReleaseExamMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RocketMQMessageListener(
        endpoints = "127.0.0.1:9081",
        topic = "JudgeTopic",
        consumerGroup = "judge_consumer_group",
        tag = "*", // 监听 USER, MANAGER, RELEASE
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
    private JudgeUserResultMapper judgeUserResultMapper;
    @Resource
    private QuestionMapper questionMapper;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        String tag = messageView.getTag().orElse("USER");
        switch (tag) {
            case "USER" -> userJudge(messageView);
            case "MANAGER" -> managerTestJudge(messageView);
            case "RELEASE" -> releaseExam(messageView);
            default ->
                // 其他标签不处理，直接消费掉
                    System.out.println("未识别的消息标签：" + tag);
        }
        return ConsumeResult.SUCCESS;
    }

    private void userJudge(MessageView messageView) {
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
            JudgeUserResult userResult = JudgeUserResult.builder()
                    .examId(examId)
                    .examRecordId(judgeRecord.getExamRecordId())
                    .userId(judgeRecord.getUserId())
                    .questionId(judgeRecord.getQuestionId())
                    .bestJudgeRecordId(judgeRecord.getId())
                    .score(judgeRecord.getScore())
                    .status(judgeRecord.getStatus())
                    .submitTime(judgeRecord.getSubmitTime())
                    .build();
            judgeUserResultMapper.updateResult(userResult);
        } catch (Exception e) {
            e.printStackTrace();
            // 失败了更新状态为“异常”
            judgeRecord.setStatus(JudgeConstant.STATUS_UNKNOWN);
            judgeRecordMapper.updateById(judgeRecord);
        }
    }

    private void managerTestJudge(MessageView messageView) {
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

            // 修改验证状态
            new LambdaUpdateChainWrapper<>(questionMapper)
                    .eq(Question::getId, questionId)
                    .set(Question::getIsValidated, judgeResult.getStatus() == JudgeConstant.STATUS_AC ? 1 : 0)
                    .update();

            // 调用rocketmq传结果
            JudgeRecordVO judgeRecordVO = mapStruct.JudgeResultToJudgeRecordVO(judgeResult);
            notificationHandler.sendToUser(question.getCreatorId(), WsResult.of("JUDGE_RESULT", judgeRecordVO));
        } catch (Exception e) {
            // 判题失败
            e.printStackTrace();
        }
    }

    private void releaseExam(MessageView messageView) {
        ReleaseExamMessage msg;
        String json = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
        try {
            msg = objectMapper.readValue(json, ReleaseExamMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        Integer managerId = msg.getManagerId();
        List<Integer> questionIds = msg.getQuestionIds();

        // 批量查询未校验的题目（已跳过 is_validated=1）
        List<Question> questionList = questionMapper.selectList(
                Wrappers.lambdaQuery(Question.class)
                        .in(Question::getId, questionIds) // 批量传入 id 列表
        );

        // 获取所有需要判题的 questionId
        List<Integer> needJudgeIds = questionList.stream()
                .map(Question::getId).collect(Collectors.toList());
        // 一次性查询所有测试用例，并按 questionId 分组
        Map<Integer, List<QuestionTestCase>> testCaseMap = questionTestCaseMapper.selectList(
                        Wrappers.lambdaQuery(QuestionTestCase.class)
                                .in(QuestionTestCase::getQuestionId, needJudgeIds))
                .stream()
                .collect(Collectors.groupingBy(QuestionTestCase::getQuestionId));

        boolean allPass = true;

        // 批量处理每个题目ID
        for (Question question : questionList) {
            System.out.println("题目 " + question.getId() + " 开始判题");
            try {
                // 获取测试用例
                List<QuestionTestCase> questionTestCases = testCaseMap.get(question.getId());
                if (questionTestCases == null) {
                    allPass = false;
                    notificationHandler.sendToUser(question.getCreatorId(),
                            WsResult.of("RELEASE_RESULT", "题目 [" + question.getTitle() + "] 缺少测试用例，无法判题"));
                    continue;
                }

                JudgeResult judgeResult = judgeService.judge(question.getStandardSolution(), questionTestCases);

                // 修改验证状态(0 -> 1)
                if (judgeResult.getStatus() == JudgeConstant.STATUS_AC) {
                    new LambdaUpdateChainWrapper<>(questionMapper)
                            .eq(Question::getId, question.getId())
                            .set(Question::getIsValidated, 1)
                            .update();
                } else {
                    // 遇到错误，不继续判题，返回发布结果
                    // 调用rocketmq传结果
                    allPass = false;
                    notificationHandler.sendToUser(question.getCreatorId(),
                            WsResult.of("RELEASE_RESULT", "题目 [" + question.getTitle() + "] 未通过判题"));
                }
            } catch (Exception e) {
                // 判题失败
                allPass = false;
                e.printStackTrace();
            }
        }

        // 全部通过
        if (allPass) {
            examMapper.updateExamStatus(msg.getExamId(), ExamConstant.PUBLISHING, ExamConstant.PUBLISHED);
            // 通知用户：考试发布成功
            notificationHandler.sendToUser(managerId, WsResult.of("RELEASE_RESULT", "考试已成功发布"));
        } else {
            // 回退到草稿
            examMapper.updateExamStatus(msg.getExamId(), ExamConstant.PUBLISHING, ExamConstant.DRAFT);
            notificationHandler.sendToUser(managerId, WsResult.of("RELEASE_RESULT", "考试发布失败，部分题目未通过校验"));
        }
    }
}