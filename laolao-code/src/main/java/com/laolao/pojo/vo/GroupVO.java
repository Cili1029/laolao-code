package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GroupVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 组名
     */
    private String name;

    /**
     * 创建老师
     */
    private String advisor;

    /**
     * 组描述
     */
    private String description;
}

