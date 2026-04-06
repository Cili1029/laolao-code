<template>
  <RouterView />
  <Toaster />
</template>

<script setup lang="ts">
  import { RouterView } from 'vue-router'
  import 'vue-sonner/style.css'
  import { Toaster } from '@/components/ui/sonner'
  import { useUserStore } from "@/stores/UserStore"
  import { onMounted, onUnmounted } from 'vue'
  import { useWebsocketStore } from '@/stores/WebsocketStore'
  const wsStore = useWebsocketStore()

  const userStore = useUserStore()

  onMounted(async () => {
    userStore.getInfo()
  })

  onUnmounted(() => {
    wsStore.close()
  })


</script>

<style>

  html,
  body,
  #app {
    height: 100%;
    margin: 0;
    padding: 0;
  }
</style>