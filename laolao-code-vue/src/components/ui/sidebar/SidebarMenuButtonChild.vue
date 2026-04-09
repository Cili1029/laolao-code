<script setup lang="ts">
  import type { PrimitiveProps } from "reka-ui"
  import type { HTMLAttributes } from "vue"
  import type { SidebarMenuButtonVariants } from "."
  import { Primitive } from "reka-ui"
  import { cn } from "@/lib/utils"
  import { sidebarMenuButtonVariants } from "."

  export interface SidebarMenuButtonProps extends PrimitiveProps {
    variant?: SidebarMenuButtonVariants["variant"]
    size?: SidebarMenuButtonVariants["size"]
    isActive?: boolean
    class?: HTMLAttributes["class"]
  }

  const props = withDefaults(defineProps<SidebarMenuButtonProps>(), {
    as: "button",
    variant: "default",
    size: "default",
  })
</script>

<template>
  <Primitive data-slot="sidebar-menu-button" data-sidebar="menu-button" :data-size="size" :data-active="isActive"
    :class="cn(
      sidebarMenuButtonVariants({ variant, size }),
      'transition-all duration-300 ease-in-out',
      'hover:bg-blue-100 hover:text-blue-700',
      'data-[active=true]:bg-blue-100 data-[active=true]:text-blue-700',
      props.class
    )" :as="as" :as-child="asChild" v-bind="$attrs">
    <slot />
  </Primitive>
</template>
