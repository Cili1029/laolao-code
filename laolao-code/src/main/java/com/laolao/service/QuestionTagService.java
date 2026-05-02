package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.QuestionBankTagVO;

import java.util.List;

public interface QuestionTagService {
    Result<List<QuestionBankTagVO>> getTags();
}
