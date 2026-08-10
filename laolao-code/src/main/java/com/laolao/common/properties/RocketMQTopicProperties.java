package com.laolao.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rocketmq-topic")
public class RocketMQTopicProperties {
    private String judgeTopic;
    private String examEndTopic;

    public static final String TAG_MANAGER = "MANAGER";
    public static final String TAG_USER = "USER";
    public static final String TAG_RELEASE = "RELEASE";

    public String getJudgeTopicManager() {
        return judgeTopic + ":" + TAG_MANAGER;
    }

    public String getJudgeTopicUser() {
        return judgeTopic + ":" + TAG_USER;
    }

    public String getJudgeTopicRelease() {
        return judgeTopic + ":" + TAG_RELEASE;
    }
}
