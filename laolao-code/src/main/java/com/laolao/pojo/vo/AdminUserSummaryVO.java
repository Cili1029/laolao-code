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
     * 主键ID
     */
    private Integer managerCount;

    /**
     * 账号
     */
    private String userCount;
}