package com.laolao.pojo.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamDetailPermissionsVO extends ExamSummaryPermissionsVO {
    // 组管理员
    private boolean canEdit;
    private boolean canSelectQuestions;
    private boolean canRelease;
    private boolean canDelete;
    private boolean canGrade;
    private boolean canCancel;

    // 学生
    private boolean canStart;     // 首次进入
    private boolean canContinue;  // 继续答题
    private boolean canViewResult;// 查看成绩

    // 进度条 (1:开放, 2:答题, 3:提交, 4:截止)
    private int timelineStep;
}
