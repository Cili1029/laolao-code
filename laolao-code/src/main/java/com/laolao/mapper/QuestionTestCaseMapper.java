package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.QuestionTestCase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionTestCaseMapper extends BaseMapper<QuestionTestCase> {
    void insertBatch(List<QuestionTestCase> testCases);

    List<QuestionTestCase> selectBatchByQuestionIds(List<Integer> questionIds);

    void deleteDraft(List<Integer> questionIds);

    @Select("select input, output from question_test_case where question_id = #{questionId}")
    List<QuestionTestCase> selectCopyTestCase(Integer questionId);
}
