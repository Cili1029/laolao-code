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
}
