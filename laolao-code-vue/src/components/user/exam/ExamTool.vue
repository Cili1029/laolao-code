<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class="h-full flex">
                    <div class="flex flex-col w-18 py-3 space-y-2 items-center overflow-y-auto border-2 shrink-0">
                        <div v-for="(question, index) in questions" :key="index"
                            class="w-12 h-12 border-2 border-dashed border-blue-400 flex justify-center items-center cursor-pointer hover:bg-gray-100"
                            @click="examStore.currentQuestion = question">
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
                <MonacoEditor v-if="examStore.currentQuestion" v-model="examStore.currentQuestion.templateCode"
                    language="java" theme="vs" />
            </ResizablePanel>
        </ResizablePanelGroup>
        <Dialog v-model:open="examStore.judgeDialog">
            <DialogTrigger as-child>
            </DialogTrigger>
            <DialogContent class="sm:max-w-180">
                <DialogHeader>
                    <DialogTitle>编译结果</DialogTitle>
                    <DialogDescription>
                        <Alert>
                            <Spinner v-if="examStore.judgeLoading" />
                            <CheckCircle2 v-if="!examStore.judgeLoading && examStore.judgeResult?.exitCode === 0" />
                            <AlertCircle v-if="!examStore.judgeLoading && examStore.judgeResult?.exitCode !== 0" />

                            <AlertTitle>{{ examStore.judgeLoading ? '少女祈祷中。。。' :
                                examStore.judgeResult?.exitCode ? '未通过！' : '通过！'
                            }}</AlertTitle>
                            <AlertDescription>
                                {{ examStore.judgeLoading ? '耐心等待。。。' :
                                    examStore.judgeResult?.exitCode ? '去检查一下吧！' : '可以下一题了！'
                                }}
                            </AlertDescription>
                        </Alert>
                    </DialogDescription>
                </DialogHeader>
                <div v-if="examStore.judgeResult" class="flex flex-col">
                    <p class="py-3 px-2 bg-black text-white">{{ examStore.judgeResult.stderr }}</p>
                    <p>执行用时：{{ examStore.judgeResult.time }}ms</p>
                    <p>消耗内存：{{ examStore.judgeResult.memory }}MB</p>
                </div>
                <DialogFooter>
                    <DialogClose as-child>
                        <Button variant="outline">
                            关闭
                        </Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    </div>
</template>

<script setup lang="ts">
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
    import { AlertCircle, CheckCircle2 } from 'lucide-vue-next'
    import { Spinner } from '@/components/ui/spinner'
    import { Alert, AlertDescription, AlertTitle, } from '@/components/ui/alert'
    import { Button } from '@/components/ui/button'
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { onMounted, ref, computed } from 'vue'
    import axios from "@/utils/myAxios"
    import { useRoute } from 'vue-router'
    import MarkdownIt from 'markdown-it'
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()

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
            examStore.currentQuestion = questions.value[0]!
        } catch (e) {
            console.error("Fetch error:", e)
        }
    }


    const renderedContent = computed(() => {
        const content = examStore.currentQuestion?.content
        if (!content) return ''
        return md.render(content);
    })
</script>