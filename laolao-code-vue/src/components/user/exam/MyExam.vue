<template>
    <div class="h-full p-4 flex flex-col space-y-6">
        <div class="flex flex-col justify-between">
            <h1 class="text-2xl font-bold">我的考试</h1>
            <p v-if="userStore.user.role == 2" class="text-muted-foreground">查看你所有的考试任务</p>
            <p v-else class="text-muted-foreground">查看你所有发布的考试任务</p>
        </div>

        <!-- 小组列表 -->
        <div v-if="exams.length !== 0" class="space-y-2">
            <!-- 小组卡片 -->
            <RouterLink v-for="exam in exams" :key="exam.id" :to="'/exam/' + exam.id"
                class="flex flex-col md:flex-row justify-between border shadow-sm rounded-xl p-3 cursor-pointer hover:bg-gray-50 transition-colors group">
                <div class="flex items-center space-x-4">
                    <!-- 状态图标 -->
                    <div class="bg-slate-100 p-3 rounded-lg group-hover:bg-blue-50 transition-colors">
                        <ClipboardList class="h-6 w-6 text-slate-600 group-hover:text-blue-600" />
                    </div>

                    <div class="space-y-1">
                        <div class="flex items-center gap-2">
                            <h3 class="text-lg font-semibold">{{ exam.name }}</h3>
                            <Badge variant="secondary" class="border-none"
                                :class="['border-none text-white', getExamStatusUI(exam).color]">
                                {{ getExamStatusUI(exam).text }}
                            </Badge>
                            <ChevronRight
                                class="h-4 w-4 ml-1 opacity-0 group-hover:opacity-100 transition-all transform group-hover:translate-x-1" />
                        </div>
                        <div class="flex items-center text-sm text-gray-500 space-x-3">
                            <span class="flex items-center">
                                <Users class="h-3.5 w-3.5 mr-1" />
                                {{ exam.teamName }}
                            </span>
                            <span class="text-gray-300">|</span>
                            <p class="max-w-xs">{{ exam.description }}</p>
                        </div>
                    </div>
                </div>

                <!-- 时间信息 -->
                <div class="flex text-sm font-medium text-gray-600 rounded-md">
                    <CalendarDays class="h-4 w-4 mr-2 text-gray-500" />
                    {{ exam.startTime.replace("T", " ") }}
                </div>
            </RouterLink>
        </div>

        <!-- 空状态 -->
        <div v-else class="flex flex-col flex-1 justify-center items-center text-center">
            <div class="bg-white p-4 rounded-full shadow inline-block mb-4">
                <Ghost class="h-10 w-10 text-gray-600" />
            </div>
            <p class="text-gray-600 font-medium">暂无考试</p>
            <p class="text-sm text-gray-600">{{ userStore.user.role === 2 ? "导师发布后会在此显示" : "你创建的考试会在此显示" }}</p>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from 'vue'
    import axios from "@/utils/myAxios"
    import { ClipboardList, CalendarDays, Users, ChevronRight, Ghost } from "lucide-vue-next"
    import { Badge } from '@/components/ui/badge'
    import { useUserStore } from '@/stores/UserStore'
    const userStore = useUserStore()
    import dayjs from 'dayjs'

    interface Exam {
        id: number
        name: string
        teamName: string
        description: string
        summaryPermissions: SummaryPermissions
        startTime: string
        endTime: string
    }

    interface SummaryPermissions {
        // 基础状态开关
        draft: boolean;        // 草稿
        publishing: boolean
        published: boolean;    // 已发布
        grading: boolean;      // 改卷中
        completed: boolean;    // 已完成/已出分
        canceled: boolean      // 已取消
    }

    const getExamStatusUI = (exam: Exam) => {
        const p = exam.summaryPermissions;
        if (!p) return { text: '加载中', color: 'bg-gray-400' }

        if (p.canceled) return { text: '已取消', color: 'bg-red-500' }

        if (p.draft) return { text: '草稿', color: 'bg-orange-500' }

        if (p.publishing) return { text: '发布中', color: 'bg-orange-500' }

        if (p.published) {
            const now = dayjs();
            const start = dayjs(exam.startTime)
            const end = dayjs(exam.endTime)

            if (now.isBefore(start)) return { text: '未开始', color: 'bg-gray-400' }
            if (now.isAfter(end)) return { text: '已截止', color: 'bg-gray-600' }
            return { text: '进行中', color: 'bg-blue-500' }
        }

        if (p.grading) return { text: '阅卷中', color: 'bg-purple-500' }
        if (p.completed) return { text: '已结束', color: 'bg-gray-400' }

        return { text: '未知', color: 'bg-gray-200' }
    }

    const exams = ref<Exam[]>([])

    const getExam = async () => {
        try {
            const res = await axios.get("/api/exam")
            // 假设后端返回的数据结构是 res.data.data
            exams.value = res.data.data
        } catch (e) {
            console.error("获取考试失败:", e)
        }
    }

    onMounted(() => {
        getExam()
    })
</script>