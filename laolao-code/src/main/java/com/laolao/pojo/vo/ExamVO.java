package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExamVO implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 考试标题
     */
    private String name;

    /**
     * 考试说明
     */
    private String description;

    /**
     * 所属组
     */
    private String team;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}

