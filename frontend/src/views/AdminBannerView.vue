<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>轮播图管理</span>
        <el-button type="primary" @click="openCreateDialog">新增轮播图</el-button>
      </div>
    </template>

    <el-table :data="banners" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column label="图片" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.imageUrl"
            :src="scope.row.imageUrl"
            fit="cover"
            class="thumb"
            :preview-src-list="[scope.row.imageUrl]"
            preview-teleported />
          <div v-else class="thumb placeholder">无图</div>
        </template>
      </el-table-column>
      <el-table-column prop="subtitle" label="副标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="linkUrl" label="跳转链接" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sortNo" label="排序" width="90" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="620px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" maxlength="100" placeholder="请输入标题" />
      </el-form-item>
      <el-form-item label="副标题">
        <el-input v-model="form.subtitle" maxlength="200" placeholder="请输入副标题" />
      </el-form-item>
      <el-form-item label="轮播图" prop="imageUrl">
        <el-upload
          :http-request="doUpload"
          :limit="1"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.webp"
          :before-upload="beforeUpload"
          ref="uploadRef">
          <el-button type="primary" :loading="uploading">上传图片</el-button>
        </el-upload>
        <div class="upload-tip">仅支持 jpg/jpeg/png/webp，大小不超过 5MB</div>
        <div v-if="form.imageUrl" class="preview-wrap">
          <el-image :src="form.imageUrl" fit="cover" class="preview" />
          <el-button link type="danger" @click="removeImage">删除并重新上传</el-button>
        </div>
      </el-form-item>
      <el-form-item label="跳转链接">
        <el-input v-model="form.linkUrl" placeholder="请输入跳转链接（选填）" />
      </el-form-item>
      <el-form-item label="排序" prop="sortNo">
        <el-input-number v-model="form.sortNo" :min="0" :step="1" class="full-width" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadInstance, type UploadProps, type UploadRequestOptions } from 'element-plus'
import { deleteBanner, listBanners, saveBanner, updateBanner, uploadBannerImage, type BannerItem } from '../api/banner'

const loading = ref(false)
const banners = ref<BannerItem[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const uploading = ref(false)

const form = reactive<BannerItem>({
  id: undefined,
  title: '',
  subtitle: '',
  imageUrl: '',
  linkUrl: '',
  sortNo: 0,
  status: 1
})

const rules: FormRules<BannerItem> = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传轮播图图片', trigger: 'change' }],
  sortNo: [{ required: true, message: '排序不能为空', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

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
    const res = await uploadBannerImage(options.file as File)
    form.imageUrl = res.data || ''
    ElMessage.success('上传成功')
    formRef.value?.validateField('imageUrl')
    uploadRef.value?.clearFiles()
  } catch (e: any) {
    ElMessage.error(e?.message || '上传失败')
    uploadRef.value?.clearFiles()
  } finally {
    uploading.value = false
  }
}

const removeImage = () => {
  form.imageUrl = ''
  uploadRef.value?.clearFiles()
  formRef.value?.validateField('imageUrl')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listBanners()
    banners.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.title = ''
  form.subtitle = ''
  form.imageUrl = ''
  form.linkUrl = ''
  form.sortNo = 0
  form.status = 1
}

const openCreateDialog = () => {
  uploadRef.value?.clearFiles()
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  uploadRef.value?.clearFiles()
}

const openEditDialog = (row: BannerItem) => {
  uploadRef.value?.clearFiles()
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.subtitle = row.subtitle || ''
  form.imageUrl = row.imageUrl || ''
  form.linkUrl = row.linkUrl || ''
  form.sortNo = row.sortNo
  form.status = row.status
  dialogVisible.value = true
  uploadRef.value?.clearFiles()
}

const onSubmit = async () => {
  await formRef.value?.validate()
  const payload: BannerItem = {
    id: form.id,
    title: form.title.trim(),
    subtitle: form.subtitle?.trim(),
    imageUrl: form.imageUrl.trim(),
    linkUrl: form.linkUrl?.trim(),
    sortNo: Number(form.sortNo),
    status: form.status
  }
  if (isEdit.value) {
    await updateBanner(payload)
    ElMessage.success('更新成功')
  } else {
    await saveBanner(payload)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadData()
}

const onDelete = async (id?: number) => {
  if (!id) return
  await ElMessageBox.confirm('确认删除该轮播图吗？', '提示', { type: 'warning' })
  await deleteBanner(id)
  ElMessage.success('删除成功')
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
