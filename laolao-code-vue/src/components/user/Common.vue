<template>
    <div class="w-full h-full overflow-hidden flex flex-col">

        <!-- 状态一：没有 ID 时显示的空状态 -->
        <div v-if="!hasId" class="flex flex-1 flex-col items-center justify-center gap-4 text-center bg-muted/10">
            <div class="rounded-full bg-muted/30 p-4">
                <CircleArrowLeft class="h-15 w-15 text-muted-foreground/60" />
            </div>
            <div class="space-y-1">
                <h3 class="text-xl font-medium">打开一个内容</h3>
                <p class="text-base text-muted-foreground">
                    11234
                </p>
            </div>
        </div>

        <!-- 状态二：有 ID 时，渲染真正的详情内容 -->
        <!-- 这里还要继续往下“传火”，必须给 RouterView 加上 flex-1 min-h-0 -->
        <RouterView v-else class="flex flex-1 min-h-0 overflow-hidden" />

    </div>
</template>

<script setup lang="ts">
    import { CircleArrowLeft } from 'lucide-vue-next'
    import { RouterView, useRoute } from 'vue-router'
    import { computed } from 'vue'

    const route = useRoute()

    // 判断是否存在 id 参数（非空、非 undefined）
    const hasId = computed(() => {
        const id = route.params.id
        // 排除空字符串、undefined、null 等无效值
        return !!id && id !== ''
    })
</script>