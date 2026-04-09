<template>
    <div>
        <Dialog>
            <DialogTrigger as-child>
                <!-- 支持自定义触发按钮，如果不传则使用默认样式 -->
                <slot name="trigger">
                    <div
                        class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                        {{ initialData ? '编辑考试' : '创建考试' }}
                    </div>
                </slot>
            </DialogTrigger>

            <DialogContent class="sm:max-w-125">
                <DialogHeader>
                    <DialogTitle>{{ exam.id ? '编辑考试' : '创建新考试' }}</DialogTitle>
                    <DialogDescription>
                        请填写考试基本信息，设置准确的起止时间。
                    </DialogDescription>
                </DialogHeader>

                <div class="grid gap-4 py-4">
                    <!-- 标题 -->
                    <div class="grid w-full gap-1.5">
                        <Label for="title">考试名称</Label>
                        <Input id="title" v-model="exam.title" placeholder="请输入考试标题" />
                    </div>

                    <!-- 描述 -->
                    <div class="grid w-full gap-1.5">
                        <Label for="description">考试描述</Label>
                        <Textarea id="description" v-model="exam.description" placeholder="请输入考试要求或说明" />
                    </div>

                    <!-- 时间选择区域 -->
                    <div class="flex space-x-4">
                        <!-- 开始时间 -->
                        <div class="grid w-1/2 gap-1.5">
                            <Label>开始时间</Label>
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
                                        <span class="text-xs font-medium">精确时间:</span>
                                        <Input type="time" v-model="startTime" class="h-8" />
                                    </div>
                                </PopoverContent>
                            </Popover>
                        </div>

                        <!-- 结束时间 -->
                        <div class="grid w-1/2 gap-1.5">
                            <Label>结束时间</Label>
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
                                        <span class="text-xs font-medium">精确时间:</span>
                                        <Input type="time" v-model="endTime" class="h-8" />
                                    </div>
                                </PopoverContent>
                            </Popover>
                        </div>
                    </div>
                </div>

                <DialogFooter>
                    <DialogClose as-child>
                        <Button variant="ghost">取消</Button>
                    </DialogClose>
                    <Button @click="handleSubmit()"
                        :disabled="isSubmitting || !exam.title || !exam.description || !startDate || !endDate">
                        <span v-if="isSubmitting">提交中...</span>
                        <span v-else>{{ exam.id ? '保存修改' : '立即创建' }}</span>
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    </div>
</template>

<script setup lang="ts">
    import { ref, computed, watchEffect } from 'vue'
    import { useRoute } from 'vue-router'
    import router from '@/router'
    import axios from "@/utils/myAxios"
    import dayjs from 'dayjs'
    import { cn } from '@/lib/utils'
    import { Button } from '@/components/ui/button'
    import { Input } from '@/components/ui/input'
    import { Textarea } from '@/components/ui/textarea'
    import { Label } from "@/components/ui/label"
    import { Calendar } from '@/components/ui/calendar'
    import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
    import { Calendar as CalendarIcon } from 'lucide-vue-next'
    import { type DateValue, getLocalTimeZone, fromDate } from '@internationalized/date'

    interface Exam {
        id?: number
        teamId: number
        title: string
        description: string
        startTime: string
        endTime: string
    }

    const props = defineProps<{
        initialData?: Exam // 编辑模式下传入的初始数据
    }>()

    const route = useRoute()
    const isSubmitting = ref(false)

    // 响应式表单数据
    const exam = ref<Exam>({
        teamId: Number(route.params.id),
        title: '',
        description: '',
        startTime: '',
        endTime: '',
    })

    // UI 专用状态 (用于日历和时间选择器)
    const startDate = ref<DateValue>()
    const startTime = ref('00:00')
    const endDate = ref<DateValue>()
    const endTime = ref('00:00')

    /**
     * 数据回显逻辑：当 initialData 变化时，拆解字符串并赋值给 UI 组件
     */
    watchEffect(() => {
        if (props.initialData) {
            // 显式提取需要的字段，过滤掉 parent 传过来的冗余数据
            exam.value = {
                id: props.initialData.id,
                teamId: props.initialData.teamId,
                title: props.initialData.title || '',
                description: props.initialData.description || '',
                startTime: props.initialData.startTime || '',
                endTime: props.initialData.endTime || '',
            }

            if (props.initialData.startTime) {
                const d = new Date(props.initialData.startTime)
                startDate.value = fromDate(d, getLocalTimeZone())
                startTime.value = dayjs(d).format('HH:mm')
            }

            if (props.initialData.endTime) {
                const d = new Date(props.initialData.endTime)
                endDate.value = fromDate(d, getLocalTimeZone())
                endTime.value = dayjs(d).format('HH:mm')
            }
        }
    })

    /**
     * 计算属性：格式化预览文字
     */
    const displayStartDate = computed(() => {
        if (!startDate.value) return '选择开始日期'
        const jsDate = startDate.value.toDate(getLocalTimeZone())
        return dayjs(jsDate).format('YYYY-MM-DD') + ' ' + startTime.value
    })

    const displayEndDate = computed(() => {
        if (!endDate.value) return '选择结束日期'
        const jsDate = endDate.value.toDate(getLocalTimeZone())
        return dayjs(jsDate).format('YYYY-MM-DD') + ' ' + endTime.value
    })

    /**
     * 提交处理（创建或更新）
     */
    const handleSubmit = async () => {
        isSubmitting.value = true

        // 1. 提交前将 UI 状态组装回后端需要的 LocalDateTime 格式
        if (startDate.value) {
            const d = startDate.value.toDate(getLocalTimeZone())
            exam.value.startTime = dayjs(d).format('YYYY-MM-DD') + `T${startTime.value}:00`
        }
        if (endDate.value) {
            const d = endDate.value.toDate(getLocalTimeZone())
            exam.value.endTime = dayjs(d).format('YYYY-MM-DD') + `T${endTime.value}:00`
        }

        try {
            let res;
            if (exam.value.id) {
                // 编辑模式
                res = await axios.put("/api/exam/draft/update-exam", exam.value)
            } else {
                // 创建模式
                res = await axios.post("/api/exam/draft/create-exam", exam.value)
            }

            if (res.data.code === 1) {
                // 成功后跳转或处理（res.data.data 通常是考试 ID）
                const examId = exam.value.id || res.data.data
                router.push(`/exam/${examId}`)
            }
        } catch (e) {
            console.error("提交失败:", e)
        } finally {
            isSubmitting.value = false
        }
    }
</script>