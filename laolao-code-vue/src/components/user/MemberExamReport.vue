<template>
    <div class="flex bg-gray-100">
        <div class="flex flex-col flex-1 items-center p-2 space-y-2 overflow-y-auto">
            <div class=" w-6/7 border rounded py-2 px-6 space-y-2 bg-white">
                <div class="flex justify-between">
                    <span>考生：{{ report?.name }}</span>
                    <span>分数：{{ report?.score }}</span>
                </div>
                <div class="flex justify-between">
                    <span>考试：{{ report?.title }}</span>
                    <span>考试时间：{{ dayjs(report?.enterTime).format('YYYY/MM/DD HH:mm') }}</span>
                    <span>提交时间：{{ dayjs(report?.submitTime).format('YYYY/MM/DD HH:mm') }}</span>
                </div>
                <div class="flex pt-2">
                    <div @click="aiExamRecordReport()"
                        v-if="!report?.aiReport"
                        class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                        <Spinner v-if="report?.isGenerating" class="mr-1" />
                        <Rocket v-else class="h-4 w-4 mr-1" />
                        {{ report?.isGenerating ? 'AI 正在思考...' : '生成 AI 诊断报告' }}
                    </div>
                    <p v-else></p>
                </div>

                <div v-if="report?.aiReport || report?.isGenerating"
                    class="mt-4 p-4 bg-slate-50 border border-blue-100 rounded-lg">
                    <div class="text-sm text-gray-700" v-html="renderMarkdown(report.aiReport || '')">
                    </div>
                    <span v-if="report.isGenerating"
                        class="inline-block w-2 h-4 bg-blue-500 animate-pulse ml-1 align-middle"></span>
                </div>
            </div>

            <div class=" w-6/7 shrink-0 border rounded py-2 px-6 space-y-2 bg-white">
                <!-- 遍历每一道题 -->
                <div v-for="judgeRecord in report?.judgeRecords" :key="judgeRecord.id">
                    <div class="flex justify-between">
                        <p class="space-x-3">
                            <span>{{ judgeRecord.title }}</span>
                            <span>{{ judgeRecord.totalScore }}分</span>
                        </p>

                        <Badge variant="secondary" :class="['my-1',
                            judgeRecord.status !== 0 ?
                                'text-white bg-orange-500 dark:bg-orange-600' :
                                'text-white bg-green-500 dark:bg-green-600'
                        ]">
                            {{ examStore.getStatusTextByCode(judgeRecord.status) }}
                        </Badge>
                    </div>

                    <div class="flex">
                        <div class="h-96 w-1/2">
                            <MonacoEditor v-if="report" v-model="judgeRecord.answerCode" language="java" theme="vs" />
                        </div>
                        <div class="h-96 w-1/2">
                            <MonacoEditor v-if="report" v-model="judgeRecord.standardSolution" language="java" readonly
                                theme="vs" />
                        </div>
                    </div>
                    <div class="flex justify-between pt-2">
                        <div @click="aiJudgeRecordReport(judgeRecord)"
                            v-if="judgeRecord.status !== 0 && !judgeRecord.aiReport"
                            class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                            <Spinner v-if="judgeRecord.isGenerating" class="mr-1" />
                            <Rocket v-else class="h-4 w-4 mr-1" />
                            {{ judgeRecord.isGenerating ? 'AI 正在思考...' : '生成 AI 诊断报告' }}
                        </div>
                        <p v-else></p>
                        <p>得分：{{ judgeRecord.memberScore }}</p>
                    </div>

                    <div v-if="judgeRecord.aiReport || judgeRecord.isGenerating"
                        class="mt-4 p-4 bg-slate-50 border border-blue-100 rounded-lg">
                        <div class="text-sm text-gray-700" v-html="renderMarkdown(judgeRecord.aiReport || '')">
                        </div>
                        <span v-if="judgeRecord.isGenerating"
                            class="inline-block w-2 h-4 bg-blue-500 animate-pulse ml-1 align-middle"></span>
                    </div>

                    <div class=" border-t-2 border-dashed border-gray-400 mt-4 mb-8"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import axios from "@/utils/myAxios"
    import { onMounted, ref } from "vue"
    import { useRoute } from "vue-router"
    import dayjs from "dayjs"
    import MonacoEditor from "@/components/common/MonacoEditor.vue"
    import { useExamStore } from "@/stores/ExamStore"
    import Badge from "@/components/ui/badge/Badge.vue"
    import MarkdownIt from 'markdown-it'
    import Spinner from "../ui/spinner/Spinner.vue"
    import { Rocket } from "lucide-vue-next"
    const md = new MarkdownIt({ breaks: true }); // breaks: true 允许回车换行
    const renderMarkdown = (text: string) => md.render(text);

    const route = useRoute()
    const examStore = useExamStore()

    onMounted(() => {
        getReport()
    })

    interface JudgeRecord {
        id: number
        title: string
        totalScore: number
        memberScore: number
        answerCode: string
        standardSolution: string
        status: number
        aiReport: string
        // 前端专用字段
        isGenerating?: boolean
    }

    interface Member {
        id: number
        name: string
        score: number
        examId: number
        title: string
        enterTime: string
        submitTime: string
        judgeRecords: JudgeRecord[]
        aiReport: string
        // 前端专用字段
        isGenerating?: boolean
    }

    const report = ref<Member>()

    const getReport = async () => {
        try {
            const res = await axios.get("/api/member-report/detail", {
                params: {
                    recordId: route.params.id
                }
            })
            const reportData = res.data.data;
            reportData.isGenerating = false

            if (reportData && reportData.judgeRecords) {
                reportData.isGenerating = false
                reportData.judgeRecords.forEach((record: JudgeRecord) => {
                    record.answerCode = `// 你的答案\n${record.answerCode}`
                    record.standardSolution = `// 标准答案\n${record.standardSolution}`

                    record.isGenerating = false
                });
            }
            report.value = reportData
        } catch (e) {
            console.log(e);
        }
    }

    const aiJudgeRecordReport = (judgeRecord: JudgeRecord) => {
        if (judgeRecord.isGenerating) return

        judgeRecord.isGenerating = true
        judgeRecord.aiReport = ''

        const eventSource = new EventSource(`/api/ai/report/question?recordId=${judgeRecord.id}`)

        // 只要来数据，就往屏幕上打字 (默认监听 message 事件)
        eventSource.onmessage = (event) => {
            judgeRecord.aiReport += event.data.replace(/\\n/g, '\n')
        };

        // 只要连接断开 (无论是因为后端正常输出完毕，还是网络异常)，统统当作正常结束！
        eventSource.onerror = () => {
            judgeRecord.isGenerating = false
            eventSource.close()
        };
    }

    const aiExamRecordReport = () => {
        if (report.value!.isGenerating) return

        report.value!.isGenerating = true
        report.value!.aiReport = ''

        const eventSource = new EventSource(`/api/ai/report/exam-record?examId=${report.value?.examId}&examRecordId=${report.value?.id}`)

        // 只要来数据，就往屏幕上打字 (默认监听 message 事件)
        eventSource.onmessage = (event) => {
            report.value!.aiReport += event.data.replace(/\\n/g, '\n')
        };

        // 只要连接断开 (无论是因为后端正常输出完毕，还是网络异常)，统统当作正常结束！
        eventSource.onerror = () => {
            report.value!.isGenerating = false
            eventSource.close()
        };
    }
</script>