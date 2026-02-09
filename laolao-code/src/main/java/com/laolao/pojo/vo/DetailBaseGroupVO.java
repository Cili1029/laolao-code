package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailBaseGroupVO implements Serializable {

    /**
     * 组名
     */
    private String name;

    /**
     * 组描述
     */
    private String description;

    /**
     * 组员数
     */
    private Integer memberCount;

    /**
     * 导师账号
     */
    private String username;

    /**
     * 导师名字
     */
    private String advisorName;
}
