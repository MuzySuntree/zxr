<template>
  <el-row :gutter="16">
    <el-col :span="10">
      <el-card>
        <template #header>订房参数</template>
        <el-form :model="queryForm" label-width="100px">
          <el-form-item label="入住日期"><el-date-picker v-model="queryForm.checkInDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          <el-form-item label="退房日期"><el-date-picker v-model="queryForm.checkOutDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          <el-form-item label="用户性别"><el-select v-model="queryForm.userGender"><el-option label="男" :value="1" /><el-option label="女" :value="2" /></el-select></el-form-item>
          <el-form-item><el-button type="primary" @click="queryAvailableBeds">查询可用床位</el-button></el-form-item>
        </el-form>
      </el-card>
      <el-card style="margin-top: 16px">
        <template #header>创建我的订单</template>
        <el-form :model="orderForm" label-width="100px">
          <el-form-item label="备注"><el-input v-model="orderForm.remark" /></el-form-item>
          <el-form-item><el-button type="success" @click="createBookingOrder">预订（待支付）</el-button></el-form-item>
        </el-form>
      </el-card>
    </el-col>
    <el-col :span="14">
      <el-card>
        <template #header>可用床位列表</template>
        <el-table :data="availableBeds" border>
          <el-table-column prop="roomNo" label="房间号" />
          <el-table-column prop="roomName" label="房间名称" />
          <el-table-column prop="bedNo" label="床位号" />
          <el-table-column prop="price" label="价格" />
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableBedsByGender } from '../../api/bed'
import { createOrder } from '../../api/order'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const queryForm = reactive({ checkInDate: '', checkOutDate: '', userGender: loginUser?.gender || 1 })
const orderForm = reactive({ remark: '' })
const availableBeds = ref<any[]>([])

const queryAvailableBeds = async () => {
  if (!queryForm.checkInDate || !queryForm.checkOutDate) return ElMessage.warning('请选择日期')
  const res = await getAvailableBedsByGender(queryForm)
  availableBeds.value = res.data || []
}

const createBookingOrder = async () => {
  const payload = {
    userId: Number(loginUser?.userId),
    checkInDate: queryForm.checkInDate,
    checkOutDate: queryForm.checkOutDate,
    userGender: queryForm.userGender,
    remark: orderForm.remark
  }
  await createOrder(payload)
  ElMessage.success('订单创建成功，请到我的订单支付')
}
</script>
