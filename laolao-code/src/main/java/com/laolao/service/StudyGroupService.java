package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.CreateStudyGroupDTO;
import com.laolao.pojo.dto.JoinStudyGroupDTO;
import com.laolao.pojo.vo.DetailBaseStudyGroupVO;
import com.laolao.pojo.vo.DetailExamStudyGroupVO;
import com.laolao.pojo.vo.StudyGroupVO;

import java.util.List;

public interface StudyGroupService {
    Result<List<StudyGroupVO>> getSimpleGroup();

    Result<String> joinGroup(JoinStudyGroupDTO joinStudyGroupDTO);

    Result<DetailBaseStudyGroupVO> getDetailGroupBase(Integer studyGroupId);

    Result<List<DetailExamStudyGroupVO>> getDetailGroupExam(Integer studyGroupId);

    Result<String> createGroup(CreateStudyGroupDTO createStudyGroupDTO);
}
