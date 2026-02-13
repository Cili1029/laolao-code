<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class="h-full flex">
                    <div class="flex flex-col w-18 py-3 space-y-2 items-center overflow-y-auto border-2 shrink-0">
                        <div v-for="(question, index) in questions" :key="index"
                            class="w-12 h-12 border-2 border-dashed border-blue-400 flex justify-center items-center cursor-pointer hover:bg-gray-100"
                            @click="currentQuestion = question">
                            {{ index + 1 }}
                        </div>
                    </div>
                    <div class="flex-1 p-6 overflow-y-auto bg-white dark:bg-zinc-950">
                        <article class="prose prose-slate dark:prose-invert max-w-none" v-html="renderedContent">
                        </article>
                    </div>
                </div>
            </ResizablePanel>

            <ResizableHandle />

            <ResizablePanel :default-size="60">
                <MonacoEditor v-if="currentQuestion" v-model="currentQuestion.templateCode" language="java"
                    theme="vs" />
            </ResizablePanel>
        </ResizablePanelGroup>
    </div>
</template>

<script setup lang="ts">
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { onMounted, ref, computed } from 'vue'
    import axios from "@/utils/myAxios"
    import { useRoute } from 'vue-router'
    import MarkdownIt from 'markdown-it'

    const route = useRoute()
    const md = new MarkdownIt({
        html: true,    // 允许 HTML 标签
        breaks: true,  // 转化换行符
        linkify: true  // 自动识别链接
    })

    onMounted(() => {
        getQuestions()
    })

    interface Questions {
        id: number
        title: string
        content: string
        difficulty: number
        templateCode: string
    }

    const questions = ref<Questions[]>([])

    const getQuestions = async () => {
        try {
            const res = await axios.get("/api/exam/questions", {
                params: {
                    recordId: route.params.id
                }
            })
            questions.value = res.data.data
            currentQuestion.value = questions.value[0]
        } catch (e) {
            console.error("Fetch error:", e)
        }
    }

    const currentQuestion = ref<Questions>()

    const renderedContent = computed(() => {
        const content = currentQuestion.value?.content
        if (!content) return ''
        return md.render(content);
    })
</script>