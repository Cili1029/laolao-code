<template>
    <!-- 最外层：强行锁死高度，防止出现全局滚动条 -->
    <SidebarProvider :style="{ '--sidebar-width': '350px' }" class="h-screen w-full overflow-hidden">
        <AppSidebar />
        <!-- 右侧容器：限制在父级高度内，保持列布局 (flex-col) -->
        <SidebarInset class="flex flex-col flex-1 h-full overflow-hidden min-w-0">
            <!-- 顶栏：保持 shrink-0 绝不被挤压 -->
            <header class="bg-background flex shrink-0 items-center gap-2 border-b p-4">
                <SidebarTrigger
                    :class="['-ml-1', examStore.examBegin ? 'pointer-events-none select-none opacity-50' : '']" />
                <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
                <div class="flex w-full justify-between">
                    <p class="cursor-pointer" @click="examStore.examBegin = !examStore.examBegin">开始考试</p>
                    <div v-if="examStore.examBegin" @click="examStore.judgeLoading ? '' : examStore.judge()"
                        class="flex text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded cursor-pointer">
                        <CloudUpload v-if="!examStore.judgeLoading" class="h-4 w-4 mr-1" />
                        <Spinner class="mr-1" v-else />
                        提交答案
                    </div>
                    <div v-if="examStore.examBegin" @click="examStore.submitLoading ? '' : examStore.submitExam()"
                        class="flex text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded cursor-pointer">
                        <CheckSquare v-if="!examStore.submitLoading" class="h-4 w-4 mr-1" />
                        <Spinner class="mr-1" v-else />
                        交卷
                    </div>
                </div>
            </header>

            <!-- 用一个 main 包装器接管剩余空间，并承担局部滚动条的职责 -->
            <!-- min-h-0 是解决多层 Flex 嵌套导致高度被无限撑破的关键 -->
            <main class="flex flex-1 flex-col overflow-y-auto min-h-0 relative">
                <!-- 注入的路由页面会继承 main 的高度并可以继续向下使用 flex -->
                <RouterView class="flex flex-1 flex-col"></RouterView>
            </main>

        </SidebarInset>
    </SidebarProvider>
</template>

<script setup lang="ts">
    import AppSidebar from "@/components/user/sidebar/AppSidebar.vue"
    import { Separator } from "@/components/ui/separator"
    import { SidebarInset, SidebarProvider, SidebarTrigger, } from "@/components/ui/sidebar"
    import { RouterView } from "vue-router"
    import { Spinner } from '@/components/ui/spinner'
    import { CheckSquare, CloudUpload } from "lucide-vue-next"
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()

</script>