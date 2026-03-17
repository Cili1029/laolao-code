package com.laolao.common.websocket;

import com.laolao.common.result.WsResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class NotificationHandler extends TextWebSocketHandler {

    // 结构：ExamID -> (UserID -> Session)
    private static final Map<Integer, Map<Integer, WebSocketSession>> examMap = new ConcurrentHashMap<>();

    // 用于执行定时强制交卷的任务调度器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Map<String, Object> attrs = session.getAttributes();
        Integer examId = (Integer) attrs.get("examId");
        Integer userId = (Integer) attrs.get("userId");
        String name = (String) attrs.get("name");
        LocalDateTime endTime = (LocalDateTime) attrs.get("endTime");

        // 存储
        examMap.computeIfAbsent(examId, k -> new ConcurrentHashMap<>()).put(userId, session);

        // 计算剩余时间
        long delaySeconds = Duration.between(LocalDateTime.now(), endTime).getSeconds();

        if (delaySeconds > 0) {
            // 距离结束还有时间，设置定时任务
            scheduleForceSubmit(session, delaySeconds);
        } else {
            // 已过结束时间（处于5分钟宽限期内）
            sendMessage(session, "EXAM_ALREADY_ENDED");
        }

        System.out.println("考生 " + name + " 已连接考试 " + examId);
    }

    /**
     * 到点强制交卷
     */
    private void scheduleForceSubmit(WebSocketSession session, long delayInSeconds) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                if (session.isOpen()) {
                    sendMessage(session, "SYSTEM_FORCE_SUBMIT");
                    Thread.sleep(2000);
                    session.close(CloseStatus.NORMAL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delayInSeconds, TimeUnit.SECONDS);

        // 把闹钟凭证存入 session 属性中
        session.getAttributes().put("submitTask", future);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        // 心跳处理
        if ("ping".equals(message.getPayload())) {
            sendMessage(session, WsResult.of("PONG", null));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        // 获取闹钟凭证
        ScheduledFuture<?> future = (ScheduledFuture<?>) session.getAttributes().get("submitTask");

        // 如果连接断了，把这个闹钟取消掉，防止重连后产生一堆废弃闹钟
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }

        // 连接关闭时，从 Map 中移除，防止内存泄漏
        Integer examId = (Integer) session.getAttributes().get("examId");
        Integer userId = (Integer) session.getAttributes().get("userId");
        String name = (String) session.getAttributes().get("name");

        if (examId != null && userId != null) {
            Map<Integer, WebSocketSession> users = examMap.get(examId);
            if (users != null) {
                users.remove(userId);
                if (users.isEmpty()) {
                    examMap.remove(examId);
                }
            }
        }
        System.out.println("考生 " + name + " 连接断开");
    }

    /**
     * 给特定考试的特定学生发消息（比如监考老师发警告）
     */
    public void sendToUser(Integer examId, Integer userId, String message) {
        Map<Integer, WebSocketSession> users = examMap.get(examId);
        if (users != null) {
            WebSocketSession session = users.get(userId);
            sendMessage(session, message);
        }
    }

    /**
     * 通用发送逻辑
     */
    private void sendMessage(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            synchronized (session) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("消息发送失败: " + e.getMessage());
                }
            }
        }
    }
}