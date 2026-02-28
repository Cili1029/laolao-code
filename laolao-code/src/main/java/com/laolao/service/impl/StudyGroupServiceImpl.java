package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.StudyGroupMapper;
import com.laolao.pojo.dto.CreateStudyGroupDTO;
import com.laolao.pojo.dto.JoinStudyGroupDTO;
import com.laolao.pojo.entity.StudyGroup;
import com.laolao.pojo.vo.DetailBaseStudyGroupVO;
import com.laolao.pojo.vo.DetailExamStudyGroupVO;
import com.laolao.pojo.vo.StudyGroupVO;
import com.laolao.service.StudyGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudyGroupServiceImpl implements StudyGroupService {
    @Resource
    private StudyGroupMapper studyGroupMapper;

    @Override
    public Result<List<StudyGroupVO>> getSimpleGroup() {
        Integer userId = SecurityUtils.getUserId();
        List<StudyGroupVO> studyGroupVOList = SecurityUtils.hasAuthority("ROLE_ADVISOR") ?
                studyGroupMapper.selectAdvisorSimpleGroup(userId) :
                studyGroupMapper.selectStudentSimpleGroup(userId);
        return Result.success(studyGroupVOList);
    }

    @Override
    public Result<String> joinGroup(JoinStudyGroupDTO joinStudyGroupDTO) {
        Integer id = studyGroupMapper.selectGroupByInviteCode(joinStudyGroupDTO.getInviteCode());
        if (id == null) {
            return Result.error("邀请码不存在！");
        }

        joinStudyGroupDTO.setStudyGroupId(id);
        joinStudyGroupDTO.setMemberId(SecurityUtils.getUserId());
        int count = studyGroupMapper.selectCountByMemberId(joinStudyGroupDTO);

        if (count > 0) {
            return Result.error("已加入学习组！");
        }

        studyGroupMapper.insertGroupMember(joinStudyGroupDTO);
        return Result.success("加入成功！");
    }

    @Override
    public Result<DetailBaseStudyGroupVO> getDetailGroupBase(Integer studyGroupId) {
        // 学习组信息和导师信息
        DetailBaseStudyGroupVO detailBaseStudyGroupVO = studyGroupMapper.selectDetailBase(studyGroupId);
        // 班级人数
        detailBaseStudyGroupVO.setMemberCount(studyGroupMapper.selectMemberCount(studyGroupId));
        return Result.success(detailBaseStudyGroupVO);
    }

    @Override
    public Result<List<DetailExamStudyGroupVO>> getDetailGroupExam(Integer studyGroupId) {
        // 班级考试数据
        List<DetailExamStudyGroupVO> detailExamStudyGroupVOList = studyGroupMapper.selectDetailExam(studyGroupId);
        return Result.success(detailExamStudyGroupVOList);
    }

    @Override
    public Result<String> createGroup(CreateStudyGroupDTO createStudyGroupDTO) {
        StudyGroup studyGroup = StudyGroup.builder()
                .advisorId(SecurityUtils.getUserId())
                .inviteCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8))
                .name(createStudyGroupDTO.getName())
                .description(createStudyGroupDTO.getDescription()).build();
        studyGroupMapper.insert(studyGroup);
        return Result.success("创建成功");
    }
}
