package com.laolao.pojo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestCase {
    /**
     * 输入
     */
    private String input;

    /**
     * 输出
     */
    private String output;
}
