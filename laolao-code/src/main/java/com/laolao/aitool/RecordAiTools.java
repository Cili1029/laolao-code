package com.laolao.aitool;

import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.mapper.QuestionMapper;
import com.laolao.pojo.ai.*;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecordAiTools {
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private ExamMapper examMapper;
    @Resource
    private QuestionMapper questionMapper;

    @Tool(description = "【用于类型1(judge_record)】根据单题判题记录Id，获取相关题目信息、标准答案以及考生的代码作答情况与报错信息。")
    public JudgeRecordContext queryJudgeRecordByJudgeId(@ToolParam(description = "这一道题的判题记录的主键") Integer judgeRecordId) {
        return judgeRecordMapper.selectUserJudgeInfoToAi(judgeRecordId);
    }

    @Tool(description = "【用于类型2(exam_record)】根据考试Id(examId)和考生Id（userId），获取该生在本次考试中所有题目的作答情况（注意：学生代码为空代表未作答）。")
    public List<JudgeRecordContext> queryExamRecordByExamId(@ToolParam(description = "这一场考试的主键") Integer examId, @ToolParam(description = "参考学生的主键") Integer userId) {
        return judgeRecordMapper.selectUserExamInfoToAi(examId, userId);
    }

    @Tool(description = "【用于类型3(exam)】根据考试Id，一次性获取生成班级报告所需的全部数据：包括考勤与成绩分布、考试题目详情、以及全班学生的具体作答代码。")
    public managerExamReportContent queryClassExamFullData(@ToolParam(description = "这一场考试的主键") Integer examId) {
        // 查成绩和考勤
        List<ExamScoreDataContent> scores = examMapper.selectAttendanceAndScores(examId);
        // 查考题和标准答案
        List<ExamQuestionDataContent> questions = questionMapper.selectQuestionsByExamId(examId);
        // 查全班实际作答代码
        List<UserAnswerDataContent> answers = judgeRecordMapper.selectUserAnswersByExamId(examId);
        return managerExamReportContent.builder()
                .attendanceAndScores(scores)
                .examQuestions(questions)
                .userAnswers(answers)
                .build();
    }


}
