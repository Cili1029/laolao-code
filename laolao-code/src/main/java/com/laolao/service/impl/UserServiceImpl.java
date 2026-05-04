package com.laolao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.common.security.MyPasswordEncoder;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.UserMapper;
import com.laolao.pojo.dto.AdminUserUpdateDTO;
import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.AdminUserSummaryVO;
import com.laolao.pojo.vo.AdminUserVO;
import com.laolao.pojo.vo.UserInfoVO;
import com.laolao.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private MyPasswordEncoder myPasswordEncoder;

    @Override
    public Result<String> signUp(User user) {
        // 检查是否存在
        User exist = userMapper.selectUserByUsername(user.getUsername());
        if (exist != null) {
            return Result.error("已经有这个帐号了");
        }
        user.setPassword(myPasswordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
        return Result.success("注册成功");
    }

    @Override
    public Result<UserInfoVO> getInfo() {
        Integer userId = SecurityUtils.getUserId();
        UserInfoVO user = userMapper.selectInfoById(userId);
        return Result.success(user);
    }

    @Override
    public Result<Page<AdminUserVO>> getAllUser(Integer pageNum, Integer pageSize, String content) {
        Page<AdminUserVO> page = new Page<>(pageNum, pageSize);
        Page<AdminUserVO> adminUserVOList = userMapper.selectAllUser(page, content);
        return Result.success(adminUserVOList);
    }

    @Override
    @Transactional
    public Result<String> changeStatus(Integer userId) {
        userMapper.changeStatusByUserId(userId);
        return Result.success("操作成功");
    }

    @Override
    public Result<String> updateUser(AdminUserUpdateDTO adminUserUpdateDTO) {
        userMapper.updateUser(adminUserUpdateDTO);
        return Result.success("更新成功");
    }

    @Override
    public Result<AdminUserSummaryVO> getSummary() {
        AdminUserSummaryVO adminUserSummaryVO = userMapper.selectUserCount();
        return Result.success(adminUserSummaryVO);
    }
}
