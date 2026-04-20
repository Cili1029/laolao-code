<template>
    <div class="p-4 flex flex-col space-y-6">
        <div class="flex justify-between items-center">
            <div>
                <h1 class="text-2xl font-bold">我的小组</h1>
                <p v-if="UserStore.user.role == 2" class="text-muted-foreground">你已加入的小组列表及其基本信息</p>
                <p v-else class="text-muted-foreground">你管理的小组列表及其基本信息</p>
            </div>
            <GroupDialog />
        </div>

        <!-- 小组列表 -->
        <div v-if="groups.length !== 0" class="space-y-2">
            <!-- 小组卡片 -->
            <RouterLink v-for="group in groups" :key="group.id" :to="'/group/' + group.id"
                class="flex flex-col md:flex-row justify-between border shadow-sm rounded-xl p-3 cursor-pointer hover:bg-gray-50 transition-colors group">
                <div class="flex items-center space-x-4">
                    <div class="bg-blue-50 p-3 rounded-lg group-hover:bg-blue-100 transition-colors">
                        <BookOpen class="h-6 w-6 text-blue-600" />
                    </div>

                    <div class="space-y-1">
                        <h3 class="text-lg font-semibold flex items-center">
                            {{ group.name }}
                            <ChevronRight
                                class="h-4 w-4 ml-1 opacity-0 group-hover:opacity-100 transition-all transform group-hover:translate-x-1" />
                        </h3>
                        <p class="text-sm text-gray-500 max-w-md">
                            {{ group.description || '暂无小组描述' }}
                        </p>
                    </div>
                </div>

                <div class="flex items-center mt-4 md:mt-0 space-x-8">
                    <!-- 导师信息 -->
                    <div class="flex flex-col items-end">
                        <p class="text-xs text-gray-400 mb-1">组管理员</p>
                        <div class="flex items-center space-x-2">
                            <Avatar class="h-6 w-6">
                                <AvatarFallback class="text-[10px] bg-orange-100 text-orange-600">
                                    {{ group.manager?.charAt(0) }}
                                </AvatarFallback>
                            </Avatar>
                            <span class="text-sm font-medium">{{ group.manager }}</span>
                        </div>
                    </div>
                </div>
            </RouterLink>
        </div>
        <!-- 空状态 -->
        <div v-else class="flex flex-col flex-1 justify-center items-center text-center">
            <div class="bg-white p-4 rounded-full shadow inline-block mb-4">
                <Ghost class="h-10 w-10 text-gray-600" />
            </div>
            <p class="text-gray-600 font-medium">暂无小组</p>
            <p class="text-sm text-gray-600">加入小组后会在此显示</p>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from 'vue'
    import axios from "@/utils/myAxios"
    import { BookOpen, ChevronRight, Ghost } from "lucide-vue-next"
    import { Avatar, AvatarFallback } from '@/components/ui/avatar'
    import GroupDialog from './GroupDialog.vue'
    import { useUserStore } from '@/stores/UserStore'
    const UserStore = useUserStore()

    interface Group {
        id: number
        name: string
        manager: string
        description: string
    }

    const groups = ref<Group[]>([])

    const getMyTeam = async () => {
        try {
            const res = await axios.get("/api/team")
            groups.value = res.data.data
        } catch (e) {
            console.error("获取小组失败:", e)
        }
    }

    onMounted(() => {
        getMyTeam()
    })
</script>