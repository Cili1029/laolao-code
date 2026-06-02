<template>
    <div class="flex flex-col h-full pb-4 px-2">
        <!-- Chat Display -->
        <div ref="scrollRef"
            class="max-w-4xl w-full mx-auto flex-1 bg-white  overflow-y-auto p-4 space-y-4 [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            <div v-if="messages.length === 0" class="text-center text-gray-400 mt-20 font-light">
                <p class="text-lg">开始一段对话吧</p>
                <p class="text-xs mt-2">记忆保留 24 小时</p>
            </div>

            <div v-for="(msg, index) in messages" :key="index"
                :class="['flex', msg.type === 'user' ? 'justify-end' : 'justify-start']">
                <div :class="['max-w-[85%] px-4 py-2 rounded-2xl text-sm transition-all shadow-sm',
                    msg.type === 'user' ? 'bg-[#4c83eb] text-white' : 'bg-gray-100 text-gray-800']">
                    <p class="whitespace-pre-wrap">{{ msg.content }}</p>
                </div>
            </div>

            <div v-if="isLoading" class="flex justify-start">
                <div class="bg-gray-100 px-4 py-2 rounded-2xl animate-pulse text-gray-400 text-sm">AI 正在思考...</div>
            </div>
        </div>

        <div class="max-w-4xl w-full mx-auto">
            <div
                class="bg-white border border-gray-200 rounded-3xl p-2 focus-within:border-blue-400 transition-all shadow-sm">
                <textarea v-model="userInput" @keyup.enter.exact.prevent="chat" placeholder="给人工智障发送消息"
                    class="w-full px-3 py-2 h-24 resize-none outline-none text-sm text-gray-700 bg-transparent"
                    :disabled="isLoading"></textarea>

                <div class="flex justify-between items-center px-2 pb-1">
                    <span></span>

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

    const chat = async () => {
        const content = userInput.value.trim()
        if (!content || isLoading.value) return

        // 推入用户消息
        messages.value.push({ type: 'user', content })
        userInput.value = ''
        isLoading.value = true
        // 占位AI消息
        const aiIdx = messages.value.push({ type: 'assistant', content: '' }) - 1
        await scrollToBottom()

        try {
            const res = await fetch(`/api/ai/chat?userInput=${encodeURIComponent(content)}`, {
                headers: { Accept: 'text/event-stream' }
            })
            if (!res.ok || !res.body) throw new Error('请求失败')

            const reader = res.body.getReader()
            const decoder = new TextDecoder()
            let buf = '', text = ''

            while (true) {
                const { done, value } = await reader.read()
                if (done) break
                buf += decoder.decode(value, { stream: true })
                const events = buf.split('\n\n')
                buf = events.pop() || ''
                for (const evt of events) {
                    const datas = evt.split('\n').filter(s => s.startsWith('data:')).map(s => s.slice(5).trim())
                    for (const d of datas) {
                        if (d === '[DONE]') break
                        text += d
                        messages.value[aiIdx]!.content = text
                        await scrollToBottom()
                    }
                }
            }
            if (!text) messages.value[aiIdx]!.content = '（无返回内容）'
        } catch {
            messages.value[aiIdx]!.content = '无法连接到服务器！'
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