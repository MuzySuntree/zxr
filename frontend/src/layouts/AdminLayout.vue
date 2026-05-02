<template>
  <el-container class="layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="aside">
      <div class="logo">管理后台</div>

      <el-menu
          :router="true"
          :default-active="route.path"
          class="menu"
      >
        <el-menu-item index="/admin/dashboard">首页</el-menu-item>
        <el-menu-item index="/admin/banner">轮播图管理</el-menu-item>
        <el-menu-item index="/admin/activity">活动管理</el-menu-item>
        <el-menu-item index="/admin/message">留言审核</el-menu-item>
        <el-menu-item index="/admin/chat">客服会话</el-menu-item>
        <el-menu-item index="/admin/room">房间管理</el-menu-item>
        <el-menu-item index="/admin/bed">床位管理</el-menu-item>
        <el-menu-item index="/admin/order">订单管理</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="left">后台管理系统</div>

        <div class="right">
          <span class="user">
            {{ user?.realName || user?.username }}（管理员）
          </span>
          <el-button link type="danger" @click="logout">退出</el-button>
        </div>
      </el-header>

      <!-- 主体 -->
      <el-main class="main">
        <div class="content">
          <router-view />
        </div>
      </el-main>
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
/* 整体布局：只控制高度 */
.layout {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏（改为深色体系，避免发白问题） */
.aside {
  background: linear-gradient(180deg, #667eea, #764ba2);
  color: #fff;
  border: none;
}

/* logo */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 18px;
  font-weight: 700;
  color: #fff;
}

/* menu */
.menu {
  border-right: none;
  background: transparent;
}

/* 菜单项 */
:deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;

  margin: 6px 10px;
  border-radius: 10px;

  color: rgba(255,255,255,0.85);
  background: transparent;

  transition: all 0.25s;
}

/* hover */
:deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.15);
}

/* 激活 */
:deep(.el-menu-item.is-active) {
  background: rgba(255,255,255,0.25);
  color: #fff;
}

/* 右侧容器（保持默认结构，只补高度） */
.el-container {
  height: 100vh;
  overflow: hidden;
}

/* header */
.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 0 20px;

  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(10px);

  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

/* main（关键：只让这里滚动） */
.main {
  flex: 1;
  overflow: auto;

  padding: 20px;
  background: #f5f7fa;

  box-sizing: border-box; /* 关键 */
}

/* 内容区 */
.content {
  border-radius: 16px;
  padding: 20px;

  background: #fff;
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
}
</style>