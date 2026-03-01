<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="40">
                <div class="h-full flex border-t">
                    <div class="w-16 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto">
                        <div v-for="(q, index) in questions" :key="index" @click="currentQuestion = q" :class="[
                            'w-10 h-10 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                            'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600',
                            currentQuestion === q
                                ? 'border-zinc-800 text-zinc-900' : ''
                        ]">
                            {{ index + 1 }}
                        </div>
                        <div @click="addQuestion()" :class="[
                            'w-10 h-10 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                            'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600'
                        ]">
                            <Plus />
                        </div>
                    </div>

                    <div class="h-full w-full flex flex-col bg-white border overflow-y-auto p-2 space-y-2">
                        <div class="flex space-x-2 justify-between">
                            <div class="grid w-1/2 max-w-sm items-center gap-1.5">
                                <Label>分值</Label>
                                <Input type="number" v-model="currentQuestion!.questionScore" />
                            </div>

                            <div class="grid w-1/2 max-w-sm items-center gap-1.5">
                                <Label>难度</Label>
                                <Select v-model="difficultyProxy">
                                    <SelectTrigger class="">
                                        <SelectValue placeholder="请选择难度" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <!-- value 对应后端需要的 0, 1, 2 -->
                                        <SelectItem value="0">
                                            简单
                                        </SelectItem>
                                        <SelectItem value="1">
                                            中等
                                        </SelectItem>
                                        <SelectItem value="2">
                                            困难
                                        </SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>

                        <div class="flex space-x-2 justify-between">
                            <div class="grid w-1/2 max-w-sm items-center gap-1.5">
                                <Label>时间限制（ms）</Label>
                                <Input type="number" v-model="currentQuestion!.timeLimit" />
                            </div>

                            <div class="grid w-1/2 max-w-sm items-center gap-1.5">
                                <Label>内存限制（mb）</Label>
                                <Input type="number" v-model="currentQuestion!.memoryLimit" />
                            </div>
                        </div>

                        <div class="grid w-full max-w-sm items-center gap-1.5">
                            <Label>标题与描述</Label>
                            <Input type="text" v-model="currentQuestion!.title" class="w-full" />
                        </div>

                        <div class="flex-1 flex flex-col border rounded-md overflow-hidden">
                            <textarea v-model="currentQuestionContent"
                                class="flex-1 p-2 font-mono text-sm resize-none border-b focus:outline-none"
                                placeholder="请输入 Markdown 内容..." />
                        </div>
                    </div>
                </div>
            </ResizablePanel>

            <ResizableHandle />

            <ResizablePanel :default-size="60">
                <ResizablePanelGroup direction="vertical">
                    <ResizablePanel :default-size="75">
                        <MonacoEditor v-if="currentQuestion" v-model="currentQuestion.templateCode" language="java"
                            theme="vs" />
                    </ResizablePanel>
                    <ResizableHandle />
                    <ResizablePanel :default-size="25">
                        <div class="flex h-full items-center justify-center p-6">
                            <span class="font-semibold">Three</span>
                        </div>
                    </ResizablePanel>
                </ResizablePanelGroup>
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
    const route = useRoute()
    import MarkdownIt from 'markdown-it'
    import { Plus } from 'lucide-vue-next'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue, } from '@/components/ui/select'

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
        }
    ])

    const currentQuestion = ref(questions.value[0])

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

    const addQuestion = () => {
        const newQuestion = {
            title: '',
            content: '',
            questionScore: 0,
            tags: [],
            difficulty: 0,
            timeLimit: 0,
            memoryLimit: 0,
            templateCode: '',
            standardSolution: '',
        } as Question
        questions.value.push(newQuestion)
        currentQuestion.value = newQuestion
    }

    const difficultyProxy = computed({
        get() {
            // 将数字转为字符串给组件显示 ( 0 -> "0" )
            // 注意：如果是 null 或 undefined 要处理一下
            return currentQuestion.value!.difficulty !== undefined ? String(currentQuestion.value!.difficulty) : undefined
        },
        set(val) {
            // 当用户选择后，将字符串转回数字存入对象 ( "0" -> 0 )
            currentQuestion.value!.difficulty = Number(val)
        }
    })

    const currentQuestionContent = computed({
        get() {
            return currentQuestion.value!.content || ''
        },
        set(val) {
            if (currentQuestion) {
                currentQuestion.value!.content = val
            }
        }
    })


    interface SimpleJudgeRecord {
        id: number
        status: number
        score: number
        time: string
        memory: number
    }

</script>