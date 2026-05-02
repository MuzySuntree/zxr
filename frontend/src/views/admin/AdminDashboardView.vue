<template>
  <div class="dashboard-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :span="8"><el-card class="summary-card"><h3>当前有空闲床位的房间</h3><p class="num">{{ summary.roomWithFreeBeds }}</p></el-card></el-col>
      <el-col :span="8"><el-card class="summary-card"><h3>住户总数</h3><p class="num">{{ summary.userCount }}</p></el-card></el-col>
      <el-col :span="8"><el-card class="summary-card"><h3>一周收入（元）</h3><p class="num">{{ summary.weekRevenue.toFixed(2) }}</p></el-card></el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12"><el-card class="chart-card"><template #header>订单状态分布</template><div ref="orderStatusChartRef" class="chart" /></el-card></el-col>
      <el-col :span="12"><el-card class="chart-card"><template #header>近7日收入趋势</template><div ref="revenueTrendChartRef" class="chart" /></el-card></el-col>
      <el-col :span="12"><el-card class="chart-card"><template #header>房间性别类型分布</template><div ref="roomGenderChartRef" class="chart" /></el-card></el-col>
      <el-col :span="12"><el-card class="chart-card"><template #header>床位状态统计</template><div ref="bedStatusChartRef" class="chart" /></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listBeds } from '../../api/bed'
import { listOrders } from '../../api/order'
import { listRooms } from '../../api/room'
import { listUsers } from '../../api/user'

const summary = reactive({ roomWithFreeBeds: 0, userCount: 0, weekRevenue: 0 })

const orderStatusChartRef = ref<HTMLElement | null>(null)
const revenueTrendChartRef = ref<HTMLElement | null>(null)
const roomGenderChartRef = ref<HTMLElement | null>(null)
const bedStatusChartRef = ref<HTMLElement | null>(null)

let orderStatusChart: echarts.ECharts | null = null
let revenueTrendChart: echarts.ECharts | null = null
let roomGenderChart: echarts.ECharts | null = null
let bedStatusChart: echarts.ECharts | null = null

const STATUS_TEXT: Record<number, string> = { 1: '待支付', 2: '已支付', 3: '已分配', 4: '已入住', 5: '已完成', 6: '已取消' }
const ROOM_GENDER_TEXT: Record<number, string> = { 1: '男生房', 2: '女生房', 3: '混住房' }

const chartResizeHandler = () => {
  orderStatusChart?.resize(); revenueTrendChart?.resize(); roomGenderChart?.resize(); bedStatusChart?.resize()
}

const formatDate = (date: Date) => {
  const y = date.getFullYear(); const m = `${date.getMonth() + 1}`.padStart(2, '0'); const d = `${date.getDate()}`.padStart(2, '0')
  return `${y}-${m}-${d}`
}

