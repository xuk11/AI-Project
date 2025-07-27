<template>
  <div class="sound-manage-container">
    <div class="page-header">
      <div class="header-title">
        <SoundOutlined class="header-icon" />
        <h1>声音管理</h1>
      </div>
      <div class="header-actions">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索声音名称/上传者"
          style="width: 250px"
          @search="onSearch"
        />
        <a-button type="primary" @click="showBatchActions = !showBatchActions">
          <ToolOutlined />
          批量操作
        </a-button>
      </div>
    </div>

    <!-- 批量操作工具栏 -->
    <div class="batch-toolbar" v-if="showBatchActions">
      <a-space>
        <a-button 
          type="primary" 
          danger 
          :disabled="!hasSelected" 
          @click="confirmBatchDelete"
        >
          <DeleteOutlined />
          批量删除
        </a-button>
        <a-button 
          :disabled="!hasSelected" 
          @click="confirmBatchApprove"
        >
          <CheckCircleOutlined />
          批量审核通过
        </a-button>
        <a-button 
          danger 
          :disabled="!hasSelected" 
          @click="confirmBatchReject"
        >
          <CloseCircleOutlined />
          批量拒绝
        </a-button>
        <a-divider type="vertical" />
        <span class="selection-info" v-if="hasSelected">
          已选择 {{ selectedRowKeys.length }} 项
        </span>
        <a-button type="link" @click="clearSelection">
          清空选择
        </a-button>
      </a-space>
    </div>

    <!-- 高级筛选 -->
    <div class="filter-section">
      <a-form layout="inline">
        <a-form-item label="声音状态">
          <a-select
            v-model:value="filters.status"
            style="width: 120px"
            @change="filterTable"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="approved">已审核</a-select-option>
            <a-select-option value="pending">待审核</a-select-option>
            <a-select-option value="rejected">已拒绝</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="声音分类">
          <a-select
            v-model:value="filters.category"
            style="width: 120px"
            @change="filterTable"
          >
            <a-select-option value="">全部分类</a-select-option>
            <a-select-option value="music">音乐</a-select-option>
            <a-select-option value="nature">自然</a-select-option>
            <a-select-option value="voice">人声</a-select-option>
            <a-select-option value="effect">音效</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="上传日期">
          <a-range-picker
            v-model:value="filters.dateRange"
            @change="filterTable"
          />
        </a-form-item>
      </a-form>
    </div>

    <!-- 声音数据表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="soundData"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        rowKey="id"
      >
        <!-- 声音标题 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'title'">
            <div class="sound-title-cell">
              <a-badge 
                :status="getStatusBadge(record.status)" 
                :text="getStatusText(record.status)" 
              />
              <a @click="previewSound(record)">{{ record.title }}</a>
            </div>
          </template>

          <!-- 分类 -->
          <template v-if="column.dataIndex === 'category'">
            <a-tag :color="getCategoryColor(record.category)">
              {{ getCategoryName(record.category) }}
            </a-tag>
          </template>

          <!-- 时长 -->
          <template v-if="column.dataIndex === 'duration'">
            {{ formatDuration(record.duration) }}
          </template>

          <!-- 上传者 -->
          <template v-if="column.dataIndex === 'uploader'">
            <div class="uploader-cell">
              <a-avatar :src="record.uploaderAvatar" size="small" />
              <span>{{ record.uploaderName }}</span>
            </div>
          </template>

          <!-- 上传时间 -->
          <template v-if="column.dataIndex === 'uploadTime'">
            {{ formatDate(record.uploadTime) }}
          </template>

          <!-- 操作 -->
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a-button size="small" type="link" @click="previewSound(record)">
                <PlayCircleOutlined />
                播放
              </a-button>
              <a-button 
                size="small" 
                type="link" 
                :disabled="record.status === 'approved'"
                @click="approveSound(record)"
              >
                <CheckCircleOutlined />
                通过
              </a-button>
              <a-button 
                size="small" 
                type="link" 
                danger 
                :disabled="record.status === 'rejected'"
                @click="rejectSound(record)"
              >
                <CloseCircleOutlined />
                拒绝
              </a-button>
              <a-button 
                size="small" 
                type="link" 
                danger 
                @click="deleteSound(record)"
              >
                <DeleteOutlined />
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 声音预览模态框 -->
    <a-modal
      v-model:visible="previewModalVisible"
      title="声音预览"
      :footer="null"
      width="500px"
    >
      <div v-if="currentSound" class="sound-preview-modal">
        <div class="sound-info">
          <h3>{{ currentSound.title }}</h3>
          <div class="sound-meta">
            <div>
              <span>分类：</span>
              <a-tag :color="getCategoryColor(currentSound.category)">
                {{ getCategoryName(currentSound.category) }}
              </a-tag>
            </div>
            <div>
              <span>上传者：</span>
              <span>{{ currentSound.uploaderName }}</span>
            </div>
            <div>
              <span>上传时间：</span>
              <span>{{ formatDate(currentSound.uploadTime) }}</span>
            </div>
            <div>
              <span>时长：</span>
              <span>{{ formatDuration(currentSound.duration) }}</span>
            </div>
            <div v-if="currentSound.description">
              <span>描述：</span>
              <p>{{ currentSound.description }}</p>
            </div>
            <div v-if="currentSound.tags && currentSound.tags.length">
              <span>标签：</span>
              <div class="tag-list">
                <a-tag v-for="tag in currentSound.tags" :key="tag">{{ tag }}</a-tag>
              </div>
            </div>
          </div>
        </div>
        
        <div class="audio-player">
          <div class="player-controls">
            <div class="play-button" @click="togglePlay">
              <PauseCircleOutlined v-if="isPlaying" />
              <PlayCircleOutlined v-else />
            </div>
            <div class="progress-bar">
              <div class="progress-background"></div>
              <div class="progress-current" :style="{ width: `${playProgress}%` }"></div>
              <div class="progress-handle" :style="{ left: `${playProgress}%` }"></div>
            </div>
            <div class="time-display">
              {{ currentTimeFormatted }} / {{ formatDuration(currentSound.duration) }}
            </div>
          </div>
          <audio ref="audioPlayer" @timeupdate="updateProgress" @ended="onAudioEnded"></audio>
        </div>
        
        <div class="modal-actions">
          <a-space>
            <a-button 
              type="primary" 
              :disabled="currentSound.status === 'approved'"
              @click="approveSound(currentSound)"
            >
              <CheckCircleOutlined />
              通过
            </a-button>
            <a-button 
              danger 
              :disabled="currentSound.status === 'rejected'"
              @click="rejectSound(currentSound)"
            >
              <CloseCircleOutlined />
              拒绝
            </a-button>
            <a-button danger @click="deleteSound(currentSound)">
              <DeleteOutlined />
              删除
            </a-button>
          </a-space>
        </div>
      </div>
    </a-modal>

    <!-- 批量操作确认模态框 -->
    <a-modal
      v-model:visible="confirmModalVisible"
      :title="confirmModalTitle"
      :okText="confirmModalOkText"
      :okButtonProps="{ danger: confirmModalDanger }"
      @ok="handleBatchAction"
    >
      <p>{{ confirmModalContent }}</p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SoundOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  ToolOutlined
} from '@ant-design/icons-vue'

