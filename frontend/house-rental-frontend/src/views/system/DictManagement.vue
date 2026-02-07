<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：字典类型列表 -->
      <el-col :span="12">
        <el-card class="box-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>字典类型</span>
            </div>
          </template>
          
          <!-- 类型搜索 -->
          <el-form :inline="true" :model="typeQueryParams" ref="typeQueryForm" size="small">
            <el-form-item label="名称" prop="dictName">
              <el-input
                v-model="typeQueryParams.dictName"
                placeholder="请输入字典名称"
                clearable
                style="width: 120px"
                @keyup.enter="handleTypeQuery"
              />
            </el-form-item>
            <el-form-item label="类型" prop="dictType">
              <el-input
                v-model="typeQueryParams.dictType"
                placeholder="请输入字典类型"
                clearable
                style="width: 120px"
                @keyup.enter="handleTypeQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTypeQuery"><el-icon><Search /></el-icon></el-button>
              <el-button @click="resetTypeQuery"><el-icon><Refresh /></el-icon></el-button>
            </el-form-item>
          </el-form>

          <!-- 类型操作 -->
          <div class="mb-2">
            <el-button type="primary" plain size="small" @click="handleAddType" v-hasPerm="['system:dict:add']"><el-icon><Plus /></el-icon> 新增</el-button>
          </div>

          <!-- 类型表格 -->
          <el-table
            ref="typeTableRef"
            v-loading="typeLoading"
            :data="typeList"
            highlight-current-row
            @current-change="handleCurrentChange"
            style="width: 100%"
            border
          >
            <el-table-column label="字典名称" prop="dictName" :show-overflow-tooltip="true" />
            <el-table-column label="字典类型" prop="dictType" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'primary' : 'danger'">{{ scope.row.status === 1 ? '正常' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template #default="scope">
                <el-button type="primary" link size="small" @click.stop="handleEditType(scope.row)" v-hasPerm="['system:dict:edit']"><el-icon><Edit /></el-icon></el-button>
                <el-button type="danger" link size="small" @click.stop="handleDeleteType(scope.row)" v-hasPerm="['system:dict:remove']"><el-icon><Delete /></el-icon></el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 类型分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="typeQueryParams.current"
              v-model:page-size="typeQueryParams.size"
              :total="typeTotal"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, prev, pager, next"
              @size-change="handleTypeQuery"
              @current-change="handleTypeQuery"
              small
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：字典数据列表 -->
      <el-col :span="12">
        <el-card class="box-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>字典数据</span>
              <span v-if="currentDictType" class="ml-2 text-gray-400 text-sm">({{ currentDictType.dictName }})</span>
            </div>
          </template>

          <!-- 数据搜索 -->
          <el-form :inline="true" :model="dataQueryParams" ref="dataQueryForm" size="small">
            <el-form-item label="标签" prop="dictLabel">
              <el-input
                v-model="dataQueryParams.dictLabel"
                placeholder="请输入字典标签"
                clearable
                style="width: 120px"
                @keyup.enter="handleDataQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleDataQuery"><el-icon><Search /></el-icon></el-button>
              <el-button @click="resetDataQuery"><el-icon><Refresh /></el-icon></el-button>
            </el-form-item>
          </el-form>

          <!-- 数据操作 -->
          <div class="mb-2">
            <el-button type="primary" plain size="small" @click="handleAddData" :disabled="!currentDictType" v-hasPerm="['system:dict:add']"><el-icon><Plus /></el-icon> 新增</el-button>
          </div>

          <!-- 数据表格 -->
          <el-table v-loading="dataLoading" :data="dataList" border style="width: 100%">
            <el-table-column label="字典标签" prop="dictLabel" />
            <el-table-column label="字典键值" prop="dictValue" />
            <el-table-column label="排序" prop="dictSort" width="60" align="center" />
            <el-table-column label="状态" align="center" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'primary' : 'danger'">{{ scope.row.status === 1 ? '正常' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="handleEditData(scope.row)" v-hasPerm="['system:dict:edit']"><el-icon><Edit /></el-icon></el-button>
                <el-button type="danger" link size="small" @click="handleDeleteData(scope.row)" v-hasPerm="['system:dict:remove']"><el-icon><Delete /></el-icon></el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 数据分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="dataQueryParams.current"
              v-model:page-size="dataQueryParams.size"
              :total="dataTotal"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, prev, pager, next"
              @size-change="handleDataQuery"
              @current-change="handleDataQuery"
              small
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型弹窗 -->
    <el-dialog :title="typeTitle" v-model="typeOpen" width="500px" append-to-body>
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="typeForm.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="typeForm.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitTypeForm">确 定</el-button>
          <el-button @click="typeOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 字典数据弹窗 -->
    <el-dialog :title="dataTitle" v-model="dataOpen" width="500px" append-to-body>
      <el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="80px">
        <el-form-item label="字典类型">
          <el-input v-model="dataForm.dictType" disabled />
        </el-form-item>
        <el-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dictSort">
          <el-input-number v-model="dataForm.dictSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dataForm.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDataForm">确 定</el-button>
          <el-button @click="dataOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { listType, getType, delType, addType, updateType, listData, getData, delData, addData, updateData } from '@/api/dict'

