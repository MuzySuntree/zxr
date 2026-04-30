<template>
  <div class="auth-wrap">
    <div class="bg-blur"></div>

    <el-card class="card">
      <template #header>
        <div class="title">用户登录</div>
      </template>

      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item>
          <div class="btn-group">
            <el-button type="primary" class="login-btn" @click="onLogin">
              登录
            </el-button>
            <el-button class="register-btn" @click="router.push('/register')">
              去注册
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
import { loginUser } from '../api/user'
import { setLoginUser } from '../utils/auth'

const router = useRouter()
const form = reactive({ username: '', password: '' })

const onLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  const res = await loginUser(form)
  setLoginUser(res.data)
  ElMessage.success('登录成功')
  router.push(res.data.role === 1 ? '/admin/dashboard' : '/user/home')
}
</script>

<style scoped>
/* 背景 */
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
  width: 420px;
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
  letter-spacing: 1px;
}

/* 输入框 */
:deep(.el-input__wrapper) {
  border-radius: 10px;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea;
}

/* 按钮区域 */
.btn-group {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

/* 登录按钮 */
.login-btn {
  flex: 1;
  margin-right: 10px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102,126,234,0.5);
}

/* 注册按钮 */
.register-btn {
  flex: 1;
  border-radius: 10px;
  transition: all 0.3s;
}

.register-btn:hover {
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