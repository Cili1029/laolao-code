<template>
    <Dialog>
        <DialogTrigger as-child>
            <Button @click="getPrivateQuestions()" variant="outline">
                Open Dialog
            </Button>
        </DialogTrigger>
        <DialogContent class="sm:max-w-106.25">
            <DialogHeader>
                <DialogTitle>题库</DialogTitle>
                <DialogDescription>
                    选择一题题目以做修改
                </DialogDescription>
            </DialogHeader>
            <div class="flex flex-col">
                <div class="flex justify-between">
                    <p @click="handleSwitchType(0)"
                        :class="currentType === 0 ? 'text-blue-600 cursor-pointer' : 'cursor-pointer'">
                        诗人题库
                    </p>
                    <p @click="handleSwitchType(1)"
                        :class="currentType === 1 ? 'text-blue-600 cursor-pointer' : 'cursor-pointer'">
                        公共题库
                    </p>
                </div>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead class="w-25">
                                题目
                            </TableHead>
                            <TableHead>作者</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        <TableRow v-show="currentType === 0" v-for="question in privateQuestions" :key="question.id">
                            <TableCell>{{ question.title }}</TableCell>
                            <TableCell>{{ question.advisor }}</TableCell>
                        </TableRow>
                        <TableRow v-show="currentType === 1" v-for="question in publicQuestions" :key="question.id">
                            <TableCell>{{ question.title }}</TableCell>
                            <TableCell>{{ question.advisor }}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </div>
            <DialogFooter>
                <Pagination v-if="currentType === 0 && privateTotal > 0" v-model:page="privatePageNum"
                    :total="privateTotal" :items-per-page="pageSize" :sibling-count="1" show-edges>
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

                <Pagination v-if="currentType === 1 && publicTotal > 0" v-model:page="publicPageNum"
                    :total="publicTotal" :items-per-page="pageSize" :sibling-count="1" show-edges>
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
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup lang="ts">
    import axios from "@/utils/myAxios"
    import { Button } from '@/components/ui/button'
    import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationNext, PaginationPrevious, } from '@/components/ui/pagination'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { ref, watch } from "vue"

    const currentType = ref(0) // 0: 私有题库 1: 公共题库

    interface QuestionBank {
        id: number
        title: string
        advisor: string
    }

    // 私有题库相关
    const privateQuestions = ref<QuestionBank[]>([])
    const privatePageNum = ref(1)
    const pageSize = ref(3)
    const privateTotal = ref(0)

    // 公共题库相关
    const publicQuestions = ref<QuestionBank[]>([])
    const publicPageNum = ref(1)
    const publicTotal = ref(0)

    // 监听私有题库页码变化
    watch(
        () => privatePageNum.value,
        () => {
            getPrivateQuestions()
        }
    )

    // 监听公共题库页码变化
    watch(
        () => publicPageNum.value,
        () => {
            getPublicQuestions()
        }
    )

    // 获取私有题库数据
    const getPrivateQuestions = async () => {
        try {
            const res = await axios.get("/api/question/private", {
                params: {
                    pageNum: privatePageNum.value,
                    pageSize: pageSize.value,
                }
            })
            const resData = res.data.data
            privateQuestions.value = resData.records
            privateTotal.value = resData.total

        } catch (e) {
            console.error('获取私有题库失败:', e)
        }
    }

    // 获取公共题库数据
    const getPublicQuestions = async () => {
        try {
            const res = await axios.get("/api/question/public", {
                params: {
                    pageNum: publicPageNum.value,
                    pageSize: pageSize.value,
                }
            })
            const resData = res.data.data
            publicQuestions.value = resData.records
            publicTotal.value = resData.total

        } catch (e) {
            console.error('获取公共题库失败:', e)
        }
    }

    // 切换题库类型的处理函数
    const handleSwitchType = (type: number) => {
        currentType.value = type
        // 切换时重置对应页码为1
        if (type === 0) {
            privatePageNum.value = 1
            getPrivateQuestions()
        } else {
            publicPageNum.value = 1
            getPublicQuestions()
        }
    }
</script>