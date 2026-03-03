package com.laolao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.vo.QuestionBankVO;

public interface QuestionService {
    Result<Integer> addOrUpdateQuestion(AddQuestionDTO addQuestionDTO);

    Result<Page<QuestionBankVO>> getPrivateQuestions(Integer pageNum, Integer pageSize);

    Result<Page<QuestionBankVO>> getPublicQuestions(Integer pageNum, Integer pageSize);
}
