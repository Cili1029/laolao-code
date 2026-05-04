package com.laolao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AdminUserUpdateDTO;
import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.AdminUserSummaryVO;
import com.laolao.pojo.vo.AdminUserVO;
import com.laolao.pojo.vo.UserInfoVO;
import com.laolao.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
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

    /**
     * 获取所有用户数据
     *
     * @return 用户数据
     */
    @GetMapping
    public Result<Page<AdminUserVO>> getUser(Integer pageNum, Integer pageSize, String content) {
        return userService.getAllUser(pageNum, pageSize, content);
    }

    /**
     * 获取网站用户概览
     *
     * @return 用户概览
     */
    @GetMapping("/summary")
    public Result<AdminUserSummaryVO> getSummary() {
        return userService.getSummary();
    }

    /**
     * 启用/禁用账户
     *
     * @param userId 账户Id
     * @return 结果信息
     */
    @PutMapping("/status")
    public Result<String> changeStatus(@RequestParam Integer userId) {
        return userService.changeStatus(userId);
    }

    /**
     * 更新账户信息
     *
     * @param adminUserUpdateDTO 更新信息
     * @return 结果信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody AdminUserUpdateDTO adminUserUpdateDTO) {
        return userService.updateUser(adminUserUpdateDTO);
    }
}
