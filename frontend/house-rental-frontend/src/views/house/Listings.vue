<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入房源标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="区域" prop="regionId">
          <el-tree-select
            v-model="queryParams.regionId"
            :data="regionOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择区域"
            clearable
            check-strictly
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="租赁状态" prop="rentStatus">
          <el-select v-model="queryParams.rentStatus" placeholder="请选择租赁状态" clearable>
            <el-option label="未租" :value="0" />
            <el-option label="已租" :value="1" />
            <el-option label="下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="通过" :value="1" />
            <el-option label="拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input v-model="queryParams.minPrice" placeholder="最低价" style="width: 120px" />
          <span style="margin: 0 8px">-</span>
          <el-input v-model="queryParams.maxPrice" placeholder="最高价" style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="operation-container">
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增房源</el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
        <el-table-column prop="price" label="月租金" width="100" align="center" />
        <el-table-column prop="area" label="面积" width="100" align="center" />
        <el-table-column label="户型" width="120" align="center">
          <template #default="scope">
            {{ scope.row.roomNum }}室{{ scope.row.hallNum }}厅
          </template>
        </el-table-column>
        <el-table-column label="楼层" width="120" align="center">
          <template #default="scope">
            {{ scope.row.floorNo }}/{{ scope.row.totalFloor }}
          </template>
        </el-table-column>
        <el-table-column prop="direction" label="朝向" width="90" align="center" />
        <el-table-column prop="rentStatus" label="租赁状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="getRentTag(scope.row.rentStatus)">{{ getRentLabel(scope.row.rentStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="getAuditTag(scope.row.auditStatus)">{{ getAuditLabel(scope.row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleDetail(scope.row)"><el-icon><View /></el-icon> 详情</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)"><el-icon><Edit /></el-icon> 编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)"><el-icon><Delete /></el-icon> 删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="680px" @close="cancel">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入房源标题" />
        </el-form-item>
        <el-form-item label="所属区域" prop="regionId">
          <el-tree-select
            v-model="form.regionId"
            :data="regionOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择所属区域"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="月租金" prop="price">
          <el-input v-model="form.price" placeholder="请输入月租金" />
        </el-form-item>
        <el-form-item label="面积" prop="area">
          <el-input v-model="form.area" placeholder="请输入面积" />
        </el-form-item>
        <el-form-item label="户型" prop="roomNum">
          <el-input v-model="form.roomNum" placeholder="室" style="width: 100px" />
          <span style="margin: 0 8px">室</span>
          <el-input v-model="form.hallNum" placeholder="厅" style="width: 100px" />
          <span style="margin-left: 8px">厅</span>
        </el-form-item>
        <el-form-item label="楼层" prop="floorNo">
          <el-input v-model="form.floorNo" placeholder="所在楼层" style="width: 140px" />
          <span style="margin: 0 8px">/</span>
          <el-input v-model="form.totalFloor" placeholder="总楼层" style="width: 140px" />
        </el-form-item>
        <el-form-item label="朝向" prop="direction">
          <el-input v-model="form.direction" placeholder="如 南/北/东南" />
        </el-form-item>
        <el-form-item label="租赁状态" prop="rentStatus">
          <el-radio-group v-model="form.rentStatus">
            <el-radio :label="0">未租</el-radio>
            <el-radio :label="1">已租</el-radio>
            <el-radio :label="2">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="form.auditStatus">
            <el-radio :label="0">待审核</el-radio>
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入房源描述" />
        </el-form-item>
        <el-form-item label="配套设施" prop="featureTags">
          <el-checkbox-group v-model="form.featureTags">
            <el-checkbox v-for="tag in featureOptions" :key="tag" :label="tag">{{ tag }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="房源图片" prop="images">
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
          <el-dialog v-model="previewVisible">
            <img w-full :src="previewImageUrl" alt="Preview Image" style="width: 100%" />
          </el-dialog>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import api from '@/api/request'
import { getDicts } from '@/api/dict'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const loading = ref(false)
const total = ref(0)
const list = ref([])
const regionOptions = ref([])
const formRef = ref(null)
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}))

const uploadAction = computed(() => (import.meta.env.VITE_API_BASE_URL || '') + '/api/oss/upload')

const featureOptions = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  regionId: '',
  rentStatus: undefined,
  auditStatus: undefined,
  minPrice: '',
  maxPrice: ''
})

const dialog = reactive({
  title: '',
  visible: false
})

