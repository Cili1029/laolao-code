<template>
  <div class="h-full p-4 flex flex-col space-y-6">
    <!-- 页面标题部分 -->
    <div class="flex flex-col justify-between">
      <h1 class="text-2xl font-bold">学生报告</h1>
      <p class="text-muted-foreground">查看你的学习进度分析、阶段测试总结及个人表现报告</p>
    </div>

    <!-- 报告列表 -->
    <div v-if="reports.length !== 0" class="space-y-4">
      <!-- 报告卡片 -->
      <RouterLink v-for="report in reports" :key="report.id" :to="'/member-report/' + report.id"
        class="flex flex-col md:flex-row justify-between border shadow-sm rounded-xl p-5 cursor-pointer hover:bg-indigo-50/30 transition-all group">
        <div class="flex items-start space-x-4">
          <!-- 报告图标 -->
          <div class="bg-indigo-50 p-3 rounded-lg group-hover:bg-indigo-100 transition-colors">
            <FileBarChart class="h-6 w-6 text-indigo-600" />
          </div>

          <div class="space-y-1">
            <div class="flex items-center gap-2">
              <h3 class="text-lg font-semibold group-hover:text-indigo-700 transition-colors">
                {{ report.name }}
              </h3>
              <Badge variant="outline" class="text-[10px] h-5 border-indigo-200 text-indigo-600">
                PDF 报告
              </Badge>
            </div>
            <div class="flex items-center text-sm text-gray-500 space-x-3">
              <span class="flex items-center">
                <Users class="h-3.5 w-3.5 mr-1 text-gray-400" />
                {{ report.studyGroup }}
              </span>
              <span class="text-gray-300">|</span>
              <p class="max-w-md text-gray-400 italic">
                {{ report.description || '点击查看详细的学习能力分析报告' }}
              </p>
            </div>
          </div>
        </div>

        <div class="flex flex-col md:items-end justify-center mt-4 md:mt-0 space-y-2">
          <!-- 生成时间 -->
          <div class="flex items-center text-xs font-medium text-gray-500">
            <Clock class="h-3.5 w-3.5 mr-1.5" />
            考试时间 {{ report.time.split('T')[0] }}
          </div>

          <!-- 操作提示 -->
          <div
            class="flex items-center text-indigo-600 text-sm font-semibold opacity-0 group-hover:opacity-100 transition-all transform translate-x-2 group-hover:translate-x-0">
            查看详情
            <ArrowUpRight class="ml-1 h-4 w-4" />
          </div>
        </div>
      </RouterLink>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col flex-1 justify-center items-center text-center">
      <div class="bg-white p-4 rounded-full shadow inline-block mb-4">
        <Ghost class="h-10 w-10 text-gray-600" />
      </div>
      <p class="text-gray-600 font-medium">暂无生成的报告</p>
      <p class="text-sm text-gray-600">导师改完题后会为你生成报告</p>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue'
  import axios from "@/utils/myAxios"
  import { FileBarChart, Users, Clock, ArrowUpRight, Ghost } from "lucide-vue-next"
  import { Badge } from '@/components/ui/badge'

  interface Report {
    id: number
    name: string
    studyGroup: string
    description: string
    time: string
  }

  const reports = ref<Report[]>([])

  const getReport = async () => {
    try {
      const res = await axios.get("/api/member-report")
      reports.value = res.data.data
    } catch (e) {
      console.error("获取报告失败:", e)
    }
  }

  onMounted(() => {
    getReport()
  })
</script>