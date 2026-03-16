package com.laolao;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LaolaoCodeApplicationTests {

    @Resource
    private RocketMQClientTemplate rocketMQClientTemplate;

    @Test
    void contextLoads() {
        rocketMQClientTemplate.convertAndSend("TestTopic", "Hello RocketMQ 5.x!");
    }
}