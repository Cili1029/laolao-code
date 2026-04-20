package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.TeamMapper;
import com.laolao.pojo.dto.CreateTeamDTO;
import com.laolao.pojo.dto.JoinTeamDTO;
import com.laolao.pojo.entity.Team;
import com.laolao.pojo.vo.DetailBaseTeamVO;
import com.laolao.pojo.vo.DetailExamTeamVO;
import com.laolao.pojo.vo.TeamVO;
import com.laolao.service.TeamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TeamServiceImpl implements TeamService {
    @Resource
    private TeamMapper teamMapper;

    @Override
    public Result<List<TeamVO>> getSimpleTeam() {
        Integer userId = SecurityUtils.getUserId();
        List<TeamVO> teamVOList = SecurityUtils.hasAuthority("MANAGER") ?
                teamMapper.selectManagerSimpleTeam(userId) :
                teamMapper.selectUserSimpleTeam(userId);
        return Result.success(teamVOList);
    }

    @Override
    public Result<String> joinTeam(JoinTeamDTO joinTeamDTO) {
        Integer id = teamMapper.selectTeamByInviteCode(joinTeamDTO.getInviteCode());
        if (id == null) {
            return Result.error("邀请码不存在！");
        }

        joinTeamDTO.setTeamId(id);
        joinTeamDTO.setUserId(SecurityUtils.getUserId());
        int count = teamMapper.selectCountByUserId(joinTeamDTO);

        if (count > 0) {
            return Result.error("已加入小组！");
        }

        teamMapper.insertTeamUser(joinTeamDTO);
        return Result.success("加入成功！");
    }

    @Override
    public Result<DetailBaseTeamVO> getDetailTeamBase(Integer teamId) {
        // 小组信息和导师信息
        DetailBaseTeamVO detailBaseTeamVO = teamMapper.selectDetailBase(teamId);
        // 班级人数
        detailBaseTeamVO.setUserCount(teamMapper.selectUserCount(teamId));
        return Result.success(detailBaseTeamVO);
    }

    @Override
    public Result<List<DetailExamTeamVO>> getDetailTeamExam(Integer teamId) {
        // 班级考试数据
        List<DetailExamTeamVO> detailExamTeamVOList = teamMapper.selectDetailExam(teamId, SecurityUtils.getUserId());
        return Result.success(detailExamTeamVOList);
    }

    @Override
    public Result<String> createTeam(CreateTeamDTO createTeamDTO) {
        Team team = Team.builder()
                .managerId(SecurityUtils.getUserId())
                .inviteCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8))
                .name(createTeamDTO.getName())
                .description(createTeamDTO.getDescription()).build();
        teamMapper.insert(team);
        return Result.success("创建成功");
    }
}
