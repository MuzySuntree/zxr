<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>留言审核</span>
        <div class="filters">
          <el-select v-model="categoryFilter" placeholder="分类" clearable style="width: 140px">
            <el-option :value="1" label="失物招领" /><el-option :value="2" label="寻找搭子" /><el-option :value="3" label="入住交流" />
            <el-option :value="4" label="建议反馈" /><el-option :value="9" label="其他" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 140px">
            <el-option :value="0" label="待审核" /><el-option :value="1" label="已发布" /><el-option :value="2" label="已下架" />
          </el-select>
          <el-button @click="loadData">查询</el-button>
        </div>
      </div>
    </template>

    <el-table :data="filteredMessages" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column label="分类" width="110"><template #default="scope">{{ categoryText(scope.row.category) }}</template></el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
      <el-table-column prop="contactInfo" label="联系方式" width="150" show-overflow-tooltip />
      <el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag></template></el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="90" />
      <el-table-column prop="createTime" label="创建时间" width="170"><template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" :disabled="scope.row.status === 1" @click="onAudit(scope.row.id, 1)">发布</el-button>
          <el-button link type="warning" :disabled="scope.row.status === 2" @click="onAudit(scope.row.id, 2)">下架</el-button>
          <el-button link type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auditMessage, deleteMessage, listAdminMessages, type MessageItem } from '../api/message'

const loading = ref(false)
const messages = ref<MessageItem[]>([])
const categoryFilter = ref<number | undefined>()
const statusFilter = ref<number | undefined>()

const categoryText = (c?: number) => ({ 1: '失物招领', 2: '寻找搭子', 3: '入住交流', 4: '建议反馈', 9: '其他' }[c || 9] || '其他')
const statusText = (s?: number) => ({ 0: '待审核', 1: '已发布', 2: '已下架' }[s ?? 0] || '待审核')
const statusTagType = (s?: number) => ({ 0: 'info', 1: 'success', 2: 'warning' }[s ?? 0] as any)
const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 16) : '-')

const filteredMessages = computed(() => {
  return messages.value.filter((item) => {
    const byCategory = categoryFilter.value === undefined ? true : item.category === categoryFilter.value
    const byStatus = statusFilter.value === undefined ? true : item.status === statusFilter.value
    return byCategory && byStatus
  })
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await listAdminMessages()
    messages.value = res.data || []
  } finally {
    loading.value = false
  }
}

const onAudit = async (id: number, status: 1 | 2) => {
  await auditMessage(id, status)
  ElMessage.success(status === 1 ? '审核发布成功' : '下架成功')
  await loadData()
}

const onDelete = async (id: number) => {
  await ElMessageBox.confirm('确认删除该留言吗？', '提示', { type: 'warning' })
  await deleteMessage(id)
  ElMessage.success('删除成功')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.filters { display: flex; gap: 8px; align-items: center; }
</style>
