<template>
  <RouterView />
  <Toaster />
</template>

<script setup lang="ts">
  import { RouterView } from 'vue-router'
  import 'vue-sonner/style.css'
  import { Toaster } from '@/components/ui/sonner'
  import axios from "@/utils/myAxios"
  import { useUserStore } from "@/store/UserStore"
  import { onMounted } from 'vue'

  const userStore = useUserStore()

  onMounted(() => {
    getInfo()
  })

  const getInfo = async () => {
    try {
      const res = await axios.get("/api/user/info")
      if (res.data.code === 1) {
        userStore.setUser(res.data.data)
      }
    } catch (e) {
      console.log(e);
    }
  }
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