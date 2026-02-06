package com.laolao.mapper;

import com.laolao.pojo.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectUserByUsername(String username);

    @Insert("insert into user(username, password, name, role) value(#{username}, #{password}, #{name}, #{role})")
    void insertUser(User user);
}
