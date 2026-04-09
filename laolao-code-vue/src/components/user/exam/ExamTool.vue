<template>
    <div class="h-full">
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class="h-full flex border-t">
                    <div
                        class="w-14 shrink-0 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
                        <div v-for="(q, index) in examStore.questions" :key="index"
                            @click="examStore.currentQuestion = q, currentSelect = 0" :class="[
                                'w-10 h-10 shrink-0 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                                'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600',
                                q.userScore === q.questionScore
                                    ? 'bg-emerald-50 border-emerald-400 text-emerald-600' : '',
                                examStore.currentQuestion === q
                                    ? 'border-zinc-800 text-zinc-900' : ''
                            ]">
                            {{ index + 1 }}
                        </div>
                    </div>

                    <div class="flex-1 flex flex-col bg-white border">
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
                        <div class="flex-1 overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
                            <div v-show="currentSelect == 0" class="flex flex-col p-1">
                                <div class="flex justify-between">
                                    <p>
                                        分值：
                                        <span>{{ examStore.currentQuestion?.userScore }}</span>
                                        /
                                        <span>{{ examStore.currentQuestion?.questionScore }}</span>
                                    </p>
                                    <Badge variant="secondary" :class="['mb-2',
                                        examStore.currentQuestion?.difficulty === 0 ? 'bg-green-50 border-green-300 text-green-700' :
                                            examStore.currentQuestion?.difficulty === 1 ? 'bg-amber-50 border-amber-300 text-amber-700' :
                                                'bg-rose-50 border-rose-300 text-rose-700']">
                                        {{ examStore.currentQuestion?.difficulty === 0 ? '简单' :
                                            examStore.currentQuestion?.difficulty === 1 ? '中等' : '困难' }}
                                    </Badge>
                                </div>

                                <article v-html="renderedContent" />
                            </div>

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
                                        @click="simple.status !== -1 && simple.status !== 6 && simple.status !== 7 && getJudgeRecord(simple.id)">
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
        <JudgeDialog />
    </div>
</template>

<script setup lang="ts">
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { onMounted, ref, computed, onUnmounted } from 'vue'
    import axios from "@/utils/myAxios"
    import { useRoute } from 'vue-router'
    import MarkdownIt from 'markdown-it'
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()
    import { Cpu, ScrollText, Timer, TimerReset } from 'lucide-vue-next'
    import { Badge } from '@/components/ui/badge'
    import JudgeDialog from '../question/JudgeDialog.vue'
    import { useWebsocketStore } from '@/stores/WebsocketStore'
    const wsStore = useWebsocketStore()


    const route = useRoute()
    const md = new MarkdownIt({
        html: true,    // 允许 HTML 标签
        breaks: true,  // 转化换行符
        linkify: true  // 自动识别链接
    })

    onMounted(async () => {
        await getQuestions()
        examStore.beginExam()
        wsStore.bindExamWhenReady(examStore.examId!)
    })

    onUnmounted(() => {
        wsStore.sendJsonMessage("REMOVE_EXAM", null)
    })

    const getQuestions = async () => {
        try {
            const res = await axios.get("/api/exam/user/begin", {
                params: {
                    recordId: route.params.id
                }
            })
            examStore.examId = res.data.data.examId
            examStore.recordId = Number(route.params.id)
            examStore.questions = res.data.data.questions
            examStore.currentQuestion = examStore.questions?.[0] ?? null
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