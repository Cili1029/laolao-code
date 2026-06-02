package com.laolao.ai.service;

import com.laolao.ai.pojo.vo.GetOldSessionVO;
import com.laolao.common.result.Result;
import reactor.core.publisher.Flux;

public interface ChatService {
    Flux<String> chat(String userInput);

    Result<GetOldSessionVO> getOldSession();

    Result<String> clear();
}
