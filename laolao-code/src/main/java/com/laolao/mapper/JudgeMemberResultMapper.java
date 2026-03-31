package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.JudgeMemberResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JudgeMemberResultMapper extends BaseMapper<JudgeMemberResult> {
    @Insert("""
            INSERT INTO judge_member_result
            (exam_id, exam_record_id, user_id, question_id, best_judge_record_id, score, status, submit_time)
            VALUES (#{examId}, #{examRecordId}, #{userId}, #{questionId}, #{bestJudgeRecordId}, #{score}, #{status}, #{submitTime})
                       ON DUPLICATE KEY UPDATE
                       -- 只有当新分数大于等于当前最高分时，才更新指针和分数
                          best_judge_record_id = IF(VALUES(score) >= score, VALUES(best_judge_record_id), best_judge_record_id),
                          score = IF(VALUES(score) >= score, VALUES(score), score),
                          status = IF(VALUES(score) >= score, VALUES(status), status),
                          submit_time = IF(VALUES(score) >= score, VALUES(submit_time), submit_time)
            """)
    void updateResult(JudgeMemberResult result);
}
