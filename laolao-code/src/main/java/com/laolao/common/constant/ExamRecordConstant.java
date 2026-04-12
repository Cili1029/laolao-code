package com.laolao.common.constant;

public class ExamRecordConstant {
    public static final int ONGOING = 0;        // 进行中（学生正在答题，未提交）
    public static final int SUBMITTED = 1;      // 已提交（学生完成交卷，批改中）
    public static final int GRADING_COMPLETE = 2; // 批改完成（AI/老师批改结束，已出成绩）
    public static final int INVALID = 3;        // 无效（作弊/超时提交/数据异常，无批改价值）
    public static final int REVIEWING = 4;        // 复核中（批改结果有异议，老师复核）
    public static final int REVIEW_COMPLETE = 5; // 复核完成（复核结束，最终成绩确定）
}
