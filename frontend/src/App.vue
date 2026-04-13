<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="title">青年旅社管理系统（毕业设计演示）</div>
      <el-menu mode="horizontal" :router="true" :default-active="route.path">
        <el-menu-item index="/dashboard">首页</el-menu-item>
        <el-menu-item index="/room">房间管理</el-menu-item>
        <el-menu-item index="/bed">床位管理</el-menu-item>
        <el-menu-item index="/booking">订房</el-menu-item>
        <el-menu-item index="/order">订单</el-menu-item>
        <el-menu-item index="/notice">通知</el-menu-item>
      </el-menu>
    </el-header>

    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.header {
  display: flex;
  align-items: center;
  gap: 20px;
  border-bottom: 1px solid #eee;
}
.title {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}
.main {
  background: #f7f8fa;
}
</style>
