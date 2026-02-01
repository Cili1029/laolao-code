<template>
    <SidebarProvider :style="{ '--sidebar-width': '350px', }">
        <AppSidebar />
        <SidebarInset>
            <header class="bg-background sticky top-0 flex shrink-0 items-center gap-2 border-b p-4">
                <SidebarTrigger class="-ml-1" />
                <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
            </header>
            <!-- <div class="flex flex-1 flex-col gap-4 p-4">
                <div v-for="index in 24" :key="index" class="aspect-video h-12 w-full rounded-lg bg-muted/50" />
            </div> -->
            <ResizablePanelGroup direction="horizontal" class="w-full">
                <ResizablePanel :default-size="30">
                    <div class="flex items-center justify-center p-6">
                        <span class="font-semibold">One</span>
                    </div>
                </ResizablePanel>
                <ResizableHandle />
                <ResizablePanel :default-size="80">
                    <div class="flex items-center justify-center p-6">
                        <span class="font-semibold">Two</span>
                    </div>
                </ResizablePanel>
            </ResizablePanelGroup>
        </SidebarInset>
    </SidebarProvider>
</template>

<script setup lang="ts">
    import AppSidebar from "@/components/user/sidebar/AppSidebar.vue"
    import { Separator } from "@/components/ui/separator"
    import { SidebarInset, SidebarProvider, SidebarTrigger, } from "@/components/ui/sidebar"
    import {
        ResizableHandle,
        ResizablePanel,
        ResizablePanelGroup,
    } from '@/components/ui/resizable'
</script>