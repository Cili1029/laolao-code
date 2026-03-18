package com.laolao.pojo.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamRecordIdAndUserIdVO {
    /**
     * 考试记录Id
     */
    private Integer examRecordId;

    /**
     * 用户Id
     */
    private Integer userId;
}
