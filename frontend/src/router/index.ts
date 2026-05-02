import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { getLoginUser } from '../utils/auth'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('../views/admin/AdminDashboardView.vue') },
      { path: 'banner', component: () => import('../views/AdminBannerView.vue') },
      { path: 'room', component: () => import('../views/RoomView.vue') },
      { path: 'bed', component: () => import('../views/BedView.vue') },
      { path: 'order', component: () => import('../views/OrderView.vue') }
    ]
  },
  {
    path: '/user',
    component: () => import('../layouts/UserLayout.vue'),
    children: [
      { path: '', redirect: '/user/home' },
      { path: 'home', component: () => import('../views/user/UserHomeView.vue') },
      { path: 'booking', component: () => import('../views/user/UserBookingView.vue') },
      { path: 'activity', component: () => import('../views/user/UserActivityView.vue') },
      { path: 'order', component: () => import('../views/user/UserOrderView.vue') },
      { path: 'notice', component: () => import('../views/user/UserNoticeView.vue') },
      { path: 'message', component: () => import('../views/user/UserMessageBoardView.vue') },
      { path: 'chat', component: () => import('../views/user/UserChatView.vue') },
      { path: 'payment', component: () => import('../views/user/UserPaymentView.vue') },
      { path: 'profile', component: () => import('../views/user/UserProfileView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _, next) => {
  const user = getLoginUser()
  if (['/login', '/register'].includes(to.path)) return next()
  if (!user) return next('/login')
  if (to.path.startsWith('/admin') && user.role !== 1) return next('/user/home')
  if (to.path.startsWith('/user') && user.role === 1) return next('/admin/dashboard')
  next()
})

export default router
