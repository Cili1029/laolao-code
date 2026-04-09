package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName(autoResultMap = true)
public class Exam implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 导师ID
     */
    private Integer managerId;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 所属组ID
     */
    private Integer teamId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否已在队列
     */
    private Integer isQueued;
}

