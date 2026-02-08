<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="30">
                <div class="flex items-center justify-center p-6">
                    <span class="font-semibold">One</span>
                </div>
            </ResizablePanel>
            <ResizableHandle />
            <ResizablePanel :default-size="70">
                <MonacoEditor v-model="javaCode" language="java" theme="vs-dark" class="h-full" />
            </ResizablePanel>
        </ResizablePanelGroup>
        <div>

        </div>
    </div>
</template>

<script setup lang="ts">
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup, } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { ref } from 'vue';

    const javaCode = ref(`public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Java in Monaco!");
    }
}`);
</script>

<style scoped></style>