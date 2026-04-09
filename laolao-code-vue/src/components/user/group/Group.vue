<template>
    <div class="p-4 flex flex-col space-y-3">
        <div class="flex justify-between">
            <div>
                <p class="text-xl font-bold">{{ group?.name }}</p>
                <p class="text-gray-500">{{ group?.description }}</p>
                <p class="text-sm text-gray-500">邀请码：{{ group?.inviteCode }}</p>
            </div>
            <div class="grid grid-cols-3 gap-x-4 justify-end items-baseline">
                <HoverCard>
                    <HoverCardTrigger as-child>
                        <div class="text-right">
                            <p class="text-xs h-4 mb-2 text-gray-500">学习组导师</p>
                            <div class="flex items-center justify-end cursor-pointer">
                                <Avatar class="h-8 w-8 rounded-4xl">
                                    <AvatarImage :src="group?.avatar || ''" />
                                    <AvatarFallback class="rounded-4xl">
                                        <CircleUserRound class="h-8 w-8" />
                                    </AvatarFallback>
                                </Avatar>
                                <p class="text-sm ml-2">{{ group?.managerName }}</p>
                            </div>
                        </div>
                    </HoverCardTrigger>
                    <HoverCardContent>
                        <div class="space-y-1">
                            <h4 class="text-sm font-semibold">
                                {{ group?.managerName }}
                            </h4>
                            <p class="text-sm">
                                账号：{{ group?.username }}
                            </p>
                            <div class="flex items-center pt-2">
                                <CalendarDaysIcon class="mr-2 h-4 w-4 opacity-70" />
                                <span class="text-xs text-muted-foreground">
                                    26年加入劳劳Code
                                </span>
                            </div>
                        </div>
                    </HoverCardContent>
                </HoverCard>
                <div class="text-right">
                    <p class="text-xs h-4 mb-2 text-gray-500">班级人数</p>
                    <p>{{ group?.userCount }}</p>
                </div>
                <div class="text-right">
                    <p class="text-xs h-4 mb-2 text-gray-500">考试数</p>
                    <p>{{ exams.length }}</p>
                </div>
            </div>
        </div>
        <div class="space-y-2">
            <div class="flex justify-between pb-2">
                <p class="text-lg">考试列表</p>
                <CreateOrUpdateExamDialog v-if="userStore.user.role === 1 && group?.managerName === userStore.user.name" />
            </div>

            <RouterLink :to="'/exam/' + exam.id" v-for="exam in exams" :key="exam.id"
                class="flex justify-between border shadow rounded-lg p-3.5 cursor-pointer hover:bg-gray-100">
                <div class="flex items-center space-x-2">
                    <Bug class="h-8 w-8 text-blue-500" />
                    <div>
                        <p>{{ exam.title }}</p>
                        <p class="text-sm text-gray-500">开放时间：{{ exam.startTime.split("T")[0] }}</p>
                    </div>
                </div>
                <div>
                    <div class="flex justify-end mb-1">
                        <Badge v-if="userStore.user.role === 2" variant="secondary"
                            :class="['mb-2',
                                dayjs().isBetween(exam.startTime, exam.endTime, null, '[]') ?
                                    'text-white bg-blue-500 dark:bg-blue-600' : 'bg-gray-200 dark:bg-gray-800 dark:text-white']">
                            {{ dayjs().isBetween(exam.startTime, exam.endTime, null, '[]') ? '进行中' :
                                dayjs().isAfter(exam.startTime) ? "已结束" : "未开始" }}
                        </Badge>
                        <Badge v-else-if="userStore.user.role === 1" variant="secondary" :class="['mb-2',
                            exam?.status === 0 ?
                                'text-white bg-orange-500 dark:bg-orange-600' :
                                exam?.status === 1 ? 'text-white bg-blue-500 dark:bg-blue-600' : 'bg-gray-200 dark:bg-gray-800 dark:text-white'
                        ]">
                            {{ exam?.status === 0 ? '草稿' :
                                exam?.status === 1 ? "已发布" : "已结束" }}
                        </Badge>
                    </div>

                    <div class="flex space-x-2">
                        <p class="text-sm text-gray-500">{{ group?.name }}</p>
                        <p class="flex items-center text-sm text-gray-500">
                            <User class="h-4 w-4" />
                            {{ group?.managerName }}
                        </p>
                    </div>
                </div>
            </RouterLink>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref, watch } from 'vue';
    import { useRoute } from 'vue-router'
    import axios from "@/utils/myAxios"
    import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
    import { CircleUserRound, CalendarDaysIcon, Bug, User } from 'lucide-vue-next'
    import { HoverCard, HoverCardContent, HoverCardTrigger } from '@/components/ui/hover-card'
    import { Badge } from '@/components/ui/badge'
    import dayjs from 'dayjs'
    import { useUserStore } from '@/stores/UserStore'
    import CreateOrUpdateExamDialog from '../exam/CreateOrUpdateExamDialog.vue';
    const userStore = useUserStore()

    const route = useRoute()

    onMounted(() => {
        getDetailGroup()
        getGroupExam()
    })

    watch(
        () => route.params.id,
        async () => {
            getDetailGroup()
            getGroupExam()
        }
    )

    interface DetailGroup {
        avatar: string
        name: string
        description: string
        inviteCode: string
        userCount: number
        username: string
        managerName: string
    }

    const group = ref<DetailGroup>()

    const getDetailGroup = async () => {
        try {
            const res = await axios.get("/api/team/detail-base", {
                params: {
                    teamId: route.params.id
                }
            })
            group.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }

    interface GroupExam {
        id: number,
        status: number,
        title: string,
        startTime: string,
        endTime: string
    }

    const exams = ref<GroupExam[]>([])
    const getGroupExam = async () => {
        try {
            const res = await axios.get("/api/team/detail-exam", {
                params: {
                    teamId: route.params.id
                }
            })
            exams.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }
</script>