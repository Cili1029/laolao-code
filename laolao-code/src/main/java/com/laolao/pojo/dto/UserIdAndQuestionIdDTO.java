package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserIdAndQuestionIdDTO {
    /**
     * 题目Id
     */
    private Integer questionId;

    /**
     * 用户Id
     */
    private Integer judgeRecordId;
}
