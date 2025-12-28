<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入报修标题"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完结" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="dateRange">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
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
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 提交报修
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="list"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="houseId" label="房源ID" width="100" align="center" />
        <el-table-column prop="tenantId" label="租客ID" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="success" link size="small" @click="handleProgress(scope.row)" :disabled="scope.row.status !== 0">
              <el-icon><CircleCheck /></el-icon> 开始处理
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
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

    <!-- 报修弹窗 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="640px"
      @close="cancel"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="房源ID" prop="houseId">
          <el-input v-model="form.houseId" placeholder="请输入房源ID" />
        </el-form-item>
        <el-form-item label="租客ID" prop="tenantId">
          <el-input v-model="form.tenantId" placeholder="请输入租客ID" />
        </el-form-item>
        <el-form-item label="详情" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入报修详情" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">待处理</el-radio>
            <el-radio :label="1">处理中</el-radio>
            <el-radio :label="2">已完结</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, CircleCheck } from '@element-plus/icons-vue'
// 可切换为真实接口：import api from '@/api/request'

const loading = ref(false)
const total = ref(0)
const list = ref([])
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  status: undefined,
  dateRange: []
})

const dialog = reactive({
  title: '',
  visible: false
})

const form = reactive({
  id: undefined,
  title: '',
  houseId: '',
  tenantId: '',
  content: '',
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  houseId: [{ required: true, message: '请输入房源ID', trigger: 'blur' }],
  tenantId: [{ required: true, message: '请输入租客ID', trigger: 'blur' }],
  content: [{ required: true, message: '请输入详情', trigger: 'blur' }]
}

// 模拟数据
const mockData = [
  { id: 101, title: '厨房水龙头漏水', houseId: 5001, tenantId: 3001, content: '水龙头持续滴水，影响使用', status: 0, createTime: '2023-10-25 10:00:00' },
  { id: 102, title: '卧室空调不制冷', houseId: 5002, tenantId: 3002, content: '空调只能吹风，无法制冷', status: 1, createTime: '2023-10-26 14:30:00' },
  { id: 103, title: '门锁卡顿难以打开', houseId: 5003, tenantId: 3003, content: '门锁旋转不顺畅，偶尔卡住', status: 2, createTime: '2023-10-27 09:20:00' }
]

const getStatusLabel = (s) => {
  const map = { 0: '待处理', 1: '处理中', 2: '已完结', 3: '已取消' }
  return map[s] || '未知'
}
const getStatusTag = (s) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[s] || 'info'
}

const getList = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = mockData.filter(item => {
      if (queryParams.title && !item.title.includes(queryParams.title)) return false
      if (queryParams.status !== undefined && item.status !== queryParams.status) return false
      return true
    })
    total.value = filtered.length
    const start = (queryParams.pageNum - 1) * queryParams.pageSize
    list.value = filtered.slice(start, start + queryParams.pageSize)
    loading.value = false
  }, 400)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  queryParams.status = undefined
  queryParams.dateRange = []
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '提交报修'
  dialog.visible = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialog.title = '编辑报修'
  dialog.visible = true
}

const handleProgress = (row) => {
  row.status = 1
  ElMessage.success('已开始处理该报修')
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该报修记录吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = list.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      list.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.id) {
        const idx = mockData.findIndex(item => item.id === form.id)
        if (idx !== -1) {
          mockData[idx] = { ...mockData[idx], ...form }
        }
        ElMessage.success('修改成功')
      } else {
        const newItem = {
          id: Math.floor(Math.random() * 10000) + 100,
          ...form,
          createTime: new Date().toLocaleString().replace(/\//g, '-')
        }
        mockData.unshift(newItem)
        ElMessage.success('提交成功')
      }
      dialog.visible = false
      getList()
    }
  })
}

const cancel = () => {
  dialog.visible = false
  resetForm()
}

const resetForm = () => {
  form.id = undefined
  form.title = ''
  form.houseId = ''
  form.tenantId = ''
  form.content = ''
  form.status = 0
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.operation-container { margin-bottom: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
