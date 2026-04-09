<template>
    <div class="flex bg-gray-100">
        <div
            class="h-full w-14 shrink-0 flex flex-col items-center py-4 gap-4 border-r bg-gray-50 overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            <div v-for="user in users" :key="user.id" @click="currentUser = user" :class="[
                'w-10 h-10 shrink-0 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600',
                currentUser === user ? 'border-zinc-800 text-zinc-900' : ''
            ]">
                {{ user.name.substring(0, 1) }}
            </div>

            <AlertDialog>
                <AlertDialogTrigger as-child>
                    <div :class="[
                        'w-10 h-10 shrink-0 flex items-center justify-center rounded-lg cursor-pointer text-lg border-dashed border-3 font-semibold transition',
                        'text-gray-400 border-gray-300 hover:border-gray-400 hover:text-gray-600'
                    ]">
                        <SendHorizonal />
                    </div>
                </AlertDialogTrigger>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>确定结束改卷？</AlertDialogTitle>
                        <AlertDialogDescription>
                            结束后将会公布成绩，不可再次修改
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>取消</AlertDialogCancel>
                        <AlertDialogAction @click = "graded()">确定</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
        <div class="flex flex-col flex-1 items-center p-2 space-y-2 overflow-y-auto">
            <div class=" w-5/7 border rounded py-2 px-6 space-y-2 bg-white">
                <div class="flex justify-between">
                    <span>考生：{{ currentUser?.name }}</span>
                    <span>分数：{{ currentUser?.score }}</span>
                </div>
                <div class="flex justify-between">
                    <span>考试时间：{{ dayjs(currentUser?.enterTime).format('YYYY/MM/DD HH:mm') }}</span>
                    <span>提交时间：{{ dayjs(currentUser?.submitTime).format('YYYY/MM/DD HH:mm') }}</span>
                </div>
            </div>
            <div class=" w-5/7 shrink-0 border rounded py-2 px-6 space-y-2 bg-white">
                <div v-for="judgeRecord in currentUser?.judgeRecords" :key="judgeRecord.id">
                    <div class="flex justify-between">
                        <p class="space-x-3">
                            <span>{{ judgeRecord.title }}</span>
                            <span>{{ judgeRecord.totalScore }}分</span>
                        </p>

                        <Badge variant="secondary" :class="['my-1',
                            judgeRecord.status !== 0 ?
                                'text-white bg-orange-500 dark:bg-orange-600' :
                                'text-white bg-green-500 dark:bg-green-600'
                        ]">
                            {{ examStore.getStatusTextByCode(judgeRecord.status) }}
                        </Badge>
                    </div>

                    <div class="h-96">
                        <MonacoEditor v-if="currentUser" v-model="judgeRecord.answerCode" language="java"
                            theme="vs" />
                    </div>
                    <div class="flex justify-end pt-3">
                        <ButtonGroup>
                            <Input v-model="judgeRecord!.userScore" type="number" />
                            <Button @click="updateScore(judgeRecord)" variant="outline">
                                <Check />
                            </Button>
                        </ButtonGroup>
                    </div>

                    <div class=" border-t-2 border-dashed border-gray-400 mt-4 mb-8"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import axios from "@/utils/myAxios"
    import { onMounted, ref } from "vue";
    import { useRoute } from "vue-router";
    import { Check, SendHorizonal } from 'lucide-vue-next'
    import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger, } from '@/components/ui/alert-dialog'
    import { Button } from '@/components/ui/button'
    import { ButtonGroup } from '@/components/ui/button-group'
    import { Input } from '@/components/ui/input'
    const route = useRoute()
    import dayjs from "dayjs";
    import MonacoEditor from "@/components/common/MonacoEditor.vue"
    import { useExamStore } from "@/stores/ExamStore"
    import Badge from "@/components/ui/badge/Badge.vue";
    const examStore = useExamStore()
    import router from '@/router';

    onMounted(() => {
        getUser()
    })

    interface JudgeRecord {
        id: number
        title: string
        totalScore: number
        userScore: number
        answerCode: string
        status: number
    }

    interface User {
        id: number
        name: string
        score: number
        enterTime: string
        submitTime: string
        judgeRecords: JudgeRecord[]
    }

    const users = ref<User[]>([])
    const currentUser = ref<User | undefined>(undefined)

    const getUser = async () => {
        try {
            const res = await axios.get("/api/exam/grade", {
                params: {
                    examId: route.params.id
                }
            })
            users.value = res.data.data
            currentUser.value = users.value[0]
        } catch (e) {
            console.log(e);
        }
    }

    const updateScore = async (judgeRecord: JudgeRecord) => {
        try {
            const res = await axios.put("/api/exam/grade/update-score", {
                examRecordId: currentUser.value?.id,
                judgeRecordId: judgeRecord.id,
                score: judgeRecord.userScore
            })
            if (res.data.code === 1) {
                currentUser.value!.score = currentUser.value!.score + res.data.data
            }
        } catch (e) {
            console.log(e);
        }
    }

    const graded = async () => {
        try {
            const res = await axios.put("/api/exam/grade/graded", {}, {
                params: {
                    examId: route.params.id
                }
            })
            if (res.data.code === 1) {
                router.replace("/exam")
            }
        } catch (e) {
            console.log(e);
        }
    }


</script>