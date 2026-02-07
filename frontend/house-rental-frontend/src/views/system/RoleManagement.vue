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

    <!-- 分配权限弹窗 -->
    <el-dialog title="分配权限" v-model="permDialog.visible" width="500px">
      <el-form label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限">
          <el-tree
            ref="permTreeRef"
            :data="permissionTree"
            :props="{ label: 'permName', children: 'children' }"
            show-checkbox
            node-key="id"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permDialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitPermission">确 定</el-button>
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
const roleList = ref([])
const roleFormRef = ref(null)

const permDialog = reactive({
  visible: false,
  roleId: null
})
const permissionTree = ref([])
const permTreeRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: ''
})

const dialog = reactive({
  title: '',
  visible: false
})

const form = reactive({
  id: undefined,
  roleName: '',
  roleKey: '',
  remark: ''
})

const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入权限字符', trigger: 'blur' }
  ],
}


const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.roleKey = ''
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

const handleMenuScope = async (row) => {
  form.roleName = row.roleName
  permDialog.roleId = row.id
  permDialog.visible = true
  
  // Load permission tree
  try {
    const treeRes = await api.get('/api/permission/tree')
    if (treeRes.code === 200) {
      permissionTree.value = treeRes.data
    }
    
    // Load role permissions
    const permRes = await api.get(`/api/role/${row.id}/permissions`)
    if (permRes.code === 200) {
      // Wait for tree to render then set keys
      setTimeout(() => {
        if (permTreeRef.value) {
          // Filter only leaf nodes to avoid auto-selecting all children of a parent
          const leafKeys = filterLeafKeys(permRes.data, permissionTree.value)
          permTreeRef.value.setCheckedKeys(leafKeys)
        }
      }, 100)
    }
  } catch (e) {
    console.error(e)
  }
}

// Helper to find leaf nodes
const filterLeafKeys = (allKeys, treeData) => {
  const leafKeys = []
  const keySet = new Set(allKeys)
  
  const traverse = (nodes) => {
    for (const node of nodes) {
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      } else {
        if (keySet.has(node.id)) {
          leafKeys.push(node.id)
        }
      }
    }
  }
  
  traverse(treeData)
  return leafKeys
}

const submitPermission = async () => {
  const checkedKeys = permTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys()
  const allKeys = [...checkedKeys, ...halfCheckedKeys]
  
  try {
    const res = await api.post(`/api/role/${permDialog.roleId}/permissions`, allKeys)
    if (res.code === 200) {
      ElMessage.success('分配成功')
      permDialog.visible = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error(e.message || '分配失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该角色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/role/${row.id}`)
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
  roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    const payload = {
      id: form.id,
      roleName: form.roleName,
      roleKey: form.roleKey,
      remark: form.remark
    }
    try {
      if (form.id) {
        const res = await api.put('/api/role', payload)
        if (res?.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res?.message || '修改失败')
          return
        }
      } else {
        const res = await api.post('/api/role', payload)
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
  form.roleName = ''
  form.roleKey = ''
  form.remark = ''
}

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/role/page', {
      params: {
        current: queryParams.pageNum,
        size: queryParams.pageSize,
        roleName: queryParams.roleName || undefined,
        roleKey: queryParams.roleKey || undefined
      }
    })
    if (res?.code === 200 && res?.data) {
      const page = res.data
      roleList.value = page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error(res?.message || '获取角色列表失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取角色列表失败')
  } finally {
    loading.value = false
  }
}
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
