import { createRouter, createWebHistory } from 'vue-router'
import { getLoginUser } from '../utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: () => import('../views/auth/LoginView.vue') },
    { path: '/register', component: () => import('../views/auth/RegisterView.vue') },
    {
      path: '/admin', component: () => import('../layouts/AdminLayout.vue'), children: [
        { path: 'dashboard', component: () => import('../views/admin/AdminDashboardView.vue') },
        { path: 'rooms', component: () => import('../views/admin/AdminRoomView.vue') },
        { path: 'beds', component: () => import('../views/admin/AdminBedView.vue') },
        { path: 'orders', component: () => import('../views/admin/AdminOrderView.vue') },
        { path: 'notices', component: () => import('../views/admin/AdminNoticeView.vue') }
      ]
    },
    {
      path: '/user', component: () => import('../layouts/UserLayout.vue'), children: [
        { path: 'dashboard', component: () => import('../views/user/UserDashboardView.vue') },
        { path: 'profile', component: () => import('../views/user/UserProfileView.vue') },
        { path: 'booking', component: () => import('../views/user/UserBookingView.vue') },
        { path: 'orders', component: () => import('../views/user/UserOrderView.vue') },
        { path: 'notices', component: () => import('../views/user/UserNoticeView.vue') }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const user = getLoginUser()
  const isAuthPage = to.path === '/login' || to.path === '/register'
  if (!user && !isAuthPage) return next('/login')
  if (user && isAuthPage) return next(user.role === 1 ? '/admin/dashboard' : '/user/dashboard')
  if (user && to.path.startsWith('/admin') && user.role !== 1) return next('/user/dashboard')
  if (user && to.path.startsWith('/user') && user.role === 1) return next('/admin/dashboard')
  next()
})

export default router
