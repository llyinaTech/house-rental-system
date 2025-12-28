<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="公告标题" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入公告标题"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已撤销" :value="2" />
          </el-select>
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
        <el-icon><Plus /></el-icon> 发布公告
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="noticeList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : (scope.row.status === 2 ? 'warning' : 'info')">
              {{ scope.row.status === 1 ? '已发布' : (scope.row.status === 2 ? '已撤销' : '草稿') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
            <el-button v-if="scope.row.status !== 1" type="success" link size="small" @click="handlePublish(scope.row)">
              发布
            </el-button>
            <el-button v-else type="warning" link size="small" @click="handleRevoke(scope.row)">
              撤销
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

    <!-- 弹窗 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="600px"
      @close="cancel"
    >
      <el-form ref="noticeRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
          />
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
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import api from '@/api/request'

const noticeRef = ref(null)

const loading = ref(false)
const total = ref(0)
const noticeList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  status: undefined
})

const dialog = reactive({
  visible: false,
  title: ''
})

const form = reactive({
  id: undefined,
  title: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/announcements', {
      params: {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        title: queryParams.title || undefined,
        status: queryParams.status
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      noticeList.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取公告列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  queryParams.status = undefined
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '发布公告'
  dialog.visible = true
}

const handleEdit = async (row) => {
  resetForm()
  dialog.title = '编辑公告'
  try {
    const res = await api.get(`/api/announcements/${row.id}`)
    if (res?.code === 200 && res?.data) {
      const data = res.data
      form.id = data.id
      form.title = data.title || ''
      form.content = data.content || ''
      dialog.visible = true
    } else {
      ElMessage.error(res?.message || '获取公告详情失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取公告详情失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该公告吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/announcements/${row.id}`)
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

const submitForm = () => {
  noticeRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (form.id) {
        const res = await api.put(`/api/announcements/${form.id}`, {
          title: form.title,
          content: form.content
        })
        if (res?.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res?.message || '修改失败')
          return
        }
      } else {
        const createRes = await api.post('/api/announcements', {
          title: form.title,
          content: form.content
        })
        if (createRes?.code === 200) {
          ElMessage.success('新增成功')
        } else {
          ElMessage.error(createRes?.message || '发布失败')
          return
        }
      }
      dialog.visible = false
      getList()
    } catch (e) {
      ElMessage.error(e?.response?.data?.message || e.message || '提交失败')
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
  form.content = ''
}

onMounted(() => {
  getList()
})

const handlePublish = async (row) => {
  try {
    const res = await api.put(`/api/announcements/publish/${row.id}`)
    if (res?.code === 200) {
      ElMessage.success('发布成功')
      getList()
    } else {
      ElMessage.error(res?.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '发布失败')
  }
}

const handleRevoke = async (row) => {
  try {
    const res = await api.put(`/api/announcements/revoke/${row.id}`)
    if (res?.code === 200) {
      ElMessage.success('撤销成功')
      getList()
    } else {
      ElMessage.error(res?.message || '撤销失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '撤销失败')
  }
}
</script>

<style scoped>
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
