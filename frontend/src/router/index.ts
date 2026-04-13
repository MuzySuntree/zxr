import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  { path: '/dashboard', component: () => import('../views/DashboardView.vue') },
  { path: '/room', component: () => import('../views/RoomView.vue') },
  { path: '/bed', component: () => import('../views/BedView.vue') },
  { path: '/booking', component: () => import('../views/BookingView.vue') },
  { path: '/order', component: () => import('../views/OrderView.vue') },
  { path: '/notice', component: () => import('../views/NoticeView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
