import { defineStore } from 'pinia'
import axios from "@/utils/myAxios"

interface Questions {
    id: number
    title: string
    content: string
    difficulty: number
    templateCode: string
}

interface TestCase {
    input: string
    output: string
}

interface JudgeResult {
    exitCode: number
    score: number
    stdout: string
    stderr: string
    msg: string
    testCase: TestCase
    status: string
    time: number
    memory: number
}

export const useExamStore = defineStore('sidebar', {
    state: () => ({
        examBegin: false,
        examId: null as number | null,
        recordId: null as number | null,
        currentQuestion: null as Questions | null,
        judgeResult: null as JudgeResult | null,
        judgeLoading: false, // 判题加载状态
        judgeDialog: false
    }),

    actions: {
        beginExam() {
            this.examBegin = true
        },

        endExam() {
            this.examBegin = false
            this.currentQuestion = null
            this.judgeResult = null
        },

        async judge() {
            if (!this.currentQuestion) {
                console.error('当前无选中的题目，无法判题')
                return
            }

            try {
                this.judgeLoading = true

                const res = await axios.post("/api/exam/judge", {
                    examId: this.examId,
                    recordId: this.recordId,
                    questionId: this.currentQuestion.id,
                    code: this.currentQuestion.templateCode
                })

                this.judgeResult = res.data.data
                this.judgeDialog = true
            } catch (e) {
                console.error('判题失败：', e)
                this.judgeResult = null
            } finally {
                this.judgeLoading = false
            }
        }
    }
})