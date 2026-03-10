package com.laolao.service;

import reactor.core.publisher.Flux;

public interface AiTutorService {
    Flux<String> generateQuestionReport(Integer judgeRecordId);

    Flux<String> generateMemberExamReport(Integer examId, Integer examRecordId);

    Flux<String> generateAdvisorExamReport(Integer examId);
}
