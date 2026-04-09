package com.laolao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.dto.QuestionIdDTO;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.QuestionBankVO;

public interface QuestionService {
    Result<Integer> addOrUpdateQuestion(AddQuestionDTO addQuestionDTO);

    Result<Page<QuestionBankVO>> getPrivateQuestions(Integer pageNum, Integer pageSize, String content);

    Result<Page<QuestionBankVO>> getPublicQuestions(Integer pageNum, Integer pageSize, String content);

    Result<String> changeStatus(QuestionIdDTO questionIdDTO);

    Result<String> delete(Integer questionId);

    Result<DraftQuestionVO> copyQuestion(Integer questionId, Integer examId);
}
