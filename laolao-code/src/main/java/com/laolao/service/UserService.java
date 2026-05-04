package com.laolao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AdminUserUpdateDTO;
import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.AdminUserSummaryVO;
import com.laolao.pojo.vo.AdminUserVO;
import com.laolao.pojo.vo.UserInfoVO;

public interface UserService {
    Result<String> signUp(User user);

    Result<UserInfoVO> getInfo();

    Result<Page<AdminUserVO>> getAllUser(Integer pageNum, Integer pageSize, String content);

    Result<String> changeStatus(Integer userId);

    Result<String> updateUser(AdminUserUpdateDTO adminUserUpdateDTO);

    Result<AdminUserSummaryVO> getSummary();
}
