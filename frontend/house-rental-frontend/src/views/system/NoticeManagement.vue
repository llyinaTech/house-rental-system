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
        <el-form-item label="类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
            <el-option label="通知" :value="1" />
            <el-option label="公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
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
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'info' : 'warning'">
              {{ scope.row.type === 1 ? '通知' : '公告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon> 编辑
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
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">通知</el-radio>
            <el-radio :label="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'

// 模拟数据
const mockData = [
  { id: 1, title: '系统维护通知', type: 1, content: '系统将于本周六凌晨2:00进行维护，预计耗时2小时。', status: 1, createTime: '2023-10-25 10:00:00' },
  { id: 2, title: '国庆放假安排', type: 2, content: '国庆节放假安排如下：10月1日至10月7日放假，共7天。', status: 1, createTime: '2023-09-28 09:00:00' },
  { id: 3, title: '新功能上线预告', type: 2, content: '下个月将上线在线报修功能，敬请期待。', status: 0, createTime: '2023-10-26 14:30:00' },
]

const loading = ref(false)
const total = ref(0)
const noticeList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  type: undefined,
  status: undefined
})

const dialog = reactive({
  visible: false,
  title: ''
})

const form = reactive({
  id: undefined,
  title: '',
  type: 1,
  content: '',
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const noticeRef = ref(null)

const getList = () => {
  loading.value = true
  // 模拟接口调用
  setTimeout(() => {
    let list = [...mockData]
    if (queryParams.title) {
      list = list.filter(item => item.title.includes(queryParams.title))
    }
    if (queryParams.type !== undefined) {
      list = list.filter(item => item.type === queryParams.type)
    }
    if (queryParams.status !== undefined) {
      list = list.filter(item => item.status === queryParams.status)
    }
    
    total.value = list.length
    // 简单的分页模拟
    const start = (queryParams.pageNum - 1) * queryParams.pageSize
    noticeList.value = list.slice(start, start + queryParams.pageSize)
    
    loading.value = false
  }, 500)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  queryParams.type = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleAdd = () => {
  resetForm()
  dialog.title = '发布公告'
  dialog.visible = true
}

const handleEdit = (row) => {
  resetForm()
  dialog.title = '编辑公告'
  Object.assign(form, row)
  dialog.visible = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该公告吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    // 这里需要实际调用删除接口，然后刷新列表
    getList()
  })
}

const submitForm = () => {
  noticeRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(form.id ? '修改成功' : '发布成功')
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
  form.title = ''
  form.type = 1
  form.content = ''
  form.status = 1
}

onMounted(() => {
  getList()
})
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
