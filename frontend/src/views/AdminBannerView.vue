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
      <el-table-column prop="subtitle" label="副标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="imageUrl" label="图片地址" min-width="220" show-overflow-tooltip />
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
      <el-form-item label="图片地址" prop="imageUrl">
        <el-input v-model="form.imageUrl" placeholder="请输入图片 URL" />
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
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { deleteBanner, listBanners, saveBanner, updateBanner, type BannerItem } from '../api/banner'

const loading = ref(false)
const banners = ref<BannerItem[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

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
  imageUrl: [{ required: true, message: '图片地址不能为空', trigger: 'blur' }],
  sortNo: [{ required: true, message: '排序不能为空', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row: BannerItem) => {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.subtitle = row.subtitle || ''
  form.imageUrl = row.imageUrl
  form.linkUrl = row.linkUrl || ''
  form.sortNo = row.sortNo
  form.status = row.status
  dialogVisible.value = true
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
</style>
