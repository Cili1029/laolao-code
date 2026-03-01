package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.dto.JoinStudyGroupDTO;
import com.laolao.pojo.entity.StudyGroup;
import com.laolao.pojo.vo.DetailBaseStudyGroupVO;
import com.laolao.pojo.vo.DetailExamStudyGroupVO;
import com.laolao.pojo.vo.StudyGroupVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyGroupMapper extends BaseMapper<StudyGroup> {

    @Select("""
            select gm.study_group_id as id, g.name, u.name advisor, g.description
            from study_group_member gm
                     join study_group g on gm.study_group_id = g.id
                     join user u on u.id = g.advisor_id
            where member_id = #{userId} or g.advisor_id = #{userId}
            """)
    List<StudyGroupVO> selectStudentSimpleGroup(Integer userId);

    @Select("""
            select g.id as id, g.name, u.name advisor, g.description
            from study_group g
            join user u on u.id = g.advisor_id
            where  g.advisor_id = #{userId}
            """)
    List<StudyGroupVO> selectAdvisorSimpleGroup(Integer userId);

    @Select("select id from study_group where invite_code = #{inviteCode}")
    Integer selectGroupByInviteCode(String inviteCode);

    @Select("select count(id) from study_group_member where study_group_id = #{studyGroupId} and member_id = #{memberId}")
    int selectCountByMemberId(JoinStudyGroupDTO joinStudyGroupDTO);

    @Insert("insert into study_group_member(study_group_id, member_id) value (#{studyGroupId}, #{memberId})")
    void insertGroupMember(JoinStudyGroupDTO joinStudyGroupDTO);

    @Select("""
            select g.name, g.description, g.invite_code, u.username, u.name as advisorName
            from study_group g
                     join user u on u.id = g.advisor_id
            where g.id = #{studyGroupId}
            """)
    DetailBaseStudyGroupVO selectDetailBase(Integer studyGroupId);

    @Select("select count(id) from study_group_member where study_group_id = #{studyGroupId}")
    Integer selectMemberCount(Integer studyGroupId);

    @Select("select id, title, start_time, end_time from exam where study_group_id = #{studyGroupId} order by start_time desc")
    List<DetailExamStudyGroupVO> selectDetailExam(Integer studyGroupId);


}
