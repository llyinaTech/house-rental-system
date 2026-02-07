import { defineStore } from 'pinia'
import api from '@/api/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user')) || null,
    permissions: JSON.parse(localStorage.getItem('permissions')) || [],
    menus: [], // 动态菜单
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.user?.role || '',
    hasPermission: (state) => (perm) => {
      const roles = state.user?.roles || []
      return state.permissions.includes(perm) || state.permissions.includes('*:*:*') || roles.includes('admin')
    }
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(user, permissions = []) {
      this.user = user
      this.permissions = permissions
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('permissions', JSON.stringify(permissions))
    },
    async fetchMenus() {
      try {
        const res = await api.get('/api/user/menus')
        this.menus = res.data
        return res.data
      } catch (error) {
        console.error('Fetch menus failed:', error)
        return []
      }
    },
    logout() {
      this.token = ''
      this.user = null
      this.permissions = []
      this.menus = []
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('permissions')
    },
  },
})