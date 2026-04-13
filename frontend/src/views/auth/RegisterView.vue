<template><el-card class="card"><template #header>注册</template><el-form :model="form" label-width="100px"><el-form-item label="用户名"><el-input v-model="form.username"/></el-form-item><el-form-item label="密码"><el-input v-model="form.password" show-password/></el-form-item><el-form-item label="真实姓名"><el-input v-model="form.realName"/></el-form-item><el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" :value="1"/><el-option label="女" :value="2"/></el-select></el-form-item><el-form-item label="手机号"><el-input v-model="form.phone"/></el-form-item><el-form-item><el-button type="primary" @click="submit">提交</el-button></el-form-item></el-form></el-card></template>
<script setup lang="ts">
import { reactive } from 'vue';import { useRouter } from 'vue-router';import { registerUser } from '../../api/user';import { ElMessage } from 'element-plus'
const router=useRouter();const form=reactive({username:'',password:'',realName:'',gender:1,phone:''})
const submit=async()=>{await registerUser(form);ElMessage.success('注册成功');router.push('/login')}
</script>
<style scoped>.card{max-width:520px;margin:50px auto}</style>
