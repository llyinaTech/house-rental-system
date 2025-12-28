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
              <template #default>
                <el-button type="primary" link>处理</el-button>
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
                :timestamp="notice.createTime"
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
          <span class="notice-meta">发布于: {{ currentNotice.createTime }}</span>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { List, Bell } from '@element-plus/icons-vue'
import api from '@/api/request'
import dayjs from 'dayjs'

const auth = useAuthStore()
const currentDate = dayjs().format('YYYY年MM月DD日 dddd')

const noticeList = ref([])
const dialogVisible = ref(false)
const currentNotice = reactive({})

// 待办事项模拟数据
const todoItems = reactive([
  { type: '报修', title: '201室水管漏水维修申请', date: '2023-10-15' },
  { type: '合同', title: '102室租约即将到期', date: '2023-10-18' },
  { type: '租金', title: '305室租金逾期提醒', date: '2023-10-20' },
  { type: '审批', title: '新用户注册待审核', date: '2023-10-22' }
])

const getTodoTagType = (type) => {
  const map = { '报修': 'danger', '合同': 'warning', '租金': 'success', '审批': 'primary' }
  return map[type] || 'info'
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
