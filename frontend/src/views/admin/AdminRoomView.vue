<template>
<el-card>
  <template #header><div style="display:flex;justify-content:space-between"><span>房间管理（含图片）</span><el-button type="primary" @click="openCreate">新增</el-button></div></template>
  <el-table :data="rooms" border>
    <el-table-column prop="roomNo" label="房间号" />
    <el-table-column prop="roomName" label="名称" />
    <el-table-column label="图片"><template #default="s"><el-image v-if="s.row.roomImage" :src="s.row.roomImage" style="width:70px;height:50px" fit="cover"/></template></el-table-column>
    <el-table-column prop="price" label="价格" />
    <el-table-column label="操作" width="200"><template #default="s"><el-button size="small" @click="openEdit(s.row)">编辑</el-button><el-button size="small" type="danger" @click="onDelete(s.row.id)">删</el-button></template></el-table-column>
  </el-table>
  <el-dialog v-model="dialog" title="房间编辑">
    <el-form :model="form" label-width="100px">
      <el-form-item label="房间号"><el-input v-model="form.roomNo"/></el-form-item>
      <el-form-item label="房间名称"><el-input v-model="form.roomName"/></el-form-item>
      <el-form-item label="价格"><el-input-number v-model="form.price" :min="0"/></el-form-item>
      <el-form-item label="图片上传">
        <el-upload :auto-upload="false" :show-file-list="false" :before-upload="onBeforeUpload">
          <el-button>选择图片</el-button>
        </el-upload>
        <el-image v-if="form.roomImage" :src="form.roomImage" style="width:120px;height:80px;margin-left:12px" fit="cover"/>
      </el-form-item>
    </el-form>
    <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="onSubmit">保存</el-button></template>
  </el-dialog>
</el-card>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { deleteRoom, listRooms, saveRoom, updateRoom } from '../../api/room'
import { ElMessage } from 'element-plus'
const rooms=ref<any[]>([]);const dialog=ref(false);const form=reactive<any>({id:null,roomNo:'',roomName:'',roomType:1,genderType:3,price:80,maxBedCount:4,status:1,roomImage:''})
const load=async()=>{rooms.value=(await listRooms()).data||[]}
const openCreate=()=>{Object.assign(form,{id:null,roomNo:'',roomName:'',roomType:1,genderType:3,price:80,maxBedCount:4,status:1,roomImage:''});dialog.value=true}
const openEdit=(r:any)=>{Object.assign(form,r);dialog.value=true}
const onDelete=async(id:number)=>{await deleteRoom(id);ElMessage.success('已删除');load()}
const onSubmit=async()=>{form.id?await updateRoom(form):await saveRoom(form);ElMessage.success('保存成功');dialog.value=false;load()}
const onBeforeUpload=(file:File)=>{const reader=new FileReader();reader.onload=()=>form.roomImage=String(reader.result);reader.readAsDataURL(file);return false}
onMounted(load)
</script>
