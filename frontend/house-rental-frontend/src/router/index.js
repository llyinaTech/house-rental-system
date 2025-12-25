import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const Layout = () => import('@/layouts/BasicLayout.vue')

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'listings', name: 'Listings', component: () => import('@/views/Listings.vue'), meta: { requiresAuth: true } },
      { path: 'tenants', name: 'Tenants', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'contracts', name: 'Contracts', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'payments', name: 'Payments', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'repairs', name: 'Repairs', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'reports', name: 'Reports', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})      

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.token) {
    return { name: 'Login' }
  }
})

export default router