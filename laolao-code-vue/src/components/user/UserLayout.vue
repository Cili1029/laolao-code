<template>
    <SidebarProvider :style="{ '--sidebar-width': '350px', }">
        <AppSidebar />
        <SidebarInset>
            <header class="bg-background sticky top-0 flex shrink-0 items-center gap-2 border-b p-4">
                <SidebarTrigger
                    :class="['-ml-1', sidebarStore.exam ? 'pointer-events-none select-none opacity-50' : '']" />
                <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
                <p @click="sidebarStore.exam = !sidebarStore.exam">开始考试</p>
            </header>
            <RouterView class="flex flex-1"></RouterView>
        </SidebarInset>
    </SidebarProvider>
</template>

<script setup lang="ts">
    import AppSidebar from "@/components/user/sidebar/AppSidebar.vue"
    import { Separator } from "@/components/ui/separator"
    import { SidebarInset, SidebarProvider, SidebarTrigger, } from "@/components/ui/sidebar"
    import { RouterView } from "vue-router"
    import { useSidebarStore } from "@/stores/SidebarStore"
    const sidebarStore = useSidebarStore()
</script>