package com.laolao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.pojo.vo.JudgeRecordVO;
import com.laolao.pojo.vo.SimpleJudgeRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionTestCaseMapper extends BaseMapper<QuestionTestCase> {
}
