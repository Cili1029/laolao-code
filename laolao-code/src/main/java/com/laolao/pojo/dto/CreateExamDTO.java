package com.laolao.pojo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateExamDTO {
    /**
     * 组ID
     */
    private Integer teamId;

    /**
     * 考试标题
     */
    private String title;

    /**
     * 考试描述
     */
    private String description;

    /**
     * 考试开始时间
     */
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    private LocalDateTime endTime;
}
