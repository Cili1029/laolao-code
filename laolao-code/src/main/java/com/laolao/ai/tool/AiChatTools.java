package com.laolao.ai.tool;

import com.laolao.common.util.ExamHelper;
import com.laolao.mapper.ExamMapper;
import com.laolao.mapper.TeamMapper;
import com.laolao.pojo.vo.ExamSummaryPermissionsVO;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.pojo.vo.TeamVO;
import jakarta.annotation.Resource;
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

    @Tool(description = "【组员可用/组管理员可用】根据用户Id获取该用户加入/管理的小组")
    public List<TeamVO> getTeamByUserId(@ToolParam(description = "用户Id") Integer userId, @ToolParam(description = "用户类型") String role) {
        return "ROLE_MANAGER".equals(role) ?
                teamMapper.selectManagerSimpleTeam(userId) :
                teamMapper.selectUserSimpleTeam(userId);
    }

    @Tool(description = "【组员可用/组管理员可用】根据用户Id获取该用户的考试（组员为参加的考试，组管理员为他所创建的考试），考试状态(0-草稿, 5-发布中, 1-已发布, 2-改卷中, 3-已完成, 4-已取消)")
    public List<ExamVO> getExamByUserId(@ToolParam(description = "用户Id") Integer userId) {
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        examVOList.forEach(examVO -> {
            ExamSummaryPermissionsVO permissions = ExamHelper.calculateSummary(examVO.getStatus());
            examVO.setSummaryPermissions(permissions);
        });
        return examVOList;
    }
}