const form = reactive({
  id: undefined,
  title: '',
  regionId: undefined,
  address: '',
  price: '',
  area: '',
  roomNum: '',
  hallNum: '',
  floorNo: '',
  totalFloor: '',
  direction: '',
  rentStatus: 0,
  auditStatus: 0,
  description: '',
  featureTags: [],
  images: []
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  price: [{ required: true, message: '请输入月租金', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
  roomNum: [{ required: true, message: '请输入室数', trigger: 'blur' }],
  hallNum: [{ required: true, message: '请输入厅数', trigger: 'blur' }]
}

const getRentLabel = (s) => ({ 0: '未租', 1: '已租', 2: '下架' }[s] || '未知')
const getRentTag = (s) => ({ 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info')
const getAuditLabel = (s) => ({ 0: '待审核', 1: '通过', 2: '拒绝' }[s] || '未知')
const getAuditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

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
    const res = await api.get('/api/house/page', {
      params: {
        current: queryParams.pageNum,
        size: queryParams.pageSize,
        title: queryParams.title || undefined,
        regionId: queryParams.regionId || undefined,
        rentStatus: queryParams.rentStatus,
        auditStatus: queryParams.auditStatus,
        minPrice: queryParams.minPrice || undefined,
        maxPrice: queryParams.maxPrice || undefined
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      list.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取房源列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取房源列表失败')
  } finally {
    loading.value = false
  }
}

const getRegionTree = async () => {
  try {
    const res = await api.get('/api/region/tree')
    if (res?.code === 200) {
      regionOptions.value = res.data
    }
  } catch (e) {
    console.error(e)
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
  queryParams.regionId = ''
  queryParams.rentStatus = undefined
  queryParams.auditStatus = undefined
  queryParams.minPrice = ''
  queryParams.maxPrice = ''
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '新增房源'
  dialog.visible = true
}

const handleDetail = (row) => {
  router.push({ name: 'ListingDetail', params: { id: row.id } })
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  console.log('Editing row:', row)
  
  // Parse featureTags
  try {
    let tags = []
    let raw = row.featureTags
    if (typeof raw === 'string') {
       try {
          tags = JSON.parse(raw)
       } catch (e) {
          tags = raw.split(',').filter(Boolean)
       }
    } else if (Array.isArray(raw)) {
       tags = raw
    }
    // Clean tags
    if (Array.isArray(tags)) {
        form.featureTags = tags.map(t => typeof t === 'string' ? t.replace(/^['"`\s]+|['"`\s]+$/g, '') : t)
    } else {
        form.featureTags = []
    }
  } catch (e) {
    console.error('Feature parsing error in Edit:', e)
    form.featureTags = []
  }
  
  // Parse images
  try {
    let imgs = []
    let rawImages = row.images
    console.log('Raw images in Edit:', rawImages, typeof rawImages)

    // Handle potential double-serialization
    if (typeof rawImages === 'string' && rawImages.startsWith('"') && rawImages.endsWith('"')) {
       try { rawImages = JSON.parse(rawImages) } catch (e) {}
    }

    try {
      imgs = typeof rawImages === 'string' ? JSON.parse(rawImages) : (Array.isArray(rawImages) ? rawImages : [])
    } catch (e) {
      // Fallback manual parsing
      if (typeof rawImages === 'string') {
         const content = rawImages.replace(/^\s*\[|\]\s*$/g, '')
         if (content.trim()) {
           imgs = content.split(',').map(s => s.trim())
         }
      }
    }
    
    // Clean and map
    if (Array.isArray(imgs)) {
        form.images = imgs.map(url => {
           if (typeof url !== 'string') return null
           // Remove ALL quotes, backticks, spaces, and backslashes
           let cleanUrl = url.replace(/['"`\s\\]/g, '')
           cleanUrl = cleanUrl.replace(/^http:\/\//, 'https://')
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
    console.error('Error parsing images in Edit:', e)
    form.images = []
  }

  dialog.title = '编辑房源'
  dialog.visible = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该房源吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/house/${row.id}`)
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
  // logic to handle remove if needed, el-upload updates file-list automatically
}

const handlePreview = (uploadFile) => {
  previewImageUrl.value = uploadFile.url
  previewVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    // Prepare data for submission
    const submitData = { ...form }
    submitData.featureTags = JSON.stringify(form.featureTags)
    
    // Ensure we extract clean URLs from file objects
     const cleanImages = form.images.map(f => {
       let url = f.url || (f.response && f.response.url)
       if (typeof url === 'string') {
           // Remove ALL quotes, backticks, spaces, and backslashes
           let clean = url.replace(/['"`\s\\]/g, '')
           return clean.replace(/^http:\/\//, 'https://')
       }
       return url
     }).filter(Boolean)
    
    submitData.images = JSON.stringify(cleanImages)

    try {
      if (form.id) {
        const res = await api.put('/api/house', submitData)
        if (res?.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res?.message || '修改失败')
          return
        }
      } else {
        const res = await api.post('/api/house', submitData)
        if (res?.code === 200) {
          ElMessage.success('新增成功')
        } else {
          ElMessage.error(res?.message || '新增失败')
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
  form.regionId = undefined
  form.address = ''
  form.price = ''
  form.area = ''
  form.roomNum = ''
  form.hallNum = ''
  form.floorNo = ''
  form.totalFloor = ''
  form.direction = ''
  form.rentStatus = 0
  form.auditStatus = 0
  form.description = ''
}

onMounted(() => {
  getList()
  getRegionTree()
  getDicts('house_feature').then(res => {
    if (res.data && res.data.length > 0) {
      featureOptions.value = res.data.map(item => item.dictLabel)
    } else {
      featureOptions.value = ['WiFi', '空调', '热水器', '洗衣机', '冰箱', '电视', '暖气', '宽带', '沙发', '床', '衣柜', '天然气', '电梯', '车位']
    }
  })
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 16px; }
.operation-container { margin-bottom: 16px; }
.pagination-container { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
