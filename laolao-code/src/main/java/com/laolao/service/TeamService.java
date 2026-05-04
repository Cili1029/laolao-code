package com.laolao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AdminTeamUpdateDTO;
import com.laolao.pojo.dto.CreateTeamDTO;
import com.laolao.pojo.dto.JoinTeamDTO;
import com.laolao.pojo.vo.*;

import java.util.List;

public interface TeamService {
    Result<List<TeamVO>> getSimpleTeam();

    Result<String> joinTeam(JoinTeamDTO joinTeamDTO);

    Result<DetailBaseTeamVO> getDetailTeamBase(Integer teamId);

    Result<List<DetailExamTeamVO>> getDetailTeamExam(Integer teamId);

    Result<String> createTeam(CreateTeamDTO createTeamDTO);

    Result<Page<AdminTeamVO>> getAllTeam(Integer pageNum, Integer pageSize, String content);

    Result<String> changeStatus(Integer teamId);

    Result<String> updateUser(AdminTeamUpdateDTO adminTeamUpdateDTO);

    Result<AdminTeamSummaryVO> getSummary();
}
