<template>
  <el-card>
    <el-carousel height="420px" indicator-position="outside">
      <el-carousel-item v-for="item in displayBanners" :key="item.id">
        <img class="banner" :src="item.imageUrl" :alt="item.title" />
        <div class="banner-overlay">
          <h3>{{ item.title }}</h3>
          <p>{{ item.subtitle }}</p>
          <el-link v-if="item.linkUrl" :href="item.linkUrl" type="primary">查看详情</el-link>
        </div>
      </el-carousel-item>
    </el-carousel>
  </el-card>

  <el-card class="section-card" shadow="hover">
    <template #header>
      <div class="section-title">推荐活动</div>
    </template>
    <el-empty v-if="!recommendActivities.length" description="暂无推荐活动" />
    <div v-else class="activity-grid">
      <el-card v-for="item in recommendActivities" :key="item.id" class="activity-item" shadow="never">
        <img class="cover" :src="item.coverImage || defaultActivityImage" :alt="item.title" />
        <div class="activity-content">
          <h4>{{ item.title }}</h4>
          <p>时间：{{ formatDateTime(item.activityTime) }}</p>
          <p>地点：{{ item.location || '待定' }}</p>
          <p>人数：{{ item.joinedPeople || 0 }} / {{ item.maxPeople || 0 }}</p>
          <el-button type="primary" size="small" @click="toActivity">查看/报名</el-button>
        </div>
      </el-card>
    </div>
  </el-card>

  <el-card class="section-card" shadow="hover">
    <template #header>
      <div class="section-title">最新留言</div>
    </template>
    <el-empty v-if="!latestMessages.length" description="暂无留言" />
    <div v-else class="message-list">
      <el-card v-for="item in latestMessages" :key="item.id" class="message-item" shadow="never">
        <div class="message-header">
          <el-tag size="small">{{ categoryText(item.category) }}</el-tag>
          <span class="time">{{ formatDateTime(item.createTime) }}</span>
        </div>
        <h4>{{ item.title }}</h4>
        <p class="content">{{ item.content }}</p>
      </el-card>
    </div>
  </el-card>

  <footer class="footer">
    <p>Youngman Hostel 青年旅社公司 · 专注年轻人的城市旅居服务</p>
    <p>地址：杭州市青年路 88 号 | 电话：400-800-1314 | 邮箱：service@youngman-hostel.com</p>
  </footer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHomeIndex, type HomeBannerItem, type LatestMessageItem, type RecommendActivityItem } from '../../api/home'

const router = useRouter()
const banners = ref<HomeBannerItem[]>([])
const recommendActivities = ref<RecommendActivityItem[]>([])
const latestMessages = ref<LatestMessageItem[]>([])

const defaultActivityImage = 'https://picsum.photos/400/220?random=10'
const fallbackBanners: HomeBannerItem[] = [
  { id: 1, imageUrl: 'https://picsum.photos/1200/420?random=1', title: '青年旅社欢迎您', subtitle: '城市旅居新体验' },
  { id: 2, imageUrl: 'https://picsum.photos/1200/420?random=2', title: '公共空间', subtitle: '结识更多旅伴' },
  { id: 3, imageUrl: 'https://picsum.photos/1200/420?random=3', title: '舒适房间', subtitle: '自在休憩每一晚' }
]

const displayBanners = computed(() => (banners.value.length ? banners.value : fallbackBanners))

const categoryText = (category: number) => ({ 1: '失物招领', 2: '寻找搭子', 3: '入住交流', 4: '建议反馈', 9: '其他' }[category] || '其他')

const formatDateTime = (val?: string) => {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 16)
}

const toActivity = () => router.push('/user/booking')

onMounted(async () => {
  try {
    const res = await getHomeIndex()
    banners.value = res.data?.banners || []
    recommendActivities.value = res.data?.recommendActivities || []
    latestMessages.value = res.data?.latestMessages || []
  } catch (e: any) {
    ElMessage.error(e?.message || '首页数据加载失败，请稍后重试')
  }
})
</script>

<style scoped>
.banner { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; }
.banner-overlay { position: absolute; left: 24px; bottom: 20px; color: #fff; text-shadow: 0 1px 4px rgba(0,0,0,0.5); }
.banner-overlay h3 { margin: 0 0 6px; }
.banner-overlay p { margin: 0 0 8px; }
.section-card { margin-top: 20px; }
.section-title { font-weight: 600; }
.activity-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 12px; }
.activity-item .cover { width: 100%; height: 140px; object-fit: cover; border-radius: 6px; }
.activity-content h4 { margin: 8px 0; }
.activity-content p { margin: 6px 0; color: #606266; font-size: 13px; }
.message-list { display: grid; gap: 12px; }
.message-item h4 { margin: 8px 0; }
.message-header { display: flex; justify-content: space-between; align-items: center; }
.time { color: #999; font-size: 12px; }
.content { color: #606266; margin: 0; }
.footer { margin-top: 20px; padding: 22px; background: #1f2d3d; color: #fff; text-align: center; border-radius: 8px; }
</style>
