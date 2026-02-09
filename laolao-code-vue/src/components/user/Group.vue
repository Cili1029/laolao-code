<template>
    <div class="p-4 flex flex-col space-y-3">
        <div class="flex justify-between">
            <div>
                <p class="text-xl font-bold">{{ group?.name }}</p>
                <p>{{ group?.description }}</p>
            </div>
            <div class="grid grid-cols-3 gap-x-4 justify-end items-baseline">
                <HoverCard>
                    <HoverCardTrigger as-child>
                        <div class="text-right">
                            <p class="text-xs h-4 mb-2">学习组导师</p>
                            <div class="flex items-center justify-end cursor-pointer">
                                <Avatar class="h-8 w-8 rounded-4xl">
                                    <AvatarImage :src="group?.avatar || ''" />
                                    <AvatarFallback class="rounded-4xl">
                                        <CircleUserRound class="h-8 w-8" />
                                    </AvatarFallback>
                                </Avatar>
                                <p class="text-sm ml-2">{{ group?.advisorName }}</p>
                            </div>
                        </div>
                    </HoverCardTrigger>
                    <HoverCardContent>
                        <div class="space-y-1">
                            <h4 class="text-sm font-semibold">
                                {{ group?.advisorName }}
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
                    <p class="text-xs h-4 mb-2">班级人数</p>
                    <p>{{ group?.memberCount }}</p>
                </div>
                <div class="text-right">
                    <p class="text-xs h-4 mb-2">考试数</p>
                    <p>50</p>
                </div>
            </div>
        </div>
        <div>
            <p class="text-lg">考试列表</p>
        </div>

    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref, watch } from 'vue';
    import { useRoute } from 'vue-router'
    import axios from "@/utils/myAxios"
    import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
    import { CircleUserRound, CalendarDaysIcon } from 'lucide-vue-next'
    import { HoverCard, HoverCardContent, HoverCardTrigger } from '@/components/ui/hover-card'

    const route = useRoute()

    onMounted(() => {
        getDetailGroup()
    })

    watch(
        () => route.params.id,
        async () => {
            getDetailGroup()
        }
    )

    interface DetailGroup {
        avatar: string
        name: string
        description: string
        memberCount: number
        username: string
        advisorName: string
    }

    const group = ref<DetailGroup>()

    const getDetailGroup = async () => {
        try {
            const res = await axios.get("/api/group/detail-base", {
                params: {
                    groupId: route.params.id
                }
            })
            group.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }
</script>