package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.GroupVO;
import com.laolao.service.GroupService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
