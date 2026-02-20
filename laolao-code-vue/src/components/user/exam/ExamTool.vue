<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class="h-full flex border-t">
                    <div class="w-16 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto">
                        <div v-for="(q, index) in questions" :key="index" @click="examStore.currentQuestion = q" :class="[
                            'w-10 h-10 flex items-center justify-center rounded-lg cursor-pointer text-lg font-semibold transition',
                            examStore.currentQuestion === q
                                ? 'bg-black text-white'
                                : 'bg-white border text-gray-600 hover:bg-gray-200'
                        ]">
                            {{ index + 1 }}
                        </div>
                    </div>

                    <div class="flex-1 p-10 overflow-y-auto bg-white">
                        <article v-html="renderedContent" />
                    </div>
                </div>
            </ResizablePanel>

            <ResizableHandle />

            <ResizablePanel :default-size="60">
                <MonacoEditor v-if="examStore.currentQuestion" v-model="examStore.currentQuestion.templateCode"
                    language="java" theme="vs" />
            </ResizablePanel>
        </ResizablePanelGroup>
        <Dialog v-model:open="examStore.judgeDialog">
            <DialogContent class="sm:max-w-150 p-0 overflow-hidden">
                <!-- 状态头部：大标题 -->
                <div :class="[
                    'p-6 border-b',
                    examStore.judgeLoading ? 'bg-blue-50' : (examStore.judgeResult?.status === 'AC' ? 'bg-emerald-50' : 'bg-red-50')
                ]">
                    <DialogHeader>
                        <DialogTitle :class="[
                            'text-3xl font-bold tracking-tight transition-colors',
                            examStore.judgeLoading ? 'text-blue-600' : (examStore.judgeResult?.status === 'AC' ? 'text-emerald-600' : 'text-red-600')
                        ]">
                            {{ examStore.judgeLoading ? '提交中...' : examStore.judgeResult?.status }}
                        </DialogTitle>
                        <DialogDescription v-if="!examStore.judgeLoading" class="text-lg font-medium mt-1">
                            {{ examStore.judgeResult?.msg }}
                        </DialogDescription>
                    </DialogHeader>
                </div>

                <div class="p-6 space-y-6">
                    <!-- 1. 成功状态 (AC)：大字号显示统计 -->
                    <div v-if="examStore.judgeResult?.status === 'AC'" class="grid grid-cols-2 gap-6">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500 font-bold uppercase tracking-wider">执行用时</p>
                            <p class="text-3xl font-mono font-semibold text-gray-900">
                                {{ examStore.judgeResult.time }} <span
                                    class="text-lg font-normal text-gray-500">ms</span>
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500 font-bold uppercase tracking-wider">消耗内存</p>
                            <p class="text-3xl font-mono font-semibold text-gray-900">
                                {{ examStore.judgeResult.memory }} <span
                                    class="text-lg font-normal text-gray-500">MB</span>
                            </p>
                        </div>
                    </div>

                    <!-- 2. 答案错误 (WA)：清晰的对比 -->
                    <div v-if="examStore.judgeResult?.status === 'WA'" class="space-y-4">
                        <div class="space-y-2">
                            <p class="text-sm font-bold">测试输入</p>
                            <pre
                                class="w-full p-4 bg-gray-100 rounded-lg font-mono text-gray-800">{{ examStore.judgeResult.testCase?.input }}</pre>
                        </div>
                        <div class="space-y-2">
                            <p class="text-sm font-bold">预期输出</p>
                            <pre
                                class="w-full p-4 bg-gray-100 rounded-lg font-mono text-gray-800">{{ examStore.judgeResult.testCase?.output }}</pre>
                        </div>
                        <div class="space-y-2">
                            <p class="text-sm font-bold">你的输出</p>
                            <pre
                                class="w-full p-4 bg-gray-100 rounded-lg font-mono text-gray-800">{{ examStore.judgeResult.stdout }}</pre>
                        </div>
                    </div>

                    <!-- 3. 编译错误 (CE)：大字号终端感 -->
                    <div v-if="examStore.judgeResult?.status === 'CE'" class="space-y-2">
                        <p class="text-sm font-bold text-red-600">错误信息</p>
                        <pre
                            class="w-full p-5 bg-red-50 text-red-400 rounded-lg font-mono text-sm leading-relaxed overflow-x-auto max-h-75">{{ examStore.judgeResult.stderr }}</pre>
                    </div>

                    <!-- 加载中占位 -->
                    <div v-if="examStore.judgeLoading" class="flex flex-col items-center py-12 space-y-4">
                        <Spinner class="w-12 h-12 text-blue-500" />
                        <p class="text-xl font-medium text-gray-500 italic">正在评测代码，请稍后...</p>
                    </div>
                </div>

                <!-- 底部按钮 -->
                <DialogFooter class="p-4 bg-gray-50 border-t">
                    <DialogClose as-child>
                        <Button variant="outline" class="w-full sm:w-24 py-5">
                            关闭
                        </Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    </div>
</template>

<script setup lang="ts">
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { Spinner } from '@/components/ui/spinner'
    import { Button } from '@/components/ui/button'
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { onMounted, ref, computed } from 'vue'
    import axios from "@/utils/myAxios"
    import { useRoute } from 'vue-router'
    import MarkdownIt from 'markdown-it'
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()

    const route = useRoute()
    const md = new MarkdownIt({
        html: true,    // 允许 HTML 标签
        breaks: true,  // 转化换行符
        linkify: true  // 自动识别链接
    })

    onMounted(() => {
        getQuestions()
    })

    interface Questions {
        id: number
        title: string
        content: string
        difficulty: number
        templateCode: string
    }

    const questions = ref<Questions[]>([])

    const getQuestions = async () => {
        try {
            const res = await axios.get("/api/exam/begin", {
                params: {
                    recordId: route.params.id
                }
            })
            examStore.examId = res.data.data.examId
            examStore.recordId = Number(route.params.id)
            questions.value = res.data.data.questions
            examStore.currentQuestion = questions.value[0]!
        } catch (e) {
            console.error("Fetch error:", e)
        }
    }


    const renderedContent = computed(() => {
        const content = examStore.currentQuestion?.content
        if (!content) return ''
        return md.render(content);
    })
</script>