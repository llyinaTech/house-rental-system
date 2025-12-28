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
          <template #header>
            <div class="card-header">
              <span>房源出租率</span>
            </div>
          </template>
          <div ref="occupancyChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>租金收入趋势</span>
            </div>
          </template>
          <div ref="incomeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待处理事项</span>
            </div>
          </template>
          <el-table :data="todoItems" style="width: 100%">
            <el-table-column prop="type" label="类型" width="120">
              <template #default="scope">
                <el-tag :type="getTodoTagType(scope.row.type)">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="date" label="日期" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag type="warning" size="small">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="primary" link size="small">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

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

const getTodoTagType = (type) => {
  const map = {
    '报修': 'danger',
    '合同': 'warning',
    '租金': 'success'
  }
  return map[type] || 'info'
}

// Charts
const occupancyChartRef = ref(null)
const incomeChartRef = ref(null)
let occupancyChart = null
let incomeChart = null

const initOccupancyChart = () => {
  if (!occupancyChartRef.value) return
  occupancyChart = echarts.init(occupancyChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '房源状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 98, name: '已租', itemStyle: { color: '#67C23A' } },
          { value: 20, name: '未租', itemStyle: { color: '#E6A23C' } },
          { value: 10, name: '下架', itemStyle: { color: '#909399' } }
        ]
      }
    ]
  }
  occupancyChart.setOption(option)
}

const initIncomeChart = () => {
  if (!incomeChartRef.value) return
  incomeChart = echarts.init(incomeChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '租金收入',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: [82000, 93200, 90100, 93400, 129000, 133000, 132000],
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.5)' },
            { offset: 1, color: 'rgba(64,158,255,0.1)' }
          ])
        }
      }
    ]
  }
  incomeChart.setOption(option)
}

const handleResize = () => {
  occupancyChart?.resize()
  incomeChart?.resize()
}

onMounted(() => {
  initOccupancyChart()
  initIncomeChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  occupancyChart?.dispose()
  incomeChart?.dispose()
})
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
  height: 380px;
}

.chart-container {
  height: 300px;
  width: 100%;
}
</style>