package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.JoinGroupDTO;
import com.laolao.pojo.vo.DetailBaseGroupVO;
import com.laolao.pojo.vo.DetailExamGroupVO;
import com.laolao.pojo.vo.GroupVO;
import com.laolao.service.GroupService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/group")
public class GroupController {
    @Resource
    private GroupService groupService;

    /**
     * 获取小组列表
     *
     * @return 小组列表
     */
    @GetMapping
    public Result<List<GroupVO>> getSimpleGroup() {
        return groupService.getSimpleGroup();
    }

    /**
     * 加入学习小组
     *
     * @param joinGroupDTO 加入小组请求参数
     * @return 操作结果
     */
    @PostMapping("/join")
    public Result<String> joinGroup(@RequestBody JoinGroupDTO joinGroupDTO) {
        return groupService.joinGroup(joinGroupDTO);
    }

    /**
     * 获取小组基础详情
     *
     * @param groupId 小组ID
     * @return 小组基础详情
     */
    @GetMapping("/detail-base")
    public Result<DetailBaseGroupVO> getDetailGroupBase(@RequestParam Integer groupId) {
        return groupService.getDetailGroupBase(groupId);
    }

    /**
     * 获取小组考试详情
     *
     * @param groupId 小组ID
     * @return 小组考试详情列表
     */
    @GetMapping("/detail-exam")
    public Result<List<DetailExamGroupVO>> getDetailGroupExam(@RequestParam Integer groupId) {
        return groupService.getDetailGroupExam(groupId);
    }
}
