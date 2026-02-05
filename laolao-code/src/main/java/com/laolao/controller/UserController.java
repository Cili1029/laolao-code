package com.laolao.controller;

import com.laolao.pojo.dto.SignDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    @PostMapping("/sign-in")
    public String hello(@RequestBody SignDTO signDTO) {
        System.out.println(signDTO);
        return "Hello World";
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test() {
        return "Hello World";
    }
}
