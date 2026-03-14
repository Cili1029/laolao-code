<template>
    <Dialog @update:open="getQuestions()">
        <DialogTrigger as-child>
            <!-- 支持自定义触发按钮，如果不传则使用默认样式 -->
            <slot name="trigger">
                <div :class="[
                    'w-10 h-10 shrink-0 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                    'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600'
                ]">
                    <Warehouse />
                </div>
            </slot>
        </DialogTrigger>
        <DialogContent class="sm:max-w-2xl">
            <DialogHeader>
                <DialogTitle>题库</DialogTitle>
                <DialogDescription>
                    选择一题题目以做修改
                </DialogDescription>
            </DialogHeader>
            <div class="flex flex-col space-y-2">
                <div class="flex justify-between">
                    <div @click="handleSwitchType(0)"
                        class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded"
                        :class="currentType === 0 ? 'bg-gray-200' : ''">
                        <FileKey class="h-4 w-4 mr-1" />
                        我的题库
                    </div>
                    <div v-if="type !== 0" @click="handleSwitchType(1)"
                        class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded"
                        :class="currentType === 1 ? 'bg-gray-200' : ''">
                        <File class="h-4 w-4 mr-1" />
                        公共题库
                    </div>
                </div>
                <ButtonGroup class="w-full">
                    <Input v-model="searchContent" placeholder="支持模糊搜索..." />
                    <Button @click="getQuestions()" variant="outline" aria-label="Search"
                        :disabled="searchContent === ''">
                        <SearchIcon />
                    </Button>
                </ButtonGroup>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead class="w-25">
                                题目
                            </TableHead>
                            <TableHead>作者</TableHead>
                            <TableHead v-if="currentType === 0">状态</TableHead>
                            <TableHead class="text-end">操作</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        <TableRow v-for="question in questions" :key="question.id">
                            <TableCell>{{ question.title }}</TableCell>
                            <TableCell>{{ question.advisor }}</TableCell>
                            <TableCell v-if="currentType === 0">{{ question.isPublic === 0 ? '私有' : '公开' }}</TableCell>
                            <TableCell class="flex justify-end space-x-2">
                                <div v-if="currentType === 0" @click="changeQuestionStatus(question)"
                                    class="flex cursor-pointer text-orange-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                    <Copy class="h-4 w-4 mr-1" />
                                    <Spinner v-if="false" class="mr-1" />
                                    {{ question.isPublic ? '转私有' : '转公共' }}
                                </div>
                                <div v-if="type !== 0" @click="copyQuestion(question.id)"
                                    class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                    <Copy class="h-4 w-4 mr-1" />
                                    <Spinner v-if="false" class="mr-1" />
                                    克隆
                                </div>
                                <div v-if="currentType === 0" @click="deleteQuestion(question)"
                                    class="flex cursor-pointer text-red-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                    <Trash class="h-4 w-4 mr-1" />
                                    <Spinner v-if="false" class="mr-1" />
                                    删除
                                </div>
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </div>
            <DialogFooter>
                <Pagination v-if="total > 0" v-model:page="pageNum" :total="total" :items-per-page="pageSize"
                    :sibling-count="1" show-edges>
                    <PaginationContent v-slot="{ items }">
                        <PaginationPrevious />
                        <template v-for="(item, index) in items">
                            <PaginationItem v-if="item.type === 'page'" :key="index" :value="item.value"
                                :is-active="item.value === pageNum">
                                {{ item.value }}
                            </PaginationItem>
                            <PaginationEllipsis v-else :key="item.type" :index="index" />
                        </template>
                        <PaginationNext />
                    </PaginationContent>
                </Pagination>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup lang="ts">
    import axios from "@/utils/myAxios"
    import { Button } from '@/components/ui/button'
    import { Copy, File, FileKey, SearchIcon, Trash, Warehouse } from 'lucide-vue-next'
    import { ButtonGroup } from '@/components/ui/button-group'
    import { Input } from '@/components/ui/input'
    import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationNext, PaginationPrevious, } from '@/components/ui/pagination'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { ref, watch } from "vue"
    import Spinner from '@/components/ui/spinner/Spinner.vue'

    const currentType = ref(0) // 0: 私有题库 1: 公共题库

    interface QuestionBank {
        id: number
        title: string
        advisor: string
        isPublic: number
    }

    // 私有题库相关
    const questions = ref<QuestionBank[]>([])
    const pageNum = ref(1)
    const pageSize = ref(3)
    const total = ref(0)

    // 监听私有题库页码变化
    watch(
        () => pageNum.value,
        () => {
            getQuestions()
        }
    )

    const searchContent = ref("")

    // 获取私有题库数据
    const getQuestions = async () => {
        try {
            const url = currentType.value === 0
                ? "/api/question/private"
                : "/api/question/public"

            const res = await axios.get(url, {
                params: {
                    pageNum: pageNum.value,
                    pageSize: pageSize.value,
                    content: searchContent.value
                }
            })
            const resData = res.data.data
            questions.value = resData.records
            total.value = resData.total
        } catch (e) {
            console.error('获取失败:', e)
        }
    }

    watch(
        () => currentType.value,
        () => {
            pageNum.value = 1
            getQuestions()
        }
    )

    // 切换题库类型的处理函数
    const handleSwitchType = (type: number) => {
        currentType.value = type
        pageNum.value = 1
        searchContent.value = ''
    }

    const changeQuestionStatus = async (question: QuestionBank) => {
        try {
            const res = await axios.post("/api/question/status", {
                questionId: question.id
            })
            if (res.data.code === 1) {
                question.isPublic = 1 - question.isPublic
            }
        } catch (e) {
            console.error(e)
        }
    }

    const deleteQuestion = async (question: QuestionBank) => {
        try {
            await axios.delete("/api/question/delete", {
                params: {
                    questionId: question.id
                }
            })
            getQuestions()
        } catch (e) {
            console.error(e)
        }
    }

    const copyQuestion = async (id: number) => {
        try {
            const res = await axios.get("/api/question/copy", {
                params: {
                    questionId: id
                }
            })
            question.value = res.data.data
            if (res.data.code === 1 && question.value) emit('question-data', res.data.data)
        } catch (e) {
            console.error(e)
        }
    }

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

    const question = ref<Question>()

    const emit = defineEmits<{
        (e: 'question-data', question: Question): void
    }>()

    // 0为主页，1为编辑页可克隆
    const props = withDefaults(defineProps<{ type?: number }>(), {
        type: 0
    })
</script>