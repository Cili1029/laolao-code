package com.laolao.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.laolao.mapper.ExamMapper;
import com.laolao.pojo.entity.Exam;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExamEndTask {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private RocketMQClientTemplate rocketMQClientTemplate;

    @Scheduled(cron = "0 */5 * * * *")
    public void scanEndExams() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 扫描未来 10 分钟内将要结束，且未进入延时队列的考试
        LocalDateTime threshold = now.plusMinutes(10);

        List<Exam> exams = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .gt(Exam::getEndTime, now)   // 必须还没结束
                .le(Exam::getEndTime, threshold)  // 且在10分钟内结束
                .eq(Exam::getIsQueued, 0));      // 不在队列中

        for (Exam exam : exams) {
            // 更新为在队列
            exam.setIsQueued(1);
            examMapper.updateById(exam);

            // 计算延迟秒数 (结束时间 - 现在)
            long delaySeconds = Duration.between(LocalDateTime.now(), exam.getEndTime()).getSeconds();
            if (delaySeconds < 0) {
                delaySeconds = 0;
            }

            // 发送延时消息
            rocketMQClientTemplate.syncSendDelayMessage(
                    "ExamEndTopic",
                    exam.getId(),
                    Duration.ofSeconds(delaySeconds)
            );
        }
    }
}
