<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>活动管理</span>
        <el-button type="primary" @click="openCreateDialog">新增活动</el-button>
      </div>
    </template>

    <el-table :data="activities" border v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column label="封面" width="120">
        <template #default="scope">
          <el-image v-if="scope.row.coverImage" :src="scope.row.coverImage" fit="cover" class="thumb" :preview-src-list="[scope.row.coverImage]" />
          <div v-else class="thumb placeholder">无图</div>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="activityTime" label="活动时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.activityTime) }}</template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="140" />
      <el-table-column label="人数" width="120">
        <template #default="scope">{{ scope.row.joinedPeople || 0 }}/{{ scope.row.maxPeople || 0 }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="recommend" label="推荐" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.recommend === 1 ? 'warning' : 'info'">{{ scope.row.recommend === 1 ? '已推荐' : '未推荐' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortNo" label="排序" width="90" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button link type="warning" @click="onToggleRecommend(scope.row)">{{ scope.row.recommend === 1 ? '取消推荐' : '设为推荐' }}</el-button>
          <el-button link type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '新增活动'" width="700px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="标题" prop="title"><el-input v-model="form.title" maxlength="100" /></el-form-item>
      <el-form-item label="封面图">
        <el-upload ref="uploadRef" :http-request="doUpload" :limit="1" :show-file-list="false" accept=".jpg,.jpeg,.png,.webp" :before-upload="beforeUpload">
          <el-button type="primary" :loading="uploading">上传封面</el-button>
        </el-upload>
        <div class="upload-tip">仅支持 jpg/jpeg/png/webp，大小不超过 5MB</div>
        <div v-if="form.coverImage" class="preview-wrap">
          <el-image :src="form.coverImage" fit="cover" class="preview" />
          <el-button link type="danger" @click="removeCover">删除并重新上传</el-button>
        </div>
      </el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" maxlength="1000" show-word-limit /></el-form-item>
      <el-form-item label="活动时间" prop="activityTime">
        <el-date-picker v-model="form.activityTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择活动时间" class="full-width" />
      </el-form-item>
      <el-form-item label="地点" prop="location"><el-input v-model="form.location" maxlength="200" /></el-form-item>
      <el-form-item label="最大人数" prop="maxPeople"><el-input-number v-model="form.maxPeople" :min="0" class="full-width" /></el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">未发布</el-radio><el-radio :value="1">报名中</el-radio><el-radio :value="2">已结束</el-radio><el-radio :value="3">已取消</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="推荐" prop="recommend">
        <el-radio-group v-model="form.recommend"><el-radio :value="0">否</el-radio><el-radio :value="1">是</el-radio></el-radio-group>
      </el-form-item>
      <el-form-item label="排序" prop="sortNo"><el-input-number v-model="form.sortNo" :min="0" class="full-width" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible=false">取消</el-button>
      <el-button type="primary" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadInstance, type UploadProps, type UploadRequestOptions } from 'element-plus'
import { deleteActivity, listActivities, saveActivity, setActivityRecommend, updateActivity, uploadActivityImage, type ActivityItem, type ActivitySaveDTO } from '../api/activity'

const loading = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const activities = ref<ActivityItem[]>([])
const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()

const form = reactive<ActivitySaveDTO>({
  id: undefined,
  title: '',
  coverImage: '',
  description: '',
  activityTime: '',
  location: '',
  maxPeople: 0,
  joinedPeople: 0,
  status: 0,
  recommend: 0,
  sortNo: 0
})

const rules: FormRules<ActivitySaveDTO> = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  activityTime: [{ required: true, message: '活动时间不能为空', trigger: 'change' }],
  location: [{ required: true, message: '地点不能为空', trigger: 'blur' }],
  maxPeople: [{ required: true, message: '最大人数不能为空', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  recommend: [{ required: true, message: '请选择是否推荐', trigger: 'change' }],
  sortNo: [{ required: true, message: '排序不能为空', trigger: 'change' }]
}

const statusText = (status: number) => ({ 0: '未发布', 1: '报名中', 2: '已结束', 3: '已取消' }[status] || '未知')
const statusTagType = (status: number) => ({ 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }[status] as any)
const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 16) : '-')

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const allowTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!allowTypes.includes(rawFile.type)) {
    ElMessage.error('仅支持 jpg/jpeg/png/webp 格式图片')
    return false
  }
  if (rawFile.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const doUpload = async (options: UploadRequestOptions) => {
  uploading.value = true
  try {
    const res = await uploadActivityImage(options.file as File)
    form.coverImage = res.data || ''
    ElMessage.success('上传成功')
    uploadRef.value?.clearFiles()
  } catch (e: any) {
    ElMessage.error(e?.message || '上传失败')
    uploadRef.value?.clearFiles()
  } finally {
    uploading.value = false
  }
}

const removeCover = () => {
  form.coverImage = ''
  uploadRef.value?.clearFiles()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listActivities()
    activities.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.title = ''
  form.coverImage = ''
  form.description = ''
  form.activityTime = ''
  form.location = ''
  form.maxPeople = 0
  form.joinedPeople = 0
  form.status = 0
  form.recommend = 0
  form.sortNo = 0
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  uploadRef.value?.clearFiles()
}

const openEditDialog = (row: ActivityItem) => {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.coverImage = row.coverImage || ''
  form.description = row.description || ''
  form.activityTime = row.activityTime || ''
  form.location = row.location || ''
  form.maxPeople = Number(row.maxPeople || 0)
  form.joinedPeople = Number(row.joinedPeople || 0)
  form.status = row.status
  form.recommend = Number(row.recommend || 0)
  form.sortNo = Number(row.sortNo || 0)
  dialogVisible.value = true
  uploadRef.value?.clearFiles()
}

const onSubmit = async () => {
  await formRef.value?.validate()
  if (Number(form.maxPeople) < 0) {
    ElMessage.error('最大人数不能小于 0')
    return
  }
  const payload: ActivitySaveDTO = {
    id: form.id,
    title: form.title.trim(),
    coverImage: form.coverImage?.trim(),
    description: form.description?.trim(),
    activityTime: form.activityTime,
    location: form.location.trim(),
    maxPeople: Number(form.maxPeople),
    joinedPeople: Number(form.joinedPeople || 0),
    status: form.status,
    recommend: form.recommend,
    sortNo: Number(form.sortNo)
  }
  if (isEdit.value) {
    await updateActivity(payload)
    ElMessage.success('更新成功')
  } else {
    await saveActivity(payload)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadData()
}

const onDelete = async (id: number) => {
  await ElMessageBox.confirm('确认删除该活动吗？', '提示', { type: 'warning' })
  await deleteActivity(id)
  ElMessage.success('删除成功')
  await loadData()
}

const onToggleRecommend = async (row: ActivityItem) => {
  const target = row.recommend === 1 ? 0 : 1
  await setActivityRecommend(row.id, target)
  ElMessage.success(target === 1 ? '已设为推荐' : '已取消推荐')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.full-width { width: 100%; }
.thumb { width: 80px; height: 50px; border-radius: 4px; display: block; }
.placeholder { background: #f2f3f5; color: #909399; display: flex; align-items: center; justify-content: center; }
.upload-tip { margin-top: 8px; font-size: 12px; color: #909399; }
.preview-wrap { margin-top: 8px; display: flex; flex-direction: column; align-items: flex-start; gap: 8px; }
.preview { width: 180px; height: 100px; border-radius: 6px; border: 1px solid #ebeef5; }
</style>
