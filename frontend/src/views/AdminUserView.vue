<template>
  <el-card>
    <template #header><div class="toolbar"><span>用户管理</span></div></template>

    <el-table :data="users" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column label="性别" width="80"><template #default="scope">{{ genderText(scope.row.gender) }}</template></el-table-column>
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="角色" width="100"><template #default="scope">{{ roleText(scope.row.role) }}</template></el-table-column>
      <el-table-column label="状态" width="90"><template #default="scope"><el-tag :type="scope.row.status===1?'success':'danger'">{{ scope.row.status===1?'启用':'禁用' }}</el-tag></template></el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录" width="170"><template #default="scope">{{ formatDateTime(scope.row.lastLoginTime) }}</template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170"><template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="onView(scope.row)">详情</el-button>
          <el-button link type="primary" @click="onEdit(scope.row)">编辑</el-button>
          <el-button link :type="scope.row.status===1?'warning':'success'" @click="onToggleStatus(scope.row)">{{ scope.row.status===1?'禁用':'启用' }}</el-button>
          <el-button link type="danger" @click="onDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="detailVisible" title="用户详情" width="620px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="ID">{{ detailUser?.id }}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{ detailUser?.username }}</el-descriptions-item>
      <el-descriptions-item label="真实姓名">{{ detailUser?.realName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ genderText(detailUser?.gender) }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailUser?.phone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ roleText(detailUser?.role) }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detailUser?.status===1?'启用':'禁用' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ detailUser?.email || '-' }}</el-descriptions-item>
      <el-descriptions-item label="身份证">{{ detailUser?.idCard || '-' }}</el-descriptions-item>
      <el-descriptions-item label="紧急联系人">{{ detailUser?.emergencyContact || '-' }}</el-descriptions-item>
      <el-descriptions-item label="紧急电话">{{ detailUser?.emergencyPhone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="最后登录">{{ formatDateTime(detailUser?.lastLoginTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDateTime(detailUser?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailUser?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>

  <el-dialog v-model="editVisible" title="编辑用户" width="620px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="真实姓名" prop="realName"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="性别" prop="gender"><el-select v-model="form.gender" class="full"><el-option :value="1" label="男" /><el-option :value="2" label="女" /></el-select></el-form-item>
      <el-form-item label="角色" prop="role"><el-select v-model="form.role" class="full"><el-option :value="1" label="管理员" /><el-option :value="2" label="普通用户" /></el-select></el-form-item>
      <el-form-item label="状态" prop="status"><el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="身份证"><el-input v-model="form.idCard" /></el-form-item>
      <el-form-item label="紧急联系人"><el-input v-model="form.emergencyContact" /></el-form-item>
      <el-form-item label="紧急电话"><el-input v-model="form.emergencyPhone" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
    </el-form>
    <template #footer><el-button @click="editVisible=false">取消</el-button><el-button type="primary" @click="onSubmit">保存</el-button></template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { deleteUser, getUserById, listUsers, updateUser, type SysUserItem } from '../api/user'

const users = ref<SysUserItem[]>([])
const detailUser = ref<SysUserItem>()
const loading = ref(false)
const detailVisible = ref(false)
const editVisible = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<Partial<SysUserItem>>({ id: undefined, realName: '', phone: '', gender: 1, role: 2, status: 1, email: '', idCard: '', emergencyContact: '', emergencyPhone: '', remark: '' })

const rules: FormRules = {
  realName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }, { pattern: /^[\u4e00-\u9fa5]{2,10}$/, message: '真实姓名必须为2-10位中文', trigger: 'blur' }],
  phone: [{ required: true, message: '手机号不能为空', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  gender: [{ validator: (_, v, cb) => ([1, 2].includes(v) ? cb() : cb(new Error('性别只能为男或女'))), trigger: 'change' }],
  role: [{ validator: (_, v, cb) => ([1, 2].includes(v) ? cb() : cb(new Error('角色只能为管理员或普通用户'))), trigger: 'change' }],
  status: [{ validator: (_, v, cb) => ([0, 1].includes(v) ? cb() : cb(new Error('状态只能为启用或禁用'))), trigger: 'change' }]
}

const genderText = (g?: number) => ({ 1: '男', 2: '女', 0: '未知' }[g ?? 0] || '未知')
const roleText = (r?: number) => ({ 1: '管理员', 2: '普通用户' }[r ?? 2] || '普通用户')
const formatDateTime = (v?: string) => (v ? v.replace('T', ' ').slice(0, 19) : '-')

const loadData = async () => { loading.value = true; try { const res = await listUsers(); users.value = res.data || [] } finally { loading.value = false } }

const onView = async (row: SysUserItem) => { const res = await getUserById(row.id); detailUser.value = res.data; detailVisible.value = true }
const onEdit = (row: SysUserItem) => { Object.assign(form, row); editVisible.value = true }

const onToggleStatus = async (row: SysUserItem) => {
  const nextStatus = row.status === 1 ? 0 : 1
  await updateUser({ id: row.id, status: nextStatus })
  ElMessage.success(nextStatus === 1 ? '用户已启用' : '用户已禁用')
  await loadData()
}

const onDelete = async (id: number) => {
  await ElMessageBox.confirm('确认删除该用户吗？', '提示', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('删除成功')
  await loadData()
}

const onSubmit = async () => {
  await formRef.value?.validate()
  await updateUser({ ...form })
  ElMessage.success('保存成功')
  editVisible.value = false
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.full { width: 100%; }
</style>
