<template>
    <div class="p-5 flex flex-col space-y-5 h-full">
        <div class="space-y-5">
            <div class="flex space-x-5">
                <div v-for="tag in tags" :key="tag.id" @click="curTag = (curTag === tag.id ? null : tag.id)"
                    class="group cursor-pointer flex items-center space-x-1 transition-colors hover:text-blue-700"
                    :class="curTag === tag.id ? 'text-blue-700' : 'text-gray-500'">
                    <span>{{ tag.name }}</span>
                    <Badge
                        class="h-5 min-w-5 rounded-full px-1 font-mono transition-colors group-hover:bg-blue-100 group-hover:text-blue-700"
                        :class="curTag === tag.id ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-600'">
                        {{ tag.count }}
                    </Badge>
                </div>
            </div>
            <div class="flex justify-between">
                <ButtonGroup class="w-1/2">
                    <Input v-model="publicSearchContent" placeholder="支持模糊搜索..." />
                    <Button @click="getPublicQuestions()" variant="outline" aria-label="Search"
                        :disabled="publicSearchContent === ''">
                        <SearchIcon />
                    </Button>
                </ButtonGroup>

                <Sheet>
                    <SheetTrigger as-child>
                        <p @click="getPrivateQuestions()" class="flex cursor-pointer hover:text-blue-700 items-center">
                            <User />个人仓库
                        </p>
                    </SheetTrigger>
                    <SheetContent class="w-full sm:w-1/3 sm:max-w-none">
                        <SheetHeader>
                            <SheetTitle class="text-2xl">个人仓库</SheetTitle>
                            <SheetDescription>
                                <ButtonGroup class="w-full">
                                    <Input v-model="privateSearchContent" placeholder="支持模糊搜索..." />
                                    <Button @click="getPrivateQuestions()" variant="outline" aria-label="Search"
                                        :disabled="privateSearchContent === ''">
                                        <SearchIcon />
                                    </Button>
                                </ButtonGroup>
                            </SheetDescription>
                        </SheetHeader>
                        <div class="grid flex-1 auto-rows-min gap-6 px-4 overflow-y-auto">
                            <Table v-if="privateTotal > 0">
                                <TableBody>
                                    <TableRow v-for="question in privateQuestions" :key="question.id">
                                        <TableCell>
                                            <QuestionBankInfoDialog :question-id="question.id">
                                                <template #trigger>
                                                    {{ question.title }}
                                                </template>
                                            </QuestionBankInfoDialog>
                                        </TableCell>
                                        <TableCell class="flex justify-end space-x-2">
                                            <div @click=""
                                                class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                                <Edit class="h-4 w-4 mr-1" />
                                                编辑
                                            </div>
                                            <div @click="deleteQuestion(question)"
                                                class="flex cursor-pointer text-red-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                                                <Trash class="h-4 w-4 mr-1" />
                                                删除
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                </TableBody>
                            </Table>
                            <div v-else class="flex flex-col flex-1 justify-center items-center text-center py-24">
                                <div class="bg-white p-4 rounded-full shadow inline-block mb-4">
                                    <Ghost class="h-10 w-10 text-gray-600" />
                                </div>
                                <p class="text-gray-600 font-medium">暂无题目数据</p>
                                <p class="text-sm text-gray-600">你创建的题目会在此显示</p>
                            </div>
                        </div>
                        <SheetFooter>
                            <Pagination v-if="privateTotal > 0" v-model:page="privatePageNum" :total="privateTotal"
                                :items-per-page="privatePageSize" :sibling-count="1" show-edges>
                                <PaginationContent v-slot="{ items }">
                                    <PaginationPrevious />
                                    <template v-for="(item, index) in items">
                                        <PaginationItem v-if="item.type === 'page'" :key="index" :value="item.value"
                                            :is-active="item.value === privatePageNum">
                                            {{ item.value }}
                                        </PaginationItem>
                                        <PaginationEllipsis v-else :key="item.type" :index="index" />
                                    </template>
                                    <PaginationNext />
                                </PaginationContent>
                            </Pagination>
                        </SheetFooter>
                    </SheetContent>
                </Sheet>
            </div>
        </div>

        <div class="flex-1 overflow-y-auto w-2/3">
            <Table>
                <TableBody>
                    <TableRow v-for="question in publicQuestions" :key="question.id">
                        <TableCell class="cursor-pointer hover:text-blue-600">
                            <QuestionBankInfoDialog :question-id="question.id">
                                <template #trigger>
                                    {{ question.title }}
                                </template>
                            </QuestionBankInfoDialog>
                        </TableCell>
                        <TableCell>
                            <div class="flex space-x-2">
                                <Badge v-for="tag in question.tags" class="bg-green-600 text-white">
                                    {{ tag }}
                                </Badge>
                            </div>
                        </TableCell>
                        <TableCell>
                            <p variant="secondary" :class="question.difficulty === 0 ? 'text-green-500' :
                                question.difficulty === 1 ? 'text-orange-400' : 'text-red-500'">
                                {{ question.difficulty === 0 ? '简单' :
                                    question.difficulty === 1 ? '中等' : '困难' }}
                            </p>
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </div>

        <Pagination v-if="publicTotal > 0" v-model:page="publicPageNum" :total="publicTotal"
            :items-per-page="publicPageSize" :sibling-count="1" show-edges>
            <PaginationContent v-slot="{ items }">
                <PaginationPrevious />
                <template v-for="(item, index) in items">
                    <PaginationItem v-if="item.type === 'page'" :key="index" :value="item.value"
                        :is-active="item.value === publicPageNum">
                        {{ item.value }}
                    </PaginationItem>
                    <PaginationEllipsis v-else :key="item.type" :index="index" />
                </template>
                <PaginationNext />
            </PaginationContent>
        </Pagination>
    </div>
