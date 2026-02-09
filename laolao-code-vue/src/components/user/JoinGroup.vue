<template>
    <div>
        <Dialog>
            <DialogTrigger as-child>
                <Plus />
            </DialogTrigger>
            <DialogContent class="sm:max-w-106.25">
                <DialogHeader>
                    <DialogTitle>加入学习组</DialogTitle>
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
    </div>

</template>

<script setup lang="ts">
    import { Plus } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, } from '@/components/ui/dialog'
    import { Input } from '@/components/ui/input'
    import { ref } from 'vue'
    import axios from "@/utils/myAxios"

    const inviteCode = ref("")
    const JoinGroup = async () => {
        try {
            await axios.post("/api/group/join", {
                inviteCode: inviteCode.value
            })
            // const res = await axios.post("/api")
            // if(res.data.code === 1) {

            // }
        } catch (e) {
            console.log(e);
        }
    }
</script>