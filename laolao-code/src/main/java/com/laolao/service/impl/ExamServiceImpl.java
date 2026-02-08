package com.laolao.service.impl;

import com.laolao.common.context.UserContext;
import com.laolao.common.result.Result;
import com.laolao.mapper.ExamMapper;
import com.laolao.pojo.vo.ExamVO;
import com.laolao.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
    @Resource
    private ExamMapper examMapper;

    @Override
    public Result<List<ExamVO>> getSimpleExam() {
        Integer userId = UserContext.getCurrentId();
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        return Result.success(examVOList);
    }
}
