<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover" class="welcome-card">
          <div class="welcome-header">
            <div class="welcome-text">
              <h2>欢迎回来，{{ auth.user?.username || '管理员' }}！</h2>
              <p>今天是 {{ currentDate }}</p>
            </div>
            <img src="@/assets/welcome.svg" alt="welcome" class="welcome-img" v-if="false" />
          </div>
        </el-card>

        <el-card shadow="hover" class="todo-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><List /></el-icon> 待办事项</span>
            </div>
          </template>
          <el-table :data="todoItems" style="width: 100%" :show-header="false">
            <el-table-column prop="type" width="100">
              <template #default="scope">
                <el-tag :type="getTodoTagType(scope.row.type)" effect="dark">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" show-overflow-tooltip></el-table-column>
            <el-table-column prop="date" width="120" align="right"></el-table-column>
            <el-table-column width="80" align="center">
              <template #default="scope">
                <el-button type="primary" link @click="handleTodo(scope.row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="notice-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Bell /></el-icon> 系统公告</span>
              <el-button type="primary" link @click="$router.push('/system/notices')">更多</el-button>
            </div>
          </template>
          <div class="notice-list">
              <el-timeline v-if="noticeList.length">
                <el-timeline-item
                  v-for="notice in noticeList"
                  :key="notice.id"
                  :timestamp="formatDate(notice.createTime)"
                  placement="top"
                  type="primary"
                  :hollow="true"
                >
                  <div class="notice-item-content" @click="showNotice(notice)">
                  <span class="notice-title">{{ notice.title }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无公告" :image-size="60"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentNotice.title"
      width="500px"
    >
      <div class="notice-content" v-html="currentNotice.content"></div>
      <template #footer>
        <span class="dialog-footer">
          <span class="notice-meta">发布于: {{ formatDate(currentNotice.createTime) }}</span>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { List, Bell } from '@element-plus/icons-vue'
import api from '@/api/request'
import dayjs from 'dayjs'

const router = useRouter()
const auth = useAuthStore()
const currentDate = dayjs().format('YYYY年MM月DD日 dddd')

const noticeList = ref([])
const dialogVisible = ref(false)
const currentNotice = reactive({})
const todoItems = ref([])

const formatDate = (v) => {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return String(v).slice(0, 10)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const getTodoTagType = (type) => {
  const map = { '报修': 'danger', '合同': 'warning', '租金': 'success', '审批': 'primary', '房源': 'info' }
  return map[type] || 'info'
}

const loadTodos = async () => {
  todoItems.value = []
  const role = auth.user?.role
  
  // 1. 管理员: 待审核房源
  if (role === 'admin') {
     try {
       const res = await api.get('/api/house/page', { params: { current: 1, size: 5, auditStatus: 0 } })
       if (res?.code === 200 && res?.data?.records) {
         res.data.records.forEach(item => {
           todoItems.value.push({
             type: '房源',
             title: `房源 "${item.title}" 待审核`,
             date: formatDate(item.createTime),
             path: '/house/list',
             id: item.id
           })
         })
       }
     } catch {}
  }
  
  // 2. 房东: 待处理报修 + 合同到期 + 逾期账单
  if (role === 'landlord') {
     // 报修
     try {
       const res = await api.get('/api/repair-order/page', { params: { current: 1, size: 5, status: 0 } })
       if (res?.code === 200 && res?.data?.records) {
         res.data.records.forEach(item => {
           todoItems.value.push({
             type: '报修',
             title: `报修 "${item.title}" 待处理`,
             date: formatDate(item.createTime),
             path: '/service/repairs'
           })
         })
       }
     } catch {}
     // 合同 (即将到期 logic would need backend support, skipping for now or use simplified logic)
     
     // 账单 (逾期)
     try {
       const res = await api.get('/api/rent-bill/page', { params: { current: 1, size: 5, payStatus: 2 } })
       if (res?.code === 200 && res?.data?.records) {
         res.data.records.forEach(item => {
           todoItems.value.push({
             type: '租金',
             title: `账单 "${item.billNo}" 已逾期`,
             date: formatDate(item.endDate),
             path: '/finance/bills'
           })
         })
       }
     } catch {}
  }

  // 3. 租客: 待支付租金 + 合同到期
  if (role === 'tenant') {
     // 账单 (待支付)
     try {
       const res = await api.get('/api/rent-bill/page', { params: { current: 1, size: 5, payStatus: 0 } })
       if (res?.code === 200 && res?.data?.records) {
         res.data.records.forEach(item => {
           todoItems.value.push({
             type: '租金',
             title: `账单 "${item.billNo}" 待支付`,
             date: formatDate(item.endDate),
             path: '/finance/bills'
           })
         })
       }
     } catch {}
     
     // 合同 (Check status=1 for active, visually filtering near expiry if possible, else just list active)
     try {
        const res = await api.get('/api/lease-contract/page', { params: { current: 1, size: 5, status: 1 } }) // Only active
        if (res?.code === 200 && res?.data?.records) {
           const now = new Date()
           const thirtyDaysLater = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000)
           res.data.records.forEach(item => {
             const endDate = new Date(item.endDate)
             if (endDate <= thirtyDaysLater && endDate >= now) {
                todoItems.value.push({
                   type: '合同',
                   title: `合同 "${item.contractNo}" 即将到期`,
                   date: formatDate(item.endDate),
                   path: '/contract/list' // Assuming tenant view
                })
             }
           })
        }
     } catch {}
  }
}

const handleTodo = (row) => {
  if (row.path) {
    router.push(row.path)
  }
}

const getNotices = async () => {
  try {
    const res = await api.get('/api/announcements/published')
    console.log('Announcements response:', res) // Debug log
    if (res?.code === 200) {
      noticeList.value = res.data || []
    } else {
      console.warn('获取公告失败，状态码非200:', res)
    }
  } catch (e) {
    console.error('获取公告失败', e)
  }
}

const showNotice = (notice) => {
  Object.assign(currentNotice, notice)
  dialogVisible.value = true
}

onMounted(() => {
  getNotices()
  loadTodos()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #fff 0%, #f0f9eb 100%);
}
.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-text h2 {
  margin: 0 0 10px 0;
  color: #303133;
}
.welcome-text p {
  margin: 0;
  color: #909399;
}
.todo-card {
  margin-bottom: 20px;
  min-height: 300px;
}
.notice-card {
  height: 460px;
  overflow: hidden;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.notice-list {
  padding: 10px;
}
.notice-item-content {
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}
.notice-item-content:hover {
  background-color: #f5f7fa;
}
.notice-title {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}
:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #909399;
}
:deep(.el-timeline) {
  padding-left: 10px;
}
.notice-content {
  line-height: 1.6;
  min-height: 100px;
}
.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.notice-meta {
  font-size: 12px;
  color: #909399;
}
</style>
