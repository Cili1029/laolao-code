package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.UserInfoVO;
import com.laolao.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 注册用户
     *
     * @param user 用户数据
     * @return 结果消息
     */
    @PostMapping("/sign-up")
    public Result<String> signUp(@RequestBody User user) {
        return userService.signUp(user);
    }

    /**
     * 获取用户基础信息
     *
     * @return 用户基础信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getInfo() {
        return userService.getInfo();
    }
}
