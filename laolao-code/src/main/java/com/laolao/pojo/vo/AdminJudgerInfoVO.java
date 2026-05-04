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
public class AdminJudgerInfoVO implements Serializable {
    // === 容器池基本状态 ===
    /**
     * 判题池大小
     */
    private Integer poolSize;

    /**
     * 空闲判题机数量
     */
    private Integer idleCount;

    /**
     * 忙碌判题机数量
     */
    private Integer busyCount;

    // === 任务统计 (累计) ===
    /**
     * 自项目启动以来总判题数
     */
    private Integer totalJudgeCount;

    /**
     * AC数量
     */
    private Integer successCount;

    /**
     * 非AC数量(WA, TLE, MLE, RE, CE)
     */
    private Integer failCount;

    /**
     * SE数量(系统错误)
     */
    private Integer systemErrorCount;

    /**
     * 平均耗时 (ms)
     */
    private long avgTimeCost;

    // === 机器信息 ===
    /**
     * Docker引擎版本
     */
    private String dockerEngineVersion;

    /**
     * 宿主机架构 amd64/arm64
     */
    private String hostArch;

    /**
     * 宿主机系统 linux/windows
     */
    private String hostOs;

    /**
     * 宿主机总内存
     */
    private Double hostMemoryTotal;
}