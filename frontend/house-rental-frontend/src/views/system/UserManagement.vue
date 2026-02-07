<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
      <el-button type="primary" @click="handleAdd" v-hasPerm="['system:user:add']">
        <el-icon><Plus /></el-icon> 新增用户
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="userList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTag(scope.row.userType)">
              {{ getUserTypeLabel(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)" v-hasPerm="['system:user:edit']">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="primary" link size="small" @click="handleRoleAssign(scope.row)" v-hasPerm="['system:user:edit']">
              <el-icon><CircleCheck /></el-icon> 分配角色
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)" v-hasPerm="['system:user:remove']">
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

    <!-- 弹窗 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      @close="cancel"
    >
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户类型">
            <el-option label="管理员" :value="1" />
            <el-option label="房东" :value="2" />
            <el-option label="租客" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 角色分配弹窗 -->
    <el-dialog
      title="分配角色"
      v-model="roleDialog.visible"
      width="500px"
    >
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="roleDialog.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userRoleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="item in roleList"
              :key="item.id"
              :label="item.roleName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitRoleAssign">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, CircleCheck } from '@element-plus/icons-vue'
import api from '@/api/request'

const loading = ref(false)
const total = ref(0)
const userList = ref([])
const userFormRef = ref(null)

const roleDialog = reactive({
  visible: false,
  userId: null,
  username: ''
})
const roleList = ref([])
const userRoleIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  status: undefined
})

const dialog = reactive({
  title: '',
  visible: false
})

const form = reactive({
  id: undefined,
  username: '',
  realName: '',
  password: '',
  phone: '',
  userType: 3,
  status: 1,
  remark: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
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

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/user/page', {
      params: {
        current: queryParams.pageNum,
        size: queryParams.pageSize,
        username: queryParams.username || undefined,
        phone: queryParams.phone || undefined,
        status: queryParams.status
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      userList.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取用户列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const getUserTypeLabel = (type) => {
  const map = { 1: '管理员', 2: '房东', 3: '租客' }
  return map[type] || '未知'
}

const getUserTypeTag = (type) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'success' }
  return map[type] || 'info'
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
  queryParams.username = ''
  queryParams.phone = ''
  queryParams.status = undefined
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '新增用户'
  dialog.visible = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialog.title = '编辑用户'
  dialog.visible = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/user/${row.id}`)
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

const handleStatusChange = (row) => {
  // 直接提交更新状态
  api.put('/api/user', { ...row })
    .then((res) => {
      if (res?.code === 200) {
        const text = row.status === 1 ? '启用' : '禁用'
        ElMessage.success(`已${text}用户 ${row.username}`)
      } else {
        ElMessage.error(res?.message || '状态更新失败')
      }
    })
    .catch((e) => {
      ElMessage.error(e?.response?.data?.message || e.message || '状态更新失败')
    })
}

const handleRoleAssign = async (row) => {
  roleDialog.userId = row.id
  roleDialog.username = row.username
  roleDialog.visible = true
  userRoleIds.value = []
  
  try {
    // 并行获取角色列表和用户角色
    const [roleRes, userRoleRes] = await Promise.all([
      api.get('/api/role/list'),
      api.get(`/api/user/${row.id}/roles`)
    ])
    
    if (roleRes.code === 200) {
      roleList.value = roleRes.data
    }
    
    if (userRoleRes.code === 200) {
      userRoleIds.value = userRoleRes.data
    }
  } catch (e) {
    ElMessage.error('获取角色数据失败')
  }
}

const submitRoleAssign = async () => {
  try {
    const res = await api.post(`/api/user/${roleDialog.userId}/roles`, userRoleIds.value)
    if (res.code === 200) {
      ElMessage.success('角色分配成功')
      roleDialog.visible = false
    } else {
      ElMessage.error(res.message || '分配失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '分配失败')
  }
}

const submitForm = () => {
  userFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (form.id) {
        const res = await api.put('/api/user', { ...form })
        if (res?.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res?.message || '修改失败')
          return
        }
      } else {
        const res = await api.post('/api/user', { ...form })
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
  form.username = ''
  form.realName = ''
  form.password = ''
  form.phone = ''
  form.userType = 3
  form.status = 1
  form.remark = ''
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
