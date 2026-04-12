package com.laolao.listener;

import com.laolao.common.constant.ExamConstant;
import com.laolao.common.result.WsResult;
import com.laolao.common.websocket.NotificationHandler;
import com.laolao.mapper.ExamMapper;
import com.laolao.service.impl.UserExamServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RocketMQMessageListener(
        endpoints = "127.0.0.1:9081",
        topic = "ExamEndTopic",
        consumerGroup = "exam_end_consumer_group",
        tag = "*"
)
public class ExamEndListener implements RocketMQListener {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private NotificationHandler notificationHandler;
    @Resource
    private UserExamServiceImpl userExamService;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        try {
            // 获取考试ID
            int examId = Integer.parseInt(StandardCharsets.UTF_8.decode(messageView.getBody()).toString());
            int rows = examMapper.examEndConsume(examId, ExamConstant.GRADING);
            if (rows <= 0) {
                log.warn("【考试结束通知】考试ID {} 数据库更新失败（可能已被取消）", examId);
                return ConsumeResult.SUCCESS;
            }
            // 执行交卷
            userExamService.submitBatch(examId);
            // 通过 WebSocket 发送通知给前端
            notificationHandler.sendToAllUsersInExam(examId, WsResult.of("EXAM_SUBMIT", null));
            return ConsumeResult.SUCCESS;
        } catch (NumberFormatException e) {
            log.error("【考试结束通知】消息体解析失败: {}", e.getMessage());
            return ConsumeResult.SUCCESS; // 解析失败通常不需要重试，直接丢弃
        } catch (Exception e) {
            log.error("【考试结束通知】业务处理异常: {}", e.getMessage());
            return ConsumeResult.FAILURE;
        }
    }
}
