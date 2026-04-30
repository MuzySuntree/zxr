<template>
  <div class="auth-wrap">
    <div class="bg-blur"></div>

    <el-card class="card">
      <template #header>
        <div class="title">用户注册</div>
      </template>

      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="form.gender" class="full-width">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item>
          <div class="btn-group">
            <el-button type="primary" class="register-btn" @click="onSubmit">
              注册
            </el-button>
            <el-button class="login-btn" @click="router.push('/login')">
              返回登录
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerUser } from '../api/user'

const router = useRouter()
const form = reactive({
  username: '',
  password: '',
  realName: '',
  gender: 1,
  phone: ''
})

const onSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('用户名和密码必填')
    return
  }
  await registerUser(form)
  ElMessage.success('注册成功，请登录')
  router.push('/login')
}
</script>

<style scoped>
/* 与登录页统一背景 */
.auth-wrap {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  overflow: hidden;
}

/* 动态光晕 */
.bg-blur {
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(255,255,255,0.2), transparent 70%);
  filter: blur(80px);
  animation: float 8s ease-in-out infinite;
}

/* 卡片 */
.card {
  width: 460px;
  border-radius: 16px;
  backdrop-filter: blur(15px);
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  animation: fadeUp 0.6s ease;
}

/* 标题 */
.title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
}

/* 输入框统一 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 10px;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea;
}

/* select铺满 */
.full-width {
  width: 100%;
}

/* 按钮组 */
.btn-group {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

/* 主按钮 */
.register-btn {
  flex: 1;
  margin-right: 10px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  transition: all 0.3s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102,126,234,0.5);
}

/* 次按钮 */
.login-btn {
  flex: 1;
  border-radius: 10px;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
}

/* 动画 */
@keyframes float {
  0%,100% { transform: translateY(0px); }
  50% { transform: translateY(-30px); }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>