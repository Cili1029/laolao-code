package com.laolao.common.constant;

public class ExamTimeConstant {
    public static final int NOT_STARTED = 0;   // 未开始（考试尚未启动，学生无法进入）
    public static final int STARTED = 1;       // 已开始（考试已启动，学生可进入答题）
    public static final int ENTERED = 2;       // 已进入（学生进入答题页面，正在作答）
    public static final int SUBMITTED = 3;     // 已提交（学生正常交卷，不可再作答）
    public static final int ENDED = 4;         // 已结束（考试时间截止，系统自动结束）
}
