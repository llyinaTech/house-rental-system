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
      
      // System Management
      { path: 'system/users', name: 'UserManagement', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'system/roles', name: 'RoleManagement', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'system/logs', name: 'LogManagement', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      
      // House Management
      { path: 'house/regions', name: 'RegionManagement', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'house/list', name: 'HouseInfo', component: () => import('@/views/Listings.vue'), meta: { requiresAuth: true } },
      
      // Contract Management
      { path: 'contract/list', name: 'ContractList', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      
      // Finance Management
      { path: 'finance/bills', name: 'RentBills', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      { path: 'finance/stats', name: 'Statistics', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
      
      // Service Management
      { path: 'service/repairs', name: 'RepairOrders', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true } },
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