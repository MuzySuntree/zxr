<template>
  <el-row :gutter="16">
    <el-col :span="8"><el-card><h3>当前有空闲床位的房间</h3><p class="num">{{ summary.roomWithFreeBeds }}</p></el-card></el-col>
    <el-col :span="8"><el-card><h3>住户总数</h3><p class="num">{{ summary.userCount }}</p></el-card></el-col>
    <el-col :span="8"><el-card><h3>一周收入（元）</h3><p class="num">{{ summary.weekRevenue.toFixed(2) }}</p></el-card></el-col>
  </el-row>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { listBeds } from '../../api/bed'
import { listOrders } from '../../api/order'
import { listRooms } from '../../api/room'
import { listUsers } from '../../api/user'

const summary = reactive({ roomWithFreeBeds: 0, userCount: 0, weekRevenue: 0 })

const loadSummary = async () => {
  const [roomRes, bedRes, userRes, orderRes] = await Promise.all([listRooms(), listBeds(), listUsers(), listOrders()])
  const rooms = roomRes.data || []
  const beds = bedRes.data || []
  const users = (userRes.data || []).filter((u: any) => u.role !== 1)
  const orders = orderRes.data || []

  const today = new Date()
  const weekAgo = new Date(today)
  weekAgo.setDate(today.getDate() - 7)

  const occupiedBedIds = new Set(
    orders
      .filter((o: any) => [2, 3, 4].includes(o.orderStatus) && o.bedId)
      .map((o: any) => o.bedId)
  )

  const freeRoomIds = new Set(
    beds.filter((b: any) => b.status === 1 && !occupiedBedIds.has(b.id)).map((b: any) => b.roomId)
  )

  summary.roomWithFreeBeds = rooms.filter((r: any) => freeRoomIds.has(r.id)).length
  summary.userCount = users.length
  summary.weekRevenue = orders
    .filter((o: any) => o.payTime)
    .filter((o: any) => {
      const payTime = new Date(o.payTime)
      return payTime >= weekAgo && payTime <= today
    })
    .reduce((acc: number, o: any) => acc + Number(o.amount || 0), 0)
}

onMounted(loadSummary)
</script>

<style scoped>
.num { font-size: 30px; color: #409eff; margin: 8px 0 0; font-weight: 700; }
</style>
