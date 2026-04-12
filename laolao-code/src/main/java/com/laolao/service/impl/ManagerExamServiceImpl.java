package com.laolao.service.impl;

import com.laolao.common.constant.ExamConstant;
import com.laolao.common.result.Result;
import com.laolao.common.result.WsResult;
import com.laolao.common.websocket.NotificationHandler;
import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.pojo.entity.Exam;
import com.laolao.service.ManagerExamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ManagerExamServiceImpl implements ManagerExamService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private NotificationHandler notificationHandler;
    @Resource
    private ExamRecordMapper examRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> cancelExam(Integer examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        // 如果考试已经结束或已经取消，则无需重复取消
        if (exam.getStatus() != ExamConstant.PUBLISHED && exam.getStatus() != ExamConstant.GRADING) {
            return Result.error("非法操作！");
        }

        // 进行中或已结束，修改考生状态
        LocalDateTime now = LocalDateTime.now();
        // 如果考试已经开始，清理考试记录
        if (now.isAfter(exam.getStartTime())) {
            examRecordMapper.cancelExam(examId);
        }

        examMapper.cancelExam(exam, ExamConstant.CANCELED);

        // 发消息踢人
        if (now.isAfter(exam.getStartTime()) && now.isBefore(exam.getEndTime())) {
            notificationHandler.sendToAllUsersInExam(examId, WsResult.of("EXAM_CANCEL", "本场考试已被管理员取消"));
        }
        return Result.success("取消成功");
    }
}
