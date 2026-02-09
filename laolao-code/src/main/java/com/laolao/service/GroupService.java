package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.JoinGroupDTO;
import com.laolao.pojo.vo.DetailBaseGroupVO;
import com.laolao.pojo.vo.GroupVO;

import java.util.List;

public interface GroupService {
    Result<List<GroupVO>> getSimpleGroup();

    Result<String> joinGroup(JoinGroupDTO joinGroupDTO);

    Result<DetailBaseGroupVO> getDetailGroupBase(Integer groupId);

    Result<List<GroupVO>> getDetailGroupExam(Integer groupId);
}
