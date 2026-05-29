<template>
  <div class="auth-wrap">
    <div class="bg-blur"></div>

    <el-card class="card">
      <template #header>
        <div class="title">用户注册</div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" show-password placeholder="请再次输入密码" />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入中文姓名" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" class="full-width" placeholder="请选择性别">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="验证码" prop="verifyCode">
          <div class="code-row">
            <el-input v-model="form.verifyCode" placeholder="请输入验证码" />
            <el-button :disabled="countdown > 0" @click="onSendCode">
              {{ countdown > 0 ? `${countdown}s后重发` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <div class="btn-group">
            <el-button type="primary" class="register-btn" @click="onSubmit">注册</el-button>
            <el-button class="login-btn" @click="router.push('/login')">返回登录</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { registerUser, sendRegisterCode } from '../api/user'

const router = useRouter()
const formRef = ref<FormInstance>()
const timer = ref<number | null>(null)
const countdown = ref(0)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  gender: null as number | null,
  phone: '',
  verifyCode: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '用户名必填', trigger: 'blur' }, { min: 4, max: 20, message: '用户名长度需4-20位', trigger: 'blur' }],
  password: [{ required: true, message: '密码必填', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度需6-20位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '确认密码必填', trigger: 'blur' }, { validator: (_, value, cb) => value === form.password ? cb() : cb(new Error('两次密码输入不一致')), trigger: 'blur' }],
  realName: [{ required: true, message: '真实姓名必填', trigger: 'blur' }, { pattern: /^[\u4e00-\u9fa5]{2,10}$/, message: '姓名需为2-10位中文', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '手机号必填', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  verifyCode: [{ required: true, message: '验证码必填', trigger: 'blur' }]
}

const startCountdown = () => {
  countdown.value = 60
  timer.value = window.setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0 && timer.value) {
      clearInterval(timer.value)
      timer.value = null
    }
  }, 1000)
}

const onSendCode = async () => {
  await formRef.value?.validateField('phone')
  const res = await sendRegisterCode(form.phone)
  ElMessage.success(`${res?.message || '验证码已发送'}（模拟码：${res?.data?.verifyCode || ''}）`)
  startCountdown()
}

const onSubmit = async () => {
  await formRef.value?.validate()
  await registerUser({ ...form })
  ElMessage.success('注册成功，请登录')
  router.push('/login')
}

onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
})
</script>

<style scoped>
.auth-wrap { position: relative; min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea, #764ba2); overflow: hidden; }
.bg-blur { position: absolute; width: 600px; height: 600px; background: radial-gradient(circle, rgba(255,255,255,0.2), transparent 70%); filter: blur(80px); animation: float 8s ease-in-out infinite; }
.card { width: 480px; border-radius: 16px; backdrop-filter: blur(15px); background: rgba(255, 255, 255, 0.85); box-shadow: 0 10px 30px rgba(0,0,0,0.2); animation: fadeUp 0.6s ease; }
.title { text-align: center; font-size: 20px; font-weight: 600; }
:deep(.el-input__wrapper),:deep(.el-select__wrapper) { border-radius: 10px; transition: all 0.3s; }
:deep(.el-input__wrapper:hover),:deep(.el-select__wrapper:hover) { box-shadow: 0 0 0 1px #667eea; }
:deep(.el-input__wrapper.is-focus),:deep(.el-select__wrapper.is-focus) { box-shadow: 0 0 0 2px #667eea; }
.full-width { width: 100%; }
.code-row { width: 100%; display: grid; grid-template-columns: 1fr 120px; gap: 10px; }
.btn-group { width: 100%; display: flex; justify-content: space-between; }
.register-btn { flex: 1; margin-right: 10px; border-radius: 10px; background: linear-gradient(135deg, #667eea, #764ba2); border: none; transition: all 0.3s; }
.register-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(102,126,234,0.5); }
.login-btn { flex: 1; border-radius: 10px; transition: all 0.3s; }
.login-btn:hover { transform: translateY(-2px); }
@keyframes float { 0%,100% { transform: translateY(0px); } 50% { transform: translateY(-30px); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
</style>
