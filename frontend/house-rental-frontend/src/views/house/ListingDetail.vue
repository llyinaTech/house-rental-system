<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>房源详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      
      <div v-loading="loading">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
          <el-descriptions-item label="租金">{{ detail.price }} 元/月</el-descriptions-item>
          <el-descriptions-item label="面积">{{ detail.area }} ㎡</el-descriptions-item>
          <el-descriptions-item label="户型">{{ detail.roomNum }}室{{ detail.hallNum }}厅</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ detail.floorNo }} / {{ detail.totalFloor }}层</el-descriptions-item>
          <el-descriptions-item label="朝向">{{ detail.direction }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ detail.address }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">房源图片</div>
        <div class="image-list">
          <el-empty v-if="!imageList || imageList.length === 0" description="暂无图片" />
          <el-image
            v-for="(img, index) in imageList"
            :key="index"
            :src="img"
            :preview-src-list="imageList"
            fit="cover"
            class="house-image"
            @error="handleImageError"
          >
             <template #error>
               <div class="image-slot">
                 <el-icon><icon-picture /></el-icon>
                 <span>加载失败</span>
               </div>
             </template>
          </el-image>
        </div>

        <div class="section-title">房源特征</div>
        <div class="feature-list">
          <el-empty v-if="!featureList || featureList.length === 0" description="暂无特征信息" />
          <el-tag
            v-for="(tag, index) in featureList"
            :key="index"
            type="success"
            effect="light"
            class="feature-tag"
          >
            {{ tag }}
          </el-tag>
          <!-- Debug info -->
          <div v-if="featureList.length === 0 && detail.featureTags" style="color: #999; font-size: 12px; margin-top: 5px;">
             Raw Features: {{ detail.featureTags }}
          </div>
        </div>

        <div class="section-title">房源描述</div>
        <div class="description-content">
          {{ detail.description || '暂无描述' }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import api from '@/api/request'

const route = useRoute()
const loading = ref(false)
const detail = ref({})

const featureList = computed(() => {
  if (!detail.value.featureTags) return []
  console.log('ListingDetail rawFeatures:', detail.value.featureTags)
  try {
    let tags = []
    let raw = detail.value.featureTags
    if (typeof raw === 'string') {
       try {
          tags = JSON.parse(raw)
       } catch (e) {
          tags = raw.split(',').filter(Boolean)
       }
    } else if (Array.isArray(raw)) {
       tags = raw
    }
    // Clean tags similarly
     if (Array.isArray(tags)) {
        const cleanedTags = tags.map(t => typeof t === 'string' ? t.replace(/^['"`\s]+|['"`\s]+$/g, '').replace(/\\/g, '') : t)
        console.log('ListingDetail cleaned features:', cleanedTags)
        return cleanedTags
    }
    return []
  } catch (e) {
    console.error('Feature parsing error:', e)
    // Fallback: handle comma-separated string
    if (typeof detail.value.featureTags === 'string') {
      return detail.value.featureTags.split(',').filter(Boolean)
    }
    return []
  }
})

const imageList = computed(() => {
  if (!detail.value.images) return []
  let images = []
  let rawImages = detail.value.images
  console.log('ListingDetail rawImages:', rawImages, typeof rawImages)

  try {
    // Handle potential double-serialization
    if (typeof rawImages === 'string' && rawImages.startsWith('"') && rawImages.endsWith('"')) {
       try { rawImages = JSON.parse(rawImages) } catch (e) {}
    }
    
    if (typeof rawImages === 'string') {
        // Try standard JSON parse
        try {
          images = JSON.parse(rawImages)
        } catch (e) {
          // Fallback: manual parsing for non-standard JSON strings or simple comma-separated
           const content = rawImages.replace(/^\s*\[|\]\s*$/g, '') // Remove outer brackets
           if (content.trim()) {
             // Split by comma, but be careful about commas inside quotes if any (simplified here)
             images = content.split(',').map(s => s.trim())
           }
        }
    } else if (Array.isArray(rawImages)) {
        images = rawImages
    }
  } catch (e) {
    console.error('Image parsing error:', e)
    images = []
  }

  if (!Array.isArray(images)) return []

  // Clean up URLs: remove whitespace, quotes (single/double), backticks, and escape chars
  const cleaned = images.map(url => {
    if (typeof url !== 'string') return ''
    // Remove ALL quotes, backticks, spaces, and backslashes from the entire string
    // This is safe because valid URLs shouldn't contain these characters
    let cleanUrl = url.replace(/['"`\s\\]/g, '')
    // Force HTTPS
    return cleanUrl.replace(/^http:\/\//, 'https://')
  }).filter(url => url && url.length > 5 && (url.startsWith('http') || url.startsWith('/')))
  
  console.log('ListingDetail cleaned images:', cleaned)
  return cleaned
})

const handleImageError = (e) => {
  console.error('Image load failed:', e)
}

const getRentLabel = (s) => ({ 0: '未租', 1: '已租', 2: '下架' }[s] || '未知')
const getRentTag = (s) => ({ 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info')
const getAuditLabel = (s) => ({ 0: '待审核', 1: '通过', 2: '拒绝' }[s] || '未知')
const getAuditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

const formatDate = (v) => {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return String(v).slice(0, 10)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const getDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  loading.value = true
  try {
    const res = await api.get(`/api/house/${id}`)
    if (res?.code === 200 && res?.data) {
      detail.value = res.data
    } else {
      ElMessage.error(res?.message || '获取房源详情失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取房源详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.section-title {
  font-size: 16px;
  font-weight: bold;
  margin: 20px 0 10px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
.house-image {
  width: 150px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
}
.feature-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
.feature-tag {
  font-size: 14px;
}
.description-content {
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  line-height: 1.6;
  min-height: 100px;
  white-space: pre-wrap;
}
</style>
