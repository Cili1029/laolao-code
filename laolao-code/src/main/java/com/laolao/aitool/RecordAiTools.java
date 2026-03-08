package com.laolao.aitool;

import com.laolao.common.util.MapStruct;
import com.laolao.mapper.AiReportMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.pojo.ai.InsertAiReportContext;
import com.laolao.pojo.ai.JudgeRecordContext;
import com.laolao.pojo.entity.AiReport;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class RecordAiTools {
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private AiReportMapper aiReportMapper;
    @Resource
    MapStruct mapStruct;

    @Tool(description = "根据判题记录Id（考生作答记录）获取相关题目信息和考生对这一题的作答情况")
    public JudgeRecordContext queryJudgeRecordByJudgeId(@ToolParam(description = "这一道题的判题记录的主键") Integer judgeRecordId) {
        return  judgeRecordMapper.selectJudgeInfoToAi(judgeRecordId);
    }

    @Tool(description = "将报告写入报告表")
    public void insertToTable(@ToolParam(description = "表数据")InsertAiReportContext insertAiReportContext) {
        AiReport aiReport = mapStruct.insertAiReportContextToAiReport(insertAiReportContext);
        aiReportMapper.insert(aiReport);
    }
}
