package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.pojo.dto.AdminUserUpdateDTO;
import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.AdminUserSummaryVO;
import com.laolao.pojo.vo.AdminUserVO;
import com.laolao.pojo.vo.UserInfoVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    User selectUserByUsername(String username);

    @Insert("insert into user(username, password, name, role) value(#{username}, #{password}, #{name}, #{role})")
    void insertUser(User user);

    @Select("select id, username, name, role from user where id = #{userId}")
    UserInfoVO selectInfoById(Integer userId);

    Page<AdminUserVO> selectAllUser(Page<AdminUserVO> page, String content);

    @Update("update user set username = #{username}, name = #{name}, role = #{role} where id = #{id}")
    void updateUser(AdminUserUpdateDTO adminUserUpdateDTO);

    @Select("""
            select sum(if(role = 1, 1, 0)) AS manager_count,
                   sum(if(role = 2, 1, 0)) AS user_count
            from user;
            """)
    AdminUserSummaryVO selectUserCount();

    @Select("update user set status = 1 - status where id = #{userId}")
    void changeStatusByUserId(Integer userId);
}
