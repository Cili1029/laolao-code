package com.laolao.mapper;

import com.laolao.pojo.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectUserByUsername(String username);
}
