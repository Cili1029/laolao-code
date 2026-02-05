<template>
  <div class="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
    <div :class="cn('flex flex-col gap-6', props.class)" class="w-full max-w-sm md:max-w-4xl">
      <Card class="overflow-hidden p-0">
        <CardContent class="grid p-0 md:grid-cols-2">
          <form @submit.prevent="signIn()" class="p-6 md:p-8">
            <FieldGroup>
              <p class="flex justify-center items-center text-3xl font-bold">
                登录
              </p>
              <Field>
                <FieldLabel for="username">
                  账号
                </FieldLabel>
                <Input v-model="username" id="username" type="text" required />
              </Field>
              <Field>
                <div class="flex items-center">
                  <FieldLabel for="password">
                    密码
                  </FieldLabel>
                  <a href="#" class="ml-auto text-sm underline-offset-2 hover:underline">
                    忘记密码？
                  </a>
                </div>
                <Input v-model="password" id="password" type="password" required />
              </Field>
              <Field>
                <Button type="submit">
                  登录
                </Button>
              </Field>
              <FieldDescription class="text-center">
                没有账号？
                <a href="#">
                  去注册
                </a>
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
        日落尤其温柔 人间皆是浪漫
      </FieldDescription>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, type HTMLAttributes } from "vue"
  import { cn } from "@/lib/utils"
  import { Button } from '@/components/ui/button'
  import { Card, CardContent } from '@/components/ui/card'
  import { Field, FieldDescription, FieldGroup, FieldLabel } from '@/components/ui/field'
  import { Input } from '@/components/ui/input'
  import axios from "axios"

  const props = defineProps<{
    class?: HTMLAttributes["class"]
  }>()

  const username = ref("")
  const password = ref("")

  const signIn = async () => {
    try {
      const res = await axios.postForm("/api/user/sign-in", {
        username: username.value,
        password: password.value
      }, {
        withCredentials: true
      })
    } catch (e) {
      console.log(e);
    }
  }

</script>