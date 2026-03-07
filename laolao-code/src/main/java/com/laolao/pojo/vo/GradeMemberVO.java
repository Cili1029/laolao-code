package com.laolao.pojo.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GradeMemberVO implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 名字
     */
    private String name;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 考生进入时间
     */
    private LocalDateTime enterTime;

    /**
     * 学生提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 作答情况
     */
    private List<GradeJudgeRecordVO> judgeRecords;
}