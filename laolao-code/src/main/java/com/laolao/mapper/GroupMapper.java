package com.laolao.mapper;

import com.laolao.pojo.vo.GroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMapper {

    @Select("""
            select gm.group_id as id, g.name, u.name advisor, g.description
            from group_member gm
                     join `group` g on gm.group_id = g.id
                     join user u on u.id = g.advisor_id
            where member_id = #{userId}
            """)
    List<GroupVO> selectSimpleGroup(Integer userId);
}
