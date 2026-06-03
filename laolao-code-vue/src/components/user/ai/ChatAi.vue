<template>
    <div class="flex flex-col h-full pb-4 px-2">
        <div ref="scrollRef"
            class="max-w-4xl w-full mx-auto flex-1 bg-white  overflow-y-auto p-4 space-y-4 [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            <div v-if="messages.length === 0" class="text-center text-gray-400 mt-20 font-light">
                <p class="text-lg">开始一段对话吧</p>
                <p class="text-xs mt-2">记忆保留 24 小时</p>
            </div>

            <div v-for="(msg, index) in messages" :key="index"
                :class="['flex', msg.type === 'user' ? 'justify-end' : 'justify-start']">
                <div :class="['max-w-[85%] px-4 py-2 rounded-2xl text-sm shadow-sm',
                    msg.type === 'user' ? 'bg-[#4c83eb] text-white' : 'bg-gray-100 text-gray-800']">

                    <div class="markdown-body" v-html="renderMarkdown(msg.content || '')"></div>
                </div>
            </div>
        </div>

        <div class="max-w-4xl w-full mx-auto">
            <div class="bg-white border border-gray-200 rounded-3xl p-2 shadow-sm">
                <textarea v-model="userInput" @keyup.enter.exact.prevent="chat" placeholder="给人工智障发送消息"
                    class="w-full px-3 py-2 h-16 resize-none outline-none text-sm text-gray-700 bg-transparent"
                    :disabled="isLoading"></textarea>

                <div class="flex justify-between items-center px-2 pb-1">
                    <AlertDialog>
                        <AlertDialogTrigger>
                            <button
                                class="w-8 h-8 flex items-center justify-center rounded-full bg-gray-100 text-red-400">
                                <Trash class="w-5 h-5" />
                            </button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                            <AlertDialogHeader>
                                <AlertDialogTitle>删除记忆?</AlertDialogTitle>
                                <AlertDialogDescription>
                                    此操作不可撤销
                                </AlertDialogDescription>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                                <AlertDialogCancel>取消</AlertDialogCancel>
                                <AlertDialogAction @click="clear()">确定</AlertDialogAction>
                            </AlertDialogFooter>
                        </AlertDialogContent>
                    </AlertDialog>


                    <button @click="chat" :disabled="!userInput.trim() || isLoading"
                        :class="userInput.trim() && !isLoading ? 'bg-black text-white hover:opacity-80' : 'bg-gray-100 text-gray-400'"
                        class="w-8 h-8 flex items-center justify-center rounded-full transition-all">
                        <span v-if="isLoading"
                            class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                        <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2.5"
                            viewBox="0 0 24 24">
                            <path d="M5 10l7-7m0 0l7 7m-7-7v18" />
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { ref, onMounted, nextTick } from 'vue'
    import axios from 'axios'
    import { Trash } from 'lucide-vue-next'
    import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger, } from '@/components/ui/alert-dialog'
    import MarkdownIt from 'markdown-it'
    const md = new MarkdownIt({
        breaks: true,
        linkify: true,
        html: true
    })
    const renderMarkdown = (text: string) => md.render(text);

    onMounted(() => {
        loadHistory()
    })

    interface ChatMessage {
        type: 'user' | 'assistant'
        content: string
    }

    const userInput = ref('')
    const messages = ref<ChatMessage[]>([])
    const isLoading = ref<boolean>(false)

    const loadHistory = async () => {

        try {
            const res = await axios.get('/api/ai/chat/history')

            if (res.data.data) {
                messages.value = res.data.data.messages
                await scrollToBottom()
            }
        } catch (error) {
            console.log(error)
        }
    }

    const END_MARKER = '[DONE]'; // 根据你后端定义的结束标识修改

    const chat = async () => {
        const content = userInput.value.trim()
        if (!content || isLoading.value) return

        // 1. 推入用户消息
        messages.value.push({ type: 'user', content })
        userInput.value = ''
        isLoading.value = true

        // 2. 占位 AI 消息
        const aiIdx = messages.value.push({ type: 'assistant', content: '' }) - 1
        await scrollToBottom()

        // 定义内部变量
        let buffer = ''
        let eventDataLines: string[] = []

        try {
            const res = await fetch(`/api/ai/chat?userInput=${encodeURIComponent(content)}`, {
                headers: {
                    'Accept': 'text/event-stream',
                    // 如果需要鉴权记得加在此处
                }
            })

            if (!res.ok || !res.body) throw new Error('请求失败')

            const reader = res.body.getReader()
            const decoder = new TextDecoder('utf-8')

            // 开始循环读取流
            while (true) {
                const { done, value } = await reader.read()

                if (done) {
                    // 处理最后可能残留的数据
                    if (eventDataLines.length > 0) {
                        const lastContent = eventDataLines.join('\n')
                        if (lastContent && !lastContent.includes(END_MARKER)) {
                            messages.value[aiIdx]!.content += lastContent
                        }
                    }
                    break // 读取完成，退出循环
                }

                // 解码当前的 chunk
                const text = decoder.decode(value, { stream: true })
                buffer += text

                // 按行拆分数据
                const lines = buffer.split('\n')
                // 关键：保留最后一行不完整的，存回 buffer 等待下一个 chunk
                buffer = lines.pop() || ''

                for (const line of lines) {
                    const cleanLine = line.endsWith('\r') ? line.slice(0, -1) : line

                    if (cleanLine.startsWith('data:')) {
                        const data = cleanLine.substring(5)

                        // 检查是否包含结束标记
                        if (data.includes(END_MARKER)) {
                            // 先把之前收集的数据吐出来
                            if (eventDataLines.length > 0) {
                                messages.value[aiIdx]!.content += eventDataLines.join('\n')
                                eventDataLines = []
                            }
                            // 提前关闭流
                            await reader.cancel()
                            isLoading.value = false
                            return
                        }

                        // 收集数据行
                        eventDataLines.push(data)

                    } else if (cleanLine === '') {
                        // SSE 标准：遇到空行表示一个事件块结束，此时渲染一次 UI
                        if (eventDataLines.length > 0) {
                            const messageChunk = eventDataLines.join('\n')
                            eventDataLines = []

                            if (messageChunk !== ':heartbeat') {
                                // 响应式更新 UI
                                messages.value[aiIdx]!.content += messageChunk
                                // 滚动到底部
                                await scrollToBottom()
                            }
                        }
                    }
                }
            }
        } catch (err) {
            console.error('Streaming error:', err)
            messages.value[aiIdx]!.content = '无法连接到服务器或处理数据出错！'
        } finally {
            isLoading.value = false
            await scrollToBottom()
        }
    }

    const clear = async () => {
        messages.value = []
        try {
            await axios.post('/api/ai/chat/clear')
        } catch (e) {
            console.log(e)
        }
    }

    const scrollRef = ref<HTMLDivElement | null>(null);
    const scrollToBottom = async () => {
        await nextTick()
        if (scrollRef.value) {
            scrollRef.value.scrollTop = scrollRef.value.scrollHeight
        }
    }
