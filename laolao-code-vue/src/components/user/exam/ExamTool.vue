<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class=" max-h-max flex border-t">
                    <div class="w-16 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto">
                        <div v-for="(q, index) in questions" :key="index"
                            @click="examStore.currentQuestion = q, currentSelect = 0" :class="[
                                'w-10 h-10 flex items-center justify-center rounded-lg cursor-pointer text-lg font-semibold transition',
                                examStore.currentQuestion === q
                                    ? 'bg-black text-white'
                                    : 'bg-white border text-gray-600 hover:bg-gray-200'
                            ]">
                            {{ index + 1 }}
                        </div>
                    </div>

                    <div class="h-150 w-full flex flex-col bg-white border">
                        <div class="flex border-b">
                            <p @click="currentSelect = 0" class="flex py-1 px-2 items-center hover:bg-gray-100"
                                :class="currentSelect == 0 ? 'bg-gray-100' : ''">
                                <ScrollText class="h-5 w-5 mr-2 text-blue-500" />
                                题目描述
                            </p>
                            <p @click="currentSelect === 1 ? '' : getSimpleJudgeRecord(), currentSelect = 1"
                                class="flex py-1 px-2 items-center hover:bg-gray-100"
                                :class="currentSelect == 1 ? 'bg-gray-100' : ''">
                                <TimerReset class="h-5 w-5 mr-2 text-blue-500" />
                                提交记录
                            </p>
                        </div>
                        <div class="flex-1 overflow-y-auto">
                            <article v-show="currentSelect == 0" v-html="renderedContent" class="p-1" />
                            <Table v-show="currentSelect == 1">
                                <TableHeader>
                                    <TableRow>
                                        <TableHead></TableHead>
                                        <TableHead class="text-left">状态</TableHead>
                                        <TableHead class="text-center">得分</TableHead>
                                        <TableHead class="text-center">执行用时</TableHead>
                                        <TableHead class="text-right">消耗内存</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    <TableRow v-for="(simple, index) in simpleJudgeRecords" :key="simple.id"
                                        @click="getJudgeRecord(simple.id)">
                                        <TableCell>
                                            {{ simpleJudgeRecords.length - index }}
                                        </TableCell>
                                        <TableCell class="text-left"
                                            :class="simple.status === 0 ? 'text-green-500' : 'text-red-500'">
                                            {{ examStore.getStatusTextByCode(simple.status) }}
                                        </TableCell>
                                        <TableCell class="text-center">
                                            {{ simple.score }}
                                        </TableCell>
                                        <TableCell class="text-center">
                                            <p class="flex justify-center items-center">
                                                <Timer class="pr-1" />
                                                {{ simple.status === 0 ? simple.time + "ms" : "N/A" }}
                                            </p>
                                        </TableCell>
                                        <TableCell class="text-right">
                                            <p class="flex justify-end items-center">
                                                <Cpu class="pr-1" />
                                                {{ simple.status === 0 ? simple.memory + "MB" : "N/A" }}
                                            </p>
                                        </TableCell>
                                    </TableRow>
                                </TableBody>
                            </Table>
                        </div>
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
                    examStore.judgeLoading ? 'bg-blue-50' : (examStore.judgeRecord?.status === 0 ? 'bg-emerald-50' : 'bg-red-50')
                ]">
                    <DialogHeader>
                        <DialogTitle :class="[
                            'text-3xl font-bold tracking-tight transition-colors',
                            examStore.judgeRecord?.status === 0 ? 'text-emerald-600' : 'text-red-600'
                        ]">
                            {{ examStore.statusText }}
                        </DialogTitle>
                        <DialogDescription v-if="!examStore.judgeLoading" class="text-lg font-medium mt-1">
                            {{ examStore.judgeRecord?.msg }}
                        </DialogDescription>
                    </DialogHeader>
                </div>

                <div class="p-6 space-y-6">
                    <!-- 1. 成功状态 (AC)：大字号显示统计 -->
                    <div v-if="examStore.judgeRecord?.status === 0" class="grid grid-cols-2 gap-6">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500 font-bold uppercase tracking-wider">执行用时</p>
                            <p class="text-3xl font-mono font-semibold text-gray-900">
                                {{ examStore.judgeRecord.time }} <span
                                    class="text-lg font-normal text-gray-500">ms</span>
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500 font-bold uppercase tracking-wider">消耗内存</p>
                            <p class="text-3xl font-mono font-semibold text-gray-900">
                                {{ examStore.judgeRecord.memory }} <span
                                    class="text-lg font-normal text-gray-500">MB</span>
                            </p>
                        </div>
                    </div>

                    <!-- 2. 答案错误 (WA)：清晰的对比 -->
                    <div v-if="examStore.judgeRecord?.status === 1" class="space-y-4">
                        <div class="space-y-2">
                            <p class="text-sm font-bold">测试输入</p>
                            <pre
                                class="w-full p-4 bg-gray-100 rounded-lg font-mono text-gray-800">{{ examStore.judgeRecord.testCase?.input }}</pre>
                        </div>
                        <div class="space-y-2">
                            <p class="text-sm font-bold">预期输出</p>
                            <pre
                                class="w-full p-4 bg-gray-100 rounded-lg font-mono text-gray-800">{{ examStore.judgeRecord.testCase?.output }}</pre>
                        </div>
                        <div class="space-y-2">
                            <p class="text-sm font-bold">你的输出</p>
                            <pre
                                class="w-full p-4 bg-gray-100 rounded-lg font-mono text-gray-800">{{ examStore.judgeRecord.stdout }}</pre>
                        </div>
                    </div>

                    <!-- 3. 编译错误 (CE)：大字号终端感 -->
                    <div v-if="examStore.judgeRecord?.status === 5" class="space-y-2">
                        <p class="text-sm font-bold text-red-600">错误信息</p>
                        <pre
                            class="w-full p-5 bg-red-50 text-red-400 rounded-lg font-mono text-sm leading-relaxed overflow-x-auto max-h-75">{{ examStore.judgeRecord.stderr }}</pre>
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
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()
    import { Cpu, ScrollText, Timer, TimerReset } from 'lucide-vue-next'


    const route = useRoute()
    const md = new MarkdownIt({
        html: true,    // 允许 HTML 标签
        breaks: true,  // 转化换行符
        linkify: true  // 自动识别链接
    })

    onMounted(async () => {
        await getQuestions()
        getSimpleJudgeRecord()
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

    const currentSelect = ref(0)

    interface SimpleJudgeRecord {
        id: number
        status: number
        score: number
        time: string
        memory: number
    }

    const simpleJudgeRecords = ref<SimpleJudgeRecord[]>([])
    const getSimpleJudgeRecord = async () => {
        try {
            const res = await axios.get("/api/judge-record/simple", {
                params: {
                    examRecordId: examStore.recordId,
                    questionId: examStore.currentQuestion?.id
                }
            })
            simpleJudgeRecords.value = res.data.data
        } catch (e) {
            console.log(e);
        }
    }

    const getJudgeRecord = async (JudgeRecordId: number) => {
        try {
            const res = await axios.get("/api/judge-record", {
                params: {
                    judgeRecordId: JudgeRecordId
                }
            })
            examStore.judgeRecord = res.data.data
            examStore.judgeDialog = true
        } catch (e) {
            console.log(e);
        }
    }
</script>