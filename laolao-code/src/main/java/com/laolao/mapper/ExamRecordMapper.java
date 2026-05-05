package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.ExamRecord;
import com.laolao.pojo.vo.*;
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
            select er.id, concat(e.title,'考试报告') as name, g.name as team, er.enter_time as time
            from exam_record er
                     join exam e on e.id = er.exam_id
                     join team g on g.id = e.team_id
            where er.user_id = #{userId} and e.status = 3
            """)
    List<ExamRecordVO> selectSimpleExamRecord(Integer userId);

    @Select("select id, user_id, status, enter_time, submit_time from exam_record where exam_id = #{examId} and user_id = #{userId}")
    UserExamRecord selectStatusByExamId(Integer examId, Integer userId);

    @Select("""
            select er.id, u.name, er.score, er.enter_time, er.submit_time
            from exam_record er
                     join user u on er.user_id = u.id
            where exam_id = #{examId};
            """)
    List<GradeUserVO> selectRecordIdAndUserByExamId(Integer examId);

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
    UserReportVO selectUserInfoByRecordId(Integer recordId);

    @Update("""
            UPDATE exam_record er
                LEFT JOIN (
                    SELECT exam_record_id, SUM(score) as total_score
                    FROM judge_user_result
                    WHERE exam_id = #{examId}
                    GROUP BY exam_record_id) AS score_table ON er.id = score_table.exam_record_id
            
            SET er.score       = IFNULL(score_table.total_score, 0), -- 总分计算
                er.status      = 1,                                  -- 1 = 已交卷
                er.submit_time = IFNULL(er.submit_time, NOW())       -- 如果学生没手动交卷，设为当前结束时间
            WHERE er.exam_id = #{examId}
            """)
    void batchSubmitAndCalculateScore(Integer examId);

    @Update("update exam_record set status = 1, submit_time = now() where id = #{recordId} and user_id = #{userId}")
    void updateStatusToSubmitted(Integer recordId, Integer userId);

    @Update("update exam_record set status = 3 where exam_id = #{examId}")
    void cancelExam(Integer examId);

    @Update("update exam_record set status = #{newStatus} where exam_id = #{examId} and status = #{oldStatus}")
    void updateStatus(Integer examId, int oldStatus, int newStatus);

    @Select("""
            SELECT u.name   AS name,
                   er.score AS score,
                   er.id    AS exam_record_id
            FROM
                -- 通过考试ID找到对应的小组
                exam e
                    -- 关联该小组的所有成员
                    JOIN team_user gm ON gm.team_id = e.team_id
                    -- 关联成员的用户信息
                    JOIN user u ON u.id = gm.user_id
                    -- 左连接该考试的成绩（缺考则score为NULL）
                    LEFT JOIN exam_record er ON er.user_id = gm.user_id AND er.exam_id = e.id
            -- 只需要指定考试ID，无需指定组ID
            WHERE e.id = #{examId}
            ORDER BY er.score DESC, er.score IS NULL
            """)
    List<ExamCompleteUserVO> selectUserJoinExamInfo(Integer examId);

    @Select("select id as exam_record_id, score from exam_record where exam_id = #{examId} and user_id = #{userId}")
    UserExamAnswerInfoVO selectUserExamScore(Integer examId, Integer userId);
}