</script>

<style scoped>

    /* 针对 markdown 内容的样式定制 */
    :deep(.markdown-body) {
        word-break: break-word;
        line-height: 1.75;
    }

    /* 标题样式：解决 ### 不变大的问题 */
    :deep(.markdown-body h1),
    :deep(.markdown-body h2),
    :deep(.markdown-body h3) {
        font-weight: 700;
        margin-top: 1.5rem;
        margin-bottom: 0.75rem;
        display: block;
        /* 确保是块级元素，强制换行 */
    }

    :deep(.markdown-body h1) {
        font-size: 1.5rem;
    }

    :deep(.markdown-body h2) {
        font-size: 1.25rem;
    }

    :deep(.markdown-body h3) {
        font-size: 1.1rem;
    }

    /* 加粗 */
    :deep(.markdown-body strong) {
        font-weight: bold;
        color: #1a1a1a;
    }

    /* 列表 */
    :deep(.markdown-body ul),
    :deep(.markdown-body ol) {
        margin-left: 1.5rem;
        margin-bottom: 1rem;
        list-style: disc;
    }

    /* 分割线 */
    :deep(.markdown-body hr) {
        margin: 1.5rem 0;
        border: none;
        border-top: 1px solid #e5e7eb;
    }

    /* 表格（看你截图有对比，可能需要表格样式） */
    :deep(.markdown-body table) {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 1rem;
    }

    :deep(.markdown-body th),
    :deep(.markdown-body td) {
        border: 1px solid #e5e7eb;
        padding: 0.5rem;
    }
</style>