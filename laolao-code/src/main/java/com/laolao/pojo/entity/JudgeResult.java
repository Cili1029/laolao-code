package com.laolao.pojo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JudgeResult {
    /**
     * 退出状态码（0为成功，非0为失败）
     */
    private Long exitCode;

    /**
     * 标准输出
     */
    private String stdout;

    /**
     * 错误输出（包括报错信息）
     */
    private String stderr;

    /**
     * 消耗时间 (ms)
     */
    private Double time;

    /**
     * 消耗内存 (mb)
     */
    private Long memory;
}
