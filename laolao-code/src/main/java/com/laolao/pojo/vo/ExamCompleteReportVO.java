package com.laolao.pojo.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExamCompleteReportVO {
    /**
     * ai报告
     */
    public String aiReport;

    /**
     * 组学生
     */
    public List<ExamCompleteUserVO> userList;
}
