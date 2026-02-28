<template>
  <RouterView />
  <Toaster />
</template>

<script setup lang="ts">
  import { RouterView } from 'vue-router'
  import 'vue-sonner/style.css'
  import { Toaster } from '@/components/ui/sonner'
  import { useUserStore } from "@/stores/UserStore"
  import { onMounted } from 'vue'

  const userStore = useUserStore()

  onMounted(() => {
    userStore.getInfo()
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