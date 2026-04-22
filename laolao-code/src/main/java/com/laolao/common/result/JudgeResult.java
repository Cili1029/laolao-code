package com.laolao.common.result;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JudgeResult {

    /**
     * 判题状态
     */
    private Integer status;

    /**
     * 编译错误/系统异常
     */
    private String errorMessage;

    /**
     * 测试示例总数
     */
    private Integer totalCount;

    /**
     * 测试示例通过数
     */
    private Integer passCount;

    /**
     * 未通过的的测试示例输入
     */
    private String failedInput;

    /**
     * 未通过的的测试示例预期输出
     */
    private String failedExpect;

    /**
     * 未通过的的测试示例实际输出
     */
    private String failedActual;

    /**
     * 消耗时间 (ms)
     */
    private Integer time;

    /**
     * 消耗内存 (MB)
     */
    private Integer memory;
}
