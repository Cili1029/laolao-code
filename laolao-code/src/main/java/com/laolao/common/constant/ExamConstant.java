package com.laolao.common.constant;

public class ExamConstant {
    public static final int DRAFT = 0;         // 草稿状态（考试创建后未发布，仅创建者可编辑/查看）
    public static final int PUBLISHED = 1;     // 已发布状态：（考试对外可见，可参与）
    public static final int ENDED = 2;
    public static final int CANCELED = 3;      // 已取消状态：（发布后因特殊原因取消，不可参与）
}
