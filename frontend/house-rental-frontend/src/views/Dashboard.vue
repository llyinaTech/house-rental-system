<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card class="stat-card">
          <div class="card-title">{{ card.title }}</div>
          <div class="card-value">{{ card.value }}</div>
          <div class="card-footer">{{ card.footer }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">房源出租率</div>
          <div class="chart-placeholder">图表区域 - 房源出租率</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">租金收入趋势</div>
          <div class="chart-placeholder">图表区域 - 租金收入趋势</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card>
          <div slot="header">待处理事项</div>
          <el-table :data="todoItems" style="width: 100%">
            <el-table-column prop="type" label="类型" width="120"></el-table-column>
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="date" label="日期" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="120"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="text" size="small">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const statCards = reactive([
  { title: '房源总数', value: '128', footer: '套' },
  { title: '在租房源', value: '98', footer: '套' },
  { title: '租客总数', value: '112', footer: '人' },
  { title: '本月收入', value: '￥86,500', footer: '元' }
])

const todoItems = reactive([
  { type: '报修', title: '水管漏水维修申请', date: '2023-10-15', status: '待处理' },
  { type: '合同', title: '租约到期提醒', date: '2023-10-18', status: '待处理' },
  { type: '租金', title: '租金逾期提醒', date: '2023-10-20', status: '待处理' },
  { type: '报修', title: '门锁更换申请', date: '2023-10-22', status: '待处理' }
])
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  height: 120px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 14px;
  color: #909399;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.card-footer {
  font-size: 12px;
  color: #909399;
}

.chart-row, .table-row {
  margin-top: 20px;
}

.chart-card {
  height: 300px;
}

.chart-placeholder {
  height: 250px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}
</style>