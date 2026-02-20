package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.laolao.pojo.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("select test_cases from question where id = #{questionId}")
    @Results({@Result(column = "test_cases", property = "testCases", typeHandler = JacksonTypeHandler.class)})
    Question selectTestCaseById(Integer questionId);
}
