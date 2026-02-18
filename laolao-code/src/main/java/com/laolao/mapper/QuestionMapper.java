package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laolao.pojo.entity.Question;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