// ---------------- 字典类型相关 ----------------
const typeLoading = ref(true)
const typeList = ref([])
const typeTotal = ref(0)
const typeTitle = ref('')
const typeOpen = ref(false)
const typeFormRef = ref(null)
const typeTableRef = ref(null)

const typeQueryParams = reactive({
  current: 1,
  size: 10,
  dictName: undefined,
  dictType: undefined,
  status: undefined
})

const typeForm = reactive({
  dictId: undefined,
  dictName: undefined,
  dictType: undefined,
  status: 1,
  remark: undefined
})

const typeRules = {
  dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }],
  dictType: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }]
}

// ---------------- 字典数据相关 ----------------
const currentDictType = ref(null) // 当前选中的字典类型对象
const dataLoading = ref(false)
const dataList = ref([])
const dataTotal = ref(0)
const dataTitle = ref('')
const dataOpen = ref(false)
const dataFormRef = ref(null)

const dataQueryParams = reactive({
  current: 1,
  size: 10,
  dictType: undefined,
  dictLabel: undefined,
  status: undefined
})

const dataForm = reactive({
  dictCode: undefined,
  dictLabel: undefined,
  dictValue: undefined,
  dictSort: 0,
  status: 1,
  remark: undefined,
  dictType: undefined
})

const dataRules = {
  dictLabel: [{ required: true, message: '数据标签不能为空', trigger: 'blur' }],
  dictValue: [{ required: true, message: '数据键值不能为空', trigger: 'blur' }],
  dictSort: [{ required: true, message: '数据顺序不能为空', trigger: 'blur' }]
}

// ---------------- 方法实现 ----------------

// 查询类型列表
function getTypeList() {
  typeLoading.value = true
  listType(typeQueryParams).then(res => {
    typeList.value = res.data.records
    typeTotal.value = res.data.total
    typeLoading.value = false
    // 默认选中第一行
    if (typeList.value.length > 0) {
      nextTick(() => {
        typeTableRef.value.setCurrentRow(typeList.value[0])
      })
    }
  })
}

// 查询数据列表
function getDataList() {
  if (!currentDictType.value) {
    dataList.value = []
    dataTotal.value = 0
    return
  }
  console.log('Fetching dict data for:', currentDictType.value.dictType)
  dataLoading.value = true
  dataQueryParams.dictType = currentDictType.value.dictType
  listData(dataQueryParams).then(res => {
    console.log('Dict data response:', res)
    dataList.value = res.data.records
    dataTotal.value = res.data.total
    dataLoading.value = false
  }).catch(err => {
    console.error('Dict data error:', err)
    dataLoading.value = false
  })
}

