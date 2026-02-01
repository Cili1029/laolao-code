<template>
    <RouterView />
    <Toaster />
</template>

<script setup lang="ts">
  import { RouterView } from 'vue-router'
  import 'vue-sonner/style.css'
  import { Toaster } from '@/components/ui/sonner'


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