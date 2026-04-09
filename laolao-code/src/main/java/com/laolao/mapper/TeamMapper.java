package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.dto.JoinTeamDTO;
import com.laolao.pojo.entity.Team;
import com.laolao.pojo.vo.DetailBaseTeamVO;
import com.laolao.pojo.vo.DetailExamTeamVO;
import com.laolao.pojo.vo.TeamVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    @Select("""
            select gm.team_id as id, g.name, u.name manager, g.description
            from team_user gm
                     join team g on gm.team_id = g.id
                     join user u on u.id = g.manager_id
            where user_id = #{userId} or g.manager_id = #{userId}
            """)
    List<TeamVO> selectUserSimpleTeam(Integer userId);

    @Select("""
            select g.id as id, g.name, u.name manager, g.description
            from team g
            join user u on u.id = g.manager_id
            where  g.manager_id = #{userId}
            """)
    List<TeamVO> selectManagerSimpleTeam(Integer userId);

    @Select("select id from team where invite_code = #{inviteCode}")
    Integer selectTeamByInviteCode(String inviteCode);

    @Select("select count(id) from team_user where team_id = #{teamId} and user_id = #{userId}")
    int selectCountByUserId(JoinTeamDTO joinTeamDTO);

    @Insert("insert into team_user(team_id, user_id) value (#{teamId}, #{userId})")
    void insertTeamUser(JoinTeamDTO joinTeamDTO);

    @Select("""
            select g.name, g.description, g.invite_code, u.username, u.name as managerName
            from team g
                     join user u on u.id = g.manager_id
            where g.id = #{teamId}
            """)
    DetailBaseTeamVO selectDetailBase(Integer teamId);

    @Select("select count(id) from team_user where team_id = #{teamId}")
    Integer selectUserCount(Integer teamId);

    @Select("""
            select distinct id, title, start_time, end_time, status
            from exam
            where team_id = #{teamId}
              and (status != 0 or manager_id = #{userId})
            order by start_time desc;
            """)
    List<DetailExamTeamVO> selectDetailExam(Integer teamId, Integer userId);

    @Select("""
            select count(gm.id)
            from team_user gm
                     join exam e on gm.team_id = e.team_id
            where e.id = #{examId}
            """)
    Integer selectUserCountByExamId(Integer examId);
}
