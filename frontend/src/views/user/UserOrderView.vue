<template>
  <el-card>
    <template #header><div class="toolbar"><span>我的订单</span><el-button @click="loadData">刷新</el-button></div></template>
    <el-table :data="orders" border>
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="orderNo" label="订单号" min-width="220" />
      <el-table-column prop="checkInDate" label="入住日期" />
      <el-table-column prop="checkOutDate" label="退房日期" />
      <el-table-column prop="orderStatus" label="状态" />
      <el-table-column prop="roomNo" label="房间号" />
      <el-table-column prop="bedNo" label="床位号" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" type="success" @click="onPay(scope.row.orderId)">支付</el-button>
          <el-button size="small" type="warning" @click="onCancel(scope.row.orderId)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cancelOrder, listOrders, payOrder } from '../../api/order'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const orders = ref<any[]>([])

const loadData = async () => {
  const res = await listOrders()
  orders.value = (res.data || []).filter((o: any) => o.userId === loginUser?.userId)
}

const onPay = async (orderId: number) => {
  await payOrder(orderId)
  ElMessage.success('支付成功')
  loadData()
}

const onCancel = async (orderId: number) => {
  await cancelOrder(orderId)
  ElMessage.success('订单已取消')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: center; }</style>
