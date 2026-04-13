<template>
  <el-card class="card">
    <template #header>登录</template>
    <el-form :model="form" label-width="90px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onLogin">登录</el-button>
        <el-button @click="router.push('/register')">去注册</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginUser } from '../api/user'

const router = useRouter()
const form = reactive({ username: '', password: '' })

const onLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  const res = await loginUser(form)
  localStorage.setItem('loginUser', JSON.stringify(res.data))
  ElMessage.success('登录成功')
  router.push('/dashboard')
}
</script>

<style scoped>
.card { max-width: 460px; margin: 80px auto; }
</style>
