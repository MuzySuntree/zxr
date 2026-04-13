<template>
  <el-row :gutter="16">
    <el-col :span="8"><el-card><h4>当前有空闲床位的房间</h4><h2>{{ freeRoomCount }}</h2></el-card></el-col>
    <el-col :span="8"><el-card><h4>当前住户人数</h4><h2>{{ currentOccupants }}</h2></el-card></el-col>
    <el-col :span="8"><el-card><h4>近7天收入</h4><h2>￥{{ weekIncome.toFixed(2) }}</h2></el-card></el-col>
  </el-row>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listBeds } from '../../api/bed'
import { listRooms } from '../../api/room'
import { listOrders } from '../../api/order'

const freeRoomCount = ref(0)
const currentOccupants = ref(0)
const weekIncome = ref(0)

const load = async () => {
  const [roomRes, bedRes, orderRes] = await Promise.all([listRooms(), listBeds(), listOrders()])
  const rooms = roomRes.data || []; const beds = bedRes.data || []; const orders = orderRes.data || []
  const now = new Date();
  const occupiedBedIds = new Set<number>()
  currentOccupants.value = 0
  weekIncome.value = 0
  const oneWeekAgo = new Date(now.getTime() - 7 * 24 * 3600 * 1000)

  orders.forEach((o: any) => {
    const inDate = new Date(o.checkInDate)
    const outDate = new Date(o.checkOutDate)
    if ([2,3,4].includes(o.orderStatus) && inDate <= now && now < outDate && o.bedId) {
      occupiedBedIds.add(o.bedId); currentOccupants.value++
    }
    if (o.payTime && new Date(o.payTime) >= oneWeekAgo) weekIncome.value += Number(o.amount || 0)
  })

  const roomHasFree = new Set<number>()
  beds.filter((b:any)=>b.status===1 && !occupiedBedIds.has(b.id)).forEach((b:any)=>roomHasFree.add(b.roomId))
  freeRoomCount.value = rooms.filter((r:any)=>roomHasFree.has(r.id)).length
}
onMounted(load)
</script>
