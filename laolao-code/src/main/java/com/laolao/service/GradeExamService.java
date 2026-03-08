package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.dto.UpdateScoreDTO;
import com.laolao.pojo.vo.GradeMemberVO;

import java.util.List;

public interface GradeExamService {
    Result<List<GradeMemberVO>> getGradeMember(Integer examId);

    Result<Integer> updateScore(UpdateScoreDTO updateScoreDTO);

    Result<Integer> graded(Integer examId);
}
