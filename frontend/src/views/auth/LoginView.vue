<template><el-card class="card"><template #header>登录</template><el-form :model="form" label-width="90px"><el-form-item label="用户名"><el-input v-model="form.username"/></el-form-item><el-form-item label="密码"><el-input v-model="form.password" show-password/></el-form-item><el-form-item><el-button type="primary" @click="login">登录</el-button><el-button @click="router.push('/register')">注册</el-button></el-form-item></el-form></el-card></template>
<script setup lang="ts">
import { reactive } from 'vue';import { useRouter } from 'vue-router';import { loginUser } from '../../api/user';import { setLoginUser } from '../../utils/auth';import { ElMessage } from 'element-plus'
const router=useRouter();const form=reactive({username:'',password:''})
const login=async()=>{const r=await loginUser(form);setLoginUser(r.data);ElMessage.success('登录成功');router.replace(r.data.role===1?'/admin/dashboard':'/user/dashboard')}
</script>
<style scoped>.card{max-width:420px;margin:80px auto}</style>
