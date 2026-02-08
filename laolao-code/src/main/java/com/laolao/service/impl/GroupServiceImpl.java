package com.laolao.service.impl;

import com.laolao.common.context.UserContext;
import com.laolao.common.result.Result;
import com.laolao.mapper.GroupMapper;
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
}
