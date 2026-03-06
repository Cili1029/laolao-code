package com.laolao.service.impl;

import com.laolao.common.result.Result;
import com.laolao.common.util.SecurityUtils;
import com.laolao.common.util.StudentExamStatusCalculator;
import com.laolao.mapper.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.ExamQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExamQueryQueryServiceImpl implements ExamQueryService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;

    @Override
    public Result<List<ExamVO>> getSimpleExam() {
        Integer userId = SecurityUtils.getUserId();
        List<ExamVO> examVOList = examMapper.selectSimpleExam(userId);
        return Result.success(examVOList);
    }

    @Override
    public Result<ExamInfoVO> getExamInfo(Integer examId) {
        ExamInfoVO examInfoVO = examMapper.selectExamInfo(examId);
        // 如果是导师
        if (SecurityUtils.hasAuthority("ADVISOR")) {
            // 不用填写时间和学生状态，直接返回
            return Result.success(examInfoVO);
        }
        // 查询考生是否已经进入，顺便获取进入时间和交卷时间
        ExamRecord examRecord = examRecordMapper.selectStatusByExamId(examId, SecurityUtils.getUserId());
        if (examRecord != null) {
            examInfoVO.setEnterTime(examRecord.getEnterTime());
            examInfoVO.setSubmitTime(examRecord.getSubmitTime());
        }
        // 设置当前状态
        examInfoVO.setStudentStatus(StudentExamStatusCalculator.calculate(examInfoVO.getStartTime(), examInfoVO.getEndTime(),
                examInfoVO.getEnterTime(), examInfoVO.getSubmitTime()));
        return Result.success(examInfoVO);
    }
}
