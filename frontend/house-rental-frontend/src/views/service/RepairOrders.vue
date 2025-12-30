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
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="houseName" label="房源名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="tenantName" label="租客姓名" width="120" align="center" />
        <el-table-column label="报修图片" width="120" align="center">
          <template #default="scope">
             <el-image 
                v-if="getImages(scope.row).length > 0"
                style="width: 50px; height: 50px"
                :src="getImages(scope.row)[0]"
                :preview-src-list="getImages(scope.row)"
                fit="cover"
                preview-teleported
             />
             <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
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
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
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
        <el-form-item label="房源名称" prop="houseName">
          <el-input v-model="form.houseName" placeholder="房源名称" disabled />
        </el-form-item>
        <el-form-item label="租客姓名" prop="tenantName">
          <el-input v-model="form.tenantName" placeholder="租客姓名" disabled />
        </el-form-item>
        <el-form-item label="详情" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入报修详情" />
        </el-form-item>
        <el-form-item label="报修图片" prop="images">
          <el-upload
            :action="uploadAction"
            :headers="uploadHeaders"
            list-type="picture-card"
            v-model:file-list="form.images"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :on-preview="handlePreview"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-dialog v-model="previewVisible" append-to-body>
            <img w-full :src="previewImageUrl" alt="Preview Image" style="width: 100%" />
          </el-dialog>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" :disabled="authStore.user?.role === 'tenant'">
            <el-radio :label="0">待处理</el-radio>
            <el-radio :label="1">处理中</el-radio>
            <el-radio :label="2">已完结</el-radio>
            <el-radio :label="3">已取消</el-radio>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, CircleCheck } from '@element-plus/icons-vue'
import api from '@/api/request'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const formRef = ref(null)

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}))

const uploadAction = computed(() => (import.meta.env.VITE_API_BASE_URL || '') + '/api/oss/upload')

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
  houseName: '',
  tenantId: '',
  tenantName: '',
  content: '',
  status: 0,
  images: []
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入详情', trigger: 'blur' }]
}

const previewVisible = ref(false)
const previewImageUrl = ref('')

const handleUploadSuccess = (response, uploadFile) => {
  if (response.success) {
    uploadFile.url = response.url
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleRemove = (uploadFile, uploadFiles) => {
}

const handlePreview = (uploadFile) => {
  previewImageUrl.value = uploadFile.url
  previewVisible.value = true
}

const getImages = (row) => {
  if (!row.images) return []
  try {
    let imgs = []
    let raw = row.images
    console.log('Raw repair images:', raw)
    if (typeof raw === 'string') {
        // Handle double serialization
        if (raw.startsWith('"') && raw.endsWith('"')) {
            try { raw = JSON.parse(raw) } catch(e) {}
        }
        try {
            imgs = JSON.parse(raw)
        } catch(e) {
            imgs = raw.split(',').filter(Boolean)
        }
    } else if (Array.isArray(raw)) {
        imgs = raw
    }
    if (Array.isArray(imgs)) {
        return imgs.map(url => {
            if (typeof url === 'string') {
                // Remove ALL quotes, backticks, spaces, and backslashes
                let cleanUrl = url.replace(/['"`\s\\]/g, '')
                // Force HTTPS
                return cleanUrl.replace(/^http:\/\//, 'https://')
            }
            return ''
        }).filter(url => url && (url.startsWith('http') || url.startsWith('/')))
    }
  } catch (e) {
    console.error('Error parsing repair images:', e)
  }
  return []
}

const toStart = (d) => {
  if (!d) return undefined
  return Array.isArray(d) && d[0] ? d[0] : undefined
}
const toEnd = (d) => {
  if (!d) return undefined
  return Array.isArray(d) && d[1] ? d[1] : undefined
}

const getStatusLabel = (s) => {
  const map = { 0: '待处理', 1: '处理中', 2: '已完结', 3: '已取消' }
  return map[s] || '未知'
}
const getStatusTag = (s) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[s] || 'info'
}

const formatDate = (v) => {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return String(v).slice(0, 10)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/repair-order/page', {
      params: {
        current: queryParams.pageNum,
        size: queryParams.pageSize,
        title: queryParams.title || undefined,
        status: queryParams.status,
        startDateBegin: toStart(queryParams.dateRange),
        startDateEnd: toEnd(queryParams.dateRange)
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      list.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取报修列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取报修列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const handlePageChange = (page) => {
  queryParams.pageNum = page
  getList()
}

const handleSizeChange = (size) => {
  queryParams.pageSize = size
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

const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await api.get(`/api/repair-order/${row.id}`)
    if (res?.code === 200 && res?.data) {
      Object.assign(form, res.data || {})
      
      // Handle images for edit
      try {
        let imgs = []
        let raw = res.data.images
        if (typeof raw === 'string') {
            if (raw.startsWith('"') && raw.endsWith('"')) {
                try { raw = JSON.parse(raw) } catch(e) {}
            }
            try {
                imgs = JSON.parse(raw)
            } catch(e) {
                imgs = raw.split(',').filter(Boolean)
            }
        } else if (Array.isArray(raw)) {
            imgs = raw
        }
        if (Array.isArray(imgs)) {
             form.images = imgs.map(url => {
                 if (typeof url !== 'string') return null
                 let cleanUrl = url.replace(/['"`\s\\]/g, '')
                 if (!cleanUrl) return null
                 return {
                     name: cleanUrl.substring(cleanUrl.lastIndexOf('/') + 1) || 'image',
                     url: cleanUrl
                 }
             }).filter(Boolean)
        } else {
            form.images = []
        }
      } catch (e) {
        form.images = []
      }

      dialog.title = '编辑报修'
      dialog.visible = true
    } else {
      ElMessage.error(res?.message || '获取报修详情失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取报修详情失败')
  }
}

const handleProgress = async (row) => {
  try {
    const res = await api.put(`/api/repair-order/start/${row.id}`)
    if (res?.code === 200) {
      ElMessage.success('已开始处理该报修')
      getList()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该报修记录吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/repair-order/${row.id}`)
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
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    const submitData = { ...form }
    // Process images
    const cleanImages = form.images.map(f => {
       let url = f.url || (f.response && f.response.url)
       if (typeof url === 'string') {
           return url.replace(/['"`\s\\]/g, '')
       }
       return url
    }).filter(Boolean)
    submitData.images = JSON.stringify(cleanImages)

    try {
      if (form.id) {
        const res = await api.put('/api/repair-order', submitData)
        if (res?.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res?.message || '修改失败')
          return
        }
      } else {
        const res = await api.post('/api/repair-order', submitData)
        if (res?.code === 200) {
          ElMessage.success('提交成功')
        } else {
          ElMessage.error(res?.message || '提交失败')
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
