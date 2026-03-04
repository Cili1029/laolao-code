package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.ExamQuestionConfig;
import com.laolao.pojo.vo.DraftQuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamQuestionConfigMapper extends BaseMapper<ExamQuestionConfig> {
    @Select("""
            select q.id,
                   q.title,
                   q.content,
                   eqc.score as question_score,
                   q.difficulty,
                   q.time_limit,
                   q.memory_limit,
                   q.template_code,
                   q.standard_solution
            from question q
                     join exam_question_config eqc on eqc.question_id = q.id
            where exam_id = #{examId};
            """)
    List<DraftQuestionVO> selectDraftQuestion(Integer examId);
}
