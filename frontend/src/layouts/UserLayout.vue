<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="brand">青年旅社</div>
      <el-menu mode="horizontal" :router="true" :default-active="route.path">
        <el-menu-item index="/user/home">首页</el-menu-item>
        <el-menu-item index="/user/booking">订房</el-menu-item>
        <el-menu-item index="/user/activity">活动</el-menu-item>
        <el-menu-item index="/user/order">我的订单</el-menu-item>
        <el-menu-item index="/user/notice">订房通知</el-menu-item>
        <el-menu-item index="/user/message">留言板</el-menu-item>
        <el-menu-item index="/user/chat">客服聊天</el-menu-item>
        <el-menu-item index="/user/profile">个人信息</el-menu-item>
      </el-menu>
      <div class="right">
        <span>{{ user?.realName || user?.username }}</span>
        <el-button link type="danger" @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
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
.header { display: flex; align-items: center; justify-content: space-between; gap: 12px; border-bottom: 1px solid #eee; }
.brand { width: 120px; font-size: 18px; font-weight: 700; }
.right { width: 180px; display: flex; justify-content: flex-end; gap: 12px; align-items: center; }
.main { background: #f5f7fa; }
</style>
