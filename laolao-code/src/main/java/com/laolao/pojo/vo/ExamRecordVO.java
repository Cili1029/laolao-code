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
public class ExamRecordVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 记录标题
     */
    private String name;

    /**
     * 记录整体评价
     */
    private String description;

    /**
     * 所属组
     */
    private String group;

    /**
     * 开始时间
     */
    private LocalDateTime time;
}
