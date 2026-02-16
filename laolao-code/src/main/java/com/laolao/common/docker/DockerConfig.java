package com.laolao.common.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DockerConfig {
    @Bean
    public DockerClient dockerClient() {
        // 初始化 Docker 客户端配置
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        // 创建 HTTP 传输层 (底层通信工具)
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())         // 指定 Docker 地址
                .sslConfig(config.getSSLConfig())           // 指定 SSL 配置
                .maxConnections(100)                  // 最大连接数，决定了并发判题的能力
                .connectionTimeout(Duration.ofSeconds(30))  // 连接超时
                .responseTimeout(Duration.ofSeconds(45))    // 响应超时
                .build();

        // 实例化 DockerClient
        return DockerClientImpl.getInstance(config, httpClient);
    }
}