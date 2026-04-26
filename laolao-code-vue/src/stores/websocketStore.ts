import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useExamStore } from './ExamStore'
import { toast } from "vue-sonner"
import router from '@/router'

export const useWebsocketStore = defineStore('websocket', () => {
    // 响应式 WebSocket 实例
    const socket = ref<WebSocket | null>(null)
    // 连接状态标志
    const isConnected = ref(false)

    const examStore = useExamStore()

    // 等连接成功后，要发送的 BIND_EXAM
    const pendingBindExamId = ref<number | null>(null)

    // 心跳定时器 ID（浏览器环境使用 number，Node 环境可能不同）
    let heartbeatTimer: number | null = null

    /**
     * 初始化 WebSocket 连接
     */
    const connect = () => {
        // 如果已有连接且处于打开状态，则直接返回，避免重复连接
        if (socket.value && socket.value.readyState === WebSocket.OPEN) return

        // 构建 WebSocket URL（这里硬编码了后端地址，生产环境应从环境变量读取）
        const url = `ws://localhost:8080/ws/connect`
        socket.value = new WebSocket(url)

        // 连接建立时的处理
        socket.value.onopen = () => {
            isConnected.value = true
            // 考试页刷新自动进入考试
            if (pendingBindExamId.value !== null) {
                sendJsonMessage('BIND_EXAM', pendingBindExamId.value)
                pendingBindExamId.value = null // 发完清空
            }
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
                sendJsonMessage("PING", null)
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
        // 先去除首尾空白字符（避免传输中多空格导致判断失效）
        const cleanData = data.trim()
        try {
            // 统一解析为 JSON 格式（后端所有消息都遵循 WsResult 结构）
            const json = JSON.parse(cleanData)

            // 统一处理失败，项目中并没有失败 + 发数据
            if (json.code === 0) {
                toast.error(json.msg)

                // 特殊：判题失败
                if (json.type === 'JUDGE_RESULT') {
                    examStore.judgeLoading = false
                }
                return
            }

            // 统一处理成功信息
            if(json.msg) {
                toast.success(json.msg)
            }

            // 心跳响应处理
            if (json.type === 'PONG') {
                return
            }
            
            // 考试提交或者考试中管理员取消考试，返回主页
            if (json.type === 'EXAM_SUBMIT' || json.type === 'EXAM_CANCEL') {
                router.push("/")
                return
            }

            // 发布考试，成功了也应该刷新页面（重新请求）
            if (json.type === 'RELEASE_RESULT') {
                location.reload()
                return
            }

            // 判题结果推送
            if (json.type === 'JUDGE_RESULT') {
                examStore.judgeRecord = json.data
                examStore.judgeDialog = true
                examStore.judgeLoading = false

                const targetQuestion = examStore.questions?.find(q => q.id === json.data.questionId)
                if (targetQuestion) {
                    targetQuestion.userScore = examStore.judgeRecord!.score
                }
                return
            }

            // 其他未匹配的消息类型（便于调试）
            console.log("接收到未知类型的WebSocket消息:", json)
        } catch (e) {
            // 非 JSON 格式的消息（兼容旧格式/异常消息）
            console.warn("接收到非JSON格式的WebSocket消息:", cleanData)
        }
    }

    /**
     * 主动关闭 WebSocket 连接
     */
    const close = () => {
        socket.value?.close()
        socket.value = null
    }

    const sendJsonMessage = (type: string, data: any) => {
        if (!socket.value || socket.value.readyState !== WebSocket.OPEN) {
            // 未连接
            return
        }
        const jsonStr = JSON.stringify({
            type: type,
            data: data
        })
        socket.value.send(jsonStr)
    }

    // 考试页面调用这个，不是直接发
    function bindExamWhenReady(examId: number) {
        if (isConnected.value) {
            // 已连接 → 直接发
            sendJsonMessage('BIND_EXAM', examId)
        } else {
            // 未连接 → 先存起来，等连接成功自动发
            pendingBindExamId.value = examId
        }
    }

    // 返回公开的方法和状态，供组件使用
    return { isConnected, connect, close, sendJsonMessage, bindExamWhenReady }
})