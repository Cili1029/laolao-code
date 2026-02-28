package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.CreateStudyGroupDTO;
import com.laolao.pojo.dto.JoinStudyGroupDTO;
import com.laolao.pojo.vo.DetailBaseStudyGroupVO;
import com.laolao.pojo.vo.DetailExamStudyGroupVO;
import com.laolao.pojo.vo.StudyGroupVO;
import com.laolao.service.StudyGroupService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/group")
public class StudyGroupController {
    @Resource
    private StudyGroupService studyGroupService;

    /**
     * 获取小组列表
     *
     * @return 小组列表
     */
    @GetMapping
    public Result<List<StudyGroupVO>> getSimpleGroup() {
        return studyGroupService.getSimpleGroup();
    }

    /**
     * 加入学习小组
     *
     * @param joinStudyGroupDTO 加入小组请求参数
     * @return 操作结果
     */
    @PostMapping("/join")
    public Result<String> joinGroup(@RequestBody JoinStudyGroupDTO joinStudyGroupDTO) {
        return studyGroupService.joinGroup(joinStudyGroupDTO);
    }

    /**
     * 获取小组基础详情
     *
     * @param studyGroupId 小组ID
     * @return 小组基础详情
     */
    @GetMapping("/detail-base")
    public Result<DetailBaseStudyGroupVO> getDetailGroupBase(@RequestParam Integer studyGroupId) {
        return studyGroupService.getDetailGroupBase(studyGroupId);
    }

    /**
     * 获取小组考试详情
     *
     * @param studyGroupId 小组ID
     * @return 小组考试详情列表
     */
    @GetMapping("/detail-exam")
    public Result<List<DetailExamStudyGroupVO>> getDetailGroupExam(@RequestParam Integer studyGroupId) {
        return studyGroupService.getDetailGroupExam(studyGroupId);
    }

    /**
     * 导师创建学习小组
     *
     * @param createStudyGroupDTO 创建小组请求参数
     * @return 操作结果
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADVISOR')")
    public Result<String> createGroup(@RequestBody CreateStudyGroupDTO createStudyGroupDTO) {
        return studyGroupService.createGroup(createStudyGroupDTO);
    }
}
