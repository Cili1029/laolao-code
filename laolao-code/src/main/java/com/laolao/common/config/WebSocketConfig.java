package com.laolao.common.config;

import com.laolao.common.websocket.MyHandshakeInterceptor;
import com.laolao.common.websocket.NotificationHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private NotificationHandler notificationHandler;
    @Resource
    private MyHandshakeInterceptor myHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationHandler, "/ws/exam") // 前端连接地址
                .addInterceptors(myHandshakeInterceptor)
                .setAllowedOrigins("*"); // 允许跨域
    }
}