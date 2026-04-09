<template>
    <div class="h-full p-4 flex flex-col space-y-6">
        <div class="flex flex-col justify-between">
            <h1 class="text-2xl font-bold">我的考试</h1>
            <p v-if="UserStore.user.role == 2" class="text-muted-foreground">查看你所有的考试任务</p>
            <p v-else class="text-muted-foreground">查看你所有发布的考试任务</p>
        </div>

        <!-- 学习组列表 -->
        <div v-if="exams.length !== 0" class="space-y-2">
            <!-- 学习组卡片 -->
            <RouterLink v-for="exam in exams" :key="exam.id" :to="'/exam/' + exam.id"
                class="flex flex-col md:flex-row justify-between border shadow-sm rounded-xl p-3 cursor-pointer hover:bg-gray-50 transition-colors group">
                <div class="flex items-center space-x-4">
                    <!-- 状态图标 -->
                    <div class="bg-slate-100 p-3 rounded-lg group-hover:bg-blue-50 transition-colors">
                        <Bug v-if="exam.name.includes('代码') || exam.name.includes('Debug')"
                            class="h-6 w-6 text-blue-600" />
                        <ClipboardList v-else class="h-6 w-6 text-slate-600 group-hover:text-blue-600" />
                    </div>

                    <div class="space-y-1">
                        <div class="flex items-center gap-2">
                            <h3 class="text-lg font-semibold">{{ exam.name }}</h3>
                            <Badge variant="secondary" class="border-none" :class="dayjs().isBetween(exam.startTime, exam.endTime, null, '[]') ?
                                'bg-green-100 text-green-700' : 'bg-gray-100'">
                                {{ dayjs().isBetween(exam.startTime, exam.endTime, null, '[]') ? '进行中' :
                                    dayjs().isAfter(exam.startTime) ? "已结束" : dayjs(exam.startTime).fromNow() }}
                            </Badge>
                        </div>
                        <div class="flex items-center text-sm text-gray-500 space-x-3">
                            <span class="flex items-center">
                                <Users class="h-3.5 w-3.5 mr-1" />
                                {{ exam.team }}
                            </span>
                            <span class="text-gray-300">|</span>
                            <p class="max-w-xs">{{ exam.description }}</p>
                        </div>
                    </div>
                </div>

                <div class="flex flex-col md:items-end justify-center mt-4 md:mt-0 space-y-2">
                    <!-- 时间信息 -->
                    <div class="flex items-center text-sm font-medium text-gray-600 px-3 py-1 rounded-md">
                        <CalendarDays class="h-4 w-4 mr-2 text-gray-500" />
                        {{ exam.startTime.replace("T", " ") }}
                    </div>

                    <!-- 提示文字 -->
                    <p
                        class="text-[12px] text-blue-600 font-medium flex items-center opacity-0 group-hover:opacity-100 transition-opacity">
                        点击进入考试系统
                        <ChevronRight class="h-3 w-3 ml-0.5" />
                    </p>
                </div>
            </RouterLink>
        </div>

        <!-- 空状态 -->
        <div v-else class="flex flex-col flex-1 justify-center items-center text-center">
            <div class="bg-white p-4 rounded-full shadow inline-block mb-4">
                <Ghost class="h-10 w-10 text-gray-600" />
            </div>
            <p class="text-gray-600 font-medium">暂无考试</p>
            <p class="text-sm text-gray-600">导师发布后会在此显示</p>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from 'vue'
    import axios from "@/utils/myAxios"
    import { ClipboardList, CalendarDays, Users, Bug, ChevronRight, Ghost } from "lucide-vue-next"
    import { Badge } from '@/components/ui/badge'
    import { useUserStore } from '@/stores/UserStore'
    const UserStore = useUserStore()
    import dayjs from 'dayjs'

    interface Exam {
        id: number
        name: string
        team: string
        description: string
        startTime: string
        endTime: string
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