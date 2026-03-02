<template>
    <div>
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="30">
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

                    <div class="h-full w-full flex flex-col bg-white border overflow-y-auto space-y-2">
                        <div class="flex px-2 pt-2 justify-between">
                            <div @click="saveAndAddToExam()"
                                class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                <Save class="h-4 w-4 mr-1" />
                                <Spinner v-if="false" class="mr-1" />
                                保存并写入考试
                            </div>
                            <div @click="deleteQuestion()"
                                class="flex cursor-pointer text-red-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                <Trash class="h-4 w-4 mr-1" />
                                <Spinner v-if="false" class="mr-1" />
                                移出考试
                            </div>
                        </div>
                        <div class="flex space-x-2 justify-between px-2">
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

                        <div class="flex space-x-2 justify-between px-2">
                            <div class="grid w-1/2 max-w-sm items-center gap-1.5">
                                <Label>时间限制（ms）</Label>
                                <Input type="number" v-model="currentQuestion!.timeLimit" />
                            </div>

                            <div class="grid w-1/2 max-w-sm items-center gap-1.5">
                                <Label>内存限制（mb）</Label>
                                <Input type="number" v-model="currentQuestion!.memoryLimit" />
                            </div>
                        </div>

                        <div class="grid w-full max-w-sm items-center gap-1.5 px-2">
                            <Label>标题与描述</Label>
                            <Input type="text" v-model="currentQuestion!.title" class="w-full" />
                        </div>

                        <div class="flex-1 flex flex-col border rounded-md overflow-hidden mx-2 mb-2">
                            <textarea v-model="currentQuestionContent"
                                class="flex-1 p-2 font-mono text-sm resize-none border-b focus:outline-none"
                                placeholder="请输入 Markdown 内容..." />
                        </div>
                    </div>
                </div>
            </ResizablePanel>

            <ResizableHandle />

            <ResizablePanel :default-size="70">
                <ResizablePanelGroup direction="vertical">
                    <ResizablePanel :default-size="65">
                        <ResizablePanelGroup direction="horizontal">
                            <ResizablePanel :default-size="50">
                                <div class="flex justify-between">
                                    <p class="text-sm p-0.5">标准答案</p>
                                    <p class="text-sm p-0.5 cursor-pointer"
                                        @click="currentQuestion!.templateCode = currentQuestion!.standardSolution">
                                        |-复制-></p>
                                </div>
                                <div class="h-full">
                                    <MonacoEditor v-if="currentQuestion" v-model="currentQuestion.standardSolution"
                                        language="java" theme="vs" />
                                </div>
                            </ResizablePanel>
                            <ResizableHandle />
                            <ResizablePanel :default-size="50">
                                <p class="text-sm p-0.5">提供给学生的模板</p>
                                <div class="h-full">
                                    <MonacoEditor v-if="currentQuestion" v-model="currentQuestion.templateCode"
                                        language="java" theme="vs" />
                                </div>
                            </ResizablePanel>
                        </ResizablePanelGroup>
                    </ResizablePanel>
                    <ResizableHandle />
                    <ResizablePanel :default-size="35">
                        <div class="h-full p-2 space-y-2 overflow-y-auto">
                            <div v-for="(testCase, index) in currentQuestion?.testCases"
                                class="flex border p-2 justify-between rounded">
                                <div class="flex">
                                    <p>输入：</p>
                                    <Dialog>
                                        <DialogTrigger as-child>
                                            <p class="cursor-pointer w-32 truncate">
                                                {{ testCase.input }}
                                            </p>
                                        </DialogTrigger>
                                        <DialogContent class="sm:max-w-106.25">
                                            <DialogHeader>
                                                <DialogTitle>输入示例</DialogTitle>
                                                <DialogDescription>
                                                    多组输入时应该用回车分开
                                                </DialogDescription>
                                            </DialogHeader>
                                            <Textarea v-model="testCase.input" />
                                        </DialogContent>
                                    </Dialog>
                                </div>
                                <div class="flex">
                                    <p>输出：</p>
                                    <Dialog>
                                        <DialogTrigger as-child>
                                            <p class="cursor-pointer w-32 truncate">
                                                {{ testCase.output }}
                                            </p>
                                        </DialogTrigger>
                                        <DialogContent class="sm:max-w-106.25">
                                            <DialogHeader>
                                                <DialogTitle>输出示例</DialogTitle>
                                                <DialogDescription>
                                                    多组输出时应该用回车分开
                                                </DialogDescription>
                                            </DialogHeader>
                                            <Textarea v-model="testCase.output" />
                                        </DialogContent>
                                    </Dialog>
                                </div>
                                <div class="flex space-x-2">
                                    <div @click="!examStore.judgeLoading && runTestCase(testCase)"
                                        class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                        <Spinner v-if="examStore.judgeLoading" class="mr-1" />
                                        <Save v-else class="h-4 w-4 mr-1" />
                                        运行示例
                                    </div>
                                    <div @click="deleteTestCase(currentQuestion!, index)"
                                        class="flex cursor-pointer text-red-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                        <Trash class="h-4 w-4 mr-1" />
                                        <Spinner v-if="false" class="mr-1" />
                                        删除示例
                                    </div>
                                </div>
                            </div>
                            <div class="flex px-2 pb-2 justify-center items-center">
                                <div @click="addTestCase(currentQuestion!)"
                                    class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                    <CirclePlus />添加示例
                                </div>
                            </div>
                        </div>
                    </ResizablePanel>
                </ResizablePanelGroup>
            </ResizablePanel>
        </ResizablePanelGroup>
        <JudgeDialog />
    </div>
