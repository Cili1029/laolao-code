<template>
  <SidebarMenu>
    <SidebarMenuItem>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground md:h-8 md:p-0">
            <Avatar class="h-8 w-8 rounded-lg">
              <AvatarImage :src="userStore.user?.avatar || ''" />
              <AvatarFallback class="rounded-lg">
                <Ghost />
              </AvatarFallback>
            </Avatar>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-medium">{{ userStore.user.name }}</span>
              <span class="truncate text-xs">{{ userStore.user.username }}</span>
            </div>
            <ChevronsUpDown class="ml-auto size-4" />
          </SidebarMenuButton>
        </DropdownMenuTrigger>
        <DropdownMenuContent class="w-[--reka-dropdown-menu-trigger-width] min-w-56 rounded-lg"
          :side="isMobile ? 'bottom' : 'right'" align="end" :side-offset="4">
          <DropdownMenuLabel class="p-0 font-normal">
            <div class="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
              <Avatar class="h-8 w-8 rounded-lg">
                <AvatarImage :src="userStore.user?.avatar || ''" />
                <AvatarFallback class="rounded-lg">
                  <Ghost />
                </AvatarFallback>
              </Avatar>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-medium">{{ userStore.user.name }}</span>
                <span class="truncate text-xs">{{ userStore.user.username }}</span>
              </div>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem>
              <UserPen />
              个人信息
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Ghost />
              待定
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Ghost />
              待定
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem @click="signOut()">
            <LogOut />
            退出
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </SidebarMenuItem>
  </SidebarMenu>
</template>

<script setup lang="ts">
  import { ChevronsUpDown, Ghost, LogOut, UserPen } from "lucide-vue-next"
  import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
  import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
  import { SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar } from '@/components/ui/sidebar'
  import axios from "@/utils/myAxios"
  import { useUserStore } from "@/stores/UserStore"
  import router from "@/router"


  const userStore = useUserStore()
  const { isMobile } = useSidebar()

  const signOut = async () => {
    try {
      await axios.get("/api/user/sign-out")
      userStore.clearUser()
      router.push("/sign-in")
    } catch (e) {
      console.log(e);
    }
  }
</script>