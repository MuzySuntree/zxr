<template>
  <el-card>
    <el-carousel height="420px" indicator-position="outside">
      <el-carousel-item v-for="item in displayImages" :key="item.id">
        <img class="banner" :src="item.url" :alt="item.name" />
      </el-carousel-item>
    </el-carousel>
  </el-card>
  <footer class="footer">
    <p>Youngman Hostel 青年旅社公司 · 专注年轻人的城市旅居服务</p>
    <p>地址：杭州市青年路 88 号 | 电话：400-800-1314 | 邮箱：service@youngman-hostel.com</p>
  </footer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listRooms } from '../../api/room'

const roomImages = ref<any[]>([])
const fallback = [
  { id: 1, url: 'https://picsum.photos/1200/420?random=1', name: 'hostel-1' },
  { id: 2, url: 'https://picsum.photos/1200/420?random=2', name: 'hostel-2' },
  { id: 3, url: 'https://picsum.photos/1200/420?random=3', name: 'hostel-3' }
]

const displayImages = computed(() => (roomImages.value.length ? roomImages.value : fallback))

onMounted(async () => {
  const res = await listRooms()
  roomImages.value = (res.data || [])
    .filter((r: any) => r.roomImage)
    .map((r: any) => ({ id: r.id, url: r.roomImage, name: r.roomName }))
})
</script>

<style scoped>
.banner { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; }
.footer { margin-top: 20px; padding: 22px; background: #1f2d3d; color: #fff; text-align: center; border-radius: 8px; }
</style>
