package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateScoreDTO {
    /**
     * 考试纪录Id
     */
    private Integer examRecordId;
    /**
     * 代码纪录Id
     */
    private Integer judgeRecordId;

    /**
     * 分数
     */
    private Integer score;
}
