package com.laolao.aitool;

import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.ai.JudgeRecordContext;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecordAiTools {
    @Resource
    private JudgeRecordMapper judgeRecordMapper;

    @Tool(description = "根据判题记录Id（考生判题记录judgeRecord）获取相关题目信息和考生对这一题的作答情况")
    public JudgeRecordContext queryJudgeRecordByJudgeId(@ToolParam(description = "这一道题的判题记录的主键") Integer judgeRecordId) {
        return  judgeRecordMapper.selectMemberJudgeInfoToAi(judgeRecordId);
    }

    @Tool(description = "根据考试Id（examId）和考生Id（userId）获取这次考试所有题目作答情况，学生代码为空则为没有作答这一道题")
    public List<JudgeRecordContext> queryExamRecordByExamId(@ToolParam(description = "这一场考试的主键") Integer examId, @ToolParam(description = "参考学生的主键") Integer userId) {
        return  judgeRecordMapper.selectMemberExamInfoToAi(examId, userId);
    }


}
