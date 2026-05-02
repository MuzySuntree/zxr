<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>我的订单</span>
        <el-button @click="loadData">刷新</el-button>
      </div>
    </template>

    <el-table :data="orders" border v-loading="loading">
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="orderNo" label="订单号" min-width="220" />
      <el-table-column prop="checkInDate" label="入住日期" width="120">
        <template #default="scope">{{ formatDate(scope.row.checkInDate) }}</template>
      </el-table-column>
      <el-table-column prop="checkOutDate" label="退房日期" width="120">
        <template #default="scope">{{ formatDate(scope.row.checkOutDate) }}</template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column label="状态" width="110">
        <template #default="scope">
          <el-tag :type="orderStatusTag(scope.row.orderStatus)">{{ orderStatusText(scope.row.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roomNo" label="房间号" width="100">
        <template #default="scope">{{ scope.row.roomNo || '待分配' }}</template>
      </el-table-column>
      <el-table-column prop="bedNo" label="床位号" width="100">
        <template #default="scope">{{ scope.row.bedNo || '待分配' }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="220" fixed="right">
        <template #default="scope">
          <template v-if="scope.row.orderStatus === 1">
            <el-button size="small" type="success" @click="onPay(scope.row.orderId)">支付</el-button>
            <el-button size="small" type="warning" @click="onCancel(scope.row.orderId)">取消</el-button>
          </template>
          <el-tag v-else-if="scope.row.orderStatus === 2" type="primary">等待分配</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 3" type="success">查看分配</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 4" type="success">入住中</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 5" type="info">已完成</el-tag>
          <el-tag v-else-if="scope.row.orderStatus === 6" type="danger">已取消</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, listOrders, payOrder } from '../../api/order'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const orders = ref<any[]>([])
const loading = ref(false)

const formatDate = (v?: string) => (v ? v.slice(0, 10) : '-')
const orderStatusText = (status: number) => ({ 1: '待支付', 2: '已支付', 3: '已分配', 4: '已入住', 5: '已完成', 6: '已取消' }[status] || '未知')
const orderStatusTag = (status: number) => ({ 1: 'warning', 2: 'primary', 3: 'success', 4: 'success', 5: 'info', 6: 'danger' }[status] as any)

const loadData = async () => {
  if (!loginUser?.userId) {
    ElMessage.error('未获取到登录用户，请重新登录')
    return
  }
  loading.value = true
  try {
    const res = await listOrders()
    orders.value = (res.data || []).filter((o: any) => Number(o.userId) === Number(loginUser.userId))
  } finally {
    loading.value = false
  }
}

const onPay = async (orderId: number) => {
  await ElMessageBox.confirm('确认支付该订单吗？', '支付确认', { type: 'warning' })
  const res = await payOrder(orderId)
  const result = res.data || {}
  ElMessage.success('支付成功')
  await loadData()

  const parts = [
    `支付方式：${result.payTypeName || '-'}`,
    `支付金额：${result.paidAmount ?? '-'}`,
    `房间号：${result.roomNo || '待分配'}`,
    `床位号：${result.bedNo || '待分配'}`
  ]
  await ElMessageBox.alert(parts.join('<br/>'), '支付与分配结果', { dangerouslyUseHTMLString: true })
}

const onCancel = async (orderId: number) => {
  const target = orders.value.find((o) => o.orderId === orderId)
  if (target?.orderStatus !== 1) {
    ElMessage.warning('只有待支付订单可以取消')
    return
  }
  await ElMessageBox.confirm('确认取消该订单吗？', '取消确认', { type: 'warning' })
  await cancelOrder(orderId)
  ElMessage.success('订单已取消')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
