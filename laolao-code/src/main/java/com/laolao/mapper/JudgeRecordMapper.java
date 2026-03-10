package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.ai.JudgeRecordContext;
import com.laolao.pojo.ai.MemberAnswerDataContent;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.vo.GradeJudgeRecordVO;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.vo.MemberExamJudgeRecordVO;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface JudgeRecordMapper extends BaseMapper<JudgeRecord> {
    @Select("select id, status, score, time, memory from judge_record where exam_record_id = #{examRecordId} and question_id = #{questionId} order by submit_time desc")
    List<SimpleJudgeRecordVO> selectSimpleJudgeRecord(Integer examRecordId, Integer questionId);

    @Select("select status, score, stdout, stderr, question_test_case_id, time, memory from judge_record where id = #{judgeRecordId}")
    JudgeRecordVO selectDetailJudgeRecord(Integer judgeRecordId);

    @Select("""
            SELECT id, title, total_score, member_score, answer_code, status
            FROM (SELECT jr.id, q.title, qc.score as total_score, jr.score as member_score, jr.answer_code, jr.status,
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

    @Select("""
            SELECT SUM(max_score) AS score
            FROM (SELECT MAX(score) AS max_score
                  FROM judge_record
                  WHERE exam_record_id = #{recordId}
                  GROUP BY question_id) AS question_max_scores;
            """)
    Integer selectTotalScore(Integer recordId);

    @Select("select score from judge_record where id = #{judgeRecordId}")
    Integer selectScoreById(Integer judgeRecordId);

    @Select("""
            SELECT jr.id, q.title, qc.score as total_score, jr.score as member_score,
                   q.standard_solution, jr.answer_code, jr.status, ai.content as ai_report
            FROM judge_record jr
                     JOIN question q ON jr.question_id = q.id
                     JOIN exam_question_config qc ON qc.question_id = jr.question_id
                     LEFT JOIN ai_report ai on ai.target_id = jr.id
            WHERE jr.exam_record_id = #{recordId} AND jr.is_best = 1
            """)
    List<MemberExamJudgeRecordVO> selectMemberExamReportByRecordId(Integer recordId);

    @Select("""
            select q.title, jr.answer_code, q.standard_solution
            from judge_record jr
                     join question q on jr.question_id = q.id
            where jr.id = #{judgeRecordId}
            """)
    JudgeRecordContext selectMemberJudgeInfoToAi(Integer judgeRecordId);

    @Update("""
            UPDATE judge_record jr
                INNER JOIN (SELECT id
                            FROM (SELECT id,
                                         -- 按题目分组
                                         -- 排序规则：1.分数降序 2.状态码升序(0优先) 3.提交时间降序
                                         ROW_NUMBER() OVER (
                                             PARTITION BY question_id
                                             ORDER BY score DESC, status , submit_time DESC
                                             ) AS rn
                                  FROM judge_record
                                  WHERE exam_record_id = #{examRecordId}
                                 ) AS temp
                            WHERE temp.rn = 1) best_records ON jr.id = best_records.id
            SET jr.is_best = 1
            WHERE jr.exam_record_id = #{examRecordId}
            """)
    void updateBestRecord(Integer recordId);

    @Select("""
            SELECT q.title, jr.answer_code, q.standard_solution
            FROM question q
                     JOIN exam_question_config eq ON eq.question_id = q.id
                     LEFT JOIN judge_record jr ON jr.question_id = q.id
                AND jr.is_best = 1
                AND jr.user_id = #{userId}
            WHERE eq.exam_id = #{examId};
            """)
    List<JudgeRecordContext> selectMemberExamInfoToAi(Integer examId, Integer userId);

    @Select("""
            SELECT u.name AS username,
                   q.id   AS question_id,
                   jr.answer_code
            FROM exam e
                     JOIN exam_record er ON er.exam_id = e.id
                     JOIN judge_record jr ON jr.exam_record_id = er.id
                     JOIN question q ON q.id = jr.question_id
                     JOIN user u ON u.id = er.user_id
            WHERE e.id = 10
              AND jr.is_best = 1;
            """)
    List<MemberAnswerDataContent> selectMemberAnswersByExamId(Integer examId);
}
