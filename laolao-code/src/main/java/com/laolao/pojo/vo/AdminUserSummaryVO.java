package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 用户表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AdminUserSummaryVO implements Serializable {

    /**
     * 组管理员人数
     */
    private Integer managerCount;

    /**
     * 组员人数
     */
    private String userCount;

    /**
     * 在线人数
     */
    private Integer onlineCount;
}