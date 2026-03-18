package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.ExamRecord;
import com.laolao.pojo.vo.ExamRecordVO;
import com.laolao.pojo.vo.GradeMemberVO;
import com.laolao.pojo.vo.MemberReportVO;
import com.laolao.pojo.vo.ExamRecordIdAndUserIdVO;
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
            select er.id, u.name, er.score, e.id as exam_id, e.title, er.enter_time, er.submit_time, ai.content as ai_report
            from exam_record er
                     join user u on er.user_id = u.id
                     join exam e on e.id = er.exam_id
                     left join ai_report ai on ai.target_id = er.id
            where er.id = #{recordId};
            """)
    MemberReportVO selectMemberInfoByRecordId(Integer recordId);

    @Update("""
            UPDATE exam_record er -- 主表：考试记录表，别名er（简化书写）
                LEFT JOIN ( -- 关联「总分计算结果表」，即使没分数也能更新（给0分）
                    /* 第一层子查询：计算每个 exam_record 的总分（每题最高分之和） */
                    SELECT exam_record_id, SUM(max_score) as total_score
                    FROM (
                             /* 第二层子查询：取每个 record 下每道题的最高分 */
                             SELECT jr.exam_record_id, jr.question_id, MAX(jr.score) as max_score
                             FROM judge_record jr
                             JOIN exam_record er ON er.id = jr.exam_record_id
                             WHERE exam_id = #{examId} -- 只查当前考试的判分记录，缩小范围
                             GROUP BY jr.exam_record_id, jr.question_id) AS question_max_scores
                    GROUP BY exam_record_id) AS score_table ON er.id = score_table.exam_record_id
            
            SET er.score       = IFNULL(score_table.total_score, 0), -- 总分：有分数就用，没就给0
                er.status      = 1,                                  -- 状态改为「已交卷」（假设1=已交卷）
                er.submit_time = IFNULL(er.submit_time, NOW())       -- 交卷时间设为当前时间
            WHERE er.exam_id = #{examId}  -- 只处理当前考试的记录
            """)
    void batchSubmitAndCalculateScore(Integer examId);

    @Select("update exam_record set status = 1, submit_time = now() where id = #{recordId} and user_id = #{userId}")
    void updateStatusToSubmitted(Integer recordId, Integer userId);
}
