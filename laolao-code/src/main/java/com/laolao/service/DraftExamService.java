package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.CreateExamDTO;
import com.laolao.pojo.dto.JudgeTestCaseDTO;
import com.laolao.pojo.dto.SaveAndAddToExamDTO;
import com.laolao.pojo.dto.UpdateDraftDTO;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.JudgeRecordVO;

import java.util.List;

public interface DraftExamService {

    Result<Integer> createExam(CreateExamDTO createExamDTO);

    Result<JudgeRecordVO> judgeTestCase(JudgeTestCaseDTO judgeTestCaseDTO);

    Result<Integer> saveAndAddToExam(SaveAndAddToExamDTO saveAndAddToExamDTO);

    Result<String> removeQuestion(Integer examId, Integer questionId);

    Result<List<DraftQuestionVO>> getDraftQuestion(Integer examId);

    Result<Integer> releaseExam(Integer examId);

    Result<String> deleteDraft(Integer examId);

    Result<Integer> updateDraft(UpdateDraftDTO draftDTO);
}
