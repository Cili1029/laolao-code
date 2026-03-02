package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;

public interface QuestionService {
    Result<Integer> addQuestion(AddQuestionDTO addQuestionDTO);
}
