<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class="h-full flex border-t">
                    <div class="w-16 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto">
                        <div v-for="(q, index) in questions" :key="index"
                            @click="currentQuestion = q, currentSelect = 0" :class="[
                                'w-10 h-10 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                                'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600',
                                currentQuestion === q
                                    ? 'border-zinc-800 text-zinc-900' : ''
                            ]">
                            {{ index + 1 }}
                        </div>
                        <div @click="" :class="[
                            'w-10 h-10 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                            'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600'
                        ]">
                            <Plus />
                        </div>
                    </div>

                    <div class="h-full w-full flex flex-col bg-white border overflow-y-auto">
                        <div class="flex justify-between">
                            <p>
                                分值：
                                <span>{{ currentQuestion?.questionScore }}</span>
                            </p>
                            <Badge variant="secondary" :class="['mb-2',
                                currentQuestion?.difficulty === 0 ? 'bg-green-50 border-green-300 text-green-700' :
                                    currentQuestion?.difficulty === 1 ? 'bg-amber-50 border-amber-300 text-amber-700' :
                                        'bg-rose-50 border-rose-300 text-rose-700']">
                                {{ currentQuestion?.difficulty === 0 ? '简单' :
                                    currentQuestion?.difficulty === 1 ? '中等' : '困难' }}
                            </Badge>
                        </div>

                        <!-- 替换原来的单行 textarea -->
                        <div class="flex-1 flex flex-col  border rounded-md overflow-hidden">
                            <!-- 编辑区：输入 Markdown 原文本 -->
                            <textarea v-model="currentQuestionContent"
                                class="w-full h-1/2 p-4 font-mono text-sm resize-none border-b focus:outline-none"
                                placeholder="请输入 Markdown 内容..." />
                            <!-- 预览区：实时显示渲染后的格式 -->
                            <div class="w-full h-1/2 p-4 overflow-auto bg-gray-50">
                                <article v-html="renderedContent" />
                            </div>
                        </div>
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
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { Button } from '@/components/ui/button'
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { onMounted, ref, computed } from 'vue'
    import axios from "@/utils/myAxios"
    import { useRoute } from 'vue-router'
    import MarkdownIt from 'markdown-it'
    import { Badge } from '@/components/ui/badge'
    import { Plus } from 'lucide-vue-next'


    const route = useRoute()
    const md = new MarkdownIt({
        html: true,    // 允许 HTML 标签
        breaks: true,  // 转化换行符
        linkify: true  // 自动识别链接
    })

    onMounted(async () => {
        // await getQuestions()
    })

    interface Question {
        title: string
        content: string
        questionScore: number
        tags: string[]
        difficulty: number
        timeLimit: number
        memoryLimit: number
        templateCode: string
        standardSolution: string
        explanation: string
    }

    const questions = ref<Question[]>([
        // 初始示例数据
        {
            title: '两数之和',
            content: '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。',
            questionScore: 20,
            tags: ['数组', '哈希表', '简单'],
            difficulty: 0,
            timeLimit: 1000,
            memoryLimit: 128,
            templateCode: 'public class Main {\n    public static void main(String[] args) {\n        // 请编写你的代码\n    }\n}',
            standardSolution: 'public class Main { ... }',
            explanation: '本题可以使用哈希表将时间复杂度优化到 O(n)'
        }
    ])

    const currentQuestion = questions.value[0]

    // const getQuestions = async () => {
    //     try {
    //         const res = await axios.get("/api/exam/create", {
    //             params: {
    //                 recordId: route.params.id
    //             }
    //         })
    //         // 存在就赋值
    //         if (res.data.data !== null) {
    //             questions.value = res.data.data
    //         }
    //     } catch (e) {
    //         console.log(e)
    //     }
    // }

    const currentQuestionContent = computed({
        get() {
            return currentQuestion?.content || ''
        },
        set(val) {
            if (currentQuestion) {
                currentQuestion.content = val
            }
        }
    })


    const renderedContent = computed(() => {
        return md.render(currentQuestionContent.value)
    })

    const currentSelect = ref(0)

    interface SimpleJudgeRecord {
        id: number
        status: number
        score: number
        time: string
        memory: number
    }

</script>