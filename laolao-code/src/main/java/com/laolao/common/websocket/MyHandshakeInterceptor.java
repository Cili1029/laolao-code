package com.laolao.common.websocket;

import com.laolao.common.security.MyUserDetail;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.ExamMapper;
import com.laolao.pojo.dto.WebSocketExamDTO;
import jakarta.annotation.Resource;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class MyHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private ExamMapper examMapper;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // 1. 获取用户 ID (从 Spring Security 上下文)
        MyUserDetail userInfo = SecurityUtils.getUserInfo();
        if (userInfo == null) return false;

        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            String examIdStr = serverHttpRequest.getServletRequest().getParameter("examId");
            if (examIdStr == null) return false;

            try {
                int examId = Integer.parseInt(examIdStr);
                WebSocketExamDTO examInfo = examMapper.selectWebSocketExamInfo(examId);

                if (examInfo == null) return false;

                // 2. 考试时间校验
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(examInfo.getStartTime())) {
                    return false; // 还没开始，禁止连接
                }
                if (now.isAfter(examInfo.getEndTime().plusMinutes(5))) {
                    return false; // 结束超过5分钟，禁止连接
                }

                // 3. 将关键信息存入 attributes，供后续 Handler 使用
                attributes.put("examId", examId);
                attributes.put("userId", userInfo.getUserId());
                attributes.put("name", userInfo.getName());
                attributes.put("endTime", examInfo.getEndTime());

                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}