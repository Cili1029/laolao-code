import { defineStore } from 'pinia'      // 导入 Pinia 的 store 定义函数
import { ref } from 'vue'                // 导入 Vue 的响应式引用
import { toast } from 'vue-sonner'       // 导入通知组件，用于显示提示信息
import { useRouter } from 'vue-router'    // 导入 Vue Router 用于页面跳转

export const useWebsocketStore = defineStore('websocket', () => {
    // 响应式 WebSocket 实例
    const socket = ref<WebSocket | null>(null)
    // 连接状态标志
    const isConnected = ref(false)
    // Vue Router 实例
    const router = useRouter()

    // 心跳定时器 ID（浏览器环境使用 number，Node 环境可能不同）
    let heartbeatTimer: number | null = null

    /**
     * 初始化 WebSocket 连接
     * @param examId 考试 ID，用于构建连接 URL
     */
    const connect = (examId: number) => {
        // 如果已有连接且处于打开状态，则直接返回，避免重复连接
        if (socket.value && socket.value.readyState === WebSocket.OPEN) return

        // 构建 WebSocket URL（这里硬编码了后端地址，生产环境应从环境变量读取）
        const url = `ws://localhost:8080/ws/exam?examId=${examId}`
        socket.value = new WebSocket(url)

        // 连接建立时的处理
        socket.value.onopen = () => {
            isConnected.value = true
            startHeartbeat()   // 启动心跳
        }

        // 收到消息时的处理
        socket.value.onmessage = (event) => {
            handleMessage(event.data)
        }

        // 连接关闭时的处理
        socket.value.onclose = () => {
            isConnected.value = false
            stopHeartbeat()    // 停止心跳
        }
    }

    /**
     * 启动心跳定时器，每隔 30 秒发送一次 "ping" 消息，保持连接活跃
     */
    const startHeartbeat = () => {
        heartbeatTimer = window.setInterval(() => {
            if (socket.value?.readyState === WebSocket.OPEN) {
                socket.value.send("ping")
            }
        }, 30000)   // 30 秒间隔
    }

    /**
     * 停止心跳定时器
     */
    const stopHeartbeat = () => {
        if (heartbeatTimer) clearInterval(heartbeatTimer)
    }

    /**
     * 处理接收到的 WebSocket 消息
     * @param data 原始消息数据（字符串）
     */
    const handleMessage = (data: string) => {
        // 如果是心跳响应 "pong"，直接忽略
        if (data === "pong") return

        // 处理强制交卷逻辑（系统或管理员强制交卷）
        if (data === "SYSTEM_FORCE_SUBMIT" || data === "ADMIN_FORCE_SUBMIT") {
            toast.warning("考试已结束，正在强制交卷...")
            // 1. 触发全局的交卷函数（可以在这里调用 API）
            // 2. 跳转到考试结果页
            router.push('/exam/result')
            return
        }

        // 尝试解析 JSON 消息
        try {
            const json = JSON.parse(data)
            // 判题结果推送
            if (json.type === 'JUDGE_RESULT') {
                toast.success(`题目 ${json.data.problemCode} 判题完成: ${json.data.status}`)
                // 你还可以在这里触发一个全局事件，通知题目列表更新状态
            }
        } catch (e) {
            // 非 JSON 格式的消息，仅打印日志
            console.log("Receive text:", data)
        }
    }

    /**
     * 主动关闭 WebSocket 连接
     */
    const close = () => {
        socket.value?.close()
        socket.value = null
    }

    // 返回公开的方法和状态，供组件使用
    return { isConnected, connect, close }
})