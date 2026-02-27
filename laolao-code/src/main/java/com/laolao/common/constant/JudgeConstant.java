package com.laolao.common.constant;

public class JudgeConstant {
    public static final String commonImports =
            """
                        import java.util.*;
                        import java.io.*;
                        import java.math.*;
                        import java.util.stream.*;
                        import java.util.function.*;
                    """;

    // 定义标签常量，用于标识哪些容器是本项目创建的
    public static final String LABEL_KEY = "owner";
    public static final String LABEL_VALUE = "laolao-judge";

    // 判题结果状态码（从0开始）
    public static final int STATUS_AC = 0;   // 全部通过
    public static final int STATUS_WA = 1;   // 答案错误
    public static final int STATUS_MLE = 2;  // 内存超限
    public static final int STATUS_TLE = 3;  // 超时
    public static final int STATUS_RE = 4;   // 运行时错误
    public static final int STATUS_CE = 5;   // 编译错误
    public static final int STATUS_SE = 6;   // 系统错误
    public static final int STATUS_UNKNOWN = 7;// 未知错误（未覆盖的异常）
}
