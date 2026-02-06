package com.laolao.controller;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.User;
import com.laolao.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test() {
        return "Hello World";
    }

    @PostMapping("/sign-up")
    public Result<String> signUp(@RequestBody User user) {
        return userService.signUp(user);
    }
}
