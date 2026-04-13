package com.laolao.pojo.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamSummaryPermissionsVO {
    // 基础状态开关
    private boolean draft;      // 草稿
    private boolean publishing; // 发布中
    private boolean published;  // 已发布
    private boolean grading;    // 改卷中
    private boolean completed;  // 已完成/已出分
    private boolean canceled;   // 已取消
}
