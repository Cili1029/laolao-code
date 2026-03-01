<template>
    <div>
        <Dialog>
            <DialogTrigger as-child>
                <div
                    class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                    创建考试
                </div>
            </DialogTrigger>
            <DialogContent class="sm:max-w-106.25">
                <DialogHeader>
                    <DialogTitle>加入学习组</DialogTitle>
                    <DialogDescription>
                        输入导师创建的邀请码
                    </DialogDescription>
                </DialogHeader>

                <div class="grid w-full gap-1.5">
                    <Label>组名</Label>
                    <Input v-model="group.title" required />
                </div>
                <div class="grid w-full gap-1.5">
                    <Label>描述</Label>
                    <Textarea v-model="group.description" required />
                </div>
                <div class="flex space-x-2">
                    <div class="grid w-1/2 gap-1.5">
                        <Label>考试开始时间</Label>
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant="outline" :class="cn(
                                    'justify-start text-left font-normal',
                                    !startDate && 'text-muted-foreground'
                                )">
                                    <CalendarIcon class="mr-2 h-4 w-4" />
                                    {{ displayStartDate }}
                                </Button>
                            </PopoverTrigger>

                            <PopoverContent class="w-auto p-4 flex flex-col gap-4">
                                <Calendar v-model="startDate" />

                                <div class="flex items-center gap-2 border-t pt-4">
                                    <span class="text-sm">时间</span>
                                    <Input type="time" v-model="startTime" />
                                </div>

                            </PopoverContent>
                        </Popover>
                    </div>
                    <div class="grid w-1/2 gap-1.5">
                        <Label>考试结束时间</Label>
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant="outline" :class="cn(
                                    'justify-start text-left font-normal',
                                    !endDate && 'text-muted-foreground'
                                )">
                                    <CalendarIcon class="mr-2 h-4 w-4" />
                                    {{ displayEndDate }}
                                </Button>
                            </PopoverTrigger>

                            <PopoverContent class="w-auto p-4 flex flex-col gap-4">
                                <Calendar v-model="endDate" />

                                <div class="flex items-center gap-2 border-t pt-4">
                                    <span class="text-sm">时间</span>
                                    <Input type="time" v-model="endTime" />
                                </div>

                            </PopoverContent>
                        </Popover>
                    </div>
                </div>

                <DialogFooter>
                    <DialogClose as-child>
                        <Button variant="outline">
                            取消
                        </Button>
                    </DialogClose>
                    <DialogClose as-child>
                        <Button @click="createGroup()" type="submit"
                            :disabled="!group.title || !group.description || !startDate || !endDate">
                            提交
                        </Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    </div>

</template>

<script setup lang="ts">
    import { Button } from '@/components/ui/button'
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Input } from '@/components/ui/input'
    import axios from "@/utils/myAxios"
    import { Textarea } from '@/components/ui/textarea'
    import { Label } from "@/components/ui/label"
    import { ref, computed } from 'vue'
    import dayjs from 'dayjs'
    import { Calendar as CalendarIcon } from 'lucide-vue-next'
    import { type DateValue, getLocalTimeZone } from '@internationalized/date'
    import { Calendar } from '@/components/ui/calendar'
    import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
    import { cn } from '@/lib/utils'
    import { useRoute } from 'vue-router'
    import router from '@/router'
    const route = useRoute()


    interface Exam {
        studyGroupId: number
        title: string
        description: string
        startTime: string
        endTime: string
    }

    const group = ref<Exam>({
        studyGroupId: Number(route.params.id),
        title: '',
        description: '',
        startTime: '',
        endTime: '',
    })

    const createGroup = async () => {
        // 在提交前组装字符串
        if (startDate.value) {
            const d = startDate.value.toDate(getLocalTimeZone())
            group.value.startTime = dayjs(d).format('YYYY-MM-DD') + `T${startTime.value}:00`
        }
        if (endDate.value) {
            const d = endDate.value.toDate(getLocalTimeZone())
            group.value.endTime = dayjs(d).format('YYYY-MM-DD') + `T${endTime.value}:00`
        }

        try {
            const res = await axios.post("/api/exam/create", group.value)
            console.log(res.data.code)
            if (res.data.code === 1) {
                router.push(`/exam/create/${res.data.data}`)
            }
        } catch (e) {
            console.log(e);
        }
    }

    // 开始时间的状态
    const startDate = ref<DateValue>()
    const startTime = ref('00:00')

    // 结束时间的状态
    const endDate = ref<DateValue>()
    const endTime = ref('00:00')

    // 预览显示的日期字符串
    // 开始时间预览
    const displayStartDate = computed(() => {
        if (!startDate.value) return '选择开始日期'
        const jsDate = startDate.value.toDate(getLocalTimeZone())
        return dayjs(jsDate).format('YYYY-MM-DD') + ' ' + startTime.value
    })

    // 结束时间预览
    const displayEndDate = computed(() => {
        if (!endDate.value) return '选择结束日期'
        const jsDate = endDate.value.toDate(getLocalTimeZone())
        return dayjs(jsDate).format('YYYY-MM-DD') + ' ' + endTime.value
    })

</script>