<template>
    <div class="flex flex-col p-3 space-y-3">
        <div class="flex space-x-3">
            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle>组概览</CardTitle>
                </CardHeader>
                <CardContent>
                    <div class="flex justify-between">
                        <p>组数量</p>
                        <p>{{ summary?.teamCount }}</p>
                    </div>
                    <div class="flex justify-between">
                        <p>考试发布数量</p>
                        <p>{{ summary?.examCount }}</p>
                    </div>
                </CardContent>
            </Card>
            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle>TODO</CardTitle>
                </CardHeader>
                <CardContent>
                    <p>TODO</p>
                </CardContent>
            </Card>
            <div class="w-1/3 flex flex-col">
                <div class="flex-1"></div>
                <div class="flex justify-end items-end">
                    <ButtonGroup class="w-full">
                        <Input v-model="content" placeholder="支持模糊搜索..." />
                        <Button @click="getTeam()" variant="outline" aria-label="Search" :disabled="content === ''">
                            <SearchIcon />
                        </Button>
                    </ButtonGroup>
                </div>
            </div>
        </div>
        <Table v-if="teams && teams.length > 0"
            class="flex-1 overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            <TableHeader>
                <TableRow>
                    <TableHead class="w-15 text-center">ID</TableHead>
                    <TableHead>组</TableHead>
                    <TableHead>描述</TableHead>
                    <TableHead>组管理员</TableHead>
                    <TableHead class="text-center">邀请码</TableHead>
                    <TableHead class="text-center">状态</TableHead>
                    <TableHead class="text-center">操作</TableHead>
                </TableRow>
            </TableHeader>

            <TableBody>
                <TableRow v-for="team in teams" :key="team.id">
                    <TableCell class="text-center">{{ team.id }}</TableCell>
                    <TableCell>{{ team.name }}</TableCell>
                    <TableCell class="truncate">{{ team.description }}</TableCell>
                    <TableCell>
                        <div class="flex items-center space-x-3">
                            <Avatar class="h-9 w-9">
                                <AvatarFallback>{{ team.managerName.slice(0, 1).toUpperCase() }}</AvatarFallback>
                            </Avatar>
                            <div class="flex flex-col">
                                <span class="text-sm">{{ team.managerName }}</span>
                                <span class="text-xs text-muted-foreground">{{ team.managerUsername }}</span>
                            </div>
                        </div>
                    </TableCell>
                    <TableCell class="text-center">{{ team.inviteCode }}</TableCell>
                    <TableCell>
                        <div class="flex items-center justify-center gap-2">
                            <span class="flex h-2 w-2 rounded-full"
                                :class="team.status === 1 ? 'bg-green-500' : 'bg-red-500'"></span>
                            <span class="text-sm">
                                {{ team.status === 1 ? "启用" : "停用" }}
                            </span>
                        </div>
                    </TableCell>
                    <TableCell class="text-center">
                        <div class="flex justify-center space-x-2">
                            <Dialog>
                                <DialogTrigger as-child>
                                    <Button variant="outline" size="sm" @click="newData = { ...team }">编辑</Button>
                                </DialogTrigger>
                                <DialogContent class="sm:max-w-125">
                                    <DialogHeader>
                                        <DialogTitle>信息修改</DialogTitle>
                                        <DialogDescription></DialogDescription>
                                    </DialogHeader>
                                    <div class="flex flex-col">
                                        <div class="grid w-full items-center gap-4">
                                            <div class="flex flex-col space-y-1.5">
                                                <Label>组名</Label>
                                                <Input v-model="newData!.name" />
                                            </div>
                                            <div class="flex flex-col space-y-1.5">
                                                <Label>邀请码</Label>
                                                <Input v-model="newData!.inviteCode" />
                                            </div>
                                            <div class="flex flex-col space-y-1.5">
                                                <Label>描述</Label>
                                                <Textarea v-model="newData!.description" />
                                            </div>
                                        </div>
                                    </div>
                                    <DialogFooter>
                                        <DialogClose as-child>
                                            <Button type="submit" @click="update()" :disabled="!newData!.name">
                                                保存更改
                                            </Button>
                                        </DialogClose>
                                    </DialogFooter>
                                </DialogContent>
                            </Dialog>
                            <Button variant="destructive" size="sm" @click="changeStatus(team)">{{ team.status === 0 ?
                                "启用" : "禁用" }}</Button>
                        </div>
                    </TableCell>
                </TableRow>
            </TableBody>
        </Table>

        <div v-else class="flex flex-col h-full items-center justify-center gap-4 text-center">
            <div class="rounded-full bg-muted/30 p-4">
                <Ghost class="h-10 w-10 text-muted-foreground/60" />
            </div>
            <div class="space-y-1">
                <h3 class="text-lg font-medium">暂无数据</h3>
                <p class="text-sm text-muted-foreground">
                    当前没有任何组数据，或者未找到匹配的结果。
                </p>
            </div>
        </div>

        <!-- 分页固定区域：底部固定 -->
        <div class="absolute bottom-0 left-0 right-0 h-16 border-t flex items-center p-2">
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
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref, watch } from 'vue';
    import axios from '@/utils/myAxios'
    import dayjs from 'dayjs'
    import relativeTime from 'dayjs/plugin/relativeTime'
    import 'dayjs/locale/zh-cn'
    dayjs.extend(relativeTime)
    dayjs.locale('zh-cn')
    import { Button } from '@/components/ui/button'
    import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from '@/components/ui/table'
    import { Avatar, AvatarFallback } from '@/components/ui/avatar'
    import { Textarea } from '@/components/ui/textarea'
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationNext, PaginationPrevious, } from '@/components/ui/pagination'
    import { Ghost, SearchIcon, } from 'lucide-vue-next'
    import { ButtonGroup } from '@/components/ui/button-group'
    import { Card, CardContent, CardHeader, CardTitle, } from '@/components/ui/card'


    onMounted(() => {
        getTeam()
        getSummary()
    })

    interface Team {
        id: number
        name: string
        description: string
        managerId: number
        managerUsername: string
        managerName: string
        inviteCode: string
        status: number
    }

    interface Summary {
        teamCount: number
        examCount: number
    }

    const teams = ref<Team[]>([])
    const summary = ref<Summary>()
    const pageNum = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const content = ref("")

    watch(pageNum, () => {
        getTeam()
    })

    watch(content, (newVal) => {
        if (newVal === '') {
            getTeam()
        }
    })


    const getTeam = async () => {
        try {
            const res = await axios.get("/api/team/all", {
                params: {
                    pageNum: pageNum.value,
                    pageSize: pageSize.value,
                    content: content.value
                }
            })
            const resData = res.data.data
            teams.value = resData.records
            total.value = resData.total
        } catch (error) {
            console.log(error)
        }
    }

    const getSummary = async () => {
        try {
            const res = await axios.get("/api/team/summary")
            summary.value = res.data.data
        } catch (error) {
            console.log(error)
        }
    }

    const changeStatus = async (team: Team) => {
        try {
            const res = await axios.put("/api/team/status", null, {
                params: {
                    teamId: team.id
                }
            })
            if (res.data.code === 1) {
                team.status = 1 - team.status
            }
        } catch (error) {
            console.log(error)
        }
    }

    const newData = ref<Team>()

    const update = async () => {
        try {
            await axios.put("/api/team/update", {
                id: newData.value?.id,
                name: newData.value?.name,
                inviteCode: newData.value?.inviteCode,
                description: newData.value?.description
            })
            getTeam()
        } catch (error) {
            console.log(error)
        }
    }
</script>