</template>

<script setup lang="ts">
    import axios from "@/utils/myAxios"
    import { Badge } from '@/components/ui/badge'
    import { ButtonGroup } from '@/components/ui/button-group'
    import { Input } from '@/components/ui/input'
    import { Button } from '@/components/ui/button'
    import { Edit, Ghost, SearchIcon, Trash, User } from 'lucide-vue-next'
    import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationNext, PaginationPrevious, } from '@/components/ui/pagination'
    import { Table, TableBody, TableCell, TableRow, } from '@/components/ui/table'
    import { onMounted, ref, watch } from "vue"
    import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger, SheetFooter } from '@/components/ui/sheet'
    import QuestionBankInfoDialog from "./QuestionBankInfoDialog.vue"

    onMounted(() => {
        getTag()
        getPublicQuestions()
    })

    interface Tag {
        id: number
        name: string
        count: number
    }

    const tags = ref<Tag[]>([])

    const getTag = async () => {
        try {
            const res = await axios.get("/api/tag")
            tags.value = res.data.data
        } catch (e) {
            console.log(e)
        }
    }

    const publicSearchContent = ref("")

    interface QuestionBank {
        id: number
        title: string
        tags: string[]
        difficulty: number
    }

    // 公共题库相关
    const publicQuestions = ref<QuestionBank[]>([])
    const publicPageNum = ref(1)
    const publicPageSize = ref(8)
    const publicTotal = ref(0)
    const curTag = ref<number | null>(null)

    // 监听公有题库页码变化
    watch(publicPageNum, () => {
        getPublicQuestions()
    })

    // 监听 curTag，变化时立即获取数据
    watch(curTag, () => {
        publicPageNum.value = 1 // 重置页码
        getPublicQuestions()
    })

    watch(publicSearchContent, (newVal) => {
        if (newVal === '') getPublicQuestions()
    })

    // 获取题库数据
    const getPublicQuestions = async () => {
        try {
            const res = await axios.get("/api/question/public", {
                params: {
                    pageNum: publicPageNum.value,
                    pageSize: publicPageSize.value,
                    content: publicSearchContent.value,
                    tagId: curTag.value
                }
            })
            const resData = res.data.data
            publicQuestions.value = resData.records
            publicTotal.value = resData.total
        } catch (e) {
            console.error('获取失败:', e)
        }
    }



    // ----------------------------------------以下为私人题库----------------------------------------
    const privateSearchContent = ref("")
    const privatePageNum = ref(1)
    const privatePageSize = ref(8)
    const privateTotal = ref(0)
    const privateQuestions = ref<QuestionBank[]>([])

    // 监听公有题库页码变化
    watch(privatePageNum, () => {
        getPrivateQuestions()
    })

    watch(privateSearchContent, (newVal) => {
        if (newVal === '') getPrivateQuestions()
    })

    const getPrivateQuestions = async () => {
        try {
            const res = await axios.get("/api/question/private", {
                params: {
                    pageNum: privatePageNum.value,
                    pageSize: privatePageSize.value,
                    content: privateSearchContent.value,
                }
            })
            const resData = res.data.data
            privateQuestions.value = resData.records
            privateTotal.value = resData.total
        } catch (e) {
            console.error('获取失败:', e)
        }
    }

    const deleteQuestion = async (question: QuestionBank) => {
        try {
            await axios.delete("/api/question/delete", {
                params: {
                    questionId: question.id
                }
            })
            getPrivateQuestions()
        } catch (e) {
            console.error(e)
        }
    }
</script>