package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.UserInfoVO;

public interface UserService {
    Result<String> signUp(User user);
    Result<UserInfoVO> getInfo();
}
