package com.laolao.common.docker;

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

//    AC	Accepted	全部通过。
//    WA	Wrong Answer	代码跑完了，但输出结果和答案对不上。
//    TLE	Time Limit Exceeded	运行时间超过了题目限制。
//    MLE	Memory Limit Exceeded	内存占用超过了题目限制。
//    RE	Runtime Error	运行时崩溃（如数组越界、除以0、空指针）。
//    CE	Compile Error	编译没通过。
//    SE	System Error	判题机本身坏了（比如 Docker 挂了）。
}
