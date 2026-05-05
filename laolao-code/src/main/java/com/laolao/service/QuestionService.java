package com.laolao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.dto.SaveEditQuestionDTO;
import com.laolao.pojo.vo.*;

public interface QuestionService {
    Result<Integer> addOrUpdateQuestion(AddQuestionDTO addQuestionDTO);

    Result<Page<QuestionBankDialogVO>> getPrivateQuestions(Integer pageNum, Integer pageSize, String content);

    Result<Page<QuestionBankDialogVO>> getPublicQuestions(Integer pageNum, Integer pageSize, String content, Integer tagId, Integer isFavorite);

    Result<String> delete(Integer questionId);

    Result<DraftQuestionVO> copyQuestion(Integer questionId, Integer examId);

    Result<QuestionBankInfoVO> getSingleQuestionInfo(Integer questionId);

    Result<String> favorite(Integer questionId);

    Result<AdminQuestionSummaryVO> getSummary();

    Result<EditQuestionVO> getEditTarget(Integer questionId);

    Result<String> saveEdit(SaveEditQuestionDTO saveEditQuestionDTO);
}
