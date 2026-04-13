<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">管理后台</div>
      <el-menu :router="true" :default-active="route.path">
        <el-menu-item index="/admin/dashboard">首页</el-menu-item>
        <el-menu-item index="/admin/room">房间管理</el-menu-item>
        <el-menu-item index="/admin/bed">床位管理</el-menu-item>
        <el-menu-item index="/admin/order">订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ user?.realName || user?.username }}（管理员）</span>
        <el-button link type="danger" @click="logout">退出登录</el-button>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { clearLoginUser, getLoginUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const user = getLoginUser()

const logout = () => {
  clearLoginUser()
  router.replace('/login')
}
</script>

<style scoped>
.layout { min-height: 100vh; }
.aside { border-right: 1px solid #eee; background: #fff; }
.logo { padding: 18px 16px; font-weight: 700; }
.header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; }
.main { background: #f5f7fa; }
</style>
