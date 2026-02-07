<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const isLoggedIn = computed(() => auth.isLoggedIn)
const isCollapse = ref(false)
const asideWidth = computed(() => (isCollapse.value ? '64px' : '220px'))
const toggleAside = () => { isCollapse.value = !isCollapse.value }
const onLogout = () => { auth.logout(); router.replace({ name: 'Login' }) }

const menus = computed(() => auth.menus)

onMounted(() => {
  if (isLoggedIn.value) {
    auth.fetchMenus()
  }
})
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="asideWidth" class="aside" :class="{ 'is-collapsed': isCollapse }">
      <div class="logo-container" @click="toggleAside" title="点击展开/收起">
        <h2 v-show="!isCollapse">房屋租赁系统</h2>
        <h2 v-show="isCollapse">房</h2>
      </div>
      <el-menu
        router
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
        :collapse="isCollapse">
        
        <el-menu-item index="/dashboard">
          <span class="menu-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 3h8v8H3V3zm10 0h8v6h-8V3zM3 13h6v8H3v-8zm8 6h10v-8H11v8z"/></svg>
          </span>
          <span class="menu-text" title="仪表盘">仪表盘</span>
        </el-menu-item>

        <!-- 动态菜单渲染 -->
        <template v-for="menu in menus" :key="menu.id">
          <!-- 有子菜单 (目录) -->
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="String(menu.id)">
            <template #title>
              <span class="menu-icon" v-if="menu.icon" aria-hidden="true">
                <!-- 这里简单处理，如果有icon字段可以渲染图标，暂且用默认图标或根据名字判断 -->
                 <svg viewBox="0 0 24 24" fill="currentColor"><path d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z"/></svg>
              </span>
              <span class="menu-text">{{ menu.permName }}</span>
            </template>
            <!-- 二级菜单 -->
            <template v-for="child in menu.children" :key="child.id">
              <el-menu-item :index="'/' + menu.path + '/' + child.path">
                {{ child.permName }}
              </el-menu-item>
            </template>
          </el-sub-menu>
          
          <!-- 无子菜单 (一级菜单) -->
          <el-menu-item v-else :index="'/' + menu.path">
            <span class="menu-icon" v-if="menu.icon" aria-hidden="true">
               <svg viewBox="0 0 24 24" fill="currentColor"><path d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z"/></svg>
            </span>
            <span class="menu-text">{{ menu.permName }}</span>
          </el-menu-item>
        </template>

      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown v-if="isLoggedIn">
            <span class="user-dropdown">
              {{ auth.user?.name || '用户' }}
              ▼
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item @click="onLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container { height: 100%; }
.aside { background-color: #304156; color: #fff; transition: width 0.3s; }
.logo-container { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; cursor: pointer; user-select: none; }
.logo-container:hover { background-color: #263445; }
.is-collapsed .logo-container h2 { display: block; }
.header { background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; }
.user-dropdown { cursor: pointer; display: flex; align-items: center; }
.el-menu-vertical { border-right: none; }
.menu-icon { display:inline-flex; width:18px; height:18px; align-items:center; justify-content:center; margin-right:12px; color:#fff; }
.menu-icon svg { width:18px; height:18px; }
.el-menu--collapse .menu-text { display:none; }
.el-menu--collapse .menu-icon { margin-right:0; }
.toggle-btn { margin-right:8px; }
.breadcrumb { margin-left:8px; }
</style>