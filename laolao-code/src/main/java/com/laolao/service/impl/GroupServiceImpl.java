package com.laolao.service.impl;

import com.laolao.common.context.UserContext;
import com.laolao.common.result.Result;
import com.laolao.mapper.GroupMapper;
import com.laolao.pojo.dto.JoinGroupDTO;
import com.laolao.pojo.vo.DetailBaseGroupVO;
import com.laolao.pojo.vo.DetailExamGroupVO;
import com.laolao.pojo.vo.GroupVO;
import com.laolao.service.GroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private GroupMapper groupMapper;

    @Override
    public Result<List<GroupVO>> getSimpleGroup() {
        Integer userId = UserContext.getCurrentId();
        List<GroupVO> groupVOList = groupMapper.selectSimpleGroup(userId);
        return Result.success(groupVOList);
    }

    @Override
    public Result<String> joinGroup(JoinGroupDTO joinGroupDTO) {
        Integer id = groupMapper.selectGroupByInviteCode(joinGroupDTO.getInviteCode());
        if (id == null) {
            return Result.error("邀请码不存在！");
        }

        joinGroupDTO.setGroupId(id);
        joinGroupDTO.setMemberId(UserContext.getCurrentId());
        int count = groupMapper.selectCountBymemberId(joinGroupDTO);

        if (count > 0) {
            return Result.error("已加入学习组！");
        }

        groupMapper.insertGroupMember(joinGroupDTO);
        return Result.success("加入成功！");
    }

    @Override
    public Result<DetailBaseGroupVO> getDetailGroupBase(Integer groupId) {
        // 学习组信息和导师信息
        DetailBaseGroupVO detailBaseGroupVO = groupMapper.selectDetailBase(groupId);
        // 班级人数
        detailBaseGroupVO.setMemberCount(groupMapper.selectMemberCount(groupId));
        return Result.success(detailBaseGroupVO);
    }

    @Override
    public Result<List<DetailExamGroupVO>> getDetailGroupExam(Integer groupId) {
        // 班级考试数据
        List<DetailExamGroupVO> detailExamGroupVOList = groupMapper.selectDetailExam(groupId);
        return Result.success(detailExamGroupVOList);
    }
}
