package com.laolao.common.util;

import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.entity.JudgeResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStruct {
    JudgeRecord JudgeResultToJudgeRecord(JudgeResult judge);
}
