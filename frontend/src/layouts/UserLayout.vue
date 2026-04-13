<template>
  <el-container style="min-height:100vh">
    <el-header>
      <el-menu mode="horizontal" :router="true" :default-active="route.path">
        <el-menu-item index="/user/dashboard">首页</el-menu-item>
        <el-menu-item index="/user/profile">个人信息</el-menu-item>
        <el-menu-item index="/user/booking">订房</el-menu-item>
        <el-menu-item index="/user/orders">我的订单</el-menu-item>
        <el-menu-item index="/user/notices">通知</el-menu-item>
      </el-menu>
    </el-header>
    <el-main><router-view /></el-main>
  </el-container>
</template>
<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>
