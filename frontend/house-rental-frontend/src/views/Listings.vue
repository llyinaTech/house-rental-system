<template>
  <div class="page-wrap">
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索房源" style="max-width:240px" />
        <el-select v-model="query.area" placeholder="所属区域" style="max-width:180px">
          <el-option label="全部" value="" />
          <el-option label="朝阳" value="chaoyang" />
          <el-option label="海淀" value="haidian" />
        </el-select>
        <el-button type="primary">查询</el-button>
        <el-button>重置</el-button>
      </div>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="rent" label="租金" width="120" />
        <el-table-column prop="area" label="面积" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button type="text">查看</el-button>
            <el-button type="text">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const query = reactive({ keyword: '', area: '' })
const tableData = reactive([
  { title: '海淀两居室', rent: '4500', area: '65㎡', status: '在租' },
  { title: '朝阳一居室', rent: '3200', area: '45㎡', status: '待租' }
])
</script>

<style scoped>
.page-wrap { padding: 20px; }
.toolbar { display: flex; gap: 10px; margin-bottom: 12px; }
</style>