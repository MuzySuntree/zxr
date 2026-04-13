<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>通知列表</span>
        <div>
          <el-input-number v-model="userId" :min="1" size="small" />
          <el-button style="margin-left: 8px" @click="loadData">查询</el-button>
        </div>
      </div>
    </template>

    <el-table :data="notices" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" width="160" />
      <el-table-column prop="content" label="内容" min-width="320" />
      <el-table-column prop="isRead" label="已读" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button size="small" type="primary" @click="onRead(scope.row.id)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listUserNotices, markNoticeRead } from '../api/notice'

const userId = ref(1)
const notices = ref<any[]>([])

const loadData = async () => {
  const res = await listUserNotices(userId.value)
  notices.value = res.data || []
}

const onRead = async (noticeId: number) => {
  await markNoticeRead(noticeId)
  ElMessage.success('已标记为已读')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
