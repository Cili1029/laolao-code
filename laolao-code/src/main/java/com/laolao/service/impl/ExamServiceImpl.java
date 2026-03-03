package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.JudgeResult;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.common.util.StudentExamStatusCalculator;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.ExamService;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private JudgeService judgeService;
    @Resource
    private JudgeRecordMapper judgeRecordMapper;
    @Resource
    private MapStruct mapStruct;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private ExamQuestionConfigMapper examQuestionConfigMapper;

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

    @Override
    public Result<Integer> startExam(Integer examId) {
        // 查看记录里是否有正在考试的记录
        Integer userId = SecurityUtils.getUserId();
        ExamRecord examRecord = examRecordMapper.selectExamRecord(userId, examId);
        if (examRecord != null && examRecord.getStatus() == 0) {
            // 考试进行中，不用创建新记录，直接返回所记录的记录Id
            return Result.success(examRecord.getId());
        }
        ExamRecord record = ExamRecord.builder()
                .examId(examId)
                .userId(userId)
                .build();
        examRecordMapper.insert(record);
        return Result.success(record.getId());
    }

    @Override
    public Result<ExamBeginVO> getExamQuestion(Integer recordId) {
        // 获取这个记录所在的考试Id
        Integer examId = examRecordMapper.selectExamIdByRecordId(recordId);
        // 获取其题目以及当前学生每道题的分数
        List<ExamQuestionVO> examQuestionVOList = examMapper.selectQuestionById(examId, recordId, SecurityUtils.getUserId());
        ExamBeginVO examBeginVO = new ExamBeginVO();
        examBeginVO.setExamId(examId);
        examBeginVO.setQuestions(examQuestionVOList);
        return Result.success(examBeginVO);
    }

    @Override
    public Result<JudgeRecordVO> judge(JudgeDTO judgeDTO) {
        try {
            // 获取测试用例
            List<QuestionTestCase> questionTestCases = questionTestCaseMapper.selectList(
                    Wrappers.lambdaQuery(QuestionTestCase.class)
                            .eq(QuestionTestCase::getQuestionId, judgeDTO.getQuestionId()));
            // 获取这一题定的分值
            Integer score = examMapper.selectScoreByQuestionId(judgeDTO.getExamId(), judgeDTO.getQuestionId());
            JudgeResult judge = judgeService.judge(judgeDTO.getCode(), questionTestCases);
            // 填写分数
            if (judge.getStatus() == JudgeConstant.STATUS_AC) {
                judge.setScore(score);
            } else if (judge.getStatus() == JudgeConstant.STATUS_WA) {
                judge.setScore((score * judge.getPassTestCaseCount() / questionTestCases.size()));
            } else {
                judge.setScore(0);
            }
            // 转换写入记录提交表
            JudgeRecord judgeRecord = mapStruct.JudgeResultToJudgeRecord(judge);
            judgeRecord.setExamRecordId(judgeDTO.getRecordId());
            judgeRecord.setQuestionId(judgeDTO.getQuestionId());
            judgeRecord.setUserId(SecurityUtils.getUserId());
            judgeRecord.setAnswerCode(judgeDTO.getCode());
            judgeRecordMapper.insert(judgeRecord);
            // 转VO返回
            JudgeRecordVO judgeRecordVO = mapStruct.JudgeResultToJudgeRecordVO(judge);
            return Result.success(judgeRecordVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("判题失败！练习管理员！");
        }
    }

    @Override
    public Result<Integer> createExam(CreateExamDTO createExamDTO) {
        if (createExamDTO.getStartTime().isAfter(createExamDTO.getEndTime())) {
            return Result.error("开始时间应该在考试时间之前！");
        }
        Exam exam = Exam.builder()
                .title(createExamDTO.getTitle())
                .description(createExamDTO.getDescription())
                .advisorId(SecurityUtils.getUserId())
                .studyGroupId(createExamDTO.getStudyGroupId())
                .startTime(createExamDTO.getStartTime())
                .endTime(createExamDTO.getEndTime())
                .build();
        examMapper.insert(exam);
        return Result.success("创建成功", exam.getId());
    }

    @Override
    public Result<JudgeRecordVO> judgeTestCase(JudgeTestCaseDTO judgeTestCaseDTO) {
        try {
            JudgeResult judge = judgeService.judge(judgeTestCaseDTO.getCode(), List.of(judgeTestCaseDTO.getTestCase()));
            JudgeRecordVO judgeRecordVO = mapStruct.JudgeResultToJudgeRecordVO(judge);
            return Result.success(judgeRecordVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("判题失败！练习管理员！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> saveAndAddToExam(SaveAndAddToExamDTO saveAndAddToExamDTO) {
        AddQuestionDTO question = saveAndAddToExamDTO.getQuestion();
        Integer finalQuestionId; // 最终要绑定到考试里的题目ID（一定是一个子题ID）

        // 情况A：老师在考试界面点击了“新建题目”
        if (question.getId() == null) {
            // 先建祖宗
            question.setParentId(0);
            Result<Integer> ancestorResult = questionService.addOrUpdateQuestion(question);
            Integer ancestorId = ancestorResult.getData();

            // 繁衍子题（专供本次考试）
            question.setId(null); // 清空ID，让底层方法走insert
            question.setParentId(ancestorId); // 认祖宗
            Result<Integer> childResult = questionService.addOrUpdateQuestion(question);
            finalQuestionId = childResult.getData();
        } else {
            // 传了ID，需要去数据库查一下它的身份
            Question existingQuestion = questionMapper.selectById(question.getId());
            if (existingQuestion.getParentId() == 0) {
                // 情况B：老师从公共题库选了一道“祖宗题”加到考试里
                // 需要生成子题
                question.setId(null); // 清空原ID，强制走 insert 生成新题
                question.setParentId(existingQuestion.getId()); // 认祖宗
                Result<Integer> copyResult = questionService.addOrUpdateQuestion(question);
                finalQuestionId = copyResult.getData();
            } else {
                // 情况C：老师正在修改当前试卷里已经存在的“子题”
                // 直接更新
                Result<Integer> updateResult = questionService.addOrUpdateQuestion(question);
                finalQuestionId = updateResult.getData();
            }
        }

        // 把最终生成的子题ID（finalQuestionId）绑定到这场考试中
        // 有则更新分数，无则插入关联
        Integer examId = saveAndAddToExamDTO.getExamId();
        examMapper.insertOrUpdateQConfig(
                examId,
                finalQuestionId,
                question.getQuestionScore()
        );

        return Result.success("保存并写入考试成功！", finalQuestionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> removeQuestion(Integer examId, Integer questionId) {
        // 删除题目，题目测试示例，考试题目配置
        questionMapper.deleteById(questionId);
        questionTestCaseMapper.delete(
                new LambdaQueryWrapper<QuestionTestCase>()
                        .eq(QuestionTestCase::getQuestionId, questionId));
        examQuestionConfigMapper.delete(
                new LambdaQueryWrapper<ExamQuestionConfig>()
                        .eq(ExamQuestionConfig::getQuestionId, questionId));
        return Result.success("移除成功！");
    }

    @Override
    public Result<List<DraftQuestionVO>> getDraftQuestion(Integer examId) {
        List<DraftQuestionVO> draftQuestionVOS = examQuestionConfigMapper.selectDraftQuestion(examId);
        if (draftQuestionVOS == null || draftQuestionVOS.isEmpty()) {
            return Result.success(draftQuestionVOS);
        }
        // 提取 ID 并批量获取测试用例
        List<Integer> questionIdList = draftQuestionVOS.stream().map(DraftQuestionVO::getId).toList();
        List<QuestionTestCase> testCases = questionTestCaseMapper.selectBatchByQuestionIds(questionIdList);
        // 按questionId分组
        Map<Integer, List<QuestionTestCase>> testCaseMap = testCases.stream()
                .collect(Collectors.groupingBy(QuestionTestCase::getQuestionId));
        // 遍历题目列表，整合
        draftQuestionVOS.forEach(vo -> vo.setTestCases(testCaseMap.getOrDefault(vo.getId(), new ArrayList<>())));
        return Result.success(draftQuestionVOS);
    }
}
