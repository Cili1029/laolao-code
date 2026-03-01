package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.Exam;
import com.laolao.pojo.vo.ExamInfoVO;
import com.laolao.pojo.vo.ExamQuestionVO;
import com.laolao.pojo.vo.ExamVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    @Select("""
            select e.id, e.title as name, e.description, g.name study_group, e.start_time time
            from exam e
                     join study_group_member gm on gm.study_group_id = e.study_group_id
                     join study_group g on gm.study_group_id = g.id
                     join user u on u.id = g.advisor_id
            where gm.member_id = #{userId};
            """)
    List<ExamVO> selectSimpleExam(Integer userId);


    @Select("""
            select e.id,
                   e.title,
                   e.description,
                   g.name              as study_group,
                   (select count(*)
                    from exam_question_config
                    where exam_id = #{examId}) as questions,
                   e.start_time,
                   e.end_time
            from exam e
                     join study_group g
                          on g.id = e.study_group_id
            where e.id = #{examId};
            """)
    ExamInfoVO selectExamInfo(Integer examId);


    @Select("""
            select q.id,
                   e.score                 as question_score,
                   ifnull(max(j.score), 0) as user_score,
                   q.title,
                   q.content,
                   q.difficulty,
                   q.template_code
            from question q
                     join exam_question_config e on q.id = e.question_id
                     left join judge_record j on j.question_id = q.id and j.user_id = #{userId} and j.exam_record_id = #{recordId}
            where e.exam_id = #{examId}
            Group by q.id, question_score, q.title, q.content, q.difficulty, q.template_code;
            """)
    List<ExamQuestionVO> selectQuestionById(Integer examId, Integer recordId, Integer userId);

    @Select("""
            select score
            from exam_question_config
            where exam_id = #{examId} and question_id = #{questionId};
            """)
    Integer selectScoreByQuestionId(Integer examId, Integer questionId);
}
