import { defineStore } from 'pinia'

export const useSidebarStore = defineStore('sidebar', {
    state: () => ({
        exam: false
    }),

    actions: {
        beginExam() {
            this.exam = true
        },

        endExam() {
            this.exam = false
        }
    }
})