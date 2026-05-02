<template>
  <el-card class="chat-card">
    <template #header>
      <div class="chat-header">客服聊天</div>
    </template>

    <div class="chat-body" ref="chatBodyRef" v-loading="loading">
      <el-empty v-if="!messages.length" description="暂无消息，快来和客服打个招呼吧" />
      <div v-else class="msg-list">
        <div v-for="msg in messages" :key="msg.id" :class="['msg-row', msg.senderRole === 1 ? 'mine' : 'other']">
          <div class="msg-bubble">
            <div class="msg-content">{{ msg.content }}</div>
            <div class="msg-time">{{ formatDateTime(msg.createTime) }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input-wrap">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="2"
        maxlength="4000"
        show-word-limit
        placeholder="请输入消息内容"
      />
      <el-button type="primary" :loading="sending" @click="onSend">发送</el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listChatMessages, markChatRead, openChatSession, sendChatMessage, type ChatMessage } from '../../api/chat'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const userId = Number(loginUser?.userId || 0)

const sessionId = ref<number | null>(null)
const messages = ref<ChatMessage[]>([])
const inputText = ref('')
const loading = ref(false)
const sending = ref(false)
const pollingTimer = ref<number | null>(null)
const chatBodyRef = ref<HTMLElement | null>(null)

const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 19) : '-')

const scrollToBottom = async () => {
  await nextTick()
  if (chatBodyRef.value) chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
}

const fetchMessages = async () => {
  if (!sessionId.value) return
  const res = await listChatMessages(sessionId.value)
  messages.value = (res.data || []).slice().sort((a, b) => (a.createTime > b.createTime ? 1 : -1))
  await markChatRead(sessionId.value, 1)
  await scrollToBottom()
}

const startPolling = () => {
  if (pollingTimer.value) clearInterval(pollingTimer.value)
  pollingTimer.value = window.setInterval(() => {
    fetchMessages().catch(() => {})
  }, 3000)
}

const initSession = async () => {
  if (!userId) {
    ElMessage.error('未获取到登录用户，请重新登录')
    return
  }
  loading.value = true
  try {
    const res = await openChatSession(userId)
    sessionId.value = res.data?.id || null
    await fetchMessages()
    startPolling()
  } catch (e: any) {
    ElMessage.error(e?.message || '打开会话失败')
  } finally {
    loading.value = false
  }
}

const onSend = async () => {
  const content = inputText.value.trim()
  if (!content) {
    ElMessage.warning('消息内容不能为空')
    return
  }
  if (content.length > 4000) {
    ElMessage.warning('消息内容不能超过4000字')
    return
  }
  if (!sessionId.value) {
    ElMessage.error('会话未初始化，请稍后重试')
    return
  }

  sending.value = true
  try {
    await sendChatMessage({
      sessionId: sessionId.value,
      senderId: userId,
      senderRole: 1,
      content
    })
    inputText.value = ''
    await fetchMessages()
  } catch (e: any) {
    ElMessage.error(e?.message || '发送失败')
  } finally {
    sending.value = false
  }
}

onMounted(initSession)

onBeforeUnmount(() => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
})
</script>

<style scoped>
.chat-card { min-height: 72vh; display: flex; flex-direction: column; }
.chat-header { font-size: 16px; font-weight: 600; }
.chat-body { height: 56vh; overflow-y: auto; background: #f7f8fa; border-radius: 8px; padding: 14px; }
.msg-list { display: flex; flex-direction: column; gap: 10px; }
.msg-row { display: flex; }
.msg-row.mine { justify-content: flex-end; }
.msg-row.other { justify-content: flex-start; }
.msg-bubble { max-width: 70%; padding: 10px 12px; border-radius: 10px; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.msg-row.mine .msg-bubble { background: #d9ecff; }
.msg-content { white-space: pre-wrap; word-break: break-word; color: #303133; }
.msg-time { margin-top: 6px; font-size: 12px; color: #909399; text-align: right; }
.chat-input-wrap { margin-top: 14px; display: grid; grid-template-columns: 1fr 90px; gap: 10px; align-items: end; }
</style>
