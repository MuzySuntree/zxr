<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>床位管理</span>
        <el-button type="primary" @click="openCreate">新增床位</el-button>
      </div>
    </template>

    <el-table :data="beds" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bedNo" label="床位号" />
      <el-table-column prop="roomId" label="房间ID" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑床位' : '新增床位'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="床位号"><el-input v-model="form.bedNo" /></el-form-item>
        <el-form-item label="所属房间">
          <el-select v-model="form.roomId" filterable>
            <el-option v-for="r in rooms" :key="r.id" :label="`${r.roomNo} - ${r.roomName}`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="可用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
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
import { deleteBed, listBeds, saveBed, updateBed } from '../api/bed'
import { listRooms } from '../api/room'

const beds = ref<any[]>([])
const rooms = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({ id: null, bedNo: '', roomId: undefined, status: 1, sortNo: 0 })

const loadData = async () => {
  const [bedRes, roomRes] = await Promise.all([listBeds(), listRooms()])
  beds.value = bedRes.data || []
  rooms.value = roomRes.data || []
}

const openCreate = () => {
  Object.assign(form, { id: null, bedNo: '', roomId: undefined, status: 1, sortNo: 0 })
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const onSubmit = async () => {
  if (!form.bedNo || !form.roomId) {
    ElMessage.warning('请填写床位号并选择所属房间')
    return
  }
  if (form.id) await updateBed(form)
  else await saveBed(form)
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const onDelete = async (id: number) => {
  await deleteBed(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
</style>
