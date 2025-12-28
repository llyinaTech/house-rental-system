<script setup>
import { computed, ref } from 'vue'
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

        <el-sub-menu index="system">
          <template #title>
            <span class="menu-icon" aria-hidden="true">
               <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm-9 9a9 9 0 1 1 18 0H3z"/></svg>
            </span>
            <span class="menu-text">系统管理</span>
          </template>
          <el-menu-item index="/system/users">用户管理</el-menu-item>
          <el-menu-item index="/system/roles">角色管理</el-menu-item>
          <el-menu-item index="/system/notices">公告管理</el-menu-item>
          <el-menu-item index="/system/logs">日志管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="house">
          <template #title>
            <span class="menu-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 3l9 8-1.5 1.3L12 6.2 4.5 12.3 3 11l9-8zm-7 9.5V21h6v-5h2v5h6v-8.5l-7-5.6-7 5.6z"/></svg>
            </span>
            <span class="menu-text">房源管理</span>
          </template>
          <el-menu-item index="/house/regions">区域管理</el-menu-item>
          <el-menu-item index="/house/list">房源信息</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="contract">
          <template #title>
            <span class="menu-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 2h9l5 5v15a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2zm8 2v5h5"/><path d="M8 10h8M8 14h8M8 18h6"/></svg>
            </span>
            <span class="menu-text">合同管理</span>
          </template>
          <el-menu-item index="/contract/list">合同列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="finance">
          <template #title>
            <span class="menu-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 5h18v4H3V5zm0 6h18v8H3v-8zm2 2v4h6v-4H5z"/></svg>
            </span>
            <span class="menu-text">财务管理</span>
          </template>
          <el-menu-item index="/finance/bills">租金账单</el-menu-item>
          <el-menu-item index="/finance/stats">数据统计</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="service">
          <template #title>
            <span class="menu-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M21 7l-4 4-2-2 4-4a3 3 0 1 0-4.24-4.24l-4 4L8 3 4 7l4 4-7 7 3 3 7-7 4 4 4-4-2-2 4-4z"/></svg>
            </span>
            <span class="menu-text">服务管理</span>
          </template>
          <el-menu-item index="/service/repairs">报修工单</el-menu-item>
        </el-sub-menu>
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