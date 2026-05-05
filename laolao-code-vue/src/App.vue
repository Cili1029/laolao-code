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
  import router from './router'
  const wsStore = useWebsocketStore()

  const userStore = useUserStore()

  onMounted(async () => {
    await userStore.getInfo()
    if (window.location.pathname === '/') {
      if (userStore.user.role === 0) {
        router.push('/docker')
      } else {
        router.push('/my-team')
      }
    }
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