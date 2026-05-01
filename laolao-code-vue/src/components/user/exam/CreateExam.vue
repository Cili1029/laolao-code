<template>
    <div class="h-full">
        <ResizablePanelGroup direction="horizontal">
            <ResizablePanel :default-size="30">
                <div class="h-full flex border-t">
                    <div
                        class="w-14 shrink-0 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
                        <div v-for="(q, index) in questions" :key="index" @click="currentQuestion = q" :class="[
                            'w-10 h-10 shrink-0 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                            'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600',
                            currentQuestion === q ? 'border-zinc-800 text-zinc-900' : ''
                        ]">
                            {{ index + 1 }}
                        </div>

                        <div @click="addQuestion()" :class="[
                            'w-10 h-10 shrink-0 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                            'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600'
                        ]">
                            <Plus />
                        </div>
                        <QuestionBank :exam-id="Number(route.params.id)" @question-data="getBankQuestion" />
                    </div>

                    <div v-if="questions.length > 0" class="h-full w-full flex flex-col bg-white border space-y-2">
                        <div class="flex px-2 pt-2 justify-between">
                            <div @click="saveAndAddToExam()"
                                class="flex cursor-pointer items-center px-2 py-1 text-sm rounded transition-all"
                                :class="testStatus.class">

                                <!-- 加载中状态 -->
                                <Spinner v-if="examStore.judgeLoading" class="mr-1" />

                                <!-- 图标逻辑 -->
                                <template v-else>
                                    <CheckCircle2 v-if="testStatus.icon === 'check'" class="h-4 w-4 mr-1" />
                                    <Rocket v-else class="h-4 w-4 mr-1" />
                                </template>

                                <span>{{ testStatus.text }}</span>

                                <!-- 只有在未保存修改时显示小红点 -->
                                <span v-if="isDirty" class="ml-1 w-2 h-2 bg-orange-500 rounded-full animate-pulse">
                                </span>
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
                    <div v-else class="flex flex-col flex-1 justify-center items-center text-center">
                        <div class="bg-white p-4 rounded-full shadow inline-block mb-4">
                            <Ghost class="h-10 w-10 text-gray-600" />
                        </div>
                        <p class="text-gray-600 font-medium">暂无题目</p>
                        <p class="text-sm text-gray-600">点击左边创建一道题</p>
                    </div>
                </div>
            </ResizablePanel>

            <ResizableHandle />

            <ResizablePanel v-if="questions.length > 0" :default-size="70">
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
                            <div v-for="(testCase, index) in currentQuestion?.testCases || []"
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
    import { onMounted, ref, computed, watch } from 'vue'
    import axios from "@/utils/myAxios"
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()
    import { CirclePlus, Ghost, Plus, Rocket, Trash, CheckCircle2 } from 'lucide-vue-next'
    import { Textarea } from '@/components/ui/textarea'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue, } from '@/components/ui/select'
    import JudgeDialog from '../question/JudgeDialog.vue'
    import Spinner from '@/components/ui/spinner/Spinner.vue'
    import { useRoute } from 'vue-router'
    import { toast } from 'vue-sonner'
    import QuestionBank from '../question/QuestionBankCopyDialog.vue'
    const route = useRoute()
    import { cloneDeep, isEqual } from 'lodash-es' // 引入 lodash 工具

    onMounted(async () => {
        getQuestions()
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
        isValidated: number
        testCases: TestCase[]
    }

    const questions = ref<Question[]>([])

    // 当前题目快照
    const currentQuestionSnapshot = ref<Question | null>(null)
    const currentQuestion = ref<Question | null>(null)

    const getQuestions = async () => {
        try {
            const res = await axios.get("/api/exam/draft/get-question", {
                params: { examId: route.params.id }
            })
            // 有数据
            if (res.data.data && res.data.data.length > 0) {
                questions.value = res.data.data
                currentQuestion.value = questions.value[0]!
                // 深度克隆当前题目
                currentQuestionSnapshot.value = cloneDeep(currentQuestion.value)
            }
        } catch (e) {
            console.error('获取题目失败：', e)
        }
    }

    // 判断当前题目是否被修改过
    const isDirty = computed(() => {
        if (!currentQuestion.value || !currentQuestionSnapshot.value) return false
        // 使用 lodash 的 isEqual 进行深度对象对比
        return !isEqual(currentQuestion.value, currentQuestionSnapshot.value)
    })

    watch(currentQuestion, (newVal) => {
        if (newVal) {
            // 切换时，重新建立当前题目的快照
            currentQuestionSnapshot.value = cloneDeep(newVal)
        }
    }, { deep: false })

    const createDefaultQuestion = (): Question => {
        return {
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
            isValidated: 0,
            testCases: [{
                id: null,
                questionId: null,
                input: '',
                output: ''
            }]
        }
    }

    const addQuestion = () => {
        const newQuestion = createDefaultQuestion()
        questions.value.push(newQuestion)
        currentQuestion.value = newQuestion
    }

    const deleteQuestion = async () => {
        // 后端删除 如果有Id就是保存过的，需要后端也进行删除
        if (currentQuestion.value?.id) {
            try {
                await axios.delete("/api/exam/draft/remove-question", {
                    params: {
                        questionId: currentQuestion.value?.id
                    }
                })
            } catch (e) {
                console.log(e);
            }
        }

        // 前端删除
        const index = questions.value.findIndex(q => q === currentQuestion.value);
        questions.value.splice(index, 1);
        if (index > 0) {
            currentQuestion.value = questions.value[index - 1]!;
        } else {
            currentQuestion.value = questions.value[0]!;
        }
    }
    
    const canRun = computed(() => {
        const q = currentQuestion.value
        if (!q || examStore.judgeLoading) return false

        // 基础字段校验
        if (!q.title.trim()) return false
        if (!q.content.trim()) return false
        if (q.questionScore <= 0) return false
        if (q.timeLimit <= 0) return false
        if (q.memoryLimit <= 0) return false

        // 校验：至少有一个测试用例，且输入输出都不为空
        const hasValidTestCase = q.testCases.some(tc => {
            return tc.input.trim() !== '' && tc.output.trim() !== ''
        })
        if (!hasValidTestCase) return false

        return true
    })

    const saveAndAddToExam = async () => {
        if (!canRun.value) {
            toast.error("请完善题目后运行")
            return
        }

        if (!isDirty.value && currentQuestion.value!.isValidated === 1) {
            toast.success("题目已通过测试，无需重复运行");
            return
        }

        examStore.judgeLoading = true

        // 如果数据没变且已有 ID，直接运行
        if (!isDirty.value && currentQuestion.value!.id) {
            await run()
            return
        }

        // 数据有变动，先执行保存
        try {
            // 修改状态为测试中
            const res = await axios.post("/api/exam/draft/add-question", {
                examId: Number(route.params.id),
                question: currentQuestion.value
            })

            if (res.data.code === 1) {
                currentQuestion.value!.id = res.data.data

                // 保存成功后，将当前的最新状态存为新的快照
                currentQuestionSnapshot.value = cloneDeep(currentQuestion.value)

                await run()
            }
        } catch (e) {
            console.error(e)
        }
    }

    const difficultyProxy = computed({
        get() {
            return currentQuestion.value ? String(currentQuestion.value.difficulty) : '0'
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
        if (question.testCases.length === 1) {
            toast.info("至少要有一个测试用例！")
            return
        }
        question.testCases.splice(index, 1);
    }

    const run = async () => {
        try {
            await axios.post("/api/exam/draft/judge", {}, {
                params: {
                    questionId: currentQuestion.value?.id
                }
            })
        } catch (e) {
            console.log(e);
        }
    }

    const testStatus = computed(() => {
        if (isDirty.value) {
            return {
                text: '保存并测试',
                class: 'text-orange-600 bg-orange-50 hover:bg-orange-100',
                icon: 'rocket'
            }
        }
        if (currentQuestion.value?.isValidated === 1) {
            return {
                text: '测试通过',
                class: 'text-green-600 bg-green-50 hover:bg-green-100',
                icon: 'check'
            }
        }
        return {
            text: '测试',
            class: 'text-blue-600 bg-gray-100 hover:bg-gray-200',
            icon: 'rocket'
        }
    })

    const getBankQuestion = (question: Question) => {
        questions.value.push(question)
        currentQuestion.value = question
    }

</script>