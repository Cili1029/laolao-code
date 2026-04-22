import { defineStore } from 'pinia'
import axios from "@/utils/myAxios"
import router from '@/router'

export interface Questions {
    id: number
    questionScore: number
    userScore: number
    title: string
    content: string
    difficulty: number
    templateCode: string
}

export interface JudgeRecord {
    // 判题状态(-1-判题中, 0-AC, 1-WA, 2-MLE, 3-TLE, 4-RE, 5-CE, 6-SE, 7-UN)
    status: number
    // 得分
    score: number
    // 编译错误/系统异常信息
    errorMessage: string
    // 测试示例总数
    totalCount: number
    // 测试示例通过数
    passCount: number
    // 未通过测试用例的输入
    failedInput: string
    // 未通过测试用例的预期输出
    failedExpect: string
    // 未通过测试用例的实际输出
    failedActual: string
    // 执行耗时(ms)
    time: number
    // 内存消耗(MB)
    memory: number
}

const statusTextMap = new Map<number, string>([
    [-1, '判题中'],
    [0, '通过'],
    [1, '答案错误'],
    [2, '内存超限'],
    [3, '超时'],
    [4, '运行时错误'],
    [5, '编译错误'],
    [6, '系统错误'],
    [7, '未知错误']
])

export const useExamStore = defineStore('sidebar', {
    state: () => ({
        // ===================== 考试核心信息 =====================
        examBegin: false,          // 考试是否已开始
        examId: null as number | null,       // 当前考试ID
        recordId: null as number | null,     // 当前考试记录ID

        // ===================== 题目相关 =====================
        questions: null as Questions[] | null,       // 所有题目列表
        currentQuestion: null as Questions | null,   // 当前作答的题目

        // ===================== 判题相关(附加组管理员编辑考试时候的判题功能) =====================
        judgeLoading: false,       // 自动判题加载状态
        judgeDialog: false,        // 判题结果弹窗显隐
        judgeRecord: null as JudgeRecord | null,     // 判题结果
        managerJudgeRecord: null as JudgeRecord | null,  // 组管理员编辑考试时判题结果

        // ===================== 交卷相关 =====================
        submitLoading: false       // 交卷操作加载状态
    }),

    getters: {
        statusText(): string {
            const targetRecord = this.judgeRecord ?? this.managerJudgeRecord;

            if (!targetRecord) return '未判题';
            return statusTextMap.get(targetRecord.status) || '未知状态';
        }
    },

    actions: {
        beginExam() {
            this.examBegin = true
        },

        endExam() {
            this.examBegin = false
            this.currentQuestion = null
            this.judgeRecord = null
        },

        async judge() {
            if (!this.currentQuestion) {
                console.error('当前无选中的题目，无法判题')
                return
            }

            try {
                this.judgeLoading = true

                const questionId = this.currentQuestion.id

                await axios.post("/api/exam/user/judge", {
                    examId: this.examId,
                    recordId: this.recordId,
                    questionId: questionId,
                    code: this.currentQuestion.templateCode
                })

            } catch (e) {
                console.error('判题失败：', e)
                this.judgeRecord = null
            }
        },

        async submitExam() {
            try {
                this.submitLoading = true
                const res = await axios.put("/api/exam/user/submit", {}, {
                    params: {
                        recordId: this.recordId
                    }
                })
                if (res.data.code === 1) {
                    router.replace(`/exam/${this.examId}`)
                }
            } catch (e) {
                console.error('交卷失败：', e)
            } finally {
                this.submitLoading = false
            }
        },

        getStatusTextByCode(statusCode: number): string {
            return statusTextMap.get(statusCode) || '未知状态'
        },
    }
})