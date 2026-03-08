package com.laolao.pojo.ai;

import lombok.Builder;


@Builder
public record JudgeRecordContext(
        String title,               // 题目标题、
        String answerCode,          // 学生提交的代码快照
        String standardSolution    // 标准答案代码 (供 AI 参考)
) {
}