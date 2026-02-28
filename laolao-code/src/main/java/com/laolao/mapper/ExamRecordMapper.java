package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.ExamRecord;
import com.laolao.pojo.vo.ExamRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    @Select("select exam_id from exam_record where id = #{recordId}")
    Integer selectExamIdByRecordId(Integer recordId);

    @Select("select id, status from exam_record where user_id = #{userId} and exam_id = #{examId}")
    ExamRecord selectExamRecord(Integer userId, Integer examId);

    @Select("""
            select er.id, concat(e.title,'考试报告') as name, g.name as studyGroup, er.enter_time as time
            from exam_record er
                     join exam e on e.id = er.exam_id
                     join study_group g on g.id = e.study_group_id
            where er.user_id = #{userId}
            """)
    List<ExamRecordVO> selectSimpleExamRecord(Integer userId);

    @Select("select status, enter_time, submit_time from exam_record where exam_id = #{examId} and user_id = #{userId}")
    ExamRecord selectStatusByExamId(Integer examId, Integer userId);
}
