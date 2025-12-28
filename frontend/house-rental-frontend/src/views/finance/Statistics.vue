<template>
  <div class="app-container">
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
              <span>租金收入趋势 (近6个月)</span>
            </div>
          </template>
          <div ref="incomeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import api from '@/api/request'
import { ElMessage } from 'element-plus'

const statCards = reactive([
  { title: '房源总数', value: '0', footer: '套' },
  { title: '在租房源', value: '0', footer: '套' },
  { title: '租客总数', value: '0', footer: '人' },
  { title: '本月收入', value: '￥0', footer: '元' }
])

// Charts
const occupancyChartRef = ref(null)
const incomeChartRef = ref(null)
let occupancyChart = null
let incomeChart = null

const fetchStats = async () => {
  try {
    const res = await api.get('/api/statistics/dashboard')
    if (res?.code === 200 && res.data) {
      const data = res.data
      
      // 更新卡片数据
      statCards[0].value = data.totalHouses || 0
      statCards[1].value = data.rentedHouses || 0
      statCards[2].value = data.totalTenants || 0
      statCards[3].value = `￥${data.monthlyIncome || 0}`
      
      // 更新图表
      initOccupancyChart(data.houseStatusStats || [])
      initIncomeChart(data.incomeTrend || [])
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取统计数据失败')
  }
}

const initOccupancyChart = (data) => {
  if (!occupancyChartRef.value) return
  
  if (!data || data.length === 0) {
      data = [
          { value: 0, name: '未租', itemStyle: { color: '#E6A23C' } },
          { value: 0, name: '已租', itemStyle: { color: '#67C23A' } },
          { value: 0, name: '下架', itemStyle: { color: '#909399' } }
      ]
  } else {
      data = data.map(item => {
          let color = '#909399'
          if (item.name === '已租') color = '#67C23A'
          else if (item.name === '未租') color = '#E6A23C'
          return { ...item, itemStyle: { color } }
      })
  }

  occupancyChart = echarts.init(occupancyChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '房源状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: data
      }
    ]
  }
  occupancyChart.setOption(option)
}

const initIncomeChart = (data) => {
  if (!incomeChartRef.value) return
  
  const xAxisDataCorrect = data.map(item => item.name)
  const seriesData = data.map(item => item.value)

  incomeChart = echarts.init(incomeChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: xAxisDataCorrect },
    yAxis: { type: 'value' },
    series: [
      {
        name: '租金收入',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: seriesData,
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
  initOccupancyChart([])
  initIncomeChart([])
  fetchStats()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  occupancyChart?.dispose()
  incomeChart?.dispose()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.stat-card { height: 120px; margin-bottom: 20px; }
.card-title { font-size: 14px; color: #909399; }
.card-value { font-size: 24px; font-weight: bold; margin: 10px 0; }
.card-footer { font-size: 12px; color: #909399; }
.chart-row { margin-top: 20px; }
.chart-card { height: 380px; }
.chart-container { height: 300px; width: 100%; }
</style>
