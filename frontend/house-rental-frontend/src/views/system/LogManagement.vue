<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="操作模块" prop="module">
          <el-input
            v-model="queryParams.module"
            placeholder="请输入操作模块"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="操作人员" prop="username">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入操作人员"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="操作类型" prop="action">
          <el-select v-model="queryParams.action" placeholder="请选择操作类型" clearable @change="handleQuery">
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
            <el-option label="查询" value="查询" />
            <el-option label="登录" value="登录" />
            <el-option label="登出" value="登出" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间" prop="dateRange">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <div class="operation-container">
      <el-button type="danger" :disabled="multiple" @click="handleDelete">
        <el-icon><Delete /></el-icon> 批量删除
      </el-button>
      <el-button type="warning" :loading="exportLoading" :disabled="multiple || exportLoading" @click="handleExport">
        <el-icon><Download /></el-icon> 导出
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="logList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="日志ID" width="80" align="center" />
        <el-table-column prop="module" label="操作模块" width="150" />
        <el-table-column prop="action" label="操作类型" width="100">
          <template #default="scope">
            <el-tag :type="getActionTag(scope.row.action)">{{ scope.row.action }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="操作人员" width="120" />
        <el-table-column prop="ip" label="操作IP" width="130" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '成功' ? 'success' : (scope.row.status === '失败' ? 'danger' : 'info')">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column prop="detail" label="详细信息" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteRow(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>
    
    <el-drawer v-model="detail.visible" :title="detail.title" size="480px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="日志ID">{{ detail.data.id }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ detail.data.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detail.data.action }}</el-descriptions-item>
        <el-descriptions-item label="操作人员">{{ detail.data.username }}</el-descriptions-item>
        <el-descriptions-item label="操作IP">{{ detail.data.ip }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.data.status }}</el-descriptions-item>
        <el-descriptions-item label="详细信息">{{ detail.data.detail }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detail.data.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue'
import api from '@/api/request'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const loading = ref(false)
const total = ref(0)
const logList = ref([])
const ids = ref([])
const multiple = ref(true)
const exportLoading = ref(false)
const detail = reactive({
  visible: false,
  title: '日志详情',
  data: {}
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  module: '',
  username: '',
  action: '',
  dateRange: []
})

const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/log/page', {
      params: {
        module: queryParams.module || undefined,
        username: queryParams.username || undefined,
        action: queryParams.action || undefined,
        startTime: Array.isArray(queryParams.dateRange) && queryParams.dateRange[0] ? queryParams.dateRange[0] : undefined,
        endTime: Array.isArray(queryParams.dateRange) && queryParams.dateRange[1] ? queryParams.dateRange[1] : undefined,
        current: queryParams.pageNum,
        size: queryParams.pageSize
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      logList.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取日志列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取日志列表失败')
  } finally {
    loading.value = false
  }
}

const getActionTag = (action) => {
  const map = {
    '新增': 'success',
    '修改': 'warning',
    '删除': 'danger',
    '查询': 'info',
    '登录': 'primary',
    '登出': 'info'
  }
  return map[action] || ''
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.module = ''
  queryParams.username = ''
  queryParams.action = ''
  queryParams.dateRange = []
  handleQuery()
}

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

const handleDelete = () => {
  ElMessageBox.confirm(`是否确认删除选中的 ${ids.value.length} 条日志?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete('/api/log/batch', { data: ids.value })
      if (res?.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res?.message || '删除失败')
      }
    } catch (e) {
      ElMessage.error(e?.response?.data?.message || e.message || '删除失败')
    }
  }).catch(() => {})
}

const handleExport = async () => {
  exportLoading.value = true
  try {
    if (!ids.value.length) {
      ElMessage.warning('请先勾选要导出的日志')
      exportLoading.value = false
      return
    }
    const auth = useAuthStore()
    const rawBase = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081').replace(/\/+$/, '')
    const exportUrl = rawBase.endsWith('/api') ? `${rawBase}/log/export` : `${rawBase}/api/log/export`
    const res = await axios.get(exportUrl, {
      params: {
        ids: ids.value.join(','),
        module: queryParams.module || undefined,
        username: queryParams.username || undefined,
        action: queryParams.action || undefined,
        startTime: Array.isArray(queryParams.dateRange) && queryParams.dateRange[0] ? queryParams.dateRange[0] : undefined,
        endTime: Array.isArray(queryParams.dateRange) && queryParams.dateRange[1] ? queryParams.dateRange[1] : undefined
      },
      responseType: 'blob',
      headers: auth.token ? { Authorization: `Bearer ${auth.token}` } : {}
    })
    const contentType = res.headers?.['content-type'] || ''
    if (!contentType.includes('application')) {
      ElMessage.error('导出失败')
      return
    }
    let filename = '系统日志导出.xlsx'
    const cd = res.headers?.['content-disposition']
    if (cd) {
      const match = /filename\*=UTF-8''([^;]+)|filename="?([^"]+)"?/.exec(cd)
      const name = decodeURIComponent(match?.[1] || match?.[2] || '')
      if (name) filename = name
    }
    const blob = new Blob([res.data], { type: contentType || 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '导出失败')
  } finally {
    exportLoading.value = false
  }
}

const fetchAllLogs = async () => {
  const params = {
    module: queryParams.module || undefined,
    username: queryParams.username || undefined,
    action: queryParams.action || undefined,
    startTime: Array.isArray(queryParams.dateRange) && queryParams.dateRange[0] ? queryParams.dateRange[0] : undefined,
    endTime: Array.isArray(queryParams.dateRange) && queryParams.dateRange[1] ? queryParams.dateRange[1] : undefined,
  }
  const pageSize = 2000
  let current = 1
  let total = 0
  const all = []
  try {
    while (true) {
      const res = await api.get('/api/log/page', {
        params: { ...params, current, size: pageSize }
      })
      if (res?.code !== 200 || !res?.data) break
      const page = res.data
      const records = page.records || []
      total = page.total || records.length
      all.push(...records)
      if (all.length >= total || records.length < pageSize) break
      current += 1
    }
  } catch {
    // ignore
  }
  return all
}

const escapeCSV = (val) => {
  const s = val == null ? '' : String(val)
  if (/[",\n]/.test(s)) {
    return `"${s.replace(/"/g, '""')}"`
  }
  return s
}

const downloadCSV = (rows) => {
  const headers = ['日志ID','操作模块','操作类型','操作人员','操作IP','状态','详细信息','操作时间']
  const lines = [headers.join(',')]
  rows.forEach(r => {
    lines.push([
      escapeCSV(r.id),
      escapeCSV(r.module),
      escapeCSV(r.action),
      escapeCSV(r.username),
      escapeCSV(r.ip),
      escapeCSV(r.status),
      escapeCSV(r.detail),
      escapeCSV(r.createTime),
    ].join(','))
  })
  const csvContent = '\uFEFF' + lines.join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '系统日志导出.csv'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(url)
}

const handleView = async (row) => {
  try {
    const res = await api.get(`/api/log/${row.id}`)
    if (res?.code === 200 && res?.data) {
      detail.data = res.data || {}
      detail.visible = true
    } else {
      ElMessage.error(res?.message || '获取详情失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取详情失败')
  }
}

const handleDeleteRow = (row) => {
  ElMessageBox.confirm(`是否确认删除日志ID: ${row.id}？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/log/${row.id}`)
      if (res?.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res?.message || '删除失败')
      }
    } catch (e) {
      ElMessage.error(e?.response?.data?.message || e.message || '删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.filter-container {
  margin-bottom: 20px;
}
.operation-container {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
