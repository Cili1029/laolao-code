package com.laolao.common.util;

import com.laolao.common.constant.StudentExamConstant;

import java.time.LocalDateTime;

/**
 * 学生考试状态计算工具类
 * 根据考试起止时间和学生的进入/交卷时间，计算当前学生所处的动态状态
 */
public final class StudentExamStatusCalculator {

    /**
     * 计算学生当前状态（使用系统当前时间）
     *
     * @param startTime  考试开始时间
     * @param endTime    考试结束时间
     * @param enterTime  学生进入时间（可为null）
     * @param submitTime 学生交卷时间（可为null）
     * @return 学生状态常量
     */
    public static int calculate(LocalDateTime startTime, LocalDateTime endTime,
                                LocalDateTime enterTime, LocalDateTime submitTime) {
        LocalDateTime now = LocalDateTime.now();
        // 参数校验：开始和结束时间不能为空（可根据业务决定是否抛出异常）
        if (startTime == null || endTime == null) {
            // 可以返回一个默认状态，例如未开始，或抛出异常
            throw new IllegalArgumentException("考试开始时间和结束时间不能为空");
        }

        // 1. 已结束：当前时间 >= 考试结束时间
        if (now.isAfter(endTime) || now.isEqual(endTime)) {
            return StudentExamConstant.ENDED;
        }

        // 2. 已提交：有交卷时间，且当前时间 >= 交卷时间（交卷时间一定 < 结束时间）
        if (submitTime != null && (now.isAfter(submitTime) || now.isEqual(submitTime))) {
            return StudentExamConstant.SUBMITTED;
        }

        // 3. 已进入：有进入时间，且当前时间 >= 进入时间（且未提交）
        if (enterTime != null && (now.isAfter(enterTime) || now.isEqual(enterTime))) {
            return StudentExamConstant.ENTERED;
        }

        // 4. 已开始：当前时间 >= 开始时间（且未进入）
        if (now.isAfter(startTime) || now.isEqual(startTime)) {
            return StudentExamConstant.STARTED;
        }

        // 5. 未开始
        return StudentExamConstant.NOT_STARTED;
    }
}
