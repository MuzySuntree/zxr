<template>
  <el-card>
    <template #header>
      <div class="header-row">
        <span class="title">留言板</span>
        <el-button type="primary" @click="openDialog">发布留言</el-button>
      </div>
    </template>

    <el-empty v-if="!messages.length" description="暂无已发布留言" />
    <div v-else class="message-list" v-loading="loading">
      <el-card v-for="item in messages" :key="item.id" shadow="never" class="message-item">
        <div class="message-top">
          <el-tag size="small">{{ categoryText(item.category) }}</el-tag>
          <span class="time">{{ formatDateTime(item.createTime) }}</span>
        </div>
        <h4>{{ item.title }}</h4>
        <p class="content">{{ item.content }}</p>
        <p class="contact" v-if="item.contactInfo">联系方式：{{ item.contactInfo }}</p>
      </el-card>
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" title="发布留言" width="560px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择分类" class="full-width">
          <el-option label="失物招领" :value="1" />
          <el-option label="寻找搭子" :value="2" />
          <el-option label="入住交流" :value="3" />
          <el-option label="建议反馈" :value="4" />
          <el-option label="其他" :value="9" />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" maxlength="200" show-word-limit placeholder="请输入标题" />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="5" maxlength="4000" show-word-limit placeholder="请输入留言内容" />
      </el-form-item>
      <el-form-item label="联系方式">
        <el-input v-model="form.contactInfo" placeholder="选填，如手机号/微信" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible=false">取消</el-button>
      <el-button type="primary" @click="onSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { listPublishedMessages, saveMessage, type MessageItem } from '../../api/message'
import { getLoginUser } from '../../utils/auth'

const loginUser = getLoginUser()
const userId = Number(loginUser?.userId || 0)
const messages = ref<MessageItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  category: null as number | null,
  title: '',
  content: '',
  contactInfo: ''
})

const rules: FormRules = {
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }, { max: 200, message: '标题最多200字', trigger: 'blur' }],
  content: [{ required: true, message: '内容不能为空', trigger: 'blur' }, { max: 4000, message: '内容最多4000字', trigger: 'blur' }]
}

const categoryText = (category: number) => ({ 1: '失物招领', 2: '寻找搭子', 3: '入住交流', 4: '建议反馈', 9: '其他' }[category] || '其他')
const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 16) : '-')

const loadMessages = async () => {
  loading.value = true
  try {
    const res = await listPublishedMessages()
    messages.value = res.data || []
  } catch (e: any) {
    ElMessage.error(e?.message || '留言加载失败')
  } finally {
    loading.value = false
  }
}

const openDialog = () => {
  if (!userId) {
    ElMessage.error('未获取到登录用户，请重新登录')
    return
  }
  dialogVisible.value = true
}

const onSubmit = async () => {
  await formRef.value?.validate()
  await saveMessage({
    userId,
    category: form.category as number,
    title: form.title.trim(),
    content: form.content.trim(),
    contactInfo: form.contactInfo.trim()
  })
  ElMessage.success('提交成功，等待管理员审核')
  dialogVisible.value = false
  form.category = null
  form.title = ''
  form.content = ''
  form.contactInfo = ''
  await loadMessages()
}

onMounted(loadMessages)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: 600; }
.message-list { display: grid; gap: 12px; }
.message-item h4 { margin: 8px 0; }
.message-top { display: flex; justify-content: space-between; align-items: center; }
.time { font-size: 12px; color: #999; }
.content { margin: 0; color: #606266; }
.contact { margin: 8px 0 0; color: #409eff; }
.full-width { width: 100%; }
</style>
