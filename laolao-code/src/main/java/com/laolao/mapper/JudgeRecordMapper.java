package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.ai.JudgeRecordContext;
import com.laolao.pojo.ai.UserAnswerDataContent;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.vo.GradeJudgeRecordVO;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.vo.UserExamJudgeRecordVO;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JudgeRecordMapper extends BaseMapper<JudgeRecord> {
    @Select("select id, status, score, time, memory from judge_record where exam_record_id = #{examRecordId} and question_id = #{questionId} order by submit_time desc")
    List<SimpleJudgeRecordVO> selectSimpleJudgeRecord(Integer examRecordId, Integer questionId);

    @Select("select status, score, stdout, stderr, question_test_case_id, time, memory from judge_record where id = #{judgeRecordId}")
    JudgeRecordVO selectDetailJudgeRecord(Integer judgeRecordId);

    @Select("""
            SELECT id, title, total_score, user_score, answer_code, status
            FROM (SELECT jr.id, q.title, qc.score as total_score, jr.score as user_score, jr.answer_code, jr.status,
                         -- 按question_id分组，按status优先级+提交时间降序排序，标记每条记录的优先级
                         ROW_NUMBER() OVER (
                             PARTITION BY jr.question_id
                             ORDER BY FIELD(jr.status, 0, 1, 2, 3, 4, 5, 6, 7), jr.submit_time DESC
                             ) AS rn -- rn=1 就是该题优先级最高的记录
                  FROM judge_record jr
                           JOIN question q ON jr.question_id = q.id
                           JOIN exam_question_config qc ON qc.question_id = jr.question_id
                  WHERE jr.exam_record_id = #{recordId} -- 指定考生本次考试
                 ) AS temp
            WHERE temp.rn = 1 -- 只取每个题的第一条（优先级最高）
            """)
    List<GradeJudgeRecordVO> selectGradeInfoByRecordId(Integer recordId);

    @Select("select score from judge_record where id = #{judgeRecordId}")
    Integer selectScoreById(Integer judgeRecordId);

    @Select("""
            
            SELECT jmr.best_judge_record_id as id,
                   q.title,
                   qc.score   as total_score,
                   jr.score   as user_score,
                   q.standard_solution,
                   jr.answer_code,
                   jr.status,
                   ai.content as ai_report
            FROM judge_user_result jmr
                     join judge_record jr on jr.id = jmr.best_judge_record_id
                     join question q on q.id = jmr.question_id
                     join exam_question_config qc on qc.question_id = jr.question_id
                     left join ai_report ai on ai.target_id = jmr.best_judge_record_id and ai.target_type = 1
            where jmr.exam_record_id = #{recordId}
            """)
    List<UserExamJudgeRecordVO> selectUserExamReportByRecordId(Integer recordId);

    @Select("""
            select q.title, jr.answer_code, q.standard_solution
            from judge_record jr
                     join question q on jr.question_id = q.id
            where jr.id = #{judgeRecordId}
            """)
    JudgeRecordContext selectUserJudgeInfoToAi(Integer judgeRecordId);

    @Select("""
            SELECT q.title, jr.answer_code, q.standard_solution
            FROM question q
                     JOIN exam_question_config eqc ON eqc.question_id = q.id
                     LEFT JOIN judge_user_result jmr ON jmr.question_id = q.id AND jmr.user_id = #{userId}
                     LEFT JOIN judge_record jr ON jr.id = jmr.best_judge_record_id
            WHERE eqc.exam_id = #{examId};
            """)
    List<JudgeRecordContext> selectUserExamInfoToAi(Integer examId, Integer userId);

    @Select("""
            SELECT u.name AS username,
                   jmr.question_id,
                   jr.answer_code
            FROM exam e
                     JOIN exam_record er ON er.exam_id = e.id
                     JOIN judge_user_result jmr ON jmr.exam_record_id = er.id
                     JOIN judge_record jr ON jr.id = jmr.best_judge_record_id
                     JOIN user u ON u.id = er.user_id
            WHERE e.id = #{examId};
            """)
    List<UserAnswerDataContent> selectUserAnswersByExamId(Integer examId);
}
