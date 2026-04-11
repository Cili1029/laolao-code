package com.laolao.pojo.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamPermissionsVO {
    // 基础状态开关
    private boolean draft;      // 0: 草稿
    private boolean published;  // 1: 已发布
    private boolean grading;    // 2: 改卷中
    private boolean completed;  // 3: 已完成/已出分

    // 动作开关 (老师)
    private boolean canEdit;
    private boolean canRelease;
    private boolean canDelete;
    private boolean canGrade;
    private boolean canSelectQuestions;


    // 动作开关 (学生)
    private boolean canStart;     // 首次进入
    private boolean canContinue;  // 继续答题
    private boolean canViewResult;// 查看成绩

    // 进度条 (1:开放, 2:答题, 3:提交, 4:截止)
    private int timelineStep;
}
