package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.ExamRecord;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.GradeMemberVO;
import com.laolao.pojo.vo.MemberReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    @Select("select exam_id, status from exam_record where id = #{recordId}")
    ExamRecord selectExamByRecordId(Integer recordId);

    @Select("select id, status from exam_record where user_id = #{userId} and exam_id = #{examId}")
    ExamRecord selectExamRecord(Integer userId, Integer examId);

    @Select("""
            select er.id, concat(e.title,'考试报告') as name, g.name as studyGroup, er.enter_time as time
            from exam_record er
                     join exam e on e.id = er.exam_id
                     join study_group g on g.id = e.study_group_id
            where er.user_id = #{userId} and e.status = 3
            """)
    List<ExamRecordVO> selectSimpleExamRecord(Integer userId);

    @Select("select status, enter_time, submit_time from exam_record where exam_id = #{examId} and user_id = #{userId}")
    ExamRecord selectStatusByExamId(Integer examId, Integer userId);

    @Update("update exam_record set status = 1, score = #{score}, submit_time = now() where id = #{recordId} and user_id = #{userId}")
    void submitExam(Integer recordId, Integer userId, Integer score);

    @Select("""
            select er.id, u.name, er.score, er.enter_time, er.submit_time
            from exam_record er
                     join user u on er.user_id = u.id
            where exam_id = #{examId};
            """)
    List<GradeMemberVO> selectRecordIdAndMemberByExamId(Integer examId);

    @Update("update exam_record set score = score + #{diffScore} where id = #{examRecordId}")
    void updateScoreByDiff(Integer examRecordId, Integer diffScore);

    @Select("""
            select er.id, u.name, er.score, e.id as exam_id, e.title, er.enter_time, er.submit_time
                        from exam_record er
                                 join user u on er.user_id = u.id
                                 join exam e on e.id = er.exam_id
                        where er.id = #{recordId};
            """)
    MemberReportVO selectMemberInfoByRecordId(Integer recordId);
}
