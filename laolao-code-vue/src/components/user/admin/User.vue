<template>
    <div class="flex flex-col p-3 space-y-3">
        <div class="flex space-x-3">
            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle class="flex items-center gap-2">
                        <Users class="w-4 h-4" />
                        网站用户数
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div class="flex items-center justify-between">
                        <span class="flex items-center gap-2 text-sm">
                            <UserCog class="w-4 h-4" />
                            组管理员
                        </span>
                        <span class="font-semibold tabular-nums">{{ summary?.managerCount }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="flex items-center gap-2 text-sm">
                            <User class="w-4 h-4" />
                            组员
                        </span>
                        <span class="font-semibold tabular-nums">{{ summary?.userCount }}</span>
                    </div>
                </CardContent>
            </Card>
            <Card class="w-1/3">
                <CardHeader>
                    <CardTitle class="flex items-center gap-2">
                        <Activity class="w-5 h-5" />
                        在线人数
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div class="flex justify-between items-center">
                        <div class="*:data-[slot=avatar]:ring-background flex -space-x-2 *:data-[slot=avatar]:ring-2">
                            <Avatar v-for="value in summary?.onlineCount" :key="value">
                                <AvatarImage :src="userAvatar" alt="@shadcn" />
                                <AvatarFallback>USER</AvatarFallback>
                            </Avatar>
                        </div>
                        <span class="font-semibold tabular-nums text-lg">{{ summary?.onlineCount }}</span>
                    </div>
                </CardContent>
            </Card>
            <div class="w-1/3 flex flex-col">
                <div class="flex-1"></div>
                <div class="flex justify-end items-end">
                    <ButtonGroup class="w-full">
                        <Input v-model="content" @keyup.enter.exact.prevent="getUser()" placeholder="支持模糊搜索..." />
                        <Button @click="getUser()" variant="outline" aria-label="Search" :disabled="content === ''">
                            <SearchIcon />
                        </Button>
                    </ButtonGroup>
                </div>
            </div>
        </div>
        <Table v-if="users && users.length > 0"
            class="flex-1 overflow-y-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            <TableHeader>
                <TableRow>
                    <TableHead class="w-15 text-center">ID</TableHead>
                    <TableHead>用户</TableHead>
                    <TableHead class="text-center">权限</TableHead>
                    <TableHead class="text-center">状态</TableHead>
                    <TableHead class="text-center">操作</TableHead>
                </TableRow>
            </TableHeader>

            <TableBody>
                <TableRow v-for="user in users" :key="user.id">
                    <TableCell class="text-center">{{ user.id }}</TableCell>
                    <TableCell>
                        <div class="flex items-center space-x-3">
                            <Avatar class="h-9 w-9">
                                <AvatarFallback>{{ user.name.slice(0, 1).toUpperCase() }}</AvatarFallback>
                            </Avatar>
                            <div class="flex flex-col">
                                <span class="text-sm">{{ user.name }}</span>
                                <span class="text-xs text-muted-foreground">{{ user.username }}</span>
                            </div>
                        </div>
                    </TableCell>
                    <TableCell class="text-center">
                        <Badge :variant="user.role === 0 ? 'default' : 'outline'">
                            {{ user.role === 0 ? "项目管理员" : user.role === 1 ? "组管理员" : "组员" }}
                        </Badge>
                    </TableCell>

                    <TableCell>
                        <div class="flex items-center justify-center gap-2">
                            <span class="flex h-2 w-2 rounded-full"
                                :class="user.status === 1 ? 'bg-green-500' : 'bg-red-500'"></span>
                            <span class="text-sm">
                                {{ user.status === 1 ? "启用" : "停用" }}
                            </span>
                        </div>
                    </TableCell>

                    <TableCell class="text-center">
                        <div class="flex justify-center space-x-2">
                            <Dialog>
                                <DialogTrigger as-child>
                                    <Button variant="outline" size="sm" @click="newData = { ...user }">编辑</Button>
                                </DialogTrigger>
                                <DialogContent class="sm:max-w-125">
                                    <DialogHeader>
                                        <DialogTitle>信息修改</DialogTitle>
                                        <DialogDescription></DialogDescription>
                                    </DialogHeader>
                                    <div class="flex flex-col">
                                        <div class="grid w-full items-center gap-4">
                                            <div class="flex flex-col space-y-1.5">
                                                <Label>账号</Label>
                                                <Input v-model="newData!.username" />
                                            </div>

                                            <div class="flex flex-col space-y-1.5">
                                                <Label>用户名</Label>
                                                <Input v-model="newData!.name" />
                                            </div>

                                            <div class="flex space-x-3">
                                                <div class="flex items-center gap-1">
                                                    <Checkbox :model-value="newData?.role === 0"
                                                        @update:modelValue="(val) => val && (newData!.role = 0)" />
                                                    <Label>项目管理员</Label>
                                                </div>
                                                <div class="flex items-center gap-1">
                                                    <Checkbox :model-value="newData?.role === 1"
                                                        @update:modelValue="(val) => val && (newData!.role = 1)" />
                                                    <Label>组管理员</Label>
                                                </div>
                                                <div class="flex items-center gap-1">
                                                    <Checkbox :model-value="newData?.role === 2"
                                                        @update:modelValue="(val) => val && (newData!.role = 2)" />
                                                    <Label>组员</Label>
                                                </div>
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
                            <Button variant="destructive" size="sm" @click="changeStatus(user)">{{ user.status === 0 ?
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
                    当前没有任何用户数据，或者未找到匹配的结果。
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
    import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
    import { Badge } from '@/components/ui/badge'
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationNext, PaginationPrevious, } from '@/components/ui/pagination'
    import { Ghost, SearchIcon, UserCog, Users, User, Activity } from 'lucide-vue-next'
    import { Checkbox } from '@/components/ui/checkbox'
    import { ButtonGroup } from '@/components/ui/button-group'
    import { Card, CardContent, CardHeader, CardTitle, } from '@/components/ui/card'
    import userAvatar from '@/assets/user.jpg'


    onMounted(() => {
        getUser()
        getSummary()
    })

    interface User {
        id: number
        username: string
        name: string
        role: number
        status: number
    }

    interface Summary {
        managerCount: number
        userCount: number
        onlineCount: number
    }

    const users = ref<User[]>([])
    const summary = ref<Summary>()
    const pageNum = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const content = ref("")

    watch(pageNum, () => {
        getUser()
    })

    watch(content, (newVal) => {
        if (newVal === '') getUser()
    })


    const getUser = async () => {
        try {
            const res = await axios.get("/api/user", {
                params: {
                    pageNum: pageNum.value,
                    pageSize: pageSize.value,
                    content: content.value
                }
            })
            const resData = res.data.data
            users.value = resData.records
            total.value = resData.total
        } catch (error) {
            console.log(error)
        }
    }

    const getSummary = async () => {
        try {
            const res = await axios.get("/api/user/summary")
            summary.value = res.data.data
        } catch (error) {
            console.log(error)
        }
    }

    const changeStatus = async (user: User) => {
        try {
            const res = await axios.put("/api/user/status", null, {
                params: {
                    userId: user.id
                }
            })
            if (res.data.code === 1) {
                user.status = 1 - user.status
            }
        } catch (error) {
            console.log(error)
        }
    }

    const newData = ref<User>()

    const update = async () => {
        try {
            await axios.put("/api/user/update", {
                id: newData.value?.id,
                username: newData.value?.username,
                name: newData.value?.name,
                role: newData.value?.role
            })
            getUser()
        } catch (error) {
            console.log(error)
        }
    }
</script>