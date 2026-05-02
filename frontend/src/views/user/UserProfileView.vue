<template>
  <el-card>
    <template #header>个人信息维护</template>
    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="性别">
        <el-select v-model="form.gender"><el-option :value="1" label="男" /><el-option :value="2" label="女" /></el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="onSave">保存</el-button></el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserById, updateUser } from '../../api/user'
import { getLoginUser, setLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const form = reactive<any>({ id: loginUser?.userId, username: '', realName: '', phone: '', gender: 1, role: 2 })

const loadData = async () => {
  const res = await getUserById(Number(loginUser?.userId))
  Object.assign(form, res.data)
}

const onSave = async () => {
  await updateUser(form)
  if (loginUser) setLoginUser({ ...loginUser, realName: form.realName, gender: form.gender })
  ElMessage.success('保存成功')
}

onMounted(loadData)
</script>