const loadDashboardData = async () => {
  try {
    const [roomRes, bedRes, userRes, orderRes] = await Promise.all([listRooms(), listBeds(), listUsers(), listOrders()])
    const rooms = roomRes.data || []
    const beds = bedRes.data || []
    const users = (userRes.data || []).filter((u: any) => u.role !== 1)
    const orders = orderRes.data || []

    const activeOrderStatuses = [2, 3, 4]
    const paidStatuses = [2, 3, 4, 5]

    const occupiedBedIds = new Set(orders.filter((o: any) => activeOrderStatuses.includes(o.orderStatus) && o.bedId).map((o: any) => o.bedId))
    const freeRoomIds = new Set(beds.filter((b: any) => b.status === 1 && !occupiedBedIds.has(b.id)).map((b: any) => b.roomId))
    summary.roomWithFreeBeds = rooms.filter((r: any) => freeRoomIds.has(r.id)).length
    summary.userCount = users.length

    const today = new Date(); const weekLabels: string[] = []; const revenueMap: Record<string, number> = {}
    for (let i = 6; i >= 0; i--) { const d = new Date(today); d.setDate(today.getDate() - i); const key = formatDate(d); weekLabels.push(key); revenueMap[key] = 0 }

    for (const o of orders) {
      if (!o.payTime || !paidStatuses.includes(o.orderStatus)) continue
      const dateKey = formatDate(new Date(o.payTime))
      if (dateKey in revenueMap) revenueMap[dateKey] += Number(o.amount ?? o.totalAmount ?? 0)
    }
    const weekRevenueData = weekLabels.map((d) => Number(revenueMap[d].toFixed(2)))
    summary.weekRevenue = weekRevenueData.reduce((a, b) => a + b, 0)

    const orderStatusCount: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0, 6: 0 }
    orders.forEach((o: any) => { if (orderStatusCount[o.orderStatus] !== undefined) orderStatusCount[o.orderStatus]++ })

    const roomGenderCount: Record<number, number> = { 1: 0, 2: 0, 3: 0 }
    rooms.forEach((r: any) => { if (roomGenderCount[r.genderLimit] !== undefined) roomGenderCount[r.genderLimit]++ })

    const availableBeds = beds.filter((b: any) => b.status === 1).length
    const disabledBeds = beds.filter((b: any) => b.status === 0).length
    const occupiedBeds = beds.filter((b: any) => occupiedBedIds.has(b.id)).length

    renderCharts(orderStatusCount, weekLabels, weekRevenueData, roomGenderCount, { availableBeds, disabledBeds, occupiedBeds })
  } catch (e: any) {
    ElMessage.error(e?.message || '仪表盘数据加载失败')
  }
}

const renderCharts = (
  orderStatusCount: Record<number, number>,
  weekLabels: string[],
  weekRevenueData: number[],
  roomGenderCount: Record<number, number>,
  bedStats: { availableBeds: number; disabledBeds: number; occupiedBeds: number }
) => {
  orderStatusChart?.setOption({ tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: '62%', data: Object.entries(orderStatusCount).map(([k,v]) => ({ name: STATUS_TEXT[Number(k)], value: v })) }] })
  revenueTrendChart?.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: weekLabels }, yAxis: { type: 'value' }, series: [{ type: 'line', smooth: true, data: weekRevenueData, areaStyle: {} }] })
  roomGenderChart?.setOption({ tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: '62%', data: Object.entries(roomGenderCount).map(([k,v]) => ({ name: ROOM_GENDER_TEXT[Number(k)], value: v })) }] })
  bedStatusChart?.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: ['可用', '停用', '已占用'] }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: [bedStats.availableBeds, bedStats.disabledBeds, bedStats.occupiedBeds], barWidth: 42 }] })
}

onMounted(async () => {
  await nextTick()
  if (orderStatusChartRef.value) orderStatusChart = echarts.init(orderStatusChartRef.value)
  if (revenueTrendChartRef.value) revenueTrendChart = echarts.init(revenueTrendChartRef.value)
  if (roomGenderChartRef.value) roomGenderChart = echarts.init(roomGenderChartRef.value)
  if (bedStatusChartRef.value) bedStatusChart = echarts.init(bedStatusChartRef.value)
  window.addEventListener('resize', chartResizeHandler)
  await loadDashboardData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', chartResizeHandler)
  orderStatusChart?.dispose(); revenueTrendChart?.dispose(); roomGenderChart?.dispose(); bedStatusChart?.dispose()
  orderStatusChart = revenueTrendChart = roomGenderChart = bedStatusChart = null
})
</script>

<style scoped>
.dashboard-page { padding: 4px; }
.summary-row { margin-bottom: 16px; }
.summary-card { height: 120px; display: flex; flex-direction: column; justify-content: center; }
.num { font-size: 30px; color: #409eff; margin: 8px 0 0; font-weight: 700; }
.chart-card { margin-bottom: 16px; }
.chart { height: 320px; }
</style>
