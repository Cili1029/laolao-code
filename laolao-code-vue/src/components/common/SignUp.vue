<template>
    <div class="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
        <div :class="cn('flex flex-col gap-6', props.class)" class="w-full max-w-sm md:max-w-4xl">
            <Card class="overflow-hidden p-0">
                <CardContent class="grid p-0 md:grid-cols-2">
                    <form @submit.prevent="signUp()" class="p-6 md:p-8">
                        <FieldGroup>
                            <p class="flex justify-center items-center text-3xl font-bold">
                                注册
                            </p>
                            <Field>
                                <FieldLabel for="username">
                                    账号
                                </FieldLabel>
                                <Input v-model="username" id="username" type="text" required />
                            </Field>
                            <Field>
                                <FieldLabel for="password">
                                    密码
                                </FieldLabel>
                                <Input v-model="password" id="password" type="password" required />
                            </Field>
                            <Field class="grid grid-cols-2 gap-4">
                                <Field>
                                    <FieldLabel for="name">
                                        真实姓名
                                    </FieldLabel>
                                    <Input v-model="name" id="name" type="text" required />
                                </Field>
                                <Field>
                                    <FieldLabel for="role">
                                        身份
                                    </FieldLabel>
                                    <RadioGroup v-model="role" class="h-full">
                                        <div class="flex items-center justify-between w-full">
                                            <div class="flex items-center gap-1">
                                                <RadioGroupItem id="member" :value="2" class="h-full" />
                                                <Label for="member">成员</Label>
                                            </div>
                                            <div class="flex items-center gap-1">
                                                <RadioGroupItem id="advisor" :value="1" class="h-full" />
                                                <Label for="advisor">导师</Label>
                                            </div>
                                        </div>
                                    </RadioGroup>
                                </Field>
                            </Field>
                            <Field>
                                <Button type="submit">
                                    注册
                                </Button>
                            </Field>
                            <FieldDescription class="text-center">
                                有账号了？
                                <RouterLink to="/sign-in">
                                    去登录
                                </RouterLink>
                            </FieldDescription>
                        </FieldGroup>
                    </form>
                    <div class="bg-muted relative hidden md:block">
                        <img src="/src/assets/logo.jpg" alt="Image"
                            class="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale">
                    </div>
                </CardContent>
            </Card>
            <FieldDescription class="px-6 text-center">
                人间皆是浪漫
            </FieldDescription>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { ref, type HTMLAttributes } from "vue"
    import { cn } from "@/lib/utils"
    import { Button } from "@/components/ui/button"
    import { Card, CardContent } from "@/components/ui/card"
    import { Field, FieldDescription, FieldGroup, FieldLabel } from "@/components/ui/field"
    import { Label } from '@/components/ui/label'
    import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
    import { Input } from "@/components/ui/input"
    import axios from "@/utils/myAxios"

    const props = defineProps<{
        class?: HTMLAttributes["class"]
    }>()

    const username = ref("")
    const password = ref("")
    const name = ref("")
    const role = ref(2)

    const signUp = async () => {
        try {
            await axios.post("/api/user/sign-up", {
                username: username.value,
                password: password.value,
                name: name.value,
                role: role.value
            })
        } catch (e) {
            console.log(e);
        }
    }
</script>