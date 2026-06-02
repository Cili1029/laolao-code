package com.laolao.ai.pojo.content;

import lombok.Builder;

import java.util.List;

@Builder
public record ManagerExamReportContent(
        // 学生的实考、缺勤和得分情况
        List<ExamScoreDataContent> attendanceAndScores,

        // 考试所有的题目详情和标准答案
        List<ExamQuestionDataContent> examQuestions,

        // 所有学生对各题的具体作答代码
        List<UserAnswerDataContent> userAnswers
) {}

