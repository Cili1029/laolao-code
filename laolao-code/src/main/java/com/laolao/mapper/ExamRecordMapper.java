package com.laolao.mapper;

import com.laolao.pojo.vo.ExamRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamRecordMapper {

    @Select("""
            select er.id, concat(e.title,'考试报告') as name, g.name `group`, er.start_time time, left(er.report, 50) as description
            from exam_record er
                     join exam e on e.id = er.exam_id
                     join `group` g on g.id = e.group_id
            where er.user_id = 2
            """)
    List<ExamRecordVO> selectSimpleExamRecord(Integer userId);
}
