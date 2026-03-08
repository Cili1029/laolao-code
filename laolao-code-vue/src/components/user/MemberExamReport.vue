<template>
    <div class="flex bg-gray-100">
        <div class="flex flex-col flex-1 items-center p-2 space-y-2 overflow-y-auto">
            <div class=" w-6/7 border rounded py-2 px-6 space-y-2 bg-white">
                <div class="flex justify-between">
                    <span>考生：{{ report?.name }}</span>
                    <span>分数：{{ report?.score }}</span>
                </div>
                <div class="flex justify-between">
                    <span>考试时间：{{ dayjs(report?.enterTime).format('YYYY/MM/DD HH:mm') }}</span>
                    <span>提交时间：{{ dayjs(report?.submitTime).format('YYYY/MM/DD HH:mm') }}</span>
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
                        <div @click="aiReport(judgeRecord)" v-if="judgeRecord.status !== 0"
                            class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                            <Spinner v-if="judgeRecord.isGenerating" class="mr-1" />
                            <Rocket v-else class="h-4 w-4 mr-1" />
                            {{ judgeRecord.isGenerating ? 'AI 正在思考...' : '生成 AI 诊断报告' }}
                        </div>
                        <p v-else></p>
                        <p>得分：{{ judgeRecord.memberScore }}</p>
                    </div>

                    <div v-if="judgeRecord.aiReportText || judgeRecord.isGenerating"
                        class="mt-4 p-4 bg-slate-50 border border-blue-100 rounded-lg">
                        <!-- 渲染 Markdown -->
                        <div class="text-sm text-gray-700" v-html="renderMarkdown(judgeRecord.aiReportText || '')">
                        </div>
                        <!-- 打字机闪烁光标 -->
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
    import { onMounted, ref } from "vue";
    import { useRoute } from "vue-router";
    import dayjs from "dayjs";
    import MonacoEditor from "@/components/common/MonacoEditor.vue"
    import { useExamStore } from "@/stores/ExamStore"
    import Badge from "@/components/ui/badge/Badge.vue";

    // 引入 markdown-it
    import MarkdownIt from 'markdown-it';
    import Spinner from "../ui/spinner/Spinner.vue";
    import { Rocket } from "lucide-vue-next";
    const md = new MarkdownIt({ breaks: true }); // breaks: true 允许回车换行
    const renderMarkdown = (text: string) => md.render(text);

    const route = useRoute()
    const examStore = useExamStore()

    onMounted(() => {
        getReport()
    })

    // 🌟 1. 扩展接口：给每道题加上 AI 专属的临时字段
    interface JudgeRecord {
        id: number
        title: string
        totalScore: number
        memberScore: number
        answerCode: string
        standardSolution: string
        status: number
        // 以下为新增字段，前端专用
        aiReportText?: string;
        isGenerating?: boolean;
    }

    interface Member {
        id: number
        name: string
        score: number
        enterTime: string
        submitTime: string
        judgeRecords: JudgeRecord[]
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

            if (reportData && reportData.judgeRecords) {
                reportData.judgeRecords.forEach((record: JudgeRecord) => {
                    const userCode = record.answerCode || '';
                    record.answerCode = `// 你的答案\n${userCode}`;

                    const stdCode = record.standardSolution || '';
                    record.standardSolution = `// 标准答案\n${stdCode}`;

                    // 初始化 AI 字段
                    record.aiReportText = '';
                    record.isGenerating = false;
                });
            }
            report.value = reportData;

        } catch (e) {
            console.log(e);
        }
    }

    // 🌟 2. 改写 AI 调用方法 (使用原生 EventSource 接收流式打字机数据)
    const aiReport = (judgeRecord: JudgeRecord) => {
        // 防止重复点击
        if (judgeRecord.isGenerating) return;

        // 状态重置
        judgeRecord.isGenerating = true;
        judgeRecord.aiReportText = '';

        // ⚠️ 注意：接收流式数据不能用 axios，必须用浏览器的 EventSource
        // 这里的 URL 请换成你实际的后端 AI 接口地址
        const eventSource = new EventSource(`/api/ai/report?recordId=${judgeRecord.id}`);

        // 监听持续返回的数据块 (打字机效果核心)
        eventSource.onmessage = (event) => {
            // 替换后端传来的换行符
            const chunk = event.data.replace(/\\n/g, '\n');
            // 将文字拼接到当前这道题的专属字段上，Vue会自动更新视图！
            judgeRecord.aiReportText += chunk;
        };

        // 监听后端发出的完成信号 (我们在 Java 里写的 event().name("finish"))
        eventSource.addEventListener("finish", () => {
            judgeRecord.isGenerating = false;
            eventSource.close();
        });

        // 新代码
        // 1. 监听后端的自定义报错信号
        eventSource.addEventListener("error", (event: any) => {
            if (event.data) { // 只有后端传了具体的报错内容，才显示出来
                judgeRecord.aiReportText += `\n\n[❌ 诊断失败: ${event.data}]`;
            }
            judgeRecord.isGenerating = false;
            eventSource.close();
        });

        // 2. 监听原生的网络层面异常 (当后端调用 emitter.complete() 时会触发这个)
        eventSource.onerror = () => {
            // 静默关闭即可，这通常意味着正常生成结束或网络波动
            judgeRecord.isGenerating = false;
            eventSource.close();
        };

        // 监听网络层面的异常断开
        eventSource.onerror = () => {
            judgeRecord.isGenerating = false;
            eventSource.close();
        };
    }
</script>

<style scoped>

    /* 给 markdown 加上极其基础的排版，防止它全糊在一起 */
    :deep(.text-sm.text-gray-700 p) {
        margin-bottom: 0.5rem;
    }

    :deep(.text-sm.text-gray-700 pre) {
        background-color: #f1f5f9;
        padding: 0.5rem;
        border-radius: 0.25rem;
        margin: 0.5rem 0;
        overflow-x: auto;
    }

    :deep(.text-sm.text-gray-700 code) {
        background-color: #f1f5f9;
        padding: 0.1rem 0.3rem;
        border-radius: 0.25rem;
        font-family: monospace;
    }
</style>