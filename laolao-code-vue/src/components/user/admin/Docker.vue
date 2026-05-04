<template>
    <div class="flex flex-col p-3 space-y-3">
        <div class="flex space-x-3">
            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle>容器池状态</CardTitle>
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
                    <CardTitle>任务统计</CardTitle>
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
                    <CardTitle>平均性能</CardTitle>
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
            <!-- 详细信息表格区 -->
            <Card class="flex flex-col w-1/2">
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
                                <TableCell>{{ adminJudgerInfo?.hostArch }} / {{ adminJudgerInfo?.hostOs }}</TableCell>
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
            
            <div
                class="border-2 border-dashed rounded-lg flex items-center justify-center text-muted-foreground text-sm italic w-1/2">
                更多监控图表（CPU波动图、容器日志）正在开发中...
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from 'vue';
    import axios from '@/utils/myAxios'
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { Badge } from '@/components/ui/badge'
    import { Card, CardContent, CardHeader, CardTitle, } from '@/components/ui/card'

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

    const adminJudgerInfo = ref<AdminJudgerInfo>()

    const getJudgerInfo = async () => {
        try {
            const res = await axios.get("/api/docker/judger")
            adminJudgerInfo.value = res.data.data
        } catch (error) {
            console.log(error)
        }
    }

    onMounted(() => {
        getJudgerInfo()
    })
</script>