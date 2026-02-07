package com.laolao.mapper;

import com.laolao.pojo.entity.User;
import com.laolao.pojo.vo.UserInfoVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectUserByUsername(String username);

    @Insert("insert into user(username, password, name, role) value(#{username}, #{password}, #{name}, #{role})")
    void insertUser(User user);

    @Select("select id, username, name, role from user where id = #{userId}")
    UserInfoVO selectInfoById(Integer userId);
}
