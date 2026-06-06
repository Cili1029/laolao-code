package com.laolao.ai.pojo.vo;

import com.laolao.pojo.vo.ExamSummaryPermissionsVO;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AiSimpleExamVO implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 考试标题
     */
    private String name;

    /**
     * 考试说明
     */
    private String description;

    /**
     * 所属组
     */
    private String teamName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 权限
     */
    private ExamSummaryPermissionsVO summaryPermissions;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 跳转链接
     */
    private String link;
}

