import { defineStore } from 'pinia'
import axios from "@/utils/myAxios"

interface Questions {
    id: number
    title: string
    content: string
    difficulty: number
    templateCode: string
}

interface JudgeResult {
    exitCode: number
    stdout: string
    stderr: string
    time: number
    memory: number
}

export const useExamStore = defineStore('sidebar', {
    state: () => ({
        exam: false,
        currentQuestion: null as Questions | null,
        judgeResult: null as JudgeResult | null,
        judgeLoading: false, // 判题加载状态
        judgeDialog: false
    }),

    actions: {
        beginExam() {
            this.exam = true
        },

        endExam() {
            this.exam = false
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
                this.judgeDialog = true

                const res = await axios.post("/api/exam/judge", {
                    id: this.currentQuestion.id,
                    code: this.currentQuestion.templateCode
                })

                this.judgeResult = res.data.data
            } catch (e) {
                console.error('判题失败：', e)
                this.judgeResult = null
            } finally {
                this.judgeLoading = false
            }
        }
    }
})