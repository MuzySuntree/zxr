<template><el-card><template #header>我的订单</template><el-table :data="orders" border><el-table-column prop="orderNo" label="订单号"/><el-table-column prop="checkInDate" label="入住"/><el-table-column prop="checkOutDate" label="退房"/><el-table-column prop="orderStatus" label="状态"/><el-table-column prop="roomNo" label="房间"/><el-table-column prop="bedNo" label="床位"/><el-table-column label="操作" width="280"><template #default="s"><el-button size="small" type="success" @click="pay(s.row.orderId)">支付</el-button><el-button size="small" @click="view(s.row.orderId)">分配结果</el-button><el-button size="small" type="danger" @click="cancel(s.row.orderId)">取消</el-button></template></el-table-column></el-table><el-dialog v-model="dialog" title="分配结果"><pre>{{ JSON.stringify(result,null,2) }}</pre></el-dialog></el-card></template>
<script setup lang="ts">
import { onMounted, ref } from 'vue';import { cancelOrder, getAllocation, listOrders, payOrder } from '../../api/order';import { getLoginUser } from '../../utils/auth';import { ElMessage } from 'element-plus'
const user=getLoginUser()!;const orders=ref<any[]>([]);const dialog=ref(false);const result=ref<any>(null)
const load=async()=>{const all=(await listOrders()).data||[];orders.value=all.filter((o:any)=>o.userId===user.userId)}
const pay=async(id:number)=>{await payOrder(id);ElMessage.success('支付成功');load()}
const view=async(id:number)=>{result.value=(await getAllocation(id)).data;dialog.value=true}
const cancel=async(id:number)=>{await cancelOrder(id);ElMessage.success('已取消');load()}
onMounted(load)
</script>
