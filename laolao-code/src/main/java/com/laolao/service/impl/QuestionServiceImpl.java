package com.laolao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.common.util.MapStruct;
import com.laolao.common.util.SecurityUtils;
import com.laolao.mapper.*;
import com.laolao.pojo.dto.AddQuestionDTO;
import com.laolao.pojo.entity.QuestionFavorite;
import com.laolao.pojo.vo.QuestionBankDialogTagVO;
import com.laolao.pojo.entity.ExamQuestionConfig;
import com.laolao.pojo.entity.Question;
import com.laolao.pojo.entity.QuestionTestCase;
import com.laolao.pojo.vo.DraftQuestionVO;
import com.laolao.pojo.vo.QuestionBankDialogVO;
import com.laolao.pojo.vo.QuestionBankInfoVO;
import com.laolao.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionTestCaseMapper questionTestCaseMapper;
    @Resource
    private MapStruct mapStruct;
    @Resource
    private ExamQuestionConfigMapper examQuestionConfigMapper;
    @Resource
    private QuestionTagMapper questionTagMapper;
    @Resource
    private QuestionFavoriteMapper questionFavoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> addOrUpdateQuestion(AddQuestionDTO addQuestionDTO) {
        Question question = mapStruct.addQuestionDTOtoQuestion(addQuestionDTO);
        question.setCreatorId(SecurityUtils.getUserId());
        if (question.getId() != null) {
            // 这是旧题，做更新
            questionMapper.updateById(question);
            // 测试示例直接全量删除
            questionTestCaseMapper.delete(new LambdaQueryWrapper<QuestionTestCase>()
                    .eq(QuestionTestCase::getQuestionId, question.getId()));
        } else {
            // 新题，做添加
            questionMapper.insert(question);
        }

        // 统一插入测试用例
        List<QuestionTestCase> testCases = addQuestionDTO.getTestCases();
        if (testCases != null && !testCases.isEmpty()) {
            for (QuestionTestCase testCase : testCases) {
                testCase.setQuestionId(question.getId());
            }
            // 批量插入测试用例
            questionTestCaseMapper.insertBatch(testCases);
        }

        return Result.success("保存成功！", question.getId());
    }

    @Override
    public Result<Page<QuestionBankDialogVO>> getPrivateQuestions(Integer pageNum, Integer pageSize, String content) {
        Page<QuestionBankDialogVO> page = new Page<>(pageNum, pageSize);
        Page<QuestionBankDialogVO> res = questionMapper.selectPrivateBank(page, SecurityUtils.getUserId(), content);
        return Result.success(res);
    }

    @Override
    public Result<Page<QuestionBankDialogVO>> getPublicQuestions(Integer pageNum, Integer pageSize, String content, Integer tagId, Integer isFavorite) {
        Page<QuestionBankDialogVO> page = new Page<>(pageNum, pageSize);
        Page<QuestionBankDialogVO> res = questionMapper.selectPublicBank(page, content, tagId, SecurityUtils.getUserId(), isFavorite);
        // 获取其标签
        List<Integer> questionIds = res.getRecords().stream().map(QuestionBankDialogVO::getId).toList();
        if (!questionIds.isEmpty()) {
            List<QuestionBankDialogTagVO> tagVOS = questionMapper.selectTags(questionIds);
            // 分组
            Map<Integer, List<String>> tagMap = tagVOS.stream().collect(
                    Collectors.groupingBy(QuestionBankDialogTagVO::getId,
                            Collectors.mapping(QuestionBankDialogTagVO::getName, Collectors.toList())
                    ));
            // 分配
            res.getRecords().forEach(vo -> vo.setTags(tagMap.getOrDefault(vo.getId(), Collections.emptyList())));
        }
        return Result.success(res);
    }

    @Override
    public Result<String> delete(Integer questionId) {
        questionMapper.deleteQuestion(SecurityUtils.getUserId(), questionId);
        return Result.success("删除成功！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<DraftQuestionVO> copyQuestion(Integer questionId, Integer examId) {
        // 题目
        Question question = questionMapper.selectCopyQuestion(questionId);
        question.setCreatorId(SecurityUtils.getUserId());
        questionMapper.insert(question);

        // 测试实例
        List<QuestionTestCase> questionTestCase = questionTestCaseMapper.selectCopyTestCase(questionId);
        questionTestCase.forEach(
                testCase -> testCase.setQuestionId(question.getId())
        );
        questionTestCaseMapper.insertBatch(questionTestCase);

        // 写入考试表
        ExamQuestionConfig questionConfig = ExamQuestionConfig.builder()
                .examId(examId)
                .questionId(question.getId())
                .score(0).build();
        examQuestionConfigMapper.insert(questionConfig);

        // 转
        DraftQuestionVO questionVO = mapStruct.questionToDraftQuestionVO(question);
        questionVO.setQuestionScore(0);
        // 测试示例
        questionVO.setTestCases(questionTestCase);
        return Result.success("克隆成功！", questionVO);
    }

    @Override
    public Result<QuestionBankInfoVO> getSingleQuestionInfo(Integer questionId) {
        QuestionBankInfoVO questionBankInfoVO = questionMapper.selectQuestionInfo(questionId);
        questionBankInfoVO.setTags(questionTagMapper.selectTagsByQuestion(questionId));
        return Result.success(questionBankInfoVO);
    }

    @Override
    @Transactional
    public Result<String> favorite(Integer questionId) {
        Integer userId = SecurityUtils.getUserId();
        // 查询收藏信息
        QuestionFavorite favorite = questionFavoriteMapper.selectOne(new LambdaQueryWrapper<QuestionFavorite>()
                .eq(QuestionFavorite::getUserId, userId)
                .eq(QuestionFavorite::getQuestionId, questionId));

        // 没有，为新收藏
        if (favorite == null) {
            QuestionFavorite newFavorite = QuestionFavorite.builder()
                    .userId(userId)
                    .questionId(questionId)
                    .build();
            questionFavoriteMapper.insert(newFavorite);
            return Result.success("收藏成功");
        } else {
            // 有，取消收藏
            questionFavoriteMapper.deleteById(favorite.getId());
        }
        return Result.success("取消成功");
    }
}
