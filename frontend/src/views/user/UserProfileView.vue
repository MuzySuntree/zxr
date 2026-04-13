<template><el-card><template #header>个人信息维护</template><el-form :model="form" label-width="100px"><el-form-item label="用户名"><el-input v-model="form.username" disabled/></el-form-item><el-form-item label="真实姓名"><el-input v-model="form.realName"/></el-form-item><el-form-item label="手机号"><el-input v-model="form.phone"/></el-form-item><el-form-item><el-button type="primary" @click="save">保存</el-button></el-form-item></el-form></el-card></template>
<script setup lang="ts">
import { onMounted, reactive } from 'vue';import { getUserById, updateUser } from '../../api/user';import { getLoginUser } from '../../utils/auth';import { ElMessage } from 'element-plus'
const current=getLoginUser()!;const form=reactive<any>({id:current.userId,username:'',realName:'',phone:''})
onMounted(async()=>Object.assign(form,(await getUserById(current.userId)).data))
const save=async()=>{await updateUser(form);ElMessage.success('保存成功')}
</script>
