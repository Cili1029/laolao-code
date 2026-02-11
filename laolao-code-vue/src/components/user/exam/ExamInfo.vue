<template>
    <div class="bg-blue-50 p-5 flex flex-col space-y-5">
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
                        <p class="text-gray-600">{{ exam?.group }}</p>
                        <p class="text-gray-600">{{ exam?.description }}</p>
                    </div>
                    <div class="w-1/2 flex flex-col">
                        <div class="flex justify-end mb-1">
                            <Badge variant="secondary"
                                :class="['mb-2',
                                    dayjs().isBetween(exam?.startTime, exam?.endTime, null, '[]') ?
                                        'text-white bg-blue-500 dark:bg-blue-600' : 'bg-gray-200 dark:bg-gray-800 dark:text-white']">
                                {{ dayjs().isBetween(exam?.startTime, exam?.endTime, null, '[]') ? '进行中' :
                                    dayjs().isAfter(exam?.startTime) ? "已结束" : "未开始" }}
                            </Badge>
                        </div>
                        <div class="flex flex-1 items-center justify-center">
                            <Button @click="startExam()" variant="outline"
                                :disabled="!dayjs().isBetween(exam?.startTime, exam?.endTime, null, '[]')">
                                {{ dayjs().isBetween(exam?.startTime, exam?.endTime, null, '[]') ? '开始答题' :
                                    dayjs().isAfter(exam?.startTime) ? "已结束" : "未开始" }}
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="w-1/5 h-full shadow rounded-lg p-3 space-y-2 bg-white">
                <p class="font-bold">题目设置</p>
                <p class="text-gray-600 flex justify-between items-center">
                    考试时长：
                    <span class="text-black">
                        {{ dayjs(exam?.endTime).diff(dayjs(exam?.startTime), 'minute') }}分钟
                    </span>
                </p>
                <p class="text-gray-600 flex justify-between items-center">
                    题目数：
                    <span class="text-black">
                        {{ exam?.questions }}
                    </span>
                </p>
            </div>
        </div>
        <div class="h-full w-6/7 mx-auto flex justify-center space-x-5">
            <div class="h-full w-4/5 shadow rounded-lg flex justify-between p-3 bg-white">
                <div class="w-1/2 space-y-2">
                    <p class="font-bold">考试信息</p>
                    <p class="text-gray-600">{{ exam?.title }}</p>
                    <p class="text-gray-600">{{ exam?.group }}</p>
                    <p class="text-gray-600">{{ exam?.description }}</p>
                </div>
            </div>
            <div class="w-1/5 h-full shadow rounded-lg p-3 space-y-2 bg-white">
                <p class="font-bold">时间线</p>
                <div class="flex justify-center items-center pt-5">
                    <div class="relative border-l border-slate-200 ml-3 space-y-8">
                        <div class="relative pl-8">
                            <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white"
                                :class="dayjs().isAfter(exam?.startTime) ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                            </div>
                            <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                dayjs(exam?.startTime).format('YYYY/MM/DD HH:mm') }}</time>
                            <h3 class="text-sm font-semibold text-slate-700">考试开放时间</h3>
                        </div>
                        <div class="relative pl-8">
                            <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white "
                                :class="dayjs().isAfter(exam?.startTime) ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                            </div>
                            <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                dayjs(exam?.startTime).format('YYYY/MM/DD HH:mm') }}</time>
                            <h3 class="text-sm font-semibold text-slate-700">考生开始答题时间</h3>
                        </div>
                        <div class="relative pl-8">
                            <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white"
                                :class="dayjs().isAfter(exam?.endTime) ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                            </div>
                            <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                dayjs(exam?.endTime).format('YYYY/MM/DD HH:mm') }}</time>
                            <h3 class="text-sm font-semibold text-slate-700">考生结束答题时间</h3>
                        </div>
                        <div class="relative pl-8">
                            <div class="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white"
                                :class="dayjs().isAfter(exam?.endTime) ? 'bg-blue-500 ring-4 ring-blue-50' : 'bg-slate-300'">
                            </div>
                            <time class="mb-1 text-xs font-mono text-slate-600 uppercase tracking-wide">{{
                                dayjs(exam?.endTime).format('YYYY/MM/DD HH:mm') }}</time>
                            <h3 class="text-sm font-semibold text-slate-700">考试结束时间</h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref, watch } from 'vue';
    import { useRoute } from 'vue-router'
    import axios from "@/utils/myAxios"
    import { Bug } from 'lucide-vue-next'
    import { Badge } from '@/components/ui/badge'
    import { Button } from '@/components/ui/button'
    import dayjs from 'dayjs'
    import router from '@/router';

    const route = useRoute()

    onMounted(() => {
        getExamInfo()
    })

    watch(
        () => route.params.id,
        async () => {
            getExamInfo()
        }
    )

    interface ExamInfo {
        id: number
        title: string
        description: string
        group: string
        questions: number
        startTime: string,
        endTime: string
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

    const startExam = async () => {
        try {
            const res = await axios.post("/api/exam/start", {}, {
                params: {
                    examId: route.params.id
                }
            })
            router.push(`/exam/start/${res.data.data}`);
        } catch (e) {
            console.log(e)
        }
    }
</script>