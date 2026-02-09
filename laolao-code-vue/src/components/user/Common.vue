<template>
    <div v-if="!hasId" class="flex flex-col items-center justify-center gap-4 text-center">
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
    <RouterView v-else />
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