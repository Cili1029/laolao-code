package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.User;

public interface UserService {
    Result<String> signUp(User user);
}
