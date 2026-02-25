package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.vo.JudgeRecordVO;
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
}
