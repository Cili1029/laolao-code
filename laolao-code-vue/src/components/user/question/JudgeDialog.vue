<template>
    <div>
        <Dialog v-model:open="examStore.judgeDialog">
            <DialogContent class="sm:max-w-150 p-0 overflow-hidden">
                <!-- 状态头部 -->
                <div :class="[
                    'p-6 border-b',
                    examStore.judgeLoading ? 'bg-blue-50' : (targetRecord?.status === 0 ? 'bg-emerald-50' : 'bg-red-50')
                ]">
                    <DialogHeader>
                        <DialogTitle :class="[
                            'text-3xl font-bold tracking-tight transition-colors',
                            targetRecord?.status === 0 ? 'text-emerald-600' : 'text-red-600'
                        ]">
                            {{ examStore.statusText }}
                        </DialogTitle>
                        <DialogDescription v-if="targetRecord?.msg" class="text-lg font-medium mt-1">
                            {{ targetRecord?.msg }}
                        </DialogDescription>
                    </DialogHeader>
                </div>

                <div class="px-4 pb-4 space-y-6">
                    <!-- 成功状态 (AC) -->
                    <div v-if="targetRecord?.status === 0" class="grid grid-cols-2 gap-6">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500 font-bold uppercase tracking-wider">执行用时</p>
                            <p class="text-3xl font-mono font-semibold text-gray-900">
                                {{ targetRecord.time }} <span class="text-lg font-normal text-gray-500">ms</span>
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500 font-bold uppercase tracking-wider">消耗内存</p>
                            <p class="text-3xl font-mono font-semibold text-gray-900">
                                {{ targetRecord.memory }} <span class="text-lg font-normal text-gray-500">MB</span>
                            </p>
                        </div>
                    </div>

                    <!-- 答案错误 (WA) -->
                    <div v-if="targetRecord?.status === 1" class="space-y-3">
                        <div class="space-y-3">
                            <p class="text-sm font-bold">测试输入</p>
                            <pre
                                class="w-full p-3 bg-gray-100 rounded font-mono text-gray-800">{{ targetRecord.questionTestCase?.input }}</pre>
                        </div>
                        <div class="flex space-x-3">
                            <p class="w-1/2 text-sm font-bold">预期输出</p>
                            <p class="w-1/2 text-sm font-bold">你的输出</p>
                        </div>
                        <div class="flex space-x-3">
                            <pre
                                class="w-1/2 p-3 bg-gray-100 rounded font-mono text-gray-800">{{ targetRecord.questionTestCase?.output }}</pre>
                            <pre
                                class="w-1/2 p-3 bg-gray-100 rounded font-mono text-gray-800">{{ targetRecord.stdout }}</pre>
                        </div>

                    </div>

                    <!-- 编译错误 (CE) -->
                    <div v-if="targetRecord?.status === 5" class="space-y-2">
                        <p class="text-sm font-bold text-red-600">错误信息</p>
                        <pre
                            class="w-full p-5 bg-red-50 text-red-400 rounded font-mono text-sm leading-relaxed overflow-x-auto max-h-75">{{ targetRecord.stderr }}</pre>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    </div>
</template>

<script setup lang="ts">
    import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { useExamStore, type JudgeRecord } from "@/stores/ExamStore"
    import { computed } from 'vue'
    const examStore = useExamStore()

    const targetRecord = computed<JudgeRecord | null>(() => {
        return examStore.managerJudgeRecord ?? examStore.judgeRecord
    })
</script>