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
                    <div class="w-1/2">
                        <div class="flex justify-end mb-1">
                            <Badge variant="secondary"
                                :class="['mb-2',
                                    dayjs().isBetween(exam?.startTime, exam?.endTime, null, '[]') ?
                                        'text-white bg-blue-500 dark:bg-blue-600' : 'bg-gray-200 dark:bg-gray-800 dark:text-white']">
                                {{ dayjs().isBetween(exam?.startTime, exam?.endTime, null, '[]') ? '进行中' :
                                    dayjs().isAfter(exam?.startTime) ? "已结束" : "未开始" }}
                            </Badge>
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
                <Stepper orientation="vertical" class="mx-auto flex w-full max-w-md flex-col justify-start gap-10">
                    <StepperItem v-for="step in steps" :key="step.step" v-slot="{ state }"
                        class="relative flex w-full items-start gap-6" :step="step.step">
                        <StepperSeparator v-if="step.step !== steps[steps.length - 1]?.step"
                            class="absolute left-4.5 top-9.5 block h-[105%] w-0.5 shrink-0 rounded-full bg-muted group-data-[state=completed]:bg-primary" />
                        <StepperTrigger as-child>
                            <Button :variant="state === 'completed' || state === 'active' ? 'default' : 'outline'"
                                size="icon" class="z-10 rounded-full shrink-0"
                                :class="[state === 'active' && 'ring-2 ring-ring ring-offset-2 ring-offset-background']">
                                <Check v-if="state === 'completed'" class="size-5" />
                                <Circle v-if="state === 'active'" />
                                <Dot v-if="state === 'inactive'" />
                            </Button>
                        </StepperTrigger>
                        <div class="flex flex-col gap-1">
                            <StepperTitle :class="[state === 'active' && 'text-primary']"
                                class="text-sm font-semibold transition">
                                {{ step.title }}
                            </StepperTitle>
                            <StepperDescription :class="[state === 'active' && 'text-primary']"
                                class="sr-only text-xs text-muted-foreground transition md:not-sr-only">
                                {{ step.description }}
                            </StepperDescription>
                        </div>
                    </StepperItem>
                </Stepper>
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
    import dayjs from 'dayjs'
    import { Check, Circle, Dot } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Stepper, StepperDescription, StepperItem, StepperSeparator, StepperTitle, StepperTrigger } from '@/components/ui/stepper'

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

    const steps = [
        {
            step: 1,
            title: dayjs(exam.value?.startTime).format('YYYY/MM/DD HH:mm:ss'),
            description:
                '考试开始时间',
        },
        {
            step: 2,
            title: dayjs(exam.value?.startTime).format('YYYY/MM/DD HH:mm:ss'),
            description: '考试实际开始答题的时间',
        },
        {
            step: 3,
            title: dayjs(exam.value?.endTime).format('YYYY/MM/DD HH:mm:ss'),
            description:
                '考生提交时间',
        },
        {
            step: 4,
            title: dayjs(exam.value?.endTime).format('YYYY/MM/DD HH:mm:ss'),
            description:
                '考生结束时间',
        },
    ]
</script>