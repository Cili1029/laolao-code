package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.Exam;
import com.laolao.pojo.entity.ExamQuestionConfig;
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
                     join study_group g on g.id = e.study_group_id
            where
               -- 条件1：成员且状态为已发布
                exists (select 1
                        from study_group_member gm
                        where gm.study_group_id = e.study_group_id
                          and gm.member_id = #{userId}) and e.status = 1
               -- 条件2：导师，全部其创建的考试
               or e.advisor_id = #{userId};
            """)
    List<ExamVO> selectSimpleExam(Integer userId);


    @Select("""
            select e.id,
                   e.status,
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

    @Update("update exam_question_config set score = #{score} where exam_id = #{examId} and question_id = #{questionId}")
    void updateScore(Integer examId, Integer questionId, Integer score);

    @Insert("""
            INSERT INTO exam_question_config (exam_id, question_id, score)
            VALUES (#{examId}, #{questionId}, #{score})
            ON DUPLICATE KEY UPDATE score = VALUES(score)
            """)
    void insertOrUpdateQConfig(Integer examId, Integer questionId, Integer score);
}
