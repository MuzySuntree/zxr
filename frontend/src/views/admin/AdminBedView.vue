<template><el-card><template #header>床位管理</template><el-table :data="beds" border><el-table-column prop="bedNo" label="床位号"/><el-table-column prop="roomId" label="房间ID"/><el-table-column label="操作" width="200"><template #default="s"><el-button size="small" @click="edit(s.row)">编辑</el-button><el-button size="small" type="danger" @click="remove(s.row.id)">删</el-button></template></el-table-column></el-table></el-card></template>
<script setup lang="ts">
import { onMounted, ref } from 'vue';import { deleteBed, listBeds } from '../../api/bed';import { ElMessage } from 'element-plus'
const beds=ref<any[]>([]);const load=async()=>beds.value=(await listBeds()).data||[];const edit=()=>ElMessage.info('可复用原编辑弹窗');const remove=async(id:number)=>{await deleteBed(id);ElMessage.success('删除成功');load()};onMounted(load)
</script>
