package com.laolao.common.util;

import com.laolao.common.constant.ExamConstant;
import com.laolao.pojo.vo.ExamPermissionsVO;
import com.laolao.pojo.vo.UserExamRecord;

import java.time.LocalDateTime;

// 计算考试的各种权限（组管理员/考试看到的按钮、状态、步骤）
public class ExamHelper {
    /**
     * @param role   角色 1=组管理员，2=考生
     * @param status 考试状态
     * @param start  考试开始时间
     * @param end    考试结束时间
     * @param record 考试的考试记录（有没有进入、有没有交卷）
     * @return 前端需要的所有权限对象
     */
    public static ExamPermissionsVO calculate(Integer role, Integer status, LocalDateTime start, LocalDateTime end, UserExamRecord record) {
        ExamPermissionsVO p = new ExamPermissionsVO();
        LocalDateTime now = LocalDateTime.now();

        // 基础考试状态
        p.setDraft(status == ExamConstant.DRAFT);           // 是否草稿
        p.setPublishing(status == ExamConstant.PUBLISHING); // 是否发布中
        p.setPublished(status == ExamConstant.PUBLISHED);   // 是否已发布
        p.setGrading(status == ExamConstant.GRADING);       // 是否批改中
        p.setCompleted(status == ExamConstant.COMPLETED);   // 是否已完成
        p.setCanceled(status == ExamConstant.CANCELED);     // 是否被取消


        // 组管理员权限（role == 1）
        if (role == 1) {
            p.setCanEdit(status == ExamConstant.DRAFT);              // 只有草稿能编辑
            p.setCanSelectQuestions(status == ExamConstant.DRAFT);   // 只有草稿能选题
            p.setCanRelease(status == ExamConstant.DRAFT);           // 只有草稿能发布
            p.setCanDelete(status == ExamConstant.DRAFT);            // 只有草稿能删除
            p.setCanGrade(status == ExamConstant.GRADING);           // 只有批改中能批改
            p.setTimelineStep(now.isAfter(end) ? 4 : 1);             // 管理员考试时间线：结束了=4，否则=1

            // 可取消 = 已发布 || 改卷中
            p.setCanCancel(status == ExamConstant.PUBLISHED || status == ExamConstant.GRADING);
        }

        // 考生权限（role == 2）
        else {
            // 当前时间是否在考试时间范围内
            boolean isTimeRange = now.isAfter(start) && now.isBefore(end);

            // 能不能开始考试 = 已发布 + 在时间内 + 没有任何考试记录（第一次进）
            p.setCanStart(status == ExamConstant.PUBLISHED && isTimeRange && record == null);

            // 能不能继续考试 = 已发布 + 在时间内 + 有记录 + 记录状态是0（进行中）
            p.setCanContinue(status == ExamConstant.PUBLISHED && isTimeRange && record != null && record.getStatus() == 0);

            // 能不能看成绩 = 考试已完成 + 有记录 + 记录状态是2（已批改完成）
            p.setCanViewResult(status == ExamConstant.COMPLETED && record != null && record.getStatus() == 2);

            // 学生考试时间线
            // 默认 -> 步骤1：未进入
            int step = 1;

            // 有记录 -> 步骤2：进行中
            if (record != null) step = 2;

            // 有记录+有交卷时间 → 步骤3：已交卷
            if (record != null && record.getSubmitTime() != null) step = 3;

            // 考试已结束 → 步骤4：已结束
            if (now.isAfter(end)) step = 4;
            p.setTimelineStep(step);
        }

        return p; // 返回所有权限给前端
    }
}