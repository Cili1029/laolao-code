package com.laolao.common.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laolao.common.result.WsResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@Component
public class NotificationHandler extends TextWebSocketHandler {

    // 全局连接池：UserID -> Session
    private static final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // 考试分组索引：ExamID -> Set<UserID> (仅用于考试广播)
    private static final Map<Integer, Set<Integer>> examUsers = new ConcurrentHashMap<>();

    // ObjectMapper
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Map<String, Object> attrs = session.getAttributes();
        Integer userId = (Integer) attrs.get("userId");
        userSessions.put(userId, session);

        String name = (String) attrs.get("name");
        log.info("用户 {} 进入网站", name);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        // 连接关闭时移除数据，防止内存泄漏
        Integer examId = (Integer) session.getAttributes().get("examId");
        Integer userId = (Integer) session.getAttributes().get("userId");
        String name = (String) session.getAttributes().get("name");

        if (examId != null && userId != null) {
            Set<Integer> users = examUsers.get(examId);
            if (users != null) {
                users.remove(userId);
                log.info("用户 {} 离开考试 {}", name, examId);
                if (users.isEmpty()) examUsers.remove(examId);
            }
        }

        if (userId != null) {
            userSessions.remove(userId);
        }
        log.info("用户 {} 连接已断开", name);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        try {
            // 先转成 WsResult<Object>
            WsResult<Object> wsResult = OBJECT_MAPPER.readValue(message.getPayload(),
                    new TypeReference<>() {
                    });

            // 处理心跳
            if ("PING".equals(wsResult.getType())) {
                sendMessage(session, WsResult.success("PONG"));
                return;
            }

            if ("BIND_EXAM".equals(wsResult.getType())) {
                Integer userId = (Integer) session.getAttributes().get("userId");
                String name = (String) session.getAttributes().get("name");
                Integer newExamId = (Integer) wsResult.getData();

                // 处理“换场”情况
                Integer oldExamId = (Integer) session.getAttributes().get("examId");
                if (oldExamId != null && !oldExamId.equals(newExamId)) {
                    Set<Integer> oldUsers = examUsers.get(oldExamId);
                    if (oldUsers != null) {
                        oldUsers.remove(userId);
                        if (oldUsers.isEmpty()) {
                            examUsers.remove(oldExamId); // 清理空的考试 Key
                        }
                    }
                }
                // 将 examId 存入 Session 的 Attributes 中，方便断开时查找
                session.getAttributes().put("examId", newExamId);

                // 更新索引映射（原本是空的，现在加进去）
                examUsers.computeIfAbsent(newExamId, k -> ConcurrentHashMap.newKeySet()).add(userId);
                log.info("用户 {} 进入考试 {}", name, newExamId);
            }

            if ("REMOVE_EXAM".equals(wsResult.getType())) {
                String name = (String) session.getAttributes().get("name");
                Integer examId = (Integer) session.getAttributes().get("examId");
                Integer userId = (Integer) session.getAttributes().get("userId");

                // 安全判断，防止空指针
                if (examId == null || userId == null) {
                    log.info("用户 {} 未绑定任何考试，无需离开", name);
                    return;
                }

                // 安全移除考试成员
                Set<Integer> users = examUsers.get(examId);
                if (users != null) {
                    users.remove(userId);
                    if (users.isEmpty()) {
                        examUsers.remove(examId);
                    }
                }

                // 清空当前会话的 examId
                session.getAttributes().remove("examId");
                log.info("用户 {} 离开考试 {}", name, examId);
            }
        } catch (Exception e) {
            log.error("解析 WebSocket 消息失败", e);
        }
    }

    /**
     * 发送给特定用户（组管理员调试、发布校验结果、学生单题判题结果）
     * 无论用户是否在考试中，只要在线就能收到
     */
    public void sendToUser(Integer userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendMessage(session, message);
        }
    }

    /**
     * 发送给整场考试的所有人（考试结束指令、全场公告）
     */
    public void sendToAllUsersInExam(Integer examId, String message) {
        Set<Integer> users = examUsers.get(examId);
        if (users != null) {
            users.forEach(uid -> sendToUser(uid, message));
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