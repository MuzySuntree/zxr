<template>
  <el-card>
    <template #header>
      <div class="header">活动列表</div>
    </template>

    <el-table :data="activities" border v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="140" />
      <el-table-column label="封面" width="100">
        <template #default="scope">
          <el-image :src="scope.row.coverImage || defaultCover" fit="cover" style="width: 70px; height: 46px; border-radius: 4px" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="activityTime" label="活动时间" width="160">
        <template #default="scope">{{ formatDateTime(scope.row.activityTime) }}</template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="140" />
      <el-table-column label="人数" width="110">
        <template #default="scope">{{ scope.row.joinedPeople || 0 }}/{{ scope.row.maxPeople || 0 }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="推荐" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.recommend === 1" type="warning">推荐</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 1 && !isSigned(scope.row.id)"
            type="primary"
            size="small"
            @click="onSignup(scope.row.id)"
          >报名</el-button>
          <el-button
            v-else-if="scope.row.status === 1 && isSigned(scope.row.id)"
            type="danger"
            size="small"
            @click="onCancel(scope.row.id)"
          >取消报名</el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cancelSignupActivity, listActivities, listUserSignedActivities, signupActivity, type ActivityItem } from '../../api/activity'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const userId = Number(loginUser?.userId || 0)
const activities = ref<ActivityItem[]>([])
const signedActivityIds = ref<number[]>([])
const loading = ref(false)
const defaultCover = 'https://picsum.photos/200/120?random=11'

const statusText = (status: number) => ({ 0: '未发布', 1: '报名中', 2: '已结束', 3: '已取消' }[status] || '未知')
const statusTagType = (status: number) => ({ 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }[status] as any)
const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 16) : '-')
const isSigned = (activityId: number) => signedActivityIds.value.includes(activityId)

const loadData = async () => {
  if (!userId) {
    ElMessage.error('未获取到登录用户信息，请重新登录')
    return
  }
  loading.value = true
  try {
    const [activityRes, signedRes] = await Promise.all([listActivities(), listUserSignedActivities(userId)])
    activities.value = activityRes.data || []
    signedActivityIds.value = (signedRes.data || []).map((a: any) => a.id)
  } catch (e: any) {
    ElMessage.error(e?.message || '加载活动数据失败')
  } finally {
    loading.value = false
  }
}

const onSignup = async (activityId: number) => {
  await signupActivity(activityId, userId)
  ElMessage.success('报名成功')
  await loadData()
}

const onCancel = async (activityId: number) => {
  await cancelSignupActivity(activityId, userId)
  ElMessage.success('已取消报名')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.header { font-size: 16px; font-weight: 600; }
</style>
