<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="角色名称" prop="roleName">
          <el-input
            v-model="queryParams.roleName"
            placeholder="请输入角色名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input
            v-model="queryParams.roleKey"
            placeholder="请输入权限字符"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
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
        <el-icon><Plus /></el-icon> 新增角色
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="roleList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleKey" label="权限字符" min-width="120" />
        <el-table-column prop="sortOrder" label="显示顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="primary" link size="small" @click="handleMenuScope(scope.row)">
              <el-icon><CircleCheck /></el-icon> 数据权限
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

    <!-- 角色表单弹窗 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      @close="cancel"
    >
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, CircleCheck } from '@element-plus/icons-vue'

const loading = ref(false)
const total = ref(0)
const roleList = ref([])
const roleFormRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: undefined
})

const dialog = reactive({
  title: '',
  visible: false
})

const form = reactive({
  id: undefined,
  roleName: '',
  roleKey: '',
  sortOrder: 0,
  status: 1,
  remark: ''
})

const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入权限字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ]
}

// 模拟数据
const mockData = [
  { id: 1, roleName: '超级管理员', roleKey: 'admin', sortOrder: 1, status: 1, createTime: '2023-01-01 12:00:00' },
  { id: 2, roleName: '房东', roleKey: 'landlord', sortOrder: 2, status: 1, createTime: '2023-01-01 12:00:00' },
  { id: 3, roleName: '租客', roleKey: 'tenant', sortOrder: 3, status: 1, createTime: '2023-01-01 12:00:00' }
]

const getList = () => {
  loading.value = true
  setTimeout(() => {
    let list = mockData.filter(item => {
      if (queryParams.roleName && !item.roleName.includes(queryParams.roleName)) return false
      if (queryParams.roleKey && !item.roleKey.includes(queryParams.roleKey)) return false
      if (queryParams.status !== undefined && item.status !== queryParams.status) return false
      return true
    })
    
    if (list.length === 0 && !queryParams.roleName && !queryParams.roleKey && queryParams.status === undefined) {
       list = mockData
    }

    roleList.value = list
    total.value = list.length
    loading.value = false
  }, 500)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.roleKey = ''
  queryParams.status = undefined
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '新增角色'
  dialog.visible = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialog.title = '编辑角色'
  dialog.visible = true
}

const handleMenuScope = (row) => {
  ElMessage.info('权限分配功能开发中...')
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该角色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = roleList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      roleList.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleStatusChange = (row) => {
  const text = row.status === 1 ? '启用' : '停用'
  ElMessage.success(`已${text}角色 ${row.roleName}`)
}

const submitForm = () => {
  roleFormRef.value.validate((valid) => {
    if (valid) {
      if (form.id) {
        const index = roleList.value.findIndex(item => item.id === form.id)
        if (index !== -1) {
          roleList.value[index] = { ...roleList.value[index], ...form }
        }
        ElMessage.success('修改成功')
      } else {
        const newRole = {
          id: roleList.value.length + 100,
          ...form,
          createTime: new Date().toLocaleString().replace(/\//g, '-')
        }
        roleList.value.push(newRole)
        ElMessage.success('新增成功')
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
  form.roleName = ''
  form.roleKey = ''
  form.sortOrder = 0
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