package com.laolao.common.util;

import com.laolao.pojo.vo.ExamPermissionsVO;
import com.laolao.pojo.vo.UserExamRecord;

import java.time.LocalDateTime;

// 工具类：计算考试的各种权限（老师/学生看到的按钮、状态、步骤）
public class ExamHelper {

    /**
     * @param role   角色 1=老师，其他=学生
     * @param status 考试状态 0=草稿 1=已发布 2=批改中 3=已完成
     * @param start  考试开始时间
     * @param end    考试结束时间
     * @param record 用户的考试记录（有没有进入、有没有交卷）
     * @return 前端需要的所有权限对象
     */
    public static ExamPermissionsVO calculate(Integer role, Integer status, LocalDateTime start, LocalDateTime end, UserExamRecord record) {
        ExamPermissionsVO p = new ExamPermissionsVO(); // 新建权限对象
        LocalDateTime now = LocalDateTime.now();       // 当前时间

        // ======================
        // 1. 基础考试状态（所有人都一样）
        // ======================
        p.setDraft(status == 0);      // 是否草稿
        p.setPublished(status == 1);  // 是否已发布
        p.setGrading(status == 2);    // 是否批改中
        p.setCompleted(status == 3);   // 是否已完成

        // ======================
        // 2. 老师权限（role == 1）
        // ======================
        if (role == 1) {
            p.setCanEdit(status == 0);      // 只有草稿能编辑
            p.setCanRelease(status == 0);    // 只有草稿能发布
            p.setCanDelete(status == 0);     // 只有草稿能删除
            p.setCanGrade(status == 2);      // 只有批改中能批改
            p.setCanSelectQuestions(status == 0);
            p.setTimelineStep(now.isAfter(end) ? 4 : 1);
            // 时间步骤：结束了=4，否则=1
        }

        // ======================
        // 3. 学生权限（重点！）
        // ======================
        else {
            // 当前时间是否在考试时间范围内
            boolean isTimeRange = now.isAfter(start) && now.isBefore(end);

            // 能不能开始考试：
            // 已发布 + 在时间内 + 没有任何考试记录（第一次进）
            p.setCanStart(
                    status == 1
                            && isTimeRange
                            && record == null
            );

            // 能不能继续考试：
            // 已发布 + 在时间内 + 有记录 + 记录状态是0（进行中）
            p.setCanContinue(
                    status == 1
                            && isTimeRange
                            && record != null
                            && record.getStatus() == 0
            );

            // 能不能看成绩：
            // 考试已完成 + 有记录 + 记录状态是3（已批改完成）
            p.setCanViewResult(
                    status == 3
                            && record != null
                            && record.getStatus() == 3
            );

            // ======================
            // 学生考试步骤（时间线）
            // ======================
            int step = 1;                     // 默认步骤1：未进入
            if (record != null) step = 2;     // 有记录 → 步骤2：进行中
            if (record != null && record.getSubmitTime() != null) step = 3;
            // 有记录+有交卷时间 → 步骤3：已交卷
            if (now.isAfter(end)) step = 4;   // 考试已结束 → 步骤4：已结束

            p.setTimelineStep(step);
        }

        return p; // 返回所有权限给前端
    }
}