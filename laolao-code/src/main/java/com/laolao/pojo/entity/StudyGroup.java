package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class StudyGroup implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 创建老师的ID
     */
    private Integer advisorId;

    /**
     * 组邀请码
     */
    private String inviteCode;
}
