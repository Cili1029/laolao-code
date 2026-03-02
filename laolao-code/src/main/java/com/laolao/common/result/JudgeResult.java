package com.laolao.common.result;

import com.laolao.common.constant.JudgeConstant;
import com.laolao.pojo.entity.QuestionTestCase;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JudgeResult {
    /**
     * 退出码，判题机用
     */
    private Integer exitCode;

    /**
     * 判题状态
     */
    private Integer status;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 标准输出
     */
    private String stdout;

    /**
     * 错误输出（包括报错信息）
     */
    private String stderr;

    /**
     * 通过的案例数
     */
    private Integer passTestCaseCount;

    /**
     * 提示
     */
    private String msg;

    /**
     * 错误示例Id（数据库存储）
     */
    private Integer questionTestCaseId;

    /**
     * 错误示例（前端展示用）
     */
    private QuestionTestCase questionTestCase;

    /**
     * 消耗时间 (ms)
     */
    private Integer time;

    /**
     * 消耗内存 (MB)
     */
    private Integer memory;

    // 最终的评判结果，用于返回前端
    public static JudgeResult success(Integer time, Integer memory) {
        JudgeResult result = new JudgeResult();
        result.msg = "全部通过";
        result.status = JudgeConstant.STATUS_AC;
        result.time = time;
        result.memory = memory;
        return result;
    }

    // 最终的评判结果，用于返回前端，错误不提供时间空间数据
    public static JudgeResult commonError(String stderr, String msg) {
        JudgeResult result = new JudgeResult();
        result.status = JudgeConstant.STATUS_UNKNOWN;
        result.score = 0;
        result.stderr = stderr;
        result.msg = msg;
        return result;
    }

    // 编译错误返回结果
    public static JudgeResult compileError(String stderr) {
        JudgeResult result = new JudgeResult();
        result.status = JudgeConstant.STATUS_CE;
        result.score = 0;
        result.stderr = stderr;
        result.msg = "编译未通过";
        return result;
    }

    // 示例错误返回结果，可以按示例给分
    public static JudgeResult testCaseError(String stdout, QuestionTestCase questionTestCase, int passTestCaseCount, int totalTestCaseCount) {
        JudgeResult result = new JudgeResult();
        result.status = JudgeConstant.STATUS_WA;
        result.stdout = stdout;
        result.questionTestCase = questionTestCase;
        result.questionTestCaseId =  questionTestCase.getId();
        result.passTestCaseCount = passTestCaseCount;
        result.msg = passTestCaseCount + " / " + totalTestCaseCount +" 个通过的测试用例";
        return result;
    }
}
