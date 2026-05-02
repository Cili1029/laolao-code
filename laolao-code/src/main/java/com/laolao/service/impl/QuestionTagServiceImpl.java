package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.mapper.QuestionTagMapper;
import com.laolao.pojo.vo.QuestionBankTagVO;
import com.laolao.service.QuestionTagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionTagServiceImpl implements QuestionTagService {
    @Resource
    private QuestionTagMapper questionTagMapper;

    @Override
    public Result<List<QuestionBankTagVO>> getTags() {
        List<QuestionBankTagVO> questionBankDialogTagVOS = questionTagMapper.selectAllTags();
        return Result.success(questionBankDialogTagVOS);
    }
}
