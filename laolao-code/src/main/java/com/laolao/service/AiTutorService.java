package com.laolao.service;

import reactor.core.publisher.Flux;

public interface AiTutorService {
    Flux<String> generateQuestionReport(Integer recordId);

    Flux<String> generateMemberExamReport(Integer examId);
}
