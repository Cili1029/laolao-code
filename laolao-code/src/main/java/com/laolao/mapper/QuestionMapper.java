package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.pojo.ai.ExamQuestionDataContent;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.vo.QuestionBankDialogTagVO;
import com.laolao.pojo.vo.QuestionBankDialogVO;
import com.laolao.pojo.vo.QuestionBankInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    Page<QuestionBankDialogVO> selectPrivateBank(Page<QuestionBankDialogVO> page, Integer userId, String content);

    Page<QuestionBankDialogVO> selectPublicBank(Page<QuestionBankDialogVO> page, String content, Integer tagId, Integer userId, Integer isFavorite);

    @Update("update question set is_deleted = 1 where question.creator_id = #{userId} and id = #{questionId}")
    void deleteQuestion(Integer userId, Integer questionId);

    void deleteDraft(List<Integer> questionIds);

    @Select("""
            select q.id as question_id, q.title, q.standard_solution
            from question q
                     join exam_question_config qc on qc.question_id = q.id
            where qc.exam_id = #{examId};
            """)
    List<ExamQuestionDataContent> selectQuestionsByExamId(Integer examId);

    @Select("""
            select title, content, difficulty,
                   time_limit, memory_limit,
                   template_code, standard_solution,
                   id as parent_id, is_validated
            from question
            where id = #{questionId}
            """)
    Question selectCopyQuestion(Integer questionId);

    List<QuestionBankDialogTagVO> selectTags(List<Integer> questionIds);

    @Select("select id, title, content, difficulty, time_limit, memory_limit, standard_solution from question where id = #{questionId}")
    QuestionBankInfoVO selectQuestionInfo(Integer questionId);
}
