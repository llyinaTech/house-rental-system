<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="合同编号" prop="contractNo">
          <el-input v-model="queryParams.contractNo" placeholder="请输入合同编号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="房源" prop="houseId">
          <el-select v-model="queryParams.houseId" placeholder="请选择房源" clearable filterable @change="handleQuery">
            <el-option v-for="h in houseOptions" :key="h.id" :label="h.title" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房东" prop="landlordId">
          <el-select v-model="queryParams.landlordId" placeholder="请选择房东" clearable filterable @change="handleQuery">
            <el-option v-for="u in userOptions" :key="`landlord-${u.id}`" :label="getUserLabel(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="租客" prop="tenantId">
          <el-select v-model="queryParams.tenantId" placeholder="请选择租客" clearable filterable @change="handleQuery">
            <el-option v-for="u in userOptions" :key="`tenant-${u.id}`" :label="getUserLabel(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待签署" :value="0" />
            <el-option label="生效中" :value="1" />
            <el-option label="已到期" :value="2" />
            <el-option label="已解约" :value="3" />
            <el-option label="已退租" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
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
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="operation-container">
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增合同</el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="contractNo" label="合同编号" min-width="140" />
        <el-table-column label="房源" min-width="160" align="center">
          <template #default="scope">
            {{ getHouseName(scope.row.houseId) }}
          </template>
        </el-table-column>
        <el-table-column label="房东" min-width="140" align="center">
          <template #default="scope">
            {{ getUserName(scope.row.landlordId) }}
          </template>
        </el-table-column>
        <el-table-column label="租客" min-width="140" align="center">
          <template #default="scope">
            {{ getUserName(scope.row.tenantId) }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="生效日期" width="120" align="center" />
        <el-table-column prop="endDate" label="到期日期" width="120" align="center" />
        <el-table-column prop="signDate" label="签署日期" width="120" align="center" />
        <el-table-column prop="rentAmount" label="月租金" width="110" align="center" />
        <el-table-column prop="depositAmount" label="押金" width="110" align="center" />
        <el-table-column prop="payPeriod" label="支付周期" width="100" align="center">
          <template #default="scope">
            <el-tag>{{ getPayPeriodLabel(scope.row.payPeriod) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="合同文件" width="120" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" :disabled="!scope.row.contractFileUrl" @click="previewFile(scope.row.contractFileUrl)">预览</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
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
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="720px" @close="cancel">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="合同编号" prop="contractNo">
          <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
        </el-form-item>
        <el-form-item label="房源" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择房源" filterable>
            <el-option v-for="h in houseOptions" :key="h.id" :label="h.title" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房东" prop="landlordId">
          <el-select v-model="form.landlordId" placeholder="请选择房东" filterable>
            <el-option v-for="u in userOptions" :key="`landlord-form-${u.id}`" :label="getUserLabel(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="租客" prop="tenantId">
          <el-select v-model="form.tenantId" placeholder="请选择租客" filterable>
            <el-option v-for="u in userOptions" :key="`tenant-form-${u.id}`" :label="getUserLabel(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="生效日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="到期日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="签署日期" prop="signDate">
          <el-date-picker v-model="form.signDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="月租金" prop="rentAmount">
          <el-input-number v-model="form.rentAmount" :min="0" :step="100" controls-position="right" placeholder="请输入月租金" />
        </el-form-item>
        <el-form-item label="押金" prop="depositAmount">
          <el-input-number v-model="form.depositAmount" :min="0" :step="100" controls-position="right" placeholder="请输入押金" />
        </el-form-item>
        <el-form-item label="支付周期" prop="payPeriod">
          <el-select v-model="form.payPeriod" placeholder="请选择支付周期">
            <el-option label="月付" :value="1" />
            <el-option label="季付" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">待签署</el-radio>
            <el-radio :label="1">生效中</el-radio>
            <el-radio :label="2">已到期</el-radio>
            <el-radio :label="3">已解约</el-radio>
            <el-radio :label="4">已退租</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="合同文件" prop="contractFileUrl">
          <el-upload
            class="upload-contract"
            :limit="1"
            :file-list="fileList"
            :on-success="onUploadSuccess"
            :on-remove="onUploadRemove"
            :before-upload="beforeUpload"
            :http-request="doUpload"
            accept=".pdf"
            :auto-upload="true"
          >
            <el-button type="primary">上传PDF</el-button>
            <template #tip>
              <div class="el-upload__tip">仅支持PDF文件，最大10MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入备注" />
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

const loading = ref(false)
const total = ref(0)
const list = ref([])
const formRef = ref(null)
const fileList = ref([])
const houseOptions = ref([])
const userOptions = ref([])
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
const uploadAction = `${baseURL}/api/file/upload`

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  contractNo: '',
  houseId: '',
  landlordId: '',
  tenantId: '',
  status: undefined,
  dateRange: []
})

const dialog = reactive({
  title: '',
  visible: false
})

const form = reactive({
  id: undefined,
  contractNo: '',
  houseId: '',
  landlordId: '',
  tenantId: '',
  startDate: '',
  endDate: '',
  signDate: '',
  rentAmount: '',
  depositAmount: '',
  payPeriod: 1,
  contractFileUrl: '',
  status: 0,
  remark: ''
})

const rules = {
  contractNo: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  houseId: [{ required: true, message: '请选择房源', trigger: 'change' }],
  landlordId: [{ required: true, message: '请选择房东', trigger: 'change' }],
  tenantId: [{ required: true, message: '请选择租客', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
  endDate: [
    { required: true, message: '请选择到期日期', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (!value || !form.startDate) return callback()
        if (new Date(value) < new Date(form.startDate)) {
          callback(new Error('到期日期不能早于生效日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  rentAmount: [
    { required: true, message: '请输入月租金', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (value === '' || value === null || value === undefined) return callback(new Error('请输入月租金'))
        if (Number(value) < 0) return callback(new Error('月租金不能为负数'))
        callback()
      },
      trigger: 'change'
    }
  ],
  depositAmount: [
    { required: true, message: '请输入押金', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (value === '' || value === null || value === undefined) return callback(new Error('请输入押金'))
        if (Number(value) < 0) return callback(new Error('押金不能为负数'))
        callback()
      },
      trigger: 'change'
    }
  ],
  payPeriod: [{ required: true, message: '请选择支付周期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getStatusLabel = (s) => ({ 0: '待签署', 1: '生效中', 2: '已到期', 3: '已解约', 4: '已退租' }[s] || '未知')
const getStatusTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'info', 3: 'danger', 4: 'primary' }[s] || 'info')
const getPayPeriodLabel = (p) => ({ 1: '月付', 3: '季付' }[p] || '未知')

const getUserLabel = (u) => (u?.realName && u.realName.trim() ? u.realName : u?.username || '')
const getHouseName = (id) => {
  if (!id) return ''
  const h = houseOptions.value.find((x) => Number(x.id) === Number(id))
  return h ? h.title : id
}
const getUserName = (id) => {
  if (!id) return ''
  const u = userOptions.value.find((x) => Number(x.id) === Number(id))
  return u ? getUserLabel(u) : id
}

const loadHouseOptions = async () => {
  try {
    const res = await api.get('/api/house/page', { params: { current: 1, size: 1000 } })
    if (res?.code === 200 && res?.data?.records) {
      houseOptions.value = res.data.records
    }
  } catch {}
}

const loadUserOptions = async () => {
  try {
    const res = await api.get('/api/user/page', { params: { current: 1, size: 1000 } })
    if (res?.code === 200 && res?.data?.records) {
      userOptions.value = res.data.records
    }
  } catch {}
}

const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/lease-contract/page', {
      params: {
        current: queryParams.pageNum,
        size: queryParams.pageSize,
        contractNo: queryParams.contractNo || undefined,
        houseId: queryParams.houseId || undefined,
        landlordId: queryParams.landlordId || undefined,
        tenantId: queryParams.tenantId || undefined,
        status: queryParams.status,
        startDateBegin: Array.isArray(queryParams.dateRange) && queryParams.dateRange[0] ? queryParams.dateRange[0] : undefined,
        startDateEnd: Array.isArray(queryParams.dateRange) && queryParams.dateRange[1] ? queryParams.dateRange[1] : undefined
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      list.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取合同列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取合同列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.contractNo = ''
  queryParams.houseId = ''
  queryParams.landlordId = ''
  queryParams.tenantId = ''
  queryParams.status = undefined
  queryParams.dateRange = []
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '新增合同'
  dialog.visible = true
}

const toAbsoluteUrl = (u) => (u && !u.startsWith('http') ? `${baseURL}${u}` : u)
const extractName = (u) => {
  if (!u) return ''
  try {
    const idx = u.lastIndexOf('/')
    return idx >= 0 ? u.substring(idx + 1) : u
  } catch { return u }
}

const handleEdit = async (row) => {
  resetForm()
  dialog.title = '编辑合同'
  try {
    const res = await api.get(`/api/lease-contract/${row.id}`)
    if (res?.code === 200 && res?.data) {
      Object.assign(form, res.data || {})
      if (form.contractFileUrl) {
        try {
          const s = await api.get('/api/file/signed-url', { params: { path: form.contractFileUrl } })
          if (s?.code === 200 && s?.data) {
            fileList.value = [{ name: extractName(form.contractFileUrl), url: s.data }]
          } else {
            fileList.value = [{ name: extractName(form.contractFileUrl), url: toAbsoluteUrl(form.contractFileUrl) }]
          }
        } catch {
          fileList.value = [{ name: extractName(form.contractFileUrl), url: toAbsoluteUrl(form.contractFileUrl) }]
        }
      } else {
        fileList.value = []
      }
      dialog.visible = true
    } else {
      ElMessage.error(res?.message || '获取合同详情失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取合同详情失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该合同吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/lease-contract/${row.id}`)
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
    try {
      if (form.id) {
        const res = await api.put('/api/lease-contract', form)
        if (res?.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res?.message || '修改失败')
          return
        }
      } else {
        const res = await api.post('/api/lease-contract', form)
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
  form.contractNo = ''
  form.houseId = ''
  form.landlordId = ''
  form.tenantId = ''
  form.startDate = ''
  form.endDate = ''
  form.signDate = ''
  form.rentAmount = ''
  form.depositAmount = ''
  form.payPeriod = 1
  form.contractFileUrl = ''
  form.status = 0
  form.remark = ''
  fileList.value = []
}

const previewFile = (url) => {
  if (!url) return
  const isHttp = /^https?:\/\//.test(url)
  const isSigned = isHttp && url.includes('aliyuncs.com') && (url.includes('Signature=') || url.includes('Expires='))
  const isAliyun = isHttp && url.includes('aliyuncs.com')
  if (isSigned) {
    window.open(url, '_blank')
    return
  }
  const params = isHttp ? { path: url } : { key: url }
  api.get('/api/file/signed-url', { params })
    .then((res) => {
      const u = res?.code === 200 && res?.data ? res.data : null
      if (u) {
        window.open(u, '_blank')
      } else {
        if (isAliyun) {
          ElMessage.error('预览失败：OSS私有桶需签名访问')
        } else {
          window.open(url, '_blank')
        }
      }
    })
    .catch(() => {
      if (isAliyun) {
        ElMessage.error('预览失败：无法获取签名URL')
      } else {
        window.open(url, '_blank')
      }
    })
}

const beforeUpload = (file) => {
  const isPDF = file.type === 'application/pdf' || /\.pdf$/i.test(file.name)
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isPDF) {
    ElMessage.error('仅支持上传PDF文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  return true
}

const onUploadSuccess = async (response, file) => {
  if (!(response?.code === 200 && response?.data)) {
    ElMessage.error(response?.message || '上传失败')
    return
  }
  form.contractFileUrl = response.data
  try {
    const s = await api.get('/api/file/signed-url', { params: { path: response.data } })
    if (s?.code === 200 && s?.data) {
      fileList.value = [{ name: file.name, url: s.data }]
    } else {
      fileList.value = [{ name: file.name, url: toAbsoluteUrl(response.data) }]
    }
  } catch {
    fileList.value = [{ name: file.name, url: toAbsoluteUrl(response.data) }]
  }
  if (form.id) {
    try {
      const saveRes = await api.put('/api/lease-contract', { id: form.id, contractFileUrl: form.contractFileUrl })
      if (saveRes?.code !== 200) {
        ElMessage.warning(saveRes?.message || '上传成功，但文件URL保存失败')
        return
      }
    } catch (e) {
      ElMessage.warning(e?.response?.data?.message || e.message || '上传成功，但文件URL保存失败')
      return
    }
  }
  ElMessage.success('上传成功')
}

const onUploadRemove = () => {
  form.contractFileUrl = ''
  fileList.value = []
}

const doUpload = async (options) => {
  const { file, onSuccess, onError, onProgress } = options
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await api.post('/api/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (evt) => {
        if (onProgress && evt.total) {
          onProgress({ percent: Math.round((evt.loaded / evt.total) * 100) })
        }
      }
    })
    if (res?.code === 200) {
      onSuccess?.(res, file)
    } else {
      const err = new Error(res?.message || '上传失败')
      onError?.(err)
      ElMessage.error(err.message)
    }
  } catch (e) {
    onError?.(e)
    ElMessage.error(e?.response?.data?.message || e.message || '上传失败')
  }
}
onMounted(() => {
  getList()
  loadHouseOptions()
  loadUserOptions()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 16px; }
.operation-container { margin-bottom: 16px; }
.pagination-container { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
