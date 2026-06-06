package com.laolao.ai.tool;

import com.laolao.ai.pojo.vo.AiSimpleExamRecordVO;
import com.laolao.ai.pojo.vo.AiSimpleExamVO;
import com.laolao.ai.pojo.vo.AiSimpleTeamVO;
import com.laolao.common.util.ExamHelper;
import com.laolao.common.util.MapStruct;
import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.ExamRecordMapper;
import com.laolao.mapper.JudgeRecordMapper;
import com.laolao.mapper.TeamMapper;
import com.laolao.pojo.vo.*;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatTools {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private MapStruct mapStruct;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;

    @Tool(description = "【组员可用/组管理员可用】根据用户Id获取该用户全部加入/管理的小组基础信息")
    public List<AiSimpleTeamVO> getTeamByUserId(ToolContext toolContext) {
        Integer userId = getUserId(toolContext);
        String role = getRole(toolContext);
        List<TeamVO> teamVOS = "ROLE_MANAGER".equals(role) ?
                teamMapper.selectManagerSimpleTeam(userId) :
                teamMapper.selectUserSimpleTeam(userId);
        return mapStruct.teamVOToAiSimpleTeamVO(teamVOS);
    }

    @Tool(description = "【组员可用/组管理员可用】根据用户Id获取该用户的全部考试基础信息（组员为参加的考试，组管理员为他所创建的考试），考试状态(0-草稿, 5-发布中, 1-已发布, 2-改卷中, 3-已完成, 4-已取消)")
    public List<AiSimpleExamVO> getExamByUserId(ToolContext toolContext) {
        List<ExamVO> examVOList = examMapper.selectSimpleExam(getUserId(toolContext));
        examVOList.forEach(examVO -> {
            ExamSummaryPermissionsVO permissions = ExamHelper.calculateSummary(examVO.getStatus());
            examVO.setSummaryPermissions(permissions);
        });
        return mapStruct.examVOToAiSimpleExamVO(examVOList);
    }

    @Tool(description = "【组员可用/组管理员可用】根据用户Id获取该用户的全部考试报告基础信息")
    public List<AiSimpleExamRecordVO> getExamReportUserId(ToolContext toolContext) {
        List<ExamRecordVO> examRecordVOList = examRecordMapper.selectSimpleExamRecord(getUserId(toolContext));
        return mapStruct.ExamRecordVOToAiSimpleExamRecordVO(examRecordVOList);
    }

    @Tool(description = "【组员可用/组管理员可用】根据考试记录Id（报告Id）获取该用户此场考试的考试情况")
    public UserReportVO getNotPassQuestionsByExamRecordId(@ToolParam(description = "报告Id")Integer examRecordId) {
        UserReportVO userReportVO = examRecordMapper.selectUserInfoByRecordId(examRecordId);
        userReportVO.setJudgeRecords(judgeRecordMapper.selectUserExamReportByRecordId(userReportVO.getId()));
        System.out.println(userReportVO);
        return userReportVO;

    }

    private Integer getUserId(ToolContext toolContext) {
        Object userId = toolContext.getContext().get("userId");
        if (userId instanceof Integer value) {
            return value;
        }
        if (userId instanceof Number value) {
            return value.intValue();
        }
        if (userId instanceof String value && !value.isBlank()) {
            return Integer.valueOf(value);
        }
        throw new IllegalStateException("未获取到当前用户信息");
    }

    private String getRole(ToolContext toolContext) {
        Object role = toolContext.getContext().get("role");
        return role == null ? "" : role.toString();
    }
}
