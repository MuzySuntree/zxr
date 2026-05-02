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
      <el-table-column label="房间图" width="160">
        <template #default="scope">
          <el-image v-if="scope.row.roomImage" :src="scope.row.roomImage" fit="cover" style="width:120px;height:70px;border-radius:6px" />
          <span v-else>暂无</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑房间' : '新增房间'" width="640px">
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
        <el-form-item label="房间图片">
          <el-upload
            class="uploader"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="onSelectImage"
            accept="image/*"
          >
            <img v-if="form.roomImage" :src="form.roomImage" class="preview" />
            <el-button v-else type="primary">选择图片</el-button>
          </el-upload>
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
import { deleteRoom, listRooms, saveRoom, updateRoom, uploadRoomImage } from '../api/room'

const rooms = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({ id: null, roomNo: '', roomName: '', roomType: 1, genderType: 3, price: 80, maxBedCount: 4, status: 1, roomImage: '' })

const loadData = async () => {
  const res = await listRooms()
  rooms.value = res.data || []
}

const openCreate = () => {
  Object.assign(form, { id: null, roomNo: '', roomName: '', roomType: 1, genderType: 3, price: 80, maxBedCount: 4, status: 1, roomImage: '' })
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const onSelectImage = async (uploadFile: any) => {
  if (!uploadFile.raw) return
  const res = await uploadRoomImage(uploadFile.raw)
  form.roomImage = res.data
  ElMessage.success('图片上传成功')
}

const onSubmit = async () => {
  if (!form.roomNo || !form.roomName) return ElMessage.warning('请填写房间号和房间名称')
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
.preview { width: 180px; height: 110px; object-fit: cover; border-radius: 6px; border: 1px solid #eee; }
</style>
