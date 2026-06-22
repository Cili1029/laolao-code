<template>
    <div class="flex flex-col p-3 space-y-3">
        <div class="flex space-x-3">
            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle class="flex items-center gap-2">
                        <Container class="w-4 h-4" />
                        容器池状态
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div class="text-2xl font-bold">{{ adminJudgerInfo?.poolSize }}
                        <span class="text-xs text-muted-foreground">
                            Total
                        </span>
                    </div>
                    <div class="space-y-1">
                        <div class="flex justify-between text-sm">
                            <p class="text-muted-foreground">空闲可用</p>
                            <p class="text-green-500">{{ adminJudgerInfo?.idleCount }}</p>
                        </div>
                        <div class="flex justify-between text-sm">
                            <p class="text-muted-foreground">正在忙碌</p>
                            <p class="text-orange-500">{{ adminJudgerInfo?.busyCount }}</p>
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle class="flex items-center gap-2">
                        <Shell class="w-4 h-4" />
                        任务统计
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div class="text-2xl font-bold">{{ adminJudgerInfo?.totalJudgeCount }}
                        <span class="text-xs text-muted-foreground">
                            次判题
                        </span>
                    </div>
                    <div class="mt-2 flex space-x-2">
                        <Badge class="bg-green-100 text-green-700">
                            AC: {{ adminJudgerInfo?.successCount }}
                        </Badge>
                        <Badge class="bg-yellow-100 text-yellow-700">
                            FAIL: {{ adminJudgerInfo?.failCount }}
                        </Badge>
                        <Badge variant="destructive">
                            SE: {{ adminJudgerInfo?.systemErrorCount }}
                        </Badge>
                    </div>
                </CardContent>
            </Card>

            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle class="flex items-center gap-2">
                        <Zap class="w-4 h-4" />
                        平均性能
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div class="text-2xl font-bold">
                        {{ adminJudgerInfo?.avgTimeCost ?? 0 }} <span class="text-xs text-muted-foreground">ms</span>
                    </div>
                    <p class="text-xs text-muted-foreground">
                        单次判题平均响应时间
                    </p>
                    <div class="mt-3 h-2 w-full bg-secondary rounded-full overflow-hidden">
                        <div class="bg-primary h-full" :style="{ width: '60%' }"></div>
                    </div>
                </CardContent>
            </Card>
        </div>

        <div class="flex flex-1 space-x-3">
            <div class="flex flex-col w-1/2 space-y-3">
                <Card>
                    <CardHeader>
                        <CardTitle>宿主机环境</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>参数项</TableHead>
                                    <TableHead>当前数值</TableHead>
                                    <TableHead>说明</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                <TableRow>
                                    <TableCell>Docker 版本</TableCell>
                                    <TableCell>{{ adminJudgerInfo?.dockerEngineVersion }}</TableCell>
                                    <TableCell>当前判题机后端使用的 Docker API 版本</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>系统架构</TableCell>
                                    <TableCell>{{ adminJudgerInfo?.hostArch }} / {{ adminJudgerInfo?.hostOs }}
                                    </TableCell>
                                    <TableCell>宿主机指令集架构与操作系统</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>宿主机总内存</TableCell>
                                    <TableCell>{{ adminJudgerInfo?.hostMemoryTotal }}GB</TableCell>
                                    <TableCell>物理机或虚拟机分配的总可用内存</TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </CardContent>
                </Card>
                <Card class="flex-1">
                    <CardHeader>
                        <CardTitle>操作</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div class="flex gap-3 items-center">
                            <p>容器池大小：</p>
                            <ButtonGroup>
                                <Input type="number" v-model="newSize" />
                                <Button @click="adjustPoolSize()" variant="outline"
                                    :disabled="newSize <= (adminJudgerInfo?.poolSize ?? 0)">
                                    应用
                                </Button>
                            </ButtonGroup>
                        </div>
                    </CardContent>
                </Card>
            </div>

            <Card class="flex flex-col w-1/2">
                <CardHeader>
                    <CardTitle>最近判题</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>ID</TableHead>
                                <TableHead class="text-left">用户</TableHead>
                                <TableHead>状态</TableHead>
                                <TableHead class="text-center">执行用时</TableHead>
                                <TableHead class="text-right">消耗内存</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            <TableRow v-for="judgeRecord in judgeRecords" :key="judgeRecord.id">
                                <TableCell>
                                    {{ judgeRecord.id }}
                                </TableCell>
                                <div class="flex items-center space-x-3">
                                    <Avatar class="h-7 w-7">
                                        <AvatarFallback>{{ judgeRecord.name.slice(0, 1).toUpperCase() }}
                                        </AvatarFallback>
                                    </Avatar>
                                    <div class="flex flex-col">
                                        <span class="text-sm">{{ judgeRecord.name }}</span>
                                        <span class="text-xs text-muted-foreground">{{ judgeRecord.username }}</span>
                                    </div>
                                </div>
                                <TableCell class="text-left"
                                    :class="judgeRecord.status === 0 ? 'text-green-500' : 'text-red-500'">
                                    {{ examStore.getStatusTextByCode(judgeRecord.status) }}
                                </TableCell>
                                <TableCell class="text-center">
                                    <p class="flex justify-center items-center">
                                        <Timer class="pr-1" />
                                        {{ judgeRecord.status === 0 ? judgeRecord.time + "ms" : "N/A" }}
                                    </p>
                                </TableCell>
                                <TableCell class="text-right">
                                    <p class="flex justify-end items-center">
                                        <Cpu class="pr-1" />
                                        {{ judgeRecord.status === 0 ? judgeRecord.memory + "MB" : "N/A" }}
                                    </p>
                                </TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from 'vue';
    import axios from '@/utils/myAxios'
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { Badge } from '@/components/ui/badge'
    import { Card, CardContent, CardHeader, CardTitle, } from '@/components/ui/card'
    import { Button } from '@/components/ui/button'
    import { ButtonGroup } from '@/components/ui/button-group'
    import { Input } from '@/components/ui/input'
    import { Avatar, AvatarFallback } from '@/components/ui/avatar'
    import { useExamStore } from "@/stores/ExamStore"
    import { Container, Cpu, Shell, Timer, Zap } from 'lucide-vue-next';
    const examStore = useExamStore()

    interface AdminJudgerInfo {
        poolSize: number
        idleCount: number
        busyCount: number
        totalJudgeCount: number
        successCount: number
        failCount: number
        systemErrorCount: number
        avgTimeCost: number
        dockerEngineVersion: string
        hostArch: string
        hostOs: string
        hostMemoryTotal: number
    }

    interface JudgeRecord {
        id: number
        username: string
        name: string
        status: number
        time: number
        memory: number
    }

    const adminJudgerInfo = ref<AdminJudgerInfo>()
    const judgeRecords = ref<JudgeRecord[]>([])

    const getJudgerInfo = async () => {
        try {
            const res = await axios.get("/api/docker/judger")
            adminJudgerInfo.value = res.data.data
            newSize.value = adminJudgerInfo.value!.poolSize
        } catch (error) {
            console.log(error)
        }
    }

    const getJudgeRecord = async () => {
        try {
            const res = await axios.get("/api/docker/judge-record")
            judgeRecords.value = res.data.data
        } catch (error) {
            console.log(error)
        }
    }

    const newSize = ref(0)

    const adjustPoolSize = async () => {
        try {
            await axios.post("/api/docker/adjust", null, {
                params: {
                    newSize: newSize.value
                }
            })
        } catch (error) {
            console.log(error)
        }
    }

    onMounted(() => {
        getJudgerInfo()
        getJudgeRecord()
    })
</script>