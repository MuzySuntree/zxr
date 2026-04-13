<template><el-card><template #header>订单管理</template><el-table :data="orders" border><el-table-column prop="orderNo" label="订单号"/><el-table-column prop="userId" label="用户ID"/><el-table-column prop="orderStatus" label="状态"/><el-table-column label="操作" width="260"><template #default="s"><el-button size="small" type="success" @click="pay(s.row.orderId)">支付</el-button><el-button size="small" type="primary" @click="alloc(s.row.orderId)">分配</el-button><el-button size="small" @click="show(s.row.orderId)">结果</el-button></template></el-table-column></el-table><el-dialog v-model="dialog" title="分配结果"><pre>{{ JSON.stringify(result,null,2) }}</pre></el-dialog></el-card></template>
<script setup lang="ts">
import { onMounted, ref } from 'vue';import { allocateOrder, getAllocation, listOrders, payOrder } from '../../api/order';import { ElMessage } from 'element-plus'
const orders=ref<any[]>([]);const dialog=ref(false);const result=ref<any>(null);const load=async()=>orders.value=(await listOrders()).data||[]
const pay=async(id:number)=>{await payOrder(id);ElMessage.success('支付成功');load()};const alloc=async(id:number)=>{await allocateOrder(id);ElMessage.success('分配成功');load()};const show=async(id:number)=>{result.value=(await getAllocation(id)).data;dialog.value=true}
onMounted(load)
</script>
