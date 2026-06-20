package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.UpdateScoreDTO;
import com.laolao.pojo.vo.GradeJudgeRecordVO;
import com.laolao.pojo.vo.GradeUserVO;

import java.util.List;

public interface GradeExamService {
    Result<List<GradeUserVO>> getGradeUser(Integer examId);

    Result<Integer> updateScore(UpdateScoreDTO updateScoreDTO);

    Result<Integer> graded(Integer examId);

    Result<List<GradeJudgeRecordVO>> getUserAnswer(Integer examRecordId);
}