// 表格列定义
const columns = [
  {
    title: '声音标题',
    dataIndex: 'title',
    sorter: true,
    width: '25%',
  },
  {
    title: '分类',
    dataIndex: 'category',
    filters: [
      { text: '音乐', value: 'music' },
      { text: '自然', value: 'nature' },
      { text: '人声', value: 'voice' },
      { text: '音效', value: 'effect' },
    ],
    width: '10%',
  },
  {
    title: '时长',
    dataIndex: 'duration',
    sorter: true,
    width: '10%',
  },
  {
    title: '上传者',
    dataIndex: 'uploader',
    width: '15%',
  },
  {
    title: '上传时间',
    dataIndex: 'uploadTime',
    sorter: true,
    width: '15%',
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: '25%',
  },
]

// 页面状态
const loading = ref(false)
const searchKeyword = ref('')
const showBatchActions = ref(false)
const soundData = ref([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 项`,
})
const filters = ref({
  status: '',
  category: '',
  dateRange: null,
})

// 选择状态
const selectedRowKeys = ref([])
const hasSelected = computed(() => selectedRowKeys.value.length > 0)

// 预览模态框
const previewModalVisible = ref(false)
const currentSound = ref(null)
const audioPlayer = ref(null)
const isPlaying = ref(false)
const playProgress = ref(0)
const currentTime = ref(0)

// 批量操作确认
const confirmModalVisible = ref(false)
const confirmModalTitle = ref('')
const confirmModalContent = ref('')
const confirmModalOkText = ref('确定')
const confirmModalDanger = ref(false)
const currentBatchAction = ref('')

// 格式化当前播放时间
const currentTimeFormatted = computed(() => {
  return formatDuration(Math.floor(currentTime.value))
})

// 获取声音列表
const fetchSoundList = async () => {
  loading.value = true
  try {
    // TODO: 替换为实际API调用
    await new Promise(resolve => setTimeout(resolve, 600))
    
    // 模拟数据
    soundData.value = Array.from({ length: 50 }, (_, i) => ({
      id: i + 1,
      title: `声音样本 ${i + 1}`,
      description: i % 3 === 0 ? '这是一段详细的声音描述，介绍了这段声音的创作背景和用途。' : '',
      uploaderName: `用户${Math.floor(Math.random() * 100)}`,
      uploaderAvatar: `https://randomuser.me/api/portraits/men/${i % 30}.jpg`,
      plays: Math.floor(Math.random() * 1000),
      likes: Math.floor(Math.random() * 500),
      duration: Math.floor(Math.random() * 300) + 10, // 10-310秒
      category: ['music', 'nature', 'voice', 'effect'][Math.floor(Math.random() * 4)],
      url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', // 样例URL
      uploadTime: new Date(Date.now() - Math.floor(Math.random() * 30) * 24 * 60 * 60 * 1000),
      status: ['approved', 'pending', 'rejected'][Math.floor(Math.random() * 3)],
      tags: i % 4 === 0 ? ['流行', '钢琴', '轻音乐'] : i % 5 === 0 ? ['自然', '雨声', '放松'] : []
    }))
    
    pagination.value.total = 50 // 模拟总数
  } catch (error) {
    message.error('获取声音列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  
  // TODO: 根据分页、筛选、排序条件重新获取数据
  fetchSoundList()
}

// 搜索处理
const onSearch = () => {
  pagination.value.current = 1
  fetchSoundList()
}

// 筛选处理
const filterTable = () => {
  pagination.value.current = 1
  fetchSoundList()
}

// 选择变更
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

// 清空选择
const clearSelection = () => {
  selectedRowKeys.value = []
}

// 预览声音
const previewSound = (sound) => {
  currentSound.value = sound
  previewModalVisible.value = true
  
  // 重置播放状态
  isPlaying.value = false
  playProgress.value = 0
  currentTime.value = 0
  
  // 设置音频源
  setTimeout(() => {
    if (audioPlayer.value) {
      audioPlayer.value.src = sound.url
      audioPlayer.value.load()
    }
  }, 100)
}

// 播放/暂停切换
const togglePlay = () => {
  if (!audioPlayer.value) return
  
  if (isPlaying.value) {
    audioPlayer.value.pause()
  } else {
    audioPlayer.value.play()
      .catch(err => {
        message.error('播放失败，请稍后再试')
        console.error('播放失败:', err)
      })
  }
  
  isPlaying.value = !isPlaying.value
}

// 更新进度
const updateProgress = () => {
  if (!audioPlayer.value || !currentSound.value) return
  
  currentTime.value = audioPlayer.value.currentTime
  playProgress.value = (audioPlayer.value.currentTime / currentSound.value.duration) * 100
}

// 音频播放结束
const onAudioEnded = () => {
  isPlaying.value = false
  playProgress.value = 0
  currentTime.value = 0
}

// 审核通过
const approveSound = async (sound) => {
  try {
    // TODO: 替换为实际API调用
    await new Promise(resolve => setTimeout(resolve, 600))
    
    // 更新本地数据
    if (sound === currentSound.value) {
      currentSound.value.status = 'approved'
    }
    
    const index = soundData.value.findIndex(item => item.id === sound.id)
    if (index !== -1) {
      soundData.value[index].status = 'approved'
    }
    
    message.success(`已通过声音"${sound.title}"的审核`)
  } catch (error) {
    message.error('操作失败')
    console.error(error)
  }
}

// 拒绝声音
const rejectSound = async (sound) => {
  try {
    // TODO: 替换为实际API调用
    await new Promise(resolve => setTimeout(resolve, 600))
    
    // 更新本地数据
    if (sound === currentSound.value) {
      currentSound.value.status = 'rejected'
    }
    
    const index = soundData.value.findIndex(item => item.id === sound.id)
    if (index !== -1) {
      soundData.value[index].status = 'rejected'
    }
    
    message.success(`已拒绝声音"${sound.title}"`)
  } catch (error) {
    message.error('操作失败')
    console.error(error)
  }
}

// 删除声音
const deleteSound = async (sound) => {
  try {
    // TODO: 替换为实际API调用
    await new Promise(resolve => setTimeout(resolve, 600))
    
    // 更新本地数据
    if (sound === currentSound.value) {
      previewModalVisible.value = false
    }
    
    soundData.value = soundData.value.filter(item => item.id !== sound.id)
    
    message.success(`已删除声音"${sound.title}"`)
  } catch (error) {
    message.error('删除失败')
    console.error(error)
  }
}

// 批量删除确认
const confirmBatchDelete = () => {
  confirmModalTitle.value = '批量删除确认'
  confirmModalContent.value = `您确定要删除选中的 ${selectedRowKeys.value.length} 个声音吗？此操作不可恢复。`
  confirmModalOkText.value = '删除'
  confirmModalDanger.value = true
  currentBatchAction.value = 'delete'
  confirmModalVisible.value = true
}

// 批量审核通过确认
const confirmBatchApprove = () => {
  confirmModalTitle.value = '批量审核确认'
  confirmModalContent.value = `您确定要批量通过选中的 ${selectedRowKeys.value.length} 个声音吗？`
  confirmModalOkText.value = '通过'
  confirmModalDanger.value = false
  currentBatchAction.value = 'approve'
  confirmModalVisible.value = true
}

// 批量拒绝确认
const confirmBatchReject = () => {
  confirmModalTitle.value = '批量拒绝确认'
  confirmModalContent.value = `您确定要批量拒绝选中的 ${selectedRowKeys.value.length} 个声音吗？`
  confirmModalOkText.value = '拒绝'
  confirmModalDanger.value = true
  currentBatchAction.value = 'reject'
  confirmModalVisible.value = true
}

// 处理批量操作
const handleBatchAction = async () => {
  try {
    // TODO: 替换为实际API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (currentBatchAction.value === 'delete') {
      // 批量删除
      soundData.value = soundData.value.filter(item => !selectedRowKeys.value.includes(item.id))
      message.success(`已删除 ${selectedRowKeys.value.length} 个声音`)
    } else if (currentBatchAction.value === 'approve') {
      // 批量通过
      soundData.value.forEach(item => {
        if (selectedRowKeys.value.includes(item.id)) {
          item.status = 'approved'
        }
      })
      message.success(`已批量通过 ${selectedRowKeys.value.length} 个声音`)
    } else if (currentBatchAction.value === 'reject') {
      // 批量拒绝
      soundData.value.forEach(item => {
        if (selectedRowKeys.value.includes(item.id)) {
          item.status = 'rejected'
        }
      })
      message.success(`已批量拒绝 ${selectedRowKeys.value.length} 个声音`)
    }
    
    // 清空选择
    selectedRowKeys.value = []
    confirmModalVisible.value = false
  } catch (error) {
    message.error('操作失败')
    console.error(error)
  }
}

