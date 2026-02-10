<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="租客姓名" prop="tenantName">
          <el-input v-model="queryParams.tenantName" placeholder="租客姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="合同ID" prop="contractId">
          <el-input v-model="queryParams.contractId" placeholder="合同ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="payStatus">
          <el-select v-model="queryParams.payStatus" placeholder="请选择状态" clearable>
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="逾期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期范围" prop="dueRange">
          <el-date-picker
            v-model="queryParams.dueRange"
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
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增账单</el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="periodDesc" label="期数描述" min-width="160" />
        <el-table-column prop="contractNo" label="合同编号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="tenantName" label="租客姓名" width="120" align="center" />
        <el-table-column prop="landlordName" label="房东姓名" width="120" align="center" />
        <el-table-column prop="billAmount" label="应缴金额" width="120" align="center" />
        <el-table-column label="到期日期" width="120" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.payStatus)">{{ getStatusLabel(scope.row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.payTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reminderCount" label="提醒次数" width="100" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button v-if="!isAdmin" type="success" link size="small" :disabled="scope.row.payStatus === 1" @click="handlePay(scope.row)">支付</el-button>
            <el-button type="primary" link size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="640px" @close="cancel">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="合同ID" prop="contractId">
          <el-input v-model="form.contractId" placeholder="请输入合同ID" />
        </el-form-item>
        <el-form-item label="租客ID" prop="tenantId">
          <el-input v-model="form.tenantId" placeholder="请输入租客ID" />
        </el-form-item>
        <el-form-item label="房东ID" prop="landlordId">
          <el-input v-model="form.landlordId" placeholder="请输入房东ID" />
        </el-form-item>
        <el-form-item label="期数描述" prop="periodDesc">
          <el-input v-model="form.periodDesc" placeholder="如：2025年6月租金" />
        </el-form-item>
        <el-form-item label="应缴金额" prop="billAmount">
          <el-input-number v-model="form.billAmount" :min="0" :step="100" controls-position="right" placeholder="请输入应缴金额" />
        </el-form-item>
        <el-form-item label="到期日期" prop="dueDate">
          <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-drawer v-model="detail.visible" :title="detail.title" size="480px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="期数描述">{{ detail.data.periodDesc }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ detail.data.contractNo || detail.data.contractId }}</el-descriptions-item>
        <el-descriptions-item label="租客姓名">{{ detail.data.tenantName || detail.data.tenantId }}</el-descriptions-item>
        <el-descriptions-item label="房东姓名">{{ detail.data.landlordName || detail.data.landlordId }}</el-descriptions-item>
        <el-descriptions-item label="应缴金额">{{ detail.data.billAmount }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ formatDate(detail.data.dueDate) }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ getStatusLabel(detail.data.payStatus) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatDate(detail.data.payTime) }}</el-descriptions-item>
        <el-descriptions-item label="提醒次数">{{ detail.data.reminderCount }}</el-descriptions-item>
        <el-descriptions-item label="上次提醒时间">{{ formatDate(detail.data.lastRemindTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(detail.data.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import api from '@/api/request'

const loading = ref(false)
const total = ref(0)
const list = ref([])
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tenantName: '',
  contractId: '',
  payStatus: undefined,
  dueRange: []
})

const dialog = reactive({
  title: '',
  visible: false
})

const detail = reactive({
  title: '账单详情',
  visible: false,
  data: {}
})

const form = reactive({
  id: undefined,
  contractId: '',
  tenantId: '',
  landlordId: '',
  periodDesc: '',
  billAmount: '',
  dueDate: '',
  payStatus: 0
})

const rules = {
  contractId: [{ required: true, message: '请输入合同ID', trigger: 'blur' }],
  tenantId: [{ required: true, message: '请输入租客ID', trigger: 'blur' }],
  landlordId: [{ required: true, message: '请输入房东ID', trigger: 'blur' }],
  periodDesc: [{ required: true, message: '请输入期数描述', trigger: 'blur' }],
  billAmount: [
    { required: true, message: '请输入应缴金额', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (value === '' || value === null || value === undefined) return callback(new Error('请输入应缴金额'))
        if (Number(value) < 0) return callback(new Error('应缴金额不能为负数'))
        callback()
      },
      trigger: 'change'
    }
  ],
  dueDate: [{ required: true, message: '请选择到期日期', trigger: 'change' }]
}

const getStatusLabel = (s) => ({ 0: '未支付', 1: '已支付', 2: '逾期' }[s] || '未知')
const getStatusTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

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
    const res = await api.get('/api/rent-bill/page', {
      params: {
        current: queryParams.pageNum,
        size: queryParams.pageSize,
        tenantName: queryParams.tenantName || undefined,
        contractId: queryParams.contractId || undefined,
        payStatus: queryParams.payStatus,
        dueDateBegin: Array.isArray(queryParams.dueRange) && queryParams.dueRange[0] ? queryParams.dueRange[0] : undefined,
        dueDateEnd: Array.isArray(queryParams.dueRange) && queryParams.dueRange[1] ? queryParams.dueRange[1] : undefined
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      list.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取账单列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取账单列表失败')
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
  queryParams.tenantName = ''
  queryParams.contractId = ''
  queryParams.payStatus = undefined
  queryParams.dueRange = []
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '新增账单'
  dialog.visible = true
}

const handleView = (row) => {
  detail.title = '账单详情'
  detail.data = { ...row }
  detail.visible = true
}

const handlePay = (row) => {
  ElMessageBox.confirm('确认支付该账单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.put(`/api/rent-bill/pay/${row.id}`)
      if (res?.code === 200) {
        ElMessage.success('支付成功')
        getList()
      } else {
        ElMessage.error(res?.message || '支付失败')
      }
    } catch (e) {
      ElMessage.error(e?.response?.data?.message || e.message || '支付失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该账单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/rent-bill/${row.id}`)
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
      const res = await api.post('/api/rent-bill', form)
      if (res?.code === 200) {
        ElMessage.success('新增成功')
        dialog.visible = false
        getList()
      } else {
        ElMessage.error(res?.message || '新增失败')
      }
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
  form.contractId = ''
  form.tenantId = ''
  form.landlordId = ''
  form.periodDesc = ''
  form.billAmount = ''
  form.dueDate = ''
  form.payStatus = 0
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
