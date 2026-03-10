package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.pojo.ai.ExamQuestionDataContent;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.QuestionBankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    Page<QuestionBankVO> selectPrivateBank(Page<QuestionBankVO> page, Integer userId, String content);

    Page<QuestionBankVO> selectPublicBank(Page<QuestionBankVO> page, String content);

    @Update("update question set is_public = 1 - is_public where advisor_id = #{userId} and id = #{questionId}")
    void updateStatus(Integer userId, Integer questionId);

    @Update("update question set is_deleted = 1 where advisor_id = #{userId} and id = #{questionId}")
    void deleteQuestion(Integer userId, Integer questionId);

    @Select("""
            select id, title, content, difficulty, time_limit, memory_limit, template_code, standard_solution
            from question
            where id = #{questionId};
            """)
    DraftQuestionVO selectQuestionById(Integer questionId);

    void deleteDraft(List<Integer> questionIds);

    @Select("""
            select q.id as question_id, q.title, q.standard_solution
            from question q
                     join exam_question_config qc on qc.question_id = q.id
            where qc.exam_id = #{examId};
            """)
    List<ExamQuestionDataContent> selectQuestionsByExamId(Integer examId);
}