// 格式化时长
const formatDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 获取分类名称
const getCategoryName = (category) => {
  const categoryMap = {
    music: '音乐',
    nature: '自然',
    voice: '人声',
    effect: '音效'
  }
  return categoryMap[category] || '未知'
}

// 获取分类颜色
const getCategoryColor = (category) => {
  const colorMap = {
    music: 'blue',
    nature: 'green',
    voice: 'purple',
    effect: 'orange'
  }
  return colorMap[category] || 'default'
}

// 获取状态徽标样式
const getStatusBadge = (status) => {
  const statusMap = {
    approved: 'success',
    pending: 'processing',
    rejected: 'error'
  }
  return statusMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    approved: '已审核',
    pending: '待审核',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  fetchSoundList()
})
</script>

<style scoped>
.sound-manage-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-title {
  display: flex;
  align-items: center;
}

.header-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #00AAFF;
}

h1 {
  margin: 0;
  color: #fff;
  font-size: 24px;
  text-shadow: 0 0 10px rgba(0, 170, 255, 0.5);
}

.header-actions {
  display: flex;
  gap: 12px;
}

.batch-toolbar {
  background: rgba(26, 31, 46, 0.7);
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.selection-info {
  color: rgba(255, 255, 255, 0.7);
}

.filter-section {
  margin-bottom: 20px;
  background: rgba(26, 31, 46, 0.7);
  padding: 16px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.table-container {
  background: rgba(26, 31, 46, 0.7);
  padding: 16px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 表格样式定制 */
:deep(.ant-table) {
  background: transparent !important;
  color: rgba(255, 255, 255, 0.85) !important;
}

:deep(.ant-table-thead > tr > th) {
  background: rgba(0, 0, 0, 0.2) !important;
  color: rgba(255, 255, 255, 0.85) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

:deep(.ant-table-tbody > tr.ant-table-row:hover > td) {
  background: rgba(0, 170, 255, 0.1) !important;
}

:deep(.ant-table-tbody > tr.ant-table-row-selected > td) {
  background: rgba(0, 170, 255, 0.2) !important;
}

:deep(.ant-pagination-item-active) {
  border-color: #00AAFF !important;
}

:deep(.ant-pagination-item-active a) {
  color: #00AAFF !important;
}

.sound-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.uploader-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 预览模态框 */
.sound-preview-modal {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sound-info h3 {
  color: #fff;
  margin-top: 0;
  margin-bottom: 12px;
}

.sound-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
}

.sound-meta p {
  margin: 4px 0 0;
}

.tag-list {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.audio-player {
  margin: 16px 0;
  padding: 16px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.play-button {
  font-size: 36px;
  color: #00AAFF;
  cursor: pointer;
  transition: all 0.3s;
}

.play-button:hover {
  transform: scale(1.1);
  color: #0095E6;
}

.progress-bar {
  flex-grow: 1;
  height: 6px;
  position: relative;
  cursor: pointer;
}

.progress-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.progress-current {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #00AAFF;
  border-radius: 3px;
  transition: width 0.1s linear;
}

.progress-handle {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: #fff;
  border-radius: 50%;
  border: 2px solid #00AAFF;
  box-shadow: 0 0 5px rgba(0, 170, 255, 0.5);
}

.time-display {
  min-width: 80px;
  text-align: right;
  color: rgba(255, 255, 255, 0.7);
  font-family: monospace;
  font-size: 14px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .page-header, .header-actions {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .ant-input-search {
    width: 100% !important;
  }
  
  .filter-section .ant-form {
    flex-direction: column;
  }
  
  .filter-section .ant-form-item {
    margin-bottom: 12px;
  }
}
</style> 