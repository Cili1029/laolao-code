package com.laolao.common.util;

import com.laolao.ai.pojo.dto.RedisMessageDTO;
import com.laolao.ai.pojo.vo.AiSimpleExamRecordVO;
import com.laolao.ai.pojo.vo.AiSimpleExamVO;
import com.laolao.ai.pojo.vo.AiSimpleTeamVO;
import com.laolao.ai.pojo.vo.RedisMessageVO;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.dto.SaveEditQuestionDTO;
import com.laolao.pojo.dto.UpdateDraftDTO;
import com.laolao.pojo.entity.Exam;
import com.laolao.common.result.JudgeResult;
import com.laolao.pojo.entity.JudgeRecord;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStruct {

    Question saveEditQuestionDTOToQuestion(SaveEditQuestionDTO saveEditQuestionDTO);

    JudgeRecordVO judgeRecordToJudgeRecordVO(JudgeRecord judge);

    Question addQuestionDTOtoQuestion(AddQuestionDTO addQuestionDTO);

    Exam updateDraftDTOToExam(UpdateDraftDTO draftDTO);

    DraftQuestionVO questionToDraftQuestionVO(Question question);

    JudgeRecordVO judgeResultToJudgeRecordVO(JudgeResult judgeResult);

    List<RedisMessageVO> messageDTOSToMessageVOS(List<RedisMessageDTO> messageDTOS);

    @Mapping(target = "link", expression = "java(source.getId() == null ? \"\" : \"http://localhost:5173/my-team/\" + source.getId())")
    AiSimpleTeamVO teamVOToAiSimpleTeamVO(TeamVO source);

    List<AiSimpleTeamVO> teamVOToAiSimpleTeamVO(List<TeamVO> list);

    @Mapping(target = "link", expression = "java(source.getId() == null ? \"\" : \"http://localhost:5173/exam/\" + source.getId())")
    AiSimpleExamVO examVOToAiSimpleExamVO(ExamVO source);

    List<AiSimpleExamVO> examVOToAiSimpleExamVO(List<ExamVO> examVOList);

    @Mapping(target = "link", expression = "java(source.getId() == null ? \"\" : \"http://localhost:5173/user-report/\" + source.getId())")
    AiSimpleExamRecordVO ExamRecordVOToAiSimpleExamRecordVO(ExamRecordVO source);

    List<AiSimpleExamRecordVO> ExamRecordVOToAiSimpleExamRecordVO(List<ExamRecordVO> examRecordVOList);
}
