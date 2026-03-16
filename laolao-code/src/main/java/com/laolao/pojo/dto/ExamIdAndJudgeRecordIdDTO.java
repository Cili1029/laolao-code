package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExamIdAndJudgeRecordIdDTO {
    /**
     * 考试ID
     */
    private Integer examId;

    /**
     * 判题记录ID
     */
    private Integer judgeRecordId;
}
