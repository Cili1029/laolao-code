<template>
    <div v-if="props.status !== 3" class="flex flex-col h-full items-center justify-center gap-4 text-center">
        <div class="rounded-full bg-muted/30 p-4">
            <Ghost class="h-10 w-10 text-muted-foreground/60" />
        </div>
        <div class="space-y-1">
            <h3 class="text-lg font-medium">{{ testStatus.text }}</h3>
            <p class="text-sm text-muted-foreground">
                {{ testStatus.description }}
            </p>
        </div>
    </div>

    <!-- 组管理员（批改后） -->
    <div v-if="props.status === 3 && userStore.user.role === 1" class="flex flex-col">
        <div class="flex" v-if="!report?.aiReport">
            <div @click="aiManagerExamReport()"
                class="flex cursor-pointer text-green-600 items-center px-2 py-2 mb-2 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                <Spinner v-if="isGenerating" class="mr-1" />
                <Rocket v-else class="h-4 w-4 mr-1" />
                {{ isGenerating ? 'AI 正在思考...' : '生成 AI 诊断报告' }}
            </div>
        </div>

        <!-- 展开/收起 按钮 -->
        <div v-if="report?.aiReport" @click="showAiReport = !showAiReport"
            class="cursor-pointer text-blue-600 text-sm mt-2 mb-1 flex items-center gap-1">
            <Play class="h-6 w-6 transform transition-transform duration-200" :class="{ 'rotate-90': showAiReport }" />
            {{ showAiReport ? '收起AI报告' : '查看AI报告' }}
        </div>

        <!-- 带过渡动画的内容 -->
        <div class="overflow-hidden transition-all duration-300 ease-in-out"
            :style="{ maxHeight: showAiReport ? '2000px' : '0px', opacity: showAiReport ? '1' : '0' }">
            <div v-if="report?.aiReport || isGenerating" class="p-3 bg-slate-50 border border-blue-100 rounded-lg">
                <div class="text-sm text-gray-700" v-html="renderMarkdown(report?.aiReport || '')"></div>
                <span v-if="isGenerating"
                    class="inline-block w-2 h-4 bg-blue-600 animate-pulse ml-1 align-middle"></span>
            </div>
        </div>

        <Table>
            <TableHeader>
                <TableRow>
                    <TableHead>
                        考生
                    </TableHead>
                    <TableHead>状态</TableHead>
                    <TableHead>分数</TableHead>
                    <TableHead></TableHead>
                </TableRow>
            </TableHeader>
            <TableBody>
                <TableRow v-for="user in report?.userList">
                    <TableCell class="font-medium">
                        {{ user.name }}
                    </TableCell>
                    <TableCell>{{ user.score ? "参加" : "缺考" }}</TableCell>
                    <TableCell>{{ user.score ? user.score : "/" }}</TableCell>
                    <TableCell class="text-right">
                        <RouterLink v-if="user.examRecordId" :to="'/user-report/' + user.examRecordId"
                            class=" text-blue-600">查看详细</RouterLink>
                    </TableCell>
                </TableRow>
            </TableBody>
        </Table>
    </div>

    <!-- 考生 各题情况 -->
    <div v-if="props.status === 3 && userStore.user.role === 2">
        <div class="flex justify-between">
            <p>
                总分：<span class="font-bold">{{ userAnswerInfo?.score ? userAnswerInfo.score : '缺考' }}</span>
            </p>
            <RouterLink v-if="userAnswerInfo?.examRecordId" :to="'/user-report/' + userAnswerInfo?.examRecordId"
                class=" text-blue-600">查看详细
            </RouterLink>
        </div>
        <Table v-if="userAnswerInfo?.examRecordId">
            <TableHeader>
                <TableRow>
                    <TableHead>题目</TableHead>
                    <TableHead>总分</TableHead>
                    <TableHead>得分</TableHead>
                </TableRow>
            </TableHeader>
            <TableBody>
                <TableRow v-for="question in userAnswerInfo?.questions">
                    <TableCell>
                        {{ question.title }}
                    </TableCell>
                    <TableCell>{{ question.questionScore }}</TableCell>
                    <TableCell>{{ question.userScore ? question.userScore : '未作答' }}</TableCell>
                </TableRow>
            </TableBody>
        </Table>
    </div>
</template>

<script setup lang="ts">
    import { Ghost, Play, Rocket } from 'lucide-vue-next';
    import { computed, ref, watch } from 'vue';
    import axios from "@/utils/myAxios"
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import MarkdownIt from 'markdown-it'
    const md = new MarkdownIt({ breaks: true }); // breaks: true 允许回车换行
    const renderMarkdown = (text: string) => md.render(text);
    import Spinner from '@/components/ui/spinner/Spinner.vue'
    import { useUserStore } from '@/stores/UserStore';
    const userStore = useUserStore()

    const props = defineProps<{
        examId: number
        status: number
    }>()

    // 数据获取方法
    const fetchData = async () => {
        if (!props.examId) return
        if (props.status === 3 && userStore.user.role === 1) {
            await getManagerReport()
        } else if (props.status === 3 && userStore.user.role === 2) {
            await getUserAnswerInfo()
        }
        return
    }

    // 监听 status 和 examId 的变化
    watch(
        () => [props.status, props.examId, userStore.user.role],
        () => {
            fetchData();
        },
        { immediate: true }
    )

    // 导师改完卷后显示
    const isGenerating = ref<boolean>(false)

    interface UserList {
        name: string
        score: number
        examRecordId: number
    }

    interface ExamCompleteReport {
        aiReport: string
        userList: UserList[]
    }

    const report = ref<ExamCompleteReport>()

    const getManagerReport = async () => {
        try {
            const res = await axios.get("/api/exam/complete-report", {
                params: {
                    examId: props.examId
                }
            })
            report.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }

    const showAiReport = ref(false)

    const aiManagerExamReport = () => {
        if (isGenerating.value) return
        isGenerating.value = true
        report.value!.aiReport = ''

        const eventSource = new EventSource(`/api/ai/report/exam-report?examId=${props.examId}`)

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

    interface Question {
        title: string
        questionScore: number
        userScore: number

    }

    interface UserAnswerInfo {
        score: number
        examRecordId: number
        questions: Question[]
    }

    const userAnswerInfo = ref<UserAnswerInfo>()

    const getUserAnswerInfo = async () => {
        try {
            const res = await axios.get("/api/exam/user-answer-report", {
                params: {
                    examId: props.examId
                }
            })
            userAnswerInfo.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }

    const testStatus = computed(() => {
        if (props.status === 0) {
            return {
                text: '草稿',
                description: '发布后会在此显示组作答信息'
            }
        }
        if (props.status === 5) {
            return {
                text: '发布中',
                description: '请耐心等待'
            }
        }
        if (props.status === 1) {
            return {
                text: '已发布',
                description: '考试完成后会在此显示组作答信息'
            }
        }
        if (props.status === 2) {
            return {
                text: '改卷中',
                description: '改卷完成后会在此显示作答信息'
            }
        }
        if (props.status === 4) {
            return {
                text: '考试已取消',
                description: ''
            }
        }
        return {
            text: '加载中',
            description: '请耐心等待'
        }
    })
</script>