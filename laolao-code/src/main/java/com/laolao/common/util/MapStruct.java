package com.laolao.common.util;

import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.common.result.JudgeResult;
import com.laolao.pojo.vo.JudgeRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStruct {
    JudgeRecord JudgeResultToJudgeRecord(JudgeResult judge);

    JudgeRecordVO JudgeResultToJudgeRecordVO(JudgeResult judge);
}
