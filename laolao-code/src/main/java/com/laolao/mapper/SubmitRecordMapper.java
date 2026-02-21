package com.laolao.mapper;

import com.laolao.pojo.vo.SimpleSubmitRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubmitRecordMapper {
    @Select("select id, status, score, time, memory from question_submit_record where exam_record_id = #{examRecordId}")
    List<SimpleSubmitRecordVO> selectSimpleSubmitRecord(Integer examRecordId);
}
