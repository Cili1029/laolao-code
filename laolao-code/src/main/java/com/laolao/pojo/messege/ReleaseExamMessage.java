package com.laolao.pojo.messege;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseExamMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer advisorId;
    private Integer examId;
    private List<Integer> questionIds;
}
