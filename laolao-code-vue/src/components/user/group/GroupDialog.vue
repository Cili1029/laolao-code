<template>
    <div>
        <!-- 学生的加入小组 -->
        <Dialog v-if="UserStore.user.role === 2">
            <DialogTrigger as-child>
                <div
                    class="flex cursor-pointer text-green-600 items-center px-2 py-1 bg-gray-100 text-sm hover:bg-gray-200 rounded">
                    加入小组
                </div>
            </DialogTrigger>
            <DialogContent class="sm:max-w-106.25">
                <DialogHeader>
                    <DialogTitle>加入小组</DialogTitle>
                    <DialogDescription>
                        输入导师创建的邀请码
                    </DialogDescription>
                </DialogHeader>
                <Input v-model="inviteCode" />
                <DialogFooter>
                    <DialogClose as-child>
                        <Button variant="outline">
                            取消
                        </Button>
                    </DialogClose>
                    <DialogClose as-child>
                        <Button @click="JoinGroup()" type="submit" :disabled="inviteCode === ''">
                            提交
                        </Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>

        <Dialog v-else-if="UserStore.user.role === 1">
            <DialogTrigger as-child>
                <span class="flex justify-end cursor-pointer hover:text-blue-500 text-gray-500 text-sm">创建小组</span>
            </DialogTrigger>
            <DialogContent class="sm:max-w-106.25">
                <DialogHeader>
                    <DialogTitle>创建小组</DialogTitle>
                    <DialogDescription>
                        填写基本小组信息
                    </DialogDescription>
                </DialogHeader>
                <div class="grid w-full gap-1.5">
                    <Label>组名</Label>
                    <Input v-model="group.name" />
                </div>
                <div class="grid w-full gap-1.5">
                    <Label>描述</Label>
                    <Textarea v-model="group.description" />
                </div>
                <DialogFooter>
                    <DialogClose as-child>
                        <Button variant="outline">
                            取消
                        </Button>
                    </DialogClose>
                    <DialogClose as-child>
                        <Button @click="createGroup()" type="submit"
                            :disabled="group.name === '' || group.description === ''">
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
    import { ref } from 'vue'
    import axios from "@/utils/myAxios"
    import { useUserStore } from '@/stores/UserStore'
    const UserStore = useUserStore()
    import { Textarea } from '@/components/ui/textarea'
    import { Label } from "@/components/ui/label"

    const inviteCode = ref("")
    const JoinGroup = async () => {
        try {
            await axios.post("/api/team/join", {
                inviteCode: inviteCode.value
            })
            // const res = await axios.post("/api")
            // if(res.data.code === 1) {

            // }
        } catch (e) {
            console.log(e);
        }
    }

    interface Group {
        name: string
        description: string
    }

    const group = ref<Group>({
        name: '',
        description: ''
    })

    // 组管理员创建
    const createGroup = async () => {
        try {
            await axios.post("/api/team/create", {
                name: group.value?.name,
                description: group.value?.description
            })
        } catch (e) {
            console.log(e);
        }
    }
</script>