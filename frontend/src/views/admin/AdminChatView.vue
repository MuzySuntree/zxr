<template>
  <el-card class="chat-workbench">
    <template #header><div class="title">客服会话</div></template>

    <div class="chat-layout">
      <div class="session-list" v-loading="sessionLoading">
        <el-empty v-if="!sessions.length" description="暂无会话" />
        <div
          v-for="item in sessions"
          :key="item.id"
          :class="['session-item', activeSessionId === item.id ? 'active' : '']"
          @click="selectSession(item.id)">
          <div class="row"><strong>#{{ item.id }}</strong><el-tag size="small" :type="item.status === 1 ? 'success' : 'info'">{{ item.status === 1 ? '进行中' : '已结束' }}</el-tag></div>
          <div class="meta">用户: {{ item.userId }} / 管理员: {{ item.adminId || '-' }}</div>
          <div class="last">{{ item.lastMessage || '暂无消息' }}</div>
          <div class="row">
            <span class="time">{{ formatDateTime(item.lastMessageTime) }}</span>
            <span>
              <el-button v-if="item.status===1" link type="primary" size="small" @click.stop="onAssign(item)">接入</el-button>
              <el-button v-if="item.status===1" link type="danger" size="small" @click.stop="onClose(item.id)">关闭</el-button>
            </span>
          </div>
        </div>
      </div>

      <div class="chat-panel" v-loading="messageLoading">
        <el-empty v-if="!activeSessionId" description="请选择左侧会话" />
        <template v-else>
          <div class="msg-box" ref="msgBoxRef">
            <el-empty v-if="!messages.length" description="暂无消息" />
            <div v-else class="msg-list">
              <div v-for="msg in messages" :key="msg.id" :class="['msg-row', msg.senderRole === 2 ? 'mine' : 'other']">
                <div class="msg-bubble"><div>{{ msg.content }}</div><div class="msg-time">{{ formatDateTime(msg.createTime) }}</div></div>
              </div>
            </div>
          </div>
          <div class="input-wrap">
            <el-input v-model="inputText" type="textarea" :rows="2" maxlength="4000" show-word-limit :disabled="activeSession?.status===2" placeholder="请输入回复内容" />
            <el-button type="primary" :loading="sending" :disabled="activeSession?.status===2" @click="onSend">发送</el-button>
          </div>
        </template>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { assignChatSession, closeChatSession, listAllChatSessions, listChatMessages, markChatRead, sendChatMessage, type ChatMessage, type ChatSession } from '../../api/chat'
import { getLoginUser } from '../../utils/auth'

const adminId = Number(getLoginUser()?.userId || 0)
const sessions = ref<ChatSession[]>([])
const messages = ref<ChatMessage[]>([])
const activeSessionId = ref<number | null>(null)
const inputText = ref('')
const sending = ref(false)
const sessionLoading = ref(false)
const messageLoading = ref(false)
const timer = ref<number | null>(null)
const msgBoxRef = ref<HTMLElement | null>(null)

const activeSession = computed(() => sessions.value.find((s) => s.id === activeSessionId.value))
const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 19) : '-')

const scrollToBottom = async () => {
  await nextTick()
  if (msgBoxRef.value) msgBoxRef.value.scrollTop = msgBoxRef.value.scrollHeight
}

const loadSessions = async () => {
  sessionLoading.value = true
  try {
    const res = await listAllChatSessions()
    sessions.value = res.data || []
  } finally {
    sessionLoading.value = false
  }
}

const loadMessages = async () => {
  if (!activeSessionId.value) return
  messageLoading.value = true
  try {
    const res = await listChatMessages(activeSessionId.value)
    messages.value = (res.data || []).slice().sort((a, b) => (a.createTime > b.createTime ? 1 : -1))
    await markChatRead(activeSessionId.value, 2)
    await scrollToBottom()
  } finally {
    messageLoading.value = false
  }
}

const selectSession = async (id: number) => {
  activeSessionId.value = id
  await loadMessages()
}

const onAssign = async (item: ChatSession) => {
  if (!adminId) return ElMessage.error('未获取管理员身份，请重新登录')
  await assignChatSession(item.id, adminId)
  ElMessage.success('接入成功')
  await loadSessions()
}

const onClose = async (sessionId: number) => {
  await closeChatSession(sessionId)
  ElMessage.success('会话已关闭')
  await loadSessions()
}

const onSend = async () => {
  const content = inputText.value.trim()
  if (!content) return ElMessage.warning('消息内容不能为空')
  if (content.length > 4000) return ElMessage.warning('消息内容不能超过4000字')
  if (!activeSessionId.value) return ElMessage.warning('请先选择会话')
  if (activeSession.value?.status === 2) return ElMessage.warning('当前会话已结束，无法发送')
  if (!adminId) return ElMessage.error('未获取管理员身份，请重新登录')

  sending.value = true
  try {
    await sendChatMessage({ sessionId: activeSessionId.value, senderId: adminId, senderRole: 2, content })
    inputText.value = ''
    await loadMessages()
    await loadSessions()
  } finally {
    sending.value = false
  }
}

const startPolling = () => {
  if (timer.value) clearInterval(timer.value)
  timer.value = window.setInterval(async () => {
    await loadSessions()
    if (activeSessionId.value) await loadMessages()
  }, 3000)
}

onMounted(async () => {
  await loadSessions()
  startPolling()
})

onBeforeUnmount(() => {
  if (timer.value) clearInterval(timer.value)
})
</script>

<style scoped>
.title { font-size: 16px; font-weight: 600; }
.chat-layout { display: grid; grid-template-columns: 320px 1fr; gap: 12px; min-height: 70vh; }
.session-list { border: 1px solid #ebeef5; border-radius: 8px; padding: 8px; overflow-y: auto; }
.session-item { border: 1px solid #ebeef5; border-radius: 8px; padding: 10px; margin-bottom: 8px; cursor: pointer; }
.session-item.active { border-color: #409eff; background: #ecf5ff; }
.row { display: flex; justify-content: space-between; align-items: center; }
.meta,.last,.time { font-size: 12px; color: #606266; margin-top: 6px; }
.chat-panel { border: 1px solid #ebeef5; border-radius: 8px; padding: 10px; display: flex; flex-direction: column; }
.msg-box { flex: 1; min-height: 52vh; max-height: 52vh; overflow-y: auto; background: #f7f8fa; border-radius: 8px; padding: 10px; }
.msg-list { display: flex; flex-direction: column; gap: 10px; }
.msg-row { display: flex; }
.msg-row.other { justify-content: flex-start; }
.msg-row.mine { justify-content: flex-end; }
.msg-bubble { max-width: 70%; background: #fff; padding: 10px 12px; border-radius: 10px; }
.msg-row.mine .msg-bubble { background: #d9ecff; }
.msg-time { margin-top: 6px; font-size: 12px; color: #909399; text-align: right; }
.input-wrap { margin-top: 10px; display: grid; grid-template-columns: 1fr 90px; gap: 10px; align-items: end; }
</style>
