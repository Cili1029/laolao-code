package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserExamRecord implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 考生Id
     */
    private Integer userId;

    /**
     * 考生状态
     */
    private Integer status;

    /**
     * 考生进入时间
     */
    private LocalDateTime enterTime;

    /**
     * 学生提交时间
     */
    private LocalDateTime submitTime;
}

