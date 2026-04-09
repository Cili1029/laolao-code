package com.laolao.service;

import reactor.core.publisher.Flux;

public interface AiTutorService {
    Flux<String> generateQuestionReport(Integer judgeRecordId);

    Flux<String> generateUserExamReport(Integer examId, Integer examRecordId);

    Flux<String> generateManagerExamReport(Integer examId);
}
