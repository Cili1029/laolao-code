package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.laolao.common.constant.ExamConstant;
import com.laolao.common.constant.JudgeConstant;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.JudgeResult;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.vo.*;
import com.laolao.service.DraftExamService;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DraftExamServiceImpl implements DraftExamService {
    @Resource
    private ExamMapper examMapper;
    @Resource
    private JudgeService judgeService;
    @Resource
    private MapStruct mapStruct;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private ExamQuestionConfigMapper examQuestionConfigMapper;

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
        setTestCasesToQuestions(draftQuestionVOS, DraftQuestionVO::getId, DraftQuestionVO::setTestCases);
        return Result.success(draftQuestionVOS);
    }

    @Override
    @Transactional
    public Result<Integer> releaseExam(Integer examId) {
        // 基础校验
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }
        if (exam.getStatus() == ExamConstant.PUBLISHED) {
            return Result.error("考试已发布，请勿重复操作");
        }
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime().isBefore(now)) {
            return Result.error("考试开始时间不能早于当前时间");
        }
        if (exam.getStartTime().isAfter(exam.getEndTime())) {
            return Result.error("开始时间必须早于结束时间");
        }

        // 分数校验
        List<ReleaseExamQuestionVO> questions = examMapper.selectQuestionsCodeByExamId(examId);
        if (questions == null || questions.isEmpty()) {
            return Result.error("考试至少需要包含一道题目");
        }
        // 判断分数是否是100 后期可以自定义
        int totalScore = questions.stream()
                .filter(Objects::nonNull)
                .filter(q -> Objects.nonNull(q.getScore()))
                .mapToInt(ReleaseExamQuestionVO::getScore)
                .sum();
        if (totalScore != 100) {
            return Result.error("分数不满足要求（100），当前分数：" + totalScore);
        }

        // 题目校验，先查出这些题目的测试案例
        // 提取 ID 并批量获取测试用例
        setTestCasesToQuestions(questions, ReleaseExamQuestionVO::getId, ReleaseExamQuestionVO::setTestCases);
        // 依次判题
        try {
            for (ReleaseExamQuestionVO q : questions) {
                // 检查是否有测试用例
                if (q.getTestCases() == null || q.getTestCases().isEmpty()) {
                    return Result.error("题目 [" + q.getTitle() + "] 缺少测试用例，无法判题");
                }
                JudgeResult judge = judgeService.judge(q.getCode(), q.getTestCases());
                if (judge.getStatus() != JudgeConstant.STATUS_AC) {
                    return Result.error("题目 [" + q.getTitle() + "] 未通过判题");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("判题失败！练习管理员！");
        }
        // 均通过，修改状态
        examMapper.updateExamStatus(exam.getId(), ExamConstant.PUBLISHED);
        return Result.success("发布成功");
    }

    @Override
    @Transactional
    public Result<String> deleteDraft(Integer examId) {
        // 获取包含的题目id
        List<Integer> questionIds = examQuestionConfigMapper.selectQuestionIdByExamId(examId);
        if (questionIds != null && !questionIds.isEmpty()) {
            // 删除题目配置
            examQuestionConfigMapper.deleteDraft(questionIds);
            // 删除测试用例
            questionTestCaseMapper.deleteDraft(questionIds);
            // 删除问题
            questionMapper.deleteDraft(questionIds);
        }
        // 删除考试
        examMapper.deleteById(examId);
        return Result.success("已删除");
    }

    @Override
    public Result<Integer> updateDraft(UpdateDraftDTO draftDTO) {
        Exam exam = mapStruct.updateDraftDTOToExam(draftDTO);
        examMapper.updateById(exam);
        return Result.success("已更新");
    }

    /**
     * 给任意类型的题目VO列表绑定对应的测试用例
     *
     * @param question       题目VO列表（支持任意类型：ReleaseExamQuestionVO/DraftQuestionVO）
     * @param idExtractor    提取VO中题目ID的函数（比如 ReleaseExamQuestionVO::getId）
     * @param testCaseSetter 给VO设置测试用例的函数（比如 ReleaseExamQuestionVO::setTestCases）
     * @param <T>            题目VO的泛型类型
     */
    public <T> void setTestCasesToQuestions(
            List<T> question,
            Function<T, Integer> idExtractor, // 提取ID的逻辑
            BiConsumer<T, List<QuestionTestCase>> testCaseSetter // 设置测试用例的逻辑
    ) {
        // 空值判断：避免空指针
        if (question == null || question.isEmpty()) {
            return;
        }
        // 提取所有题目ID
        List<Integer> questionIdList = question.stream()
                .map(idExtractor) // 通用提取ID，适配不同VO
                .filter(Objects::nonNull) // 过滤null ID，避免查询异常
                .toList();
        // 批量查询测试用例
        List<QuestionTestCase> testCases = questionTestCaseMapper.selectBatchByQuestionIds(questionIdList);
        // 按questionId分组
        Map<Integer, List<QuestionTestCase>> testCaseMap = testCases.stream()
                .collect(Collectors.groupingBy(QuestionTestCase::getQuestionId));
        // 遍历VO列表，绑定测试用例（通用设置逻辑）
        question.forEach(vo -> {
            Integer questionId = idExtractor.apply(vo);
            // 无测试用例时返回空列表，避免null
            List<QuestionTestCase> caseList = testCaseMap.getOrDefault(questionId, new ArrayList<>());
            testCaseSetter.accept(vo, caseList); // 通用设置测试用例
        });
    }
}
