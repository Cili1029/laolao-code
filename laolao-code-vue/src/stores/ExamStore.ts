import { defineStore } from 'pinia'
import axios from "@/utils/myAxios"

export interface Questions {
    id: number
    questionScore: number
    userScore: number
    title: string
    content: string
    difficulty: number
    templateCode: string
}

export interface QuestionTestCase {
    id: number
    questionId: number
    input: string
    output: string
}

export interface JudgeRecord {
    status: number
    score: number
    stdout: string
    stderr: string
    msg: string
    questionTestCase: QuestionTestCase
    time: number
    memory: number
}

const statusTextMap = new Map<number, string>([
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
        examBegin: false,
        examId: null as number | null,
        recordId: null as number | null,
        questions: null as Questions[] | null,
        currentQuestion: null as Questions | null,
        judgeRecord: null as JudgeRecord | null,
        judgeLoading: false, // 判题加载状态
        judgeDialog: false,

        // 老师的判题结果
        advisorJudgeRecord: null as JudgeRecord | null,
    }),

    getters: {
        statusText(): string {
            const targetRecord = this.judgeRecord ?? this.advisorJudgeRecord;

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

                const res = await axios.post("/api/exam/member/judge", {
                    examId: this.examId,
                    recordId: this.recordId,
                    questionId: questionId,
                    code: this.currentQuestion.templateCode
                })

                this.judgeRecord = res.data.data
                this.judgeDialog = true

                const targetQuestion = this.questions?.find(q => q.id === questionId)
                if (targetQuestion) {
                    targetQuestion.userScore = this.judgeRecord!.score!
                }
            } catch (e) {
                console.error('判题失败：', e)
                this.judgeRecord = null
            } finally {
                this.judgeLoading = false
            }
        },

        getStatusTextByCode(statusCode: number): string {
            return statusTextMap.get(statusCode) || '未知状态'
        },
    }
})