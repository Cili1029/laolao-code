package com.laolao.pojo.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateStudyGroupDTO implements Serializable {

    /**
     * 组名
     */
    private String name;

    /**
     * 组描述
     */
    private String description;

    /**
     * 创建导师的ID
     */
    private Integer advisorId;

    /**
     * 组邀请码
     */
    private String inviteCode;
}
