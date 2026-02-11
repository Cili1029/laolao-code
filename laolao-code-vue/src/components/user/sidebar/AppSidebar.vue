<template>
  <!-- 考试时就禁用 -->
  <Sidebar :class="[
    'overflow-hidden *:data-[sidebar=sidebar]:flex-row',
    sidebarStore.exam ? 'pointer-events-none select-none opacity-50' : '']" v-bind="props">
    <!-- 第一个侧边栏（图标列） -->
    <Sidebar collapsible="none" class="w-[calc(var(--sidebar-width-icon)+1px)]! border-r">
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton size="lg" as-child class="md:h-8 md:p-0">
              <RouterLink :to="'' + type[0]?.url" @click="activeItem = type[0]!"
                class="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                <img :src="logo" />
              </RouterLink>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent class="px-1.5 md:px-0">
            <SidebarMenu>
              <SidebarMenuItem v-for="item in type" :key="item.title">
                <RouterLink :to="'' + item.url">
                  <SidebarMenuButton :tooltip="h('div', { hidden: false }, item.title)"
                    :is-active="activeItem?.title === item.title" class="px-2.5 md:px-2" @click="() => {
                      activeItem = item
                      setOpen(true)
                    }">
                    <component :is="item.icon" />
                    <span>{{ item.title }}</span>
                  </SidebarMenuButton>
                </RouterLink>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter>
        <NavUser />
      </SidebarFooter>
    </Sidebar>

    <!-- 第二个侧边栏 -->
    <Sidebar collapsible="none" class="hidden flex-1 md:flex">
      <SidebarHeader class="gap-3.5 border-b p-4">
        <div class="flex w-full items-center justify-between">
          <div class="text-base font-medium text-foreground">
            {{ activeItem?.title }}
          </div>
          <JoinGroup />
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup class="px-0">
          <SidebarGroupContent>
            <RouterLink :to="activeItem.url + '/' + result.id" v-for="result in results" :key="result.id" @click="setOpen(false)"
              class="hover:bg-sidebar-accent hover:text-sidebar-accent-foreground flex flex-col items-start gap-2 border-b p-4 text-sm leading-tight whitespace-nowrap last:border-b-0">
              <p class="flex w-full items-center justify-between">
                <span class="w-50 overflow-hidden text-ellipsis whitespace-nowrap">{{ result.name }}</span>
                <span v-show="result.time" class="text-xs">{{ dayjs(result.time).fromNow() }}</span>
              </p>
              <p v-show="result.advisor" class="font-medium">导师：{{ result.advisor }}</p>
              <p v-show="result.group" class="font-medium">所属组：{{ result.group }}</p>
              <p class="line-clamp-2 whitespace-break-spaces text-xs">
                {{ result.description }}
              </p>
            </RouterLink>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  </Sidebar>
</template>

<script setup lang="ts">
  import type { SidebarProps } from '@/components/ui/sidebar'
  import { Brain, BugPlay, NotebookText, UsersRound } from "lucide-vue-next"
  import { h, onMounted, ref, watch, type Component } from "vue"
  import logo from '@/assets/logo.jpg'
  import NavUser from '@/components/user/sidebar/NavUser.vue'
  import { Sidebar, SidebarContent, SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarHeader, SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar, } from '@/components/ui/sidebar'
  import axios from "@/utils/myAxios"
  import dayjs from 'dayjs'
  import JoinGroup from '../JoinGroup.vue'
  import { useRoute } from 'vue-router'
  import { useSidebarStore } from "@/stores/SidebarStore"
  const sidebarStore = useSidebarStore()

  const props = withDefaults(defineProps<SidebarProps>(), {
    collapsible: "icon",
  })

  onMounted(() => {
    getResult()
  })

  interface Type {
    title: string
    url: String
    icon: Component
    isActive: boolean
  }

  const type = [
    { title: "我的学习组", url: "/group", icon: UsersRound, isActive: true },
    { title: "我的考试", url: "/exam", icon: BugPlay, isActive: false },
    { title: "考试记录", url: "/exam-record", icon: NotebookText, isActive: false },
    { title: "人工智障", url: "/ai", icon: Brain, isActive: false },
  ] as Type[]

  // 根据 URL 匹配激活项
  const getActiveTypeByUrl = (path: string, typeList: Type[]): Type => {
    // 提取 URL 的基础路径（去掉参数部分，比如 /exam/1 → /exam）
    const basePath = path.split('/').filter(Boolean).length > 1
      ? `/${path.split('/')[1]}` : path;
    const matchedItem = typeList.find(item => item.url === basePath);
    return matchedItem || typeList[0]!;
  }

  // 设置激活项
  const activeItem = ref<Type>(getActiveTypeByUrl(useRoute().path, type))

  interface Result {
    id: number
    name: string
    advisor: String
    description: String
    group: string
    time: string
  }

  const results = ref<Result[]>([])

  const getResult = async () => {
    try {
      const res = await axios.get("/api" + activeItem.value.url)
      results.value = res.data.data
    } catch (e) {
      console.log(e);
    }
  }

  watch(
    () => sidebarStore.exam,
    async (status) => {
      if (status) { // 开始考试
        setOpen(false)
      }
    }
  )

  watch(
    () => activeItem.value.url,
    async (newUrl) => {
      if (newUrl) { // 避免 url 为空时请求
        await getResult();
      }
    }
  )

  const { setOpen } = useSidebar()
</script>