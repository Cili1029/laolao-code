package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.laolao.common.constant.ExamConstant;
import com.laolao.common.docker.JudgeService;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.*;
import com.laolao.pojo.entity.*;
import com.laolao.pojo.messege.ReleaseExamMessage;
import com.laolao.pojo.vo.*;
import com.laolao.service.DraftExamService;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    @Resource
    private StudyGroupMapper studyGroupMapper;
    @Resource
    private RocketMQClientTemplate rocketMQClientTemplate;

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
    public Result<String> judgeTestCase(Integer questionId) {
        // 发送队列
        rocketMQClientTemplate.convertAndSend("JudgeTopic:ADVISOR", questionId);
        return Result.success();
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
        if (exam.getStatus() != ExamConstant.DRAFT) {
            return Result.error("当前考试状态不可发布");
        }
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime().isBefore(now)) {
            return Result.error("考试开始时间不能早于当前时间");
        }
        if (exam.getStartTime().isAfter(exam.getEndTime())) {
            return Result.error("开始时间必须早于结束时间");
        }

        // 至少要有一个考生
        Integer memberCount = studyGroupMapper.selectMemberCountByExamId(examId);
        if (memberCount == 0) {
            return Result.error("学习组只少要有一名成员才可以发布考试");
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

        // 排除已经测试通过的
        List<Integer> questionIds = questions.stream()
                .filter(question -> question.getIsValidated() != 1)
                .map(ReleaseExamQuestionVO::getId).toList();
        if (questionIds.isEmpty()) {
            // 均通过，修改状态
            examMapper.updateExamStatus(exam.getId(), ExamConstant.PUBLISHED);
            return Result.success("发布成功");
        }

        // 需要异步判题，修改状态为 PUBLISHING
        examMapper.updateExamStatus(examId, ExamConstant.PUBLISHING);

        // 事务提交后发送 MQ
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 封装消息对象
                ReleaseExamMessage message = new ReleaseExamMessage(SecurityUtils.getUserId(), examId, questionIds);
                rocketMQClientTemplate.convertAndSend("JudgeTopic:RELEASE", message);
            }
        });
        return Result.success("后台判题中，耐心等待");
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
