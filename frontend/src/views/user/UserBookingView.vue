<template>
  <el-card>
    <template #header>
      <div class="header-row">
        <span class="title">订房信息</span>
      </div>
    </template>

    <el-form :model="form" label-width="90px" class="booking-form">
      <el-form-item label="入住日期">
        <el-date-picker
          v-model="form.checkInDate"
          type="date"
          value-format="YYYY-MM-DD"
          :disabled-date="disabledBeforeToday"
          @change="onCheckInDateChange"
          placeholder="请选择入住日期"
          class="date-picker" />
      </el-form-item>
      <el-form-item label="退房日期">
        <el-date-picker
          v-model="form.checkOutDate"
          type="date"
          value-format="YYYY-MM-DD"
          :disabled-date="disabledCheckOutDate"
          placeholder="请选择退房日期"
          class="date-picker" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" maxlength="200" show-word-limit placeholder="选填" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="queryAvailableBeds">查询可用床位</el-button>
        <el-button type="success" @click="createBookingOrder">创建订单</el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <el-card class="section-card">
    <template #header><span>可用床位列表</span></template>
    <el-empty v-if="!availableBeds.length" description="暂无可用床位" />
    <el-table v-else :data="availableBeds" border>
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column prop="roomName" label="房间名称" min-width="140" />
      <el-table-column label="房间性别限制" min-width="120">
        <template #default="scope">{{ genderText(scope.row.genderLimit) }}</template>
      </el-table-column>
      <el-table-column prop="bedNo" label="床位号" width="100" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="description" label="房间描述" min-width="180" show-overflow-tooltip />
    </el-table>
  </el-card>

  <el-card class="section-card" v-if="createdOrderId">
    <template #header><span>订单创建结果</span></template>
    <div class="result-row">
      <span>订单ID：<strong>{{ createdOrderId }}</strong></span>
      <el-button type="primary" @click="goToOrderPage">去订单页支付</el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAvailableBedsByGender } from '../../api/bed'
import { createOrder, checkOrderAvailable } from '../../api/order'
import { getLoginUser } from '../../utils/auth'

const router = useRouter()
const loginUser = getLoginUser()
const userId = Number(loginUser?.userId || 0)
const userGender = loginUser?.gender

const form = reactive({ checkInDate: '', checkOutDate: '', remark: '' })
const availableBeds = ref<any[]>([])
const createdOrderId = ref<number | null>(null)

const genderText = (g?: number) => ({ 1: '男', 2: '女', 0: '不限' }[g ?? 0] || '不限')

const disabledBeforeToday = (time: Date) => dayjs(time).isBefore(dayjs().startOf('day'))
const disabledCheckOutDate = (time: Date) => {
  if (disabledBeforeToday(time)) return true
  if (!form.checkInDate) return false
  return !dayjs(time).isAfter(dayjs(form.checkInDate), 'day')
}

const validateDates = () => {
  if (!form.checkInDate || !form.checkOutDate) {
    ElMessage.warning('请选择入住和退房日期')
    return false
  }
  const today = dayjs().startOf('day')
  const checkIn = dayjs(form.checkInDate)
  const checkOut = dayjs(form.checkOutDate)
  if (checkIn.isBefore(today, 'day')) {
    ElMessage.warning('入住日期不能早于今天')
    return false
  }
  if (checkOut.isBefore(today, 'day')) {
    ElMessage.warning('退房日期不能早于今天')
    return false
  }
  if (!checkOut.isAfter(checkIn, 'day')) {
    ElMessage.warning('退房日期必须晚于入住日期')
    return false
  }
  return true
}

const onCheckInDateChange = () => {
  if (form.checkInDate && form.checkOutDate && !dayjs(form.checkOutDate).isAfter(dayjs(form.checkInDate), 'day')) {
    form.checkOutDate = ''
  }
}

const queryAvailableBeds = async () => {
  if (!validateDates()) return
  if (!userId) return ElMessage.error('未获取到登录用户，请重新登录')

  try {
    if (userGender === 1 || userGender === 2) {
      const res = await getAvailableBedsByGender({
        checkInDate: form.checkInDate,
        checkOutDate: form.checkOutDate,
        userGender
      })
      availableBeds.value = res.data || []
      if (!availableBeds.value.length) ElMessage.info('该时间段暂无可用床位')
      return
    }

    const res = await checkOrderAvailable({ checkInDate: form.checkInDate, checkOutDate: form.checkOutDate, userId })
    if (!res.data) {
      availableBeds.value = []
      ElMessage.info('该时间段暂无可用床位')
    } else {
      availableBeds.value = []
      ElMessage.warning('用户性别信息不完整，无法展示具体床位，请先完善个人信息')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '查询可用床位失败')
  }
}

const createBookingOrder = async () => {
  if (!validateDates()) return
  if (!userId) return ElMessage.error('未获取到登录用户，请重新登录')
  if (!availableBeds.value.length) {
    ElMessage.warning('请先查询并确认有可用床位后再创建订单')
    return
  }
  try {
    const res = await createOrder({ userId, checkInDate: form.checkInDate, checkOutDate: form.checkOutDate, remark: form.remark.trim() })
    createdOrderId.value = res.data
    ElMessage.success('订单创建成功，请前往订单页支付')
  } catch (e: any) {
    ElMessage.error(e?.message || '创建订单失败')
  }
}

const goToOrderPage = () => router.push('/user/order')
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.booking-form { max-width: 760px; }
.date-picker { width: 220px; }
.section-card { margin-top: 16px; }
.result-row { display: flex; justify-content: space-between; align-items: center; }
</style>
