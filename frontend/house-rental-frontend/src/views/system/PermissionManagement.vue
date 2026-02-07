<template>
  <div class="app-container">
    <div class="operation-container">
      <el-button type="primary" @click="handleAdd()" v-hasPerm="['system:permission:add']">
        <el-icon><Plus /></el-icon> 新增权限
      </el-button>
    </div>

    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="permissionList"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="permName" label="名称" min-width="150" />
        <el-table-column prop="permKey" label="标识" min-width="150" />
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type === 1">目录</el-tag>
            <el-tag v-else-if="scope.row.type === 2" type="success">菜单</el-tag>
            <el-tag v-else type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="150" />
        <el-table-column prop="component" label="组件路径" min-width="150" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleAdd(scope.row)" v-hasPerm="['system:permission:add']">
              <el-icon><Plus /></el-icon> 新增
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)" v-hasPerm="['system:permission:edit']">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)" v-hasPerm="['system:permission:remove']">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" @close="cancel">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="permissionOptions"
            :props="{ label: 'permName', value: 'id', children: 'children' }"
            value-key="id"
            placeholder="选择上级权限"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">目录</el-radio>
            <el-radio :label="2">菜单</el-radio>
            <el-radio :label="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权限名称" prop="permName">
          <el-input v-model="form.permName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permKey">
          <el-input v-model="form.permKey" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="form.type !== 3">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.type === 2">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import api from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const permissionList = ref([])
const permissionOptions = ref([])
const formRef = ref(null)

const dialog = reactive({
  visible: false,
  title: ''
})

const form = reactive({
  id: undefined,
  parentId: 0,
  permName: '',
  permKey: '',
  type: 1,
  path: '',
  component: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  parentId: [{ required: true, message: '请选择上级权限', trigger: 'change' }],
  permName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permKey: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/permission/tree')
    if (res.code === 200) {
      permissionList.value = res.data
      
      // Build options for tree select (add root node)
      permissionOptions.value = [
        { id: 0, permName: '主类目', children: res.data }
      ]
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.parentId = 0
  form.permName = ''
  form.permKey = ''
  form.type = 1
  form.path = ''
  form.component = ''
  form.sortOrder = 0
  form.status = 1
}

const handleAdd = (row) => {
  resetForm()
  if (row) {
    form.parentId = row.id
  }
  dialog.title = '新增权限'
  dialog.visible = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialog.title = '修改权限'
  dialog.visible = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该权限项吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete(`/api/permission/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.message)
      }
    } catch (error) {
      console.error(error)
    }
  })
}

const cancel = () => {
  dialog.visible = false
  resetForm()
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const method = form.id ? 'put' : 'post'
        const res = await api[method]('/api/permission', form)
        if (res.code === 200) {
          ElMessage.success(form.id ? '修改成功' : '新增成功')
          dialog.visible = false
          getList()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        console.error(error)
      }
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.operation-container {
  margin-bottom: 20px;
}
</style>
