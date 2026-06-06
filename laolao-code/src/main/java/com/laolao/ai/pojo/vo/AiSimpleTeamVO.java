package com.laolao.ai.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiSimpleTeamVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 组名
     */
    private String name;

    /**
     * 创建组管理员
     */
    private String manager;

    /**
     * 组描述
     */
    private String description;

    /**
     * 跳转链接
     */
    private String link;
}

