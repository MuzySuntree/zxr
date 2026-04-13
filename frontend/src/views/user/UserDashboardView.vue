<template>
  <div>
    <el-card>
      <el-carousel height="320px">
        <el-carousel-item v-for="item in images" :key="item"><el-image :src="item" fit="cover" style="width:100%;height:320px"/></el-carousel-item>
      </el-carousel>
    </el-card>
    <footer class="footer">
      <p>青年旅社有限公司 | 地址：某某市青春大道 101 号 | 电话：400-888-8888</p>
      <p>© 2026 Youngman Hostel Co.,Ltd. All rights reserved.</p>
    </footer>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listRooms } from '../../api/room'
const images=ref<string[]>([])
onMounted(async()=>{const rooms=(await listRooms()).data||[];images.value=rooms.map((r:any)=>r.roomImage).filter(Boolean);if(!images.value.length){images.value=['https://picsum.photos/1200/320?1','https://picsum.photos/1200/320?2','https://picsum.photos/1200/320?3']}})
</script>
<style scoped>.footer{margin-top:20px;background:#1f2d3d;color:#fff;padding:24px;text-align:center;line-height:1.8}</style>
