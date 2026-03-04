package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Question implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容 (Markdown格式)
     */
    private String content;

    /**
     * 难度：0-简单, 1-中等, 2-困难
     */
    private Byte difficulty;

    /**
     * 时间限制 (ms)
     */
    private Integer timeLimit;

    /**
     * 内存限制 (MB)
     */
    private Integer memoryLimit;

    /**
     * 初始化模板代码 (如 public class Main...)
     */
    private String templateCode;

    /**
     * 标准答案代码 (供 AI 参考)
     */
    private String standardSolution;

    /**
     * 创建者ID（导师ID）
     */
    private Integer advisorId;

    /**
     * 父题目ID(0-祖宗模板题，非0-考试属子题快照)
     */
    private Integer parentId;

    /**
     * 是否公开(0-私有, 1-公开)
     */
    private Integer isPublic;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（自动更新）
     */
    private LocalDateTime updateTime;
}