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

    @GetMapping
    public Result<List<GroupVO>> getSimpleGroup() {
        return groupService.getSimpleGroup();
    }

    @PostMapping("/join")
    public Result<String> joinGroup(@RequestBody JoinGroupDTO joinGroupDTO) {
        return groupService.joinGroup(joinGroupDTO);
    }

    @GetMapping("/detail-base")
    public Result<DetailBaseGroupVO> getDetailGroupBase(@RequestParam Integer groupId) {
        return groupService.getDetailGroupBase(groupId);
    }

    @GetMapping("/detail-exam")
    public Result<List<DetailExamGroupVO>> getDetailGroupExam(@RequestParam Integer groupId) {
        return groupService.getDetailGroupExam(groupId);
    }
}
