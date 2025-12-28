<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="合同编号" prop="contractNo">
          <el-input v-model="queryParams.contractNo" placeholder="请输入合同编号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="房源ID" prop="houseId">
          <el-input v-model="queryParams.houseId" placeholder="房源ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="房东ID" prop="landlordId">
          <el-input v-model="queryParams.landlordId" placeholder="房东ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="租客ID" prop="tenantId">
          <el-input v-model="queryParams.tenantId" placeholder="租客ID" clearable @keyup.enter="handleQuery" />
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
        <el-table-column prop="houseId" label="房源ID" width="100" align="center" />
        <el-table-column prop="landlordId" label="房东ID" width="100" align="center" />
        <el-table-column prop="tenantId" label="租客ID" width="100" align="center" />
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
        <el-form-item label="房源ID" prop="houseId">
          <el-input v-model="form.houseId" placeholder="请输入房源ID" />
        </el-form-item>
        <el-form-item label="房东ID" prop="landlordId">
          <el-input v-model="form.landlordId" placeholder="请输入房东ID" />
        </el-form-item>
        <el-form-item label="租客ID" prop="tenantId">
          <el-input v-model="form.tenantId" placeholder="请输入租客ID" />
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
        <el-form-item label="合同文件URL" prop="contractFileUrl">
          <el-input v-model="form.contractFileUrl" placeholder="请输入文件URL" />
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
  houseId: [{ required: true, message: '请输入房源ID', trigger: 'blur' }],
  landlordId: [{ required: true, message: '请输入房东ID', trigger: 'blur' }],
  tenantId: [{ required: true, message: '请输入租客ID', trigger: 'blur' }],
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

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialog.title = '编辑合同'
  dialog.visible = true
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
}

const previewFile = (url) => {
  if (!url) return
  window.open(url, '_blank')
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 16px; }
.operation-container { margin-bottom: 16px; }
.pagination-container { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
