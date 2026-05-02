<template>
    <Dialog @update:open="(val) => { if (val) getQuestions() }">
        <DialogTrigger as-child>
            <div class="inline-block w-full h-full cursor-pointer">
                <slot name="trigger" />
            </div>
        </DialogTrigger>
        <DialogContent class="sm:max-w-6xl">
            <DialogHeader>
                <DialogTitle>{{ question?.title }}</DialogTitle>
                <DialogDescription></DialogDescription>
            </DialogHeader>
            <div class="flex space-x-2">
                <div class="flex flex-col w-1/2 justify-between">
                    <article v-html="renderedContent" />
                    <div class="flex flex-col">
                        <div class="flex justify-between">
                            <p>
                                时间限制：<span class="font-bold">{{ question?.timeLimit }}ms</span>
                            </p>
                            <p>
                                内存限制：<span class="font-bold">{{ question?.memoryLimit }}MB</span>
                            </p>
                        </div>
                        <p>
                            难度：
                            <span variant="secondary" :class="question?.difficulty === 0 ? 'text-green-500' :
                                question?.difficulty === 1 ? 'text-orange-400' : 'text-red-500'">
                                {{ question?.difficulty === 0 ? '简单' :
                                    question?.difficulty === 1 ? '中等' : '困难' }}
                            </span>
                        </p>
                        <div class="flex">
                            <p>相关标签：</p>
                            <div class="flex space-x-2">
                                <Badge v-for="tag in question?.tags" class="bg-green-600 text-white">
                                    {{ tag }}
                                </Badge>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="w-1/2 min-h-128">
                    <MonacoEditor v-if="question" v-model="question.standardSolution" language="java" readonly
                        theme="vs" />
                </div>
            </div>
        </DialogContent>
    </Dialog>
</template>

<script setup lang="ts">
    import axios from "@/utils/myAxios"
    import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogDescription } from '@/components/ui/dialog'
    import { computed, ref } from "vue"
    import MarkdownIt from 'markdown-it'
    import { Badge } from '@/components/ui/badge'
    import MonacoEditor from "@/components/common/MonacoEditor.vue"

    interface Question {
        id: number
        title: string
        content: string
        tags: string[]
        difficulty: number
        timeLimit: number
        memoryLimit: number
        standardSolution: string
    }

    const question = ref<Question>()

    const getQuestions = async () => {
        try {
            const res = await axios.get("/api/question/single", {
                params: {
                    questionId: props.questionId
                }
            })
            question.value = res.data.data
        } catch (e) {
            console.error('获取失败:', e)
        }
    }

    const md = new MarkdownIt({
        html: true,    // 允许 HTML 标签
        breaks: true,  // 转化换行符
        linkify: true  // 自动识别链接
    })

    const renderedContent = computed(() => {
        const content = question.value?.content
        if (!content) return ''
        return md.render(content);
    })

    const props = defineProps<{
        questionId: number
    }>()
</script>