// 选中类型行
function handleCurrentChange(val) {
  if (val) {
    currentDictType.value = val
    dataQueryParams.current = 1
    getDataList()
  }
}

// 类型搜索
function handleTypeQuery() {
  typeQueryParams.current = 1
  getTypeList()
}

// 类型重置
function resetTypeQuery() {
  typeQueryParams.dictName = undefined
  typeQueryParams.dictType = undefined
  handleTypeQuery()
}

// 数据搜索
function handleDataQuery() {
  dataQueryParams.current = 1
  getDataList()
}

// 数据重置
function resetDataQuery() {
  dataQueryParams.dictLabel = undefined
  handleDataQuery()
}

// 新增类型
function handleAddType() {
  resetTypeForm()
  typeOpen.value = true
  typeTitle.value = '添加字典类型'
}

// 编辑类型
function handleEditType(row) {
  resetTypeForm()
  const dictId = row.dictId
  getType(dictId).then(res => {
    Object.assign(typeForm, res.data)
    typeOpen.value = true
    typeTitle.value = '修改字典类型'
  })
}

// 提交类型表单
function submitTypeForm() {
  typeFormRef.value.validate(valid => {
    if (valid) {
      if (typeForm.dictId != undefined) {
        updateType(typeForm).then(res => {
          ElMessage.success('修改成功')
          typeOpen.value = false
          getTypeList()
        })
      } else {
        addType(typeForm).then(res => {
          ElMessage.success('新增成功')
          typeOpen.value = false
          getTypeList()
        })
      }
    }
  })
}

// 删除类型
function handleDeleteType(row) {
  const dictIds = row.dictId
  ElMessageBox.confirm('是否确认删除字典编号为"' + dictIds + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delType(dictIds)
  }).then(() => {
    getTypeList()
    ElMessage.success('删除成功')
    if (currentDictType.value && currentDictType.value.dictId === dictIds) {
      currentDictType.value = null
      dataList.value = []
      dataTotal.value = 0
    }
  }).catch(() => {})
}

// 重置类型表单
function resetTypeForm() {
  Object.assign(typeForm, {
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: 1,
    remark: undefined
  })
  if (typeFormRef.value) typeFormRef.value.resetFields()
}


// 新增数据
function handleAddData() {
  if (!currentDictType.value) {
    ElMessage.warning('请先选择一个字典类型')
    return
  }
  resetDataForm()
  dataForm.dictType = currentDictType.value.dictType
  dataOpen.value = true
  dataTitle.value = '添加字典数据'
}

// 编辑数据
function handleEditData(row) {
  resetDataForm()
  const dictCode = row.dictCode
  getData(dictCode).then(res => {
    Object.assign(dataForm, res.data)
    dataOpen.value = true
    dataTitle.value = '修改字典数据'
  })
}

// 提交数据表单
function submitDataForm() {
  dataFormRef.value.validate(valid => {
    if (valid) {
      if (dataForm.dictCode != undefined) {
        updateData(dataForm).then(res => {
          ElMessage.success('修改成功')
          dataOpen.value = false
          getDataList()
        })
      } else {
        addData(dataForm).then(res => {
          ElMessage.success('新增成功')
          dataOpen.value = false
          getDataList()
        })
      }
    }
  })
}

// 删除数据
function handleDeleteData(row) {
  const dictCodes = row.dictCode
  ElMessageBox.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delData(dictCodes)
  }).then(() => {
    getDataList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 重置数据表单
function resetDataForm() {
  Object.assign(dataForm, {
    dictCode: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    dictSort: 0,
    status: 1,
    remark: undefined,
    dictType: undefined
  })
  if (dataFormRef.value) dataFormRef.value.resetFields()
}

onMounted(() => {
  getTypeList()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mb-2 {
  margin-bottom: 10px;
}
.ml-2 {
  margin-left: 10px;
}
</style>
