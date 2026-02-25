package com.laolao.pojo.entity;

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
     * 所属组ID
     */
    private Integer groupId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 导师ID
     */
    private Integer advisorId;
}

