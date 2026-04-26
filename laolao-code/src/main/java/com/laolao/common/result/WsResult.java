package com.laolao.common.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsResult<T> {
    /**
     * 消息类型
     */
    private String type;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 数据内容
     */
    private T data;

    // 全局复用ObjectMapper
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 成功 - 不带数据信息
    public static <T> String success(String type) {
        return of(type, 1, null, null);
    }

    // 成功 - 不带数据
    public static String success(String type, String msg) {
        return of(type, 1, msg, null);
    }

    // 成功 - 带数据
    public static <T> String success(String type, String msg, T data) {
        return of(type, 1, msg, data);
    }

    // 失败 - 不带数据
    public static String error(String type, String msg) {
        return of(type, 0, msg, null);
    }

    /**
     * 创建WsResult并序列化为JSON字符串
     * @param type 消息类型
     * @param data 数据内容
     * @return JSON字符串
     */
    public static <T> String of(String type, Integer code, String msg, T data) {
        try {
            WsResult<T> message = new WsResult<>(type, code, msg, data);
            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("WsMessage 序列化 JSON 失败", e);
        }
    }
}
