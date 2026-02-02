<template>
  <Sidebar class="overflow-hidden *:data-[sidebar=sidebar]:flex-row" v-bind="props">
    <!-- 第一个侧边栏（图标列） -->
    <Sidebar collapsible="none" class="w-[calc(var(--sidebar-width-icon)+1px)]! border-r">
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton size="lg" as-child class="md:h-8 md:p-0">
              <a href="#">
                <div
                  class="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                  <img :src="logo" />
                </div>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent class="px-1.5 md:px-0">
            <SidebarMenu>
              <SidebarMenuItem v-for="item in data.navMain" :key="item.title">
                <SidebarMenuButton :tooltip="h('div', { hidden: false }, item.title)"
                  :is-active="activeItem?.title === item.title" class="px-2.5 md:px-2" @click="() => {
                    activeItem = item
                    setOpen(true)
                  }">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter>
        <NavUser :user="data.user" />
      </SidebarFooter>
    </Sidebar>

    <!-- 第二个侧边栏（邮件列表） -->
    <Sidebar collapsible="none" class="hidden flex-1 md:flex">
      <SidebarHeader class="gap-3.5 border-b p-4">
        <div class="flex w-full items-center justify-between">
          <div class="text-base font-medium text-foreground">
            {{ activeItem?.title }}
          </div>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup class="px-0">
          <SidebarGroupContent>
            <a v-for="result in results" :key="result.id" href="#"
              class="hover:bg-sidebar-accent hover:text-sidebar-accent-foreground flex flex-col items-start gap-2 border-b p-4 text-sm leading-tight whitespace-nowrap last:border-b-0">
              <div class="flex w-full items-center gap-2">
                <span>{{ result.name }}</span>
                <span class="ml-auto text-xs">{{ result.date }}</span>
              </div>
              <span v-show="result.teacher" class="font-medium">老师：{{ result.teacher }}</span>
              <span v-show="result.class" class="font-medium">所属班级：{{ result.class }}</span>
              <span class="line-clamp-2 w-64 whitespace-break-spaces text-xs">
                {{ result.describe }}
              </span>
            </a>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  </Sidebar>
</template>

<script setup lang="ts">
  import type { SidebarProps } from '@/components/ui/sidebar'
  import { Brain, BugPlay, NotebookText, School, } from "lucide-vue-next"
  import { h, ref, type Component } from "vue"
  import logo from '@/assets/logo.jpg'
  import NavUser from '@/components/user/sidebar/NavUser.vue'
  import { Sidebar, SidebarContent, SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarHeader, SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar, } from '@/components/ui/sidebar'

  interface NavItem {
    title: string
    url: string
    icon: Component
    isActive: boolean
  }

  const props = withDefaults(defineProps<SidebarProps>(), {
    collapsible: "icon",
  })

  const data = {
    user: {
      name: "shadcn",
      email: "m@example.com",
      avatar: "/avatars/shadcn.jpg",
    },
    navMain: [
      { title: "我的班级", url: "#", icon: School, isActive: true },
      { title: "我的考试", url: "#", icon: BugPlay, isActive: false },
      { title: "考试情况", url: "#", icon: NotebookText, isActive: false },
      { title: "人工智障", url: "#", icon: Brain, isActive: false },
    ] as NavItem[],
    results: [
      {
        id: 1,
        name: "班级1",
        teacher: "甲",
        class: "",
        date: "",
        describe: "软件工程",
      },
      {
        id: 2,
        name: "考试2",
        teacher: "",
        class: "班级1",
        date: "3天后开始",
        describe: "别偷抄",
      },
      {
        id: 4,
        name: "考试情况4",
        teacher: "",
        class: "班级1",
        date: "",
        describe: "",
      },
      {
        id: 5,
        name: "AI5",
        teacher: "",
        class: "",
        date: "",
        describe: "这是一个人工智障",
      },
    ],
  }

  const activeItem = ref<NavItem>(data.navMain[0]!)
  const results = ref(data.results)
  const { setOpen } = useSidebar()
</script>