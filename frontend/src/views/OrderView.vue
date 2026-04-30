<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>订单列表</span>
        <el-button @click="loadData">刷新</el-button>
      </div>
    </template>

    <el-table :data="orders" border>
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="orderNo" label="订单号" min-width="220" />
      <el-table-column prop="checkInDate" label="入住日期" />
      <el-table-column prop="checkOutDate" label="退房日期" />
<!--      <el-table-column prop="orderStatus" label="状态" />-->
      <el-table-column prop="roomNo" label="房间号" />
      <el-table-column prop="bedNo" label="床位号" />
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="scope">
<!--          <el-button size="small" type="success" @click="onPay(scope.row.orderId)">支付</el-button>-->
          <el-button size="small" type="primary" @click="onAllocate(scope.row.orderId)">分配床位</el-button>
          <el-button size="small" @click="onViewAllocation(scope.row.orderId)">查看分配</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="allocationDialogVisible" title="分配结果" width="520px">
      <el-descriptions border :column="1" v-if="allocationResult">
        <el-descriptions-item label="订单号">{{ allocationResult.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ allocationResult.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="房间名称">{{ allocationResult.roomName }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ allocationResult.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="当前入住人数">{{ allocationResult.occupiedCount }}</el-descriptions-item>
        <el-descriptions-item label="男性人数">{{ allocationResult.maleCount }}</el-descriptions-item>
        <el-descriptions-item label="女性人数">{{ allocationResult.femaleCount }}</el-descriptions-item>
        <el-descriptions-item label="消息">{{ allocationResult.message }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { allocateOrder, getAllocation, listOrders, payOrder } from '../api/order'

const orders = ref<any[]>([])
const allocationDialogVisible = ref(false)
const allocationResult = ref<any>(null)

const loadData = async () => {
  const res = await listOrders()
  orders.value = res.data || []
}

const onPay = async (orderId: number) => {
  await payOrder(orderId)
  ElMessage.success('支付成功（已触发自动分配）')
  loadData()
}

const onAllocate = async (orderId: number) => {
  await allocateOrder(orderId)
  ElMessage.success('分配成功')
  loadData()
}

const onViewAllocation = async (orderId: number) => {
  const res = await getAllocation(orderId)
  allocationResult.value = res.data
  allocationDialogVisible.value = true
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
