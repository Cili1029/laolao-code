package com.laolao.common.websocket;

import com.laolao.common.result.WsResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
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

        // 注册到内存 Map
        examMap.computeIfAbsent(examId, k -> new ConcurrentHashMap<>()).put(userId, session);

        // 特殊情况处理：如果学生由于网络原因，在考试已经结束后（但在5分钟宽限期内）才连上
        if (LocalDateTime.now().isAfter(endTime)) {
            // 直接下发交卷指令，不给答题机会
            sendMessage(session, WsResult.of("EXAM_SUBMIT", null));
            return;
        }

        log.info("考生 {} 已连接考试 {}", name, examId);
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
     * 给特定考试的所有在线学生发送消息
     */
    public void sendToAllUsersInExam(Integer examId, String message) {
        // 获取该考试下的所有在线考生Session
        Map<Integer, WebSocketSession> users = examMap.get(examId);
        if (users == null || users.isEmpty()) {
            return;
        }
        // 遍历发送给所有在线考生
        users.values().forEach(session -> {
            if (session.isOpen()) {
                sendMessage(session, message);
                // 强制交卷后，建议在 1-2 秒后由后端主动断开 WebSocket
                // 或者由前端收到消息后执行完交卷逻辑自行断开
            }
        });
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