package com.laolao.pojo.vo;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminJudgeRecordVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;


    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户名字
     */
    private String name;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 执行耗时(ms)
     */
    private Integer time;

    /**
     * 内存消耗(MB)
     */
    private Integer memory;
}