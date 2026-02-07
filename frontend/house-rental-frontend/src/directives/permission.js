import { useAuthStore } from '@/stores/auth'

export default {
  install(app) {
    app.directive('hasPerm', {
      mounted(el, binding) {
        const { value } = binding
        const authStore = useAuthStore()
        const permissions = authStore.permissions
        const roles = authStore.user?.roles || []

        if (value && (value instanceof Array || typeof value === 'string')) {
          const requiredPerms = Array.isArray(value) ? value : [value]
          
          const hasPermission = permissions.some(perm => {
            return requiredPerms.includes(perm) || perm === '*:*:*'
          }) || roles.includes('admin')

          if (!hasPermission) {
            el.parentNode && el.parentNode.removeChild(el)
          }
        } else {
          throw new Error(`need permissions! Like v-hasPerm="['house:list']" or v-hasPerm="'house:list'"`)
        }
      }
    })
  }
}
