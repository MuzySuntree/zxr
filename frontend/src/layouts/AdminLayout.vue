<template>
  <el-container style="min-height: 100vh">
    <el-aside width="220px" class="aside">
      <h3 class="logo">管理员后台</h3>
      <el-menu :router="true" :default-active="route.path">
        <el-menu-item index="/admin/dashboard">首页统计</el-menu-item>
        <el-menu-item index="/admin/rooms">房间管理</el-menu-item>
        <el-menu-item index="/admin/beds">床位管理</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        <el-menu-item index="/admin/notices">通知管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>欢迎，{{ user?.realName || user?.username }}</span>
        <el-button link type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { clearLoginUser, getLoginUser } from '../utils/auth'
const route = useRoute(); const router = useRouter(); const user = getLoginUser()
const logout = () => { clearLoginUser(); router.replace('/login') }
</script>
<style scoped>.aside{background:#001529;color:#fff}.logo{padding:16px}.header{display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #eee}</style>
