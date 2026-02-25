package com.laolao.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 题目测试用例实体类
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class QuestionTestCase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属题目ID
     */
    private Integer questionId;

    /**
     * 测试用例输入数据
     * 使用 String 接收数据库中的 TEXT 类型
     */
    private String input;

    /**
     * 测试用例预期输出数据
     */
    private String output;
}