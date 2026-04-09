<template>
  <Sidebar v-bind="computedSidebarProps"
    :class="(examStore.examBegin ? 'pointer-events-none select-none opacity-50' : '')">
    <SidebarHeader>
      <!-- 顶部Logo -->
      <SidebarMenuButton size="lg"
        class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground">
        <div
          class="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
          <img :src="logo" />
        </div>
        <div class="grid flex-1 text-left text-sm leading-tight">
          <span class="truncate font-medium">
            劳劳 Coding
          </span>
        </div>
      </SidebarMenuButton>
    </SidebarHeader>
    <SidebarContent>
      <!-- 列表 -->
      <SidebarGroup>
        <SidebarMenu>
          <SidebarMenuItem v-for="item in data.navMain" :key="item.title">
            <SidebarMenuButton as-child :tooltip="item.title" :is-active="route.path.startsWith(item.url)">
              <RouterLink :to="item.url">
                <component :is="item.icon" v-if="item.icon" />
                <span>{{ item.title }}</span>
              </RouterLink>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarGroup>
    </SidebarContent>
    <SidebarFooter>
      <!-- 用户 -->
      <NavUser />
    </SidebarFooter>
    <SidebarRail />
  </Sidebar>
</template>


<script setup lang="ts">
  import type { SidebarProps } from '@/components/ui/sidebar'
  import { Brain, BugPlay, NotebookText, UsersRound, } from "lucide-vue-next"
  import NavUser from './NavUser.vue'
  import { Sidebar, SidebarContent, SidebarFooter, SidebarHeader, SidebarRail, } from '@/components/ui/sidebar'
  import logo from '@/assets/logo.jpg'
  import { SidebarGroup, SidebarMenu, SidebarMenuButton, SidebarMenuItem } from '@/components/ui/sidebar'
  import { useExamStore } from '@/stores/ExamStore'
  import { computed } from 'vue'
  import { useRoute } from 'vue-router'
  const route = useRoute()
  const examStore = useExamStore()

  const props = defineProps<SidebarProps>()

  const computedSidebarProps = computed(() => {
    return {
      ...props,
      // 使用 'as' 将类型从 string 收窄到具体的字面量联合类型
      collapsible: (examStore.examBegin ? "offcanvas" : "icon") as SidebarProps['collapsible']
    }
  })

  const data = {
    navMain: [
      {
        title: "我的学习组",
        url: "/group",
        icon: UsersRound,
        isActive: true,
      },
      {
        title: "我的考试",
        url: "/exam",
        icon: BugPlay,
      },
      {
        title: "考试报告",
        url: "/member-report",
        icon: NotebookText,
      },
      {
        title: "人工智障",
        url: "#",
        icon: Brain,
      },
    ]
  }
</script>