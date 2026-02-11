package com.laolao.mapper;

import com.laolao.pojo.entity.Exam;
import com.laolao.pojo.entity.ExamRecord;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamQuestionVO;
import com.laolao.pojo.vo.ExamVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamMapper {

    @Select("""
            select e.id, e.title as name, e.description, g.name `group`, e.start_time time
            from exam e
                     join group_member gm on gm.group_id = e.group_id
                     join `group` g on gm.group_id = g.id
                     join user u on u.id = g.advisor_id
            where gm.member_id = 2;
            """)
    List<ExamVO> selectSimpleExam(Integer userId);

    @Select("""
            select e.id, e.title, e.description, g.name as `group`, JSON_LENGTH(e.questions) as questions, e.start_time, e.end_time
            from exam e
                     join `group` g on g.id = e.group_id
            where e.id = #{examId};
            """)
    ExamInfoVO selectExamInfo(Integer examId);

    @Select("select id, status from exam_record where user_id = #{userId} and exam_id = #{examId}")
    ExamRecord selectExamRecord(Integer userId, Integer examId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into exam_record(exam_id, user_id) value (#{examId}, #{userId})")
    void insertRecord(ExamRecord record);

    Exam selectExamByRecordId(Integer recordId);

    List<ExamQuestionVO> selectQuestionById(List<Integer> questionIds);
}
