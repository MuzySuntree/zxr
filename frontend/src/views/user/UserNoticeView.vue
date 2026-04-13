<template><el-card><template #header>我的通知</template><el-table :data="notices" border><el-table-column prop="title" label="标题"/><el-table-column prop="content" label="内容"/><el-table-column prop="isRead" label="已读"/><el-table-column label="操作" width="120"><template #default="s"><el-button size="small" @click="read(s.row.id)">已读</el-button></template></el-table-column></el-table></el-card></template>
<script setup lang="ts">
import { onMounted, ref } from 'vue';import { listUserNotices, markNoticeRead } from '../../api/notice';import { getLoginUser } from '../../utils/auth'
const user=getLoginUser()!;const notices=ref<any[]>([])
const load=async()=>notices.value=(await listUserNotices(user.userId)).data||[]
const read=async(id:number)=>{await markNoticeRead(id);load()}
onMounted(load)
</script>
