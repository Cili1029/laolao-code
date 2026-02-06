package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.common.security.MyPasswordEncoder;
import com.laolao.mapper.UserMapper;
import com.laolao.pojo.entity.User;
import com.laolao.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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


}
