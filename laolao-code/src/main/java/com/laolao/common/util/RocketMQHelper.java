package com.laolao.common.util;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;

public class RocketMQHelper {
    private static Producer producer;
    private static final ClientServiceProvider provider = ClientServiceProvider.loadService();

    // 初始化
    public static void init(String endpoint, String topic) throws ClientException {
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoint)
                .setRequestTimeout(Duration.ofSeconds(10))
                .build();

        producer = provider.newProducerBuilder()
                .setTopics(topic)
                .setClientConfiguration(configuration)
                .build();
        System.out.println("RocketMQ Producer 已启动");
    }

    // 简单发送
    public static String send(String topic, String tag, String body) throws ClientException {
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                .setTag(tag)
                .setBody(body.getBytes(StandardCharsets.UTF_8))
                .build();

        SendReceipt receipt = producer.send(message);
        return receipt.getMessageId().toString();
    }

    /**
     * 开启监听并接收消息
     * @param endpoint 接入点 localhost:8081
     * @param topic 话题名称
     * @param consumerGroup 消费者分组（5.x 必须设置且最好在控制台先创建）
     * @param tag 标签，"*" 代表监听所有
     */
    public static void startListen(String endpoint, String topic, String consumerGroup, String tag) throws Exception {
        ClientServiceProvider provider = ClientServiceProvider.loadService();

        // 1. 基础配置
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoint)
                .build();

        // 2. 设置过滤表达式（按 Tag 过滤）
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);

        // 3. 初始化 PushConsumer
        // PushConsumer 是“推模式”，由 Proxy 主动把消息发给你的监听器
        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(configuration)
                .setConsumerGroup(consumerGroup) // 设置分组
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression)) // 订阅话题
                .setMessageListener(messageView -> {
                    // --- 核心接收逻辑开始 ---

                    // 获取消息体（ByteBuffer）
                    ByteBuffer buffer = messageView.getBody();
                    // 将字节码转回 UTF-8 字符串
                    String body = StandardCharsets.UTF_8.decode(buffer).toString();

                    System.out.println("收到新消息!");
                    System.out.println("内容: " + body);
                    System.out.println("消息ID: " + messageView.getMessageId());
                    System.out.println("标签: " + messageView.getTag().orElse("无"));

                    // 返回 SUCCESS，通知 MQ 消息已消费。如果返回 FAILURE，MQ 会稍后重试。
                    return ConsumeResult.SUCCESS;

                    // --- 核心接收逻辑结束 ---
                })
                .build();

        System.out.println("👂 监听已启动，等待消息中...");
    }
}
