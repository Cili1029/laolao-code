package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 组表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AdminTeamVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 组名
     */
    private String name;

    /**
     * 组描述
     */
    private String description;

    /**
     * 创建组管理员的ID
     */
    private Integer managerId;

    /**
     * 组管理员账号
     */
    private String managerUsername;

    /**
     * 组管理员名字
     */
    private String managerName;

    /**
     * 组邀请码
     */
    private String inviteCode;

    /**
     * 状态
     */
    private Integer status;
}
