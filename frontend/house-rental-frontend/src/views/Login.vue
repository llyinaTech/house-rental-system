<template>
  <div class="login-wrap">
    <div class="login-main">
      <div class="banner">
        <div class="banner-title">房屋租赁管理系统</div>
        <div class="banner-desc">高效管理房源、租客、合同与报修</div>
      </div>
      <div class="form-panel">
        <div class="brand">
          <div class="name">后台登录</div>
        </div>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" size="large" placeholder="用户名" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" size="large" type="password" placeholder="密码" show-password />
          </el-form-item>
          <div class="form-extra">
            <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
            <a class="link" href="javascript:void(0)">忘记密码</a>
          </div>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-button">登录</el-button>
          <div class="sub-links">
            还没有账号？<a class="link" href="javascript:void(0)">注册</a>
          </div>
        </el-form>
      </div>
    </div>
    <div class="login-footer">© 2025 房屋租赁系统</div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import api from '@/api/request'

const router = useRouter()
const auth = useAuthStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: true
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await api.post('/auth/login', {
        username: loginForm.username,
        password: loginForm.password,
      })
      if (res?.code === 200 && res?.data) {
        const { token, id, username, roles, permissions } = res.data
        auth.setToken(token)
        auth.setUser({
          id,
          username,
          role: Array.isArray(roles) && roles.length ? roles[0] : '',
          roles: Array.isArray(roles) ? roles : [],
        }, permissions || [])
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error(res?.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: radial-gradient(1200px 600px at 20% 10%, #2d3a4b 0%, #1f2d3d 35%, #192433 100%);
  width: 100%;
}
.login-main {
  width: 980px;
  max-width: 92vw;
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 16px 40px rgba(0,0,0,0.35);
}
.banner {
  padding: 48px;
  color: #fff;
  background: linear-gradient(135deg, #2b3b54 0%, #3a4b68 100%);
}
.banner-title {
  font-size: 28px;
  font-weight: 700;
}
.banner-desc { margin-top: 8px; opacity: 0.9; }
.banner-list { margin-top: 20px; line-height: 1.9; opacity: 0.95; }
.form-panel {
  padding: 40px 48px;
  background: #fff;
}
.brand { display: flex; align-items: baseline; justify-content: space-between; margin-bottom: 16px; }
.name { font-size: 22px; font-weight: 600; color: #303133; }
.form-extra { display: flex; justify-content: space-between; align-items: center; margin: 4px 0 16px; }
.login-button { width: 100%; margin-top: 8px; }
.sub-links { text-align: center; margin-top: 12px; color: #909399; }
.link { color: #409EFF; text-decoration: none; }
.login-footer { margin-top: 24px; color: #c0c4cc; font-size: 12px; }
@media (max-width: 960px) {
  .login-main { grid-template-columns: 1fr; }
  .banner { display: none; }
}
</style>
