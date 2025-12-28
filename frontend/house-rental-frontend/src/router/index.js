import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const Layout = () => import('@/layouts/BasicLayout.vue')

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { requiresAuth: true, title: '首页' } },
      
      // System Management
      { path: 'system/users', name: 'UserManagement', component: () => import('@/views/system/UserManagement.vue'), meta: { requiresAuth: true, title: '用户管理' } },
      { path: 'system/roles', name: 'RoleManagement', component: () => import('@/views/system/RoleManagement.vue'), meta: { requiresAuth: true, title: '角色管理' } },
      { path: 'system/logs', name: 'LogManagement', component: () => import('@/views/system/LogManagement.vue'), meta: { requiresAuth: true, title: '日志管理' } },
      { path: 'system/notices', name: 'NoticeManagement', component: () => import('@/views/system/NoticeManagement.vue'), meta: { requiresAuth: true, title: '公告管理' } },
      
      // House Management
      { path: 'house/list', name: 'HouseInfo', component: () => import('@/views/Listings.vue'), meta: { requiresAuth: true, title: '房源信息' } },
      
      // Contract Management
      { path: 'contract/list', name: 'ContractList', component: () => import('@/views/contract/ContractList.vue'), meta: { requiresAuth: true, title: '合同列表' } },
      
      // Finance Management
      { path: 'finance/bills', name: 'RentBills', component: () => import('@/views/finance/RentBills.vue'), meta: { requiresAuth: true, title: '租金账单' } },
      { path: 'finance/stats', name: 'Statistics', component: () => import('@/views/finance/Statistics.vue'), meta: { requiresAuth: true, title: '财务统计' } },
      
      // Service Management
      { path: 'service/repairs', name: 'RepairOrders', component: () => import('@/views/service/RepairOrders.vue'), meta: { requiresAuth: true, title: '报修工单' } },
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
