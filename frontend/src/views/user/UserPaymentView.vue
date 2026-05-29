<template>
  <el-card>
    <template #header>
      <div class="header-row">
        <span class="title">支付设置</span>
        <el-button type="primary" @click="openCreateDialog">新增支付方式</el-button>
      </div>
    </template>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="payType" label="支付方式" min-width="150">
        <template #default="scope">{{ payTypeText(scope.row.payType) }}</template>
      </el-table-column>
      <el-table-column prop="balance" label="余额" min-width="120" />
      <el-table-column prop="isDefault" label="默认" min-width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isDefault === 1 ? 'success' : 'info'">{{ scope.row.isDefault === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button
            link
            type="warning"
            :disabled="scope.row.isDefault === 1"
            @click="onSetDefault(scope.row.id)">
            设为默认
          </el-button>
          <el-button link type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑支付方式' : '新增支付方式'" width="520px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="支付方式" prop="payType">
        <el-select v-model="form.payType" class="full-width" @change="onPayTypeChange">
          <el-option :value="1" label="余额支付" />
          <el-option :value="2" label="微信模拟支付" />
          <el-option :value="3" label="支付宝模拟支付" />
        </el-select>
      </el-form-item>
      <el-form-item label="余额" prop="balance">
        <el-input-number
          v-model="form.balance"
          :min="0"
          :precision="2"
          :step="10"
          class="full-width"
          :disabled="form.payType !== 1" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-switch v-model="statusSwitch" inline-prompt active-text="启用" inactive-text="停用" />
      </el-form-item>
      <el-form-item label="默认方式">
        <el-switch v-model="defaultSwitch" inline-prompt active-text="是" inactive-text="否" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  deletePaymentSetting,
  getUserPaymentSettings,
  savePaymentSetting,
  setDefaultPaymentSetting,
  updatePaymentSetting,
  type UserPaymentSetting
} from '../../api/payment'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const userId = Number(loginUser?.userId || 0)

const list = ref<UserPaymentSetting[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<UserPaymentSetting>({
  id: undefined,
  userId,
  payType: 1,
  balance: 0,
  isDefault: 0,
  status: 1
})

const statusSwitch = computed({
  get: () => form.status === 1,
  set: (v: boolean) => {
    form.status = v ? 1 : 0
  }
})

const defaultSwitch = computed({
  get: () => form.isDefault === 1,
  set: (v: boolean) => {
    form.isDefault = v ? 1 : 0
  }
})

const rules: FormRules<UserPaymentSetting> = {
  payType: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
  balance: [{ required: true, message: '请输入余额', trigger: 'blur' }]
}

const payTypeText = (payType: number) => ({ 1: '余额支付', 2: '微信模拟支付', 3: '支付宝模拟支付' }[payType] || '未知')

const loadData = async () => {
  if (!userId) {
    ElMessage.error('未获取到登录用户，请重新登录')
    return
  }
  loading.value = true
  try {
    const res = await getUserPaymentSettings(userId)
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.userId = userId
  form.payType = 1
  form.balance = 0
  form.isDefault = 0
  form.status = 1
}

const openCreateDialog = () => {
  if (!userId) {
    ElMessage.error('未获取到登录用户，请重新登录')
    return
  }
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row: UserPaymentSetting) => {
  isEdit.value = true
  form.id = row.id
  form.userId = row.userId
  form.payType = row.payType
  form.balance = Number(row.balance || 0)
  form.isDefault = row.isDefault
  form.status = row.status
  dialogVisible.value = true
}

const onPayTypeChange = (payType: number) => {
  if (payType !== 1) form.balance = 0
}

const onSubmit = async () => {
  await formRef.value?.validate()
  if (form.payType !== 1) form.balance = 0
  const payload: UserPaymentSetting = {
    id: form.id,
    userId,
    payType: form.payType,
    balance: Number(form.balance || 0),
    isDefault: form.isDefault,
    status: form.status
  }
  if (isEdit.value) {
    await updatePaymentSetting(payload)
    ElMessage.success('更新成功')
  } else {
    await savePaymentSetting(payload)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadData()
}

const onDelete = async (id?: number) => {
  if (!id) return
  await ElMessageBox.confirm('确认删除该支付方式吗？', '提示', { type: 'warning' })
  await deletePaymentSetting(id)
  ElMessage.success('删除成功')
  await loadData()
}

const onSetDefault = async (id?: number) => {
  if (!id) return
  await setDefaultPaymentSetting(id)
  ElMessage.success('已设置为默认支付方式')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.full-width { width: 100%; }
</style>
