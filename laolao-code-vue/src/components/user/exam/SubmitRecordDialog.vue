<template>
    <Dialog>
        <DialogTrigger as-child>
            <p @click="getSimpleSubmitRecord()">提交记录123</p>
        </DialogTrigger>
        <DialogContent class="sm:max-w-106.25">
            <DialogHeader>
                <DialogTitle>提交记录</DialogTitle>
                <DialogDescription>
                </DialogDescription>
            </DialogHeader>

            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead class="text-left">状态</TableHead>
                        <TableHead class="text-center">得分</TableHead>
                        <TableHead class="text-center">执行用时</TableHead>
                        <TableHead class="text-right">消耗内存</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    <TableRow v-for="simple in simples" :key="simple.id">
                        <TableCell class="text-left" :class="simple.status == 0 ? 'text-green-500' : 'text-red-500'">
                            {{ simple.status == 0 ? '通过' : '未通过' }}
                        </TableCell>
                        <TableCell class="text-center">
                            {{ simple.score }}
                        </TableCell>
                        <TableCell class="text-center">
                            {{ simple.status == 0 ? simple.time : "N/A" }}
                        </TableCell>
                        <TableCell class="text-right">
                            {{ simple.status == 0 ? simple.memory : "N/A" }}
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </DialogContent>
    </Dialog>
</template>

<script setup lang="ts">
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Button } from '@/components/ui/button'
    import { useExamStore } from "@/stores/ExamStore"
    const examStore = useExamStore()
    import { onMounted, ref } from 'vue'
    import axios from "@/utils/myAxios"

    interface Simple {
        id: number
        status: number
        score: number
        time: string
        memory: number
    }

    const simples = ref<Simple[]>([])
    const getSimpleSubmitRecord = async () => {
        try {
            const res = await axios.get("/api/submit-record/simple", {
                params: {
                    examRecordId: examStore.recordId
                }
            })
            simples.value = res.data.data
        } catch (e) {
            console.log(e);
        }
    }
</script>