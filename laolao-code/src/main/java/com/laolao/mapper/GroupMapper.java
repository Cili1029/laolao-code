package com.laolao.mapper;

import com.laolao.pojo.dto.JoinGroupDTO;
import com.laolao.pojo.vo.DetailBaseGroupVO;
import com.laolao.pojo.vo.GroupVO;
import org.apache.ibatis.annotations.Insert;
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

    @Select("select id from `group` where invite_code = #{inviteCode}")
    Integer selectGroupByInviteCode(String inviteCode);

    @Select("select count(id) from group_member where group_id = #{groupId} and member_id = #{memberId}")
    int selectCountBymemberId(JoinGroupDTO joinGroupDTO);

    @Insert("insert into group_member(group_id, member_id) value (#{groupId}, #{memberId})")
    void insertGroupMember(JoinGroupDTO joinGroupDTO);

    @Select("""
            select g.name, g.description, u.username, u.name as advisorName
            from `group` g
                     join user u on u.id = g.advisor_id
            where g.id = #{groupId}
            """)
    DetailBaseGroupVO selectDetailBase(Integer groupId);

    @Select("select count(id) from group_member where group_id = #{groupId}")
    Integer selectMemberCount(Integer groupId);
}
