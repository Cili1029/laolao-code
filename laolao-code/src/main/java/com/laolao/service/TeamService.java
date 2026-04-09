package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.CreateTeamDTO;
import com.laolao.pojo.dto.JoinTeamDTO;
import com.laolao.pojo.vo.DetailBaseTeamVO;
import com.laolao.pojo.vo.DetailExamTeamVO;
import com.laolao.pojo.vo.TeamVO;

import java.util.List;

public interface TeamService {
    Result<List<TeamVO>> getSimpleTeam();

    Result<String> joinTeam(JoinTeamDTO joinTeamDTO);

    Result<DetailBaseTeamVO> getDetailTeamBase(Integer teamId);

    Result<List<DetailExamTeamVO>> getDetailTeamExam(Integer teamId);

    Result<String> createTeam(CreateTeamDTO createTeamDTO);
}
