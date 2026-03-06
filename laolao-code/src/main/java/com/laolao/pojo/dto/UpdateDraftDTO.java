package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateDraftDTO implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 考试标题
     */
    private String title;

    /**
     * 考试说明
     */
    private String description;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}