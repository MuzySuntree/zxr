<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>房间管理</span>
        <el-button type="primary" @click="openCreate">新增房间</el-button>
      </div>
    </template>

    <el-table :data="rooms" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roomNo" label="房间号" />
      <el-table-column prop="roomName" label="房间名称" />
      <el-table-column prop="genderType" label="性别限制" />
      <el-table-column prop="price" label="价格" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑房间' : '新增房间'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="房间号"><el-input v-model="form.roomNo" /></el-form-item>
        <el-form-item label="房间名称"><el-input v-model="form.roomName" /></el-form-item>
        <el-form-item label="性别限制">
          <el-select v-model="form.genderType">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
            <el-option label="混合" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { deleteRoom, listRooms, saveRoom, updateRoom } from '../api/room'

const rooms = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({ id: null, roomNo: '', roomName: '', roomType: 1, genderType: 3, price: 80, maxBedCount: 4, status: 1 })

const loadData = async () => {
  const res = await listRooms()
  rooms.value = res.data || []
}

const openCreate = () => {
  Object.assign(form, { id: null, roomNo: '', roomName: '', roomType: 1, genderType: 3, price: 80, maxBedCount: 4, status: 1 })
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const onSubmit = async () => {
  if (!form.roomNo || !form.roomName) {
    ElMessage.warning('请填写房间号和房间名称')
    return
  }
  if (form.id) await updateRoom(form)
  else await saveRoom(form)
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const onDelete = async (id: number) => {
  await deleteRoom(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
