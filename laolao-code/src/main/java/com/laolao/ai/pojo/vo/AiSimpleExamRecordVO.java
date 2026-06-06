package com.laolao.ai.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AiSimpleExamRecordVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 记录标题
     */
    private String name;

    /**
     * 所属组
     */
    private String team;

    /**
     * 开始时间
     */
    private LocalDateTime time;

    /**
     * 跳转链接
     */
    private String link;
}