</template>

<script setup lang="ts">
    import { Dialog, DialogTrigger, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '@/components/ui/resizable'
    import MonacoEditor from '@/components/common/MonacoEditor.vue'
    import { onMounted, ref, computed } from 'vue'
    import axios from "@/utils/myAxios"
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()
    import { CirclePlus, Plus, Save, Trash } from 'lucide-vue-next'
    import { Textarea } from '@/components/ui/textarea'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue, } from '@/components/ui/select'
    import JudgeDialog from '../JudgeDialog.vue'
    import Spinner from '@/components/ui/spinner/Spinner.vue'
    import { useRoute } from 'vue-router'
    const route = useRoute()

    onMounted(async () => {
        // await getQuestions()
    })

    interface TestCase {
        id: number | null
        questionId: number | null
        input: string
        output: string
    }

    interface Question {
        id: number | null
        title: string
        content: string
        questionScore: number
        tags: string[]
        difficulty: number
        timeLimit: number
        memoryLimit: number
        templateCode: string
        standardSolution: string
        testCases: TestCase[]
    }

    const questions = ref<Question[]>([
        // 初始示例数据
        {
            id: null,
            title: '两数之和',
            content: '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。',
            questionScore: 20,
            tags: ['数组', '哈希表', '简单'],
            difficulty: 0,
            timeLimit: 1000,
            memoryLimit: 128,
            templateCode: 'public class Main {\n    public static void main(String[] args) {\n        // 请编写你的代码\n    }\n}',
            standardSolution: 'public class Main { ... }',
            testCases: [{
                id: null,
                questionId: null,
                input: '1',
                output: '23'
            }]
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
            id: null,
            title: '',
            content: '',
            questionScore: 0,
            tags: [],
            difficulty: 0,
            timeLimit: 0,
            memoryLimit: 0,
            templateCode: '',
            standardSolution: '',
            testCases: [{
                id: null,
                questionId: null,
                input: '',
                output: ''
            }]
        } as Question
        questions.value.push(newQuestion)
        currentQuestion.value = newQuestion
    }

    const deleteQuestion = () => {
        const index = questions.value.findIndex(q => q === currentQuestion.value);
        questions.value.splice(index, 1);
        if (index > 0) {
            currentQuestion.value = questions.value[index - 1];
        } else {
            currentQuestion.value = questions.value[0];
        }
    }

    const saveAndAddToExam = async () => {
        try {
            const res = await axios.post("/api/exam/create/add", {
                examId: Number(route.params.id),
                question: currentQuestion.value
            })
            currentQuestion.value!.id = res.data.data
        } catch (e) {
            console.log(e);
        }
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

    const addTestCase = (currentQuestion: Question) => {
        const newTestCase = {
            id: null,
            questionId: null,
            input: '',
            output: ''
        } as TestCase
        currentQuestion.testCases.push(newTestCase)
    }

    // 删除测试用例方法
    const deleteTestCase = (question: Question, index: number) => {
        question.testCases.splice(index, 1);
    };

    const runTestCase = async (testCase: TestCase) => {
        try {
            examStore.judgeLoading = true
            const res = await axios.post("/api/exam/create/judge", {
                code: currentQuestion.value!.standardSolution,
                testCase: testCase
            })
            examStore.advisorJudgeRecord = res.data.data
            examStore.judgeDialog = true
            examStore.judgeLoading = false
        } catch (e) {
            console.log(e);
        }
    }

</script>