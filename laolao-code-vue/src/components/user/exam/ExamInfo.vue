<template>
    <div class="bg-blue-50 p-5 flex flex-col space-y-5 h-full">
        <div class="h-45 w-6/7 mx-auto flex justify-center space-x-5">
            <div class="w-4/5 h-full flex space-x-5">
                <div
                    class="w-2/7 h-full shadow rounded bg-linear-to-b from-blue-300 to-blue-500 flex justify-center items-center">
                    <Bug class="text-white w-25 h-25" />
                </div>
                <div class="w-5/7 h-full shadow rounded-lg flex justify-between p-3 bg-white">
                    <div class="w-1/2 space-y-2">
                        <p class="font-bold">考试信息</p>
                        <p class="text-gray-600">{{ exam?.title }}</p>
                        <p class="text-gray-600">{{ exam?.teamName }}</p>
                        <p class="text-gray-600">{{ exam?.description }}</p>
                    </div>
                    <div class="w-1/2 flex flex-col">
                        <!-- 状态标签逻辑：统一由 statusUI 处理 -->
                        <div class="flex justify-end mb-1">
                            <Badge variant="secondary" :class="['mb-2 text-white', statusUI.color]">
                                {{ statusUI.text }}
                            </Badge>
                        </div>

                        <!-- 按钮操作逻辑 -->
                        <div class="flex flex-1 items-center justify-center">
                            <!-- 学生视图按钮 -->
                            <div v-if="userStore.user.role === 2">
                                <Button v-if="mainAction" @click="mainAction.action" variant="outline"
                                    :disabled="mainAction.disabled">
                                    {{ mainAction.text }}
                                </Button>
                            </div>

                            <!-- 组管理员视图按钮 -->
                            <div v-if="userStore.user.role === 1" class="space-y-2">
                                <div class="flex space-x-2">
                                    <CreateExamDialog v-if="exam?.examPermissions.canEdit" :initial-data="exam">
                                        <template #trigger>
                                            <Button variant="outline">编辑考试</Button>
                                        </template>
                                    </CreateExamDialog>

                                    <!-- 继续选题按钮 -->
                                    <Button v-if="exam?.examPermissions.canSelectQuestions"
                                        @click="router.push(`/exam/create/${exam.id}`)" variant="outline">
                                        继续选题
                                    </Button>

                                    <!-- 主动作按钮 -->
                                    <Button v-if="mainAction" @click="mainAction.action" variant="outline"
                                        :disabled="mainAction.disabled">
                                        {{ mainAction.text }}
                                    </Button>
                                </div>
                                <div class="flex space-x-2">
                                    <Button v-if="exam?.examPermissions.canRelease" @click="releaseExam()"
                                        variant="outline">
                                        发布考试
                                    </Button>

                                    <Button v-if="exam?.examPermissions.canCancel" @click="cancelExam()"
                                        variant="outline">
                                        取消考试
                                    </Button>

                                    <Button v-if="exam?.examPermissions.canDelete" @click="deleteDraft()"
                                        variant="outline">
                                        删除草稿
                                    </Button>

                                    <!-- 已改完状态的特殊按钮（如查看统计）可以在此扩展 -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 右侧题目设置（保持不变） -->
            <div class="w-1/5 h-full shadow rounded-lg p-3 space-y-2 bg-white">
                <p class="font-bold">题目设置</p>
                <p class="text-gray-600 flex justify-between items-center">
                    考试时长：
                    <span class="text-black">
                        {{ dayjs(exam?.endTime).diff(dayjs(exam?.startTime), 'minute') }}分钟
                    </span>
                </p>
                <p class="text-gray-600 flex justify-between items-center">
                    题目数：<span class="text-black">{{ exam?.questions }}</span>
                </p>
            </div>
        </div>

        <!-- 下半部分：报告与时间线 -->
        <div class="flex-1 min-h-0 w-6/7 mx-auto flex justify-center space-x-5">
            <div
                class="h-full w-4/5 shadow rounded-lg p-2 bg-white overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
                <!-- AI报告：逻辑改为判断 isCompleted -->
                <div v-if="exam?.examPermissions.completed && userStore.user.role === 1">
                    <div class="flex" v-if="!report?.aiReport">
                        <div @click="aiManagerExamReport()"
                            class="flex cursor-pointer text-green-600 items-center px-2 py-2 mb-2 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                            <Spinner v-if="isGenerating" class="mr-1" />
                            <Rocket v-else class="h-4 w-4 mr-1" />
                            {{ isGenerating ? 'AI 正在思考...' : '生成 AI 诊断报告' }}
                        </div>
                    </div>
                    <div v-if="report?.aiReport || isGenerating"
                        class="p-4 bg-slate-50 border border-blue-100 rounded-lg">
                        <div class="text-sm text-gray-700" v-html="renderMarkdown(report?.aiReport || '')"></div>
                        <span v-if="isGenerating"
                            class="inline-block w-2 h-4 bg-blue-500 animate-pulse ml-1 align-middle"></span>
                    </div>
                </div>
            </div>

            <!-- 右侧时间线：逻辑改为判断 timelineStep -->
            <div class="w-1/5 h-full shadow rounded-lg p-3 space-y-2 bg-white">
                <p class="font-bold">时间线</p>
                <div class="flex justify-center items-center pt-5">
                    <div class="relative border-l border-slate-200 ml-3 space-y-8">
                        <div class="relative pl-8">
                            <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white"
                                :class="exam?.examPermissions.timelineStep! >= 1 ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                            </div>
                            <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                dayjs(exam?.startTime).format('YYYY/MM/DD HH:mm') }}</time>
                            <h3 class="text-sm font-semibold text-slate-700">考试开放时间</h3>
                        </div>

                        <!-- 学生专属步骤 -->
                        <template v-if="userStore.user.role === 2">
                            <div class="relative pl-8">
                                <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white "
                                    :class="exam?.examPermissions.timelineStep! >= 2 ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                                </div>
                                <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                    dayjs(exam?.userExamRecord?.enterTime).format('YYYY/MM/DD HH:mm') }}</time>
                                <h3 class="text-sm font-semibold text-slate-700">开始答题时间</h3>
                            </div>
                            <div class="relative pl-8">
                                <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white"
                                    :class="exam?.examPermissions.timelineStep! >= 3 ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                                </div>
                                <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                    dayjs(exam?.userExamRecord?.submitTime).format('YYYY/MM/DD HH:mm') }}</time>
                                <h3 class="text-sm font-semibold text-slate-700">结束答题时间</h3>
                            </div>
                        </template>

                        <div class="relative pl-8">
                            <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white"
                                :class="exam?.examPermissions.timelineStep! >= 4 ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                            </div>
                            <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                dayjs(exam?.endTime).format('YYYY/MM/DD HH:mm') }}</time>
                            <h3 class="text-sm font-semibold text-slate-700">考试截止时间</h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { computed, onMounted, ref, watch } from 'vue';
    import { useRoute } from 'vue-router'
    import axios from "@/utils/myAxios"
    import { Badge } from '@/components/ui/badge'
    import { Bug, Rocket } from 'lucide-vue-next';
    import { Button } from '@/components/ui/button'
    import dayjs from 'dayjs'
    import router from '@/router';
    import { useUserStore } from '@/stores/UserStore'
    import CreateExamDialog from './CreateOrUpdateExamDialog.vue';
    import MarkdownIt from 'markdown-it'
    import Spinner from '@/components/ui/spinner/Spinner.vue'
    const md = new MarkdownIt({ breaks: true }); // breaks: true 允许回车换行
    const renderMarkdown = (text: string) => md.render(text);
    const userStore = useUserStore()

    const route = useRoute()

    onMounted(() => {
        getExamInfo()
        getManagerReport()
    })

    watch(
        () => route.params.id,
        async () => {
            getExamInfo()
            getManagerReport()
        }
    )

    interface ExamInfo {
        id: number
        title: string
        status: number
        description: string
        teamId: number
        teamName: string
        questions: number
        startTime: string
        endTime: string
        userExamRecord: UserExamRecord
        examPermissions: ExamPermissions
    }

    interface UserExamRecord {
        id: number
        userId: number
        status: number
        description: string
        enterTime: string
        submitTime: string
    }

    interface ExamPermissions {
        // 基础状态开关
        draft: boolean;        // 草稿
        publishing: boolean
        published: boolean;    // 已发布
        grading: boolean;      // 改卷中
        completed: boolean;    // 已完成/已出分
        canceled: boolean      // 已取消

        // 组管理员权限
        canEdit: boolean;        // 可编辑
        canRelease: boolean;     // 可发布
        canDelete: boolean;      // 可删除
        canGrade: boolean;       // 可批改
        canSelectQuestions: boolean; // 可选题
        canCancel: boolean; // 可取消

        // 学生权限
        canStart: boolean;       // 首次进入考试
        canContinue: boolean;    // 继续答题
        canViewResult: boolean;  // 查看成绩

        // 进度步骤 1~4
        timelineStep: number;
    }

    const exam = ref<ExamInfo>()

    const getExamInfo = async () => {
        try {
            const res = await axios.get("/api/exam/info", {
                params: {
                    examId: route.params.id
                }
            })
            exam.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }


    // 状态 UI 映射
    const statusUI = computed(() => {
        const p = exam.value?.examPermissions;
        if (!p) return { text: '加载中', color: 'bg-gray-400' }

        if (p.canceled) return { text: '已取消', color: 'bg-red-500' }

        if (p.draft) return { text: '草稿', color: 'bg-orange-500' }

        if (p.publishing) return { text: '发布中', color: 'bg-orange-500' }

        if (p.published) {
            const now = dayjs();
            const start = dayjs(exam.value?.startTime)
            const end = dayjs(exam.value?.endTime)

            if (now.isBefore(start)) return { text: '未开始', color: 'bg-gray-400' }
            if (now.isAfter(end)) return { text: '已截止', color: 'bg-gray-600' }
            return { text: '进行中', color: 'bg-blue-500' }
        }

        if (p.grading) return { text: '阅卷中', color: 'bg-purple-500' }
        if (p.completed) return { text: '已结束', color: 'bg-gray-400' }

        return { text: '未知', color: 'bg-gray-200' }
    })

    // 按钮逻辑映射
    const mainAction = computed(() => {
        const p = exam.value?.examPermissions
        const role = userStore.user.role
        if (!p) return null

        if (role === 2) { // 学生
            if (p.canceled) return { text: '考试已取消', disabled: true }
            if (p.canStart) return { text: '开始答题', disabled: false, action: startExam }
            if (p.canContinue) return { text: '继续答题', disabled: false, action: startExam }

            // 逻辑修正：
            if (p.published) {
                const now = dayjs()
                const start = dayjs(exam.value?.startTime)

                // 时间还没到
                if (now.isBefore(start)) {
                    return { text: '等待开始', disabled: true }
                }
                // 时间到了，但后端说不能答题（说明已经有记录且提交了）
                if (exam.value?.userExamRecord) {
                    return { text: '已提交', disabled: true }
                }
                // 既没开始，也没记录
                return { text: '已截止', disabled: true }
            }

            if (p.completed) return { text: '查看结果', disabled: false, action: () => { } }
        } else { // 组管理员
            if (p.publishing) return { text: '发布中', disabled: true }
            if (p.canGrade) return { text: '去改卷', disabled: false, action: () => router.push(`/exam/grade/${exam.value?.id}`) }
            if (p.published) return { text: '进行中', disabled: true } // 组管理员看的是考试大状态
            if (p.completed) return { text: '已批改', disabled: true }
        }
        return null
    });

    const startExam = async () => {
        try {
            const res = await axios.post("/api/exam/user/start", {}, {
                params: {
                    examId: route.params.id
                }
            })

            router.push(`/exam/start/${res.data.data}`);
        } catch (e) {
            console.log(e)
        }
    }

    const releaseExam = async () => {
        try {
            const res = await axios.post("/api/exam/draft/release-exam", {}, {
                params: {
                    examId: route.params.id
                }
            })
            if (res.data.code === 1) {
                getExamInfo()
            }
        } catch (e) {
            console.log(e)
        }
    }

    const deleteDraft = async () => {
        try {
            const res = await axios.delete("/api/exam/draft/delete-draft", {
                params: {
                    examId: route.params.id
                }
            })
            if (res.data.code === 1) {
                router.replace("/exam")
            }
        } catch (e) {
            console.log(e)
        }
    }

    // 导师改完卷后显示
    const isGenerating = ref<boolean>(false)

    interface ExamCompleteReport {
        aiReport: string
    }

    const report = ref<ExamCompleteReport>()

    const getManagerReport = async () => {
        try {
            const res = await axios.get("/api/exam/complete-report", {
                params: {
                    examId: route.params.id
                }
            })
            report.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }

    const cancelExam = async () => {
        try {
            await axios.post("/api/exam/manager/cancel", {}, {
                params: {
                    examId: route.params.id
                }
            })
        } catch (e) {
            console.log(e)
        }
    }

    const aiManagerExamReport = () => {
        if (isGenerating.value) return
        isGenerating.value = true
        report.value!.aiReport = ''

        const eventSource = new EventSource(`/api/ai/report/exam-report?examId=${route.params.id}`)

        // 接收SSE消息，拼接内容
        eventSource.onmessage = (event) => {
            // 4. 响应式变量拼接必须操作.value
            report.value!.aiReport += event.data.replace(/\\n/g, '\n')
        }

        // 连接异常/结束处理
        eventSource.onerror = () => {
            isGenerating.value = false
            eventSource.close()
        }
    }
</script>