<template>
  <div class="ppt-converter-container">
    <a-card class="ppt-converter">
      <template #title>
        <div class="title-container">
          <file-text-outlined />
          <span>PPT转语音视频</span>
        </div>
      </template>

      <a-steps :current="currentStep" class="custom-steps">
        <a-step title="上传PPT" description="支持.ppt/.pptx格式" />
        <a-step title="预览确认" description="查看幻灯片内容" />
        <a-step title="选择语音" description="选择合适的配音" />
      </a-steps>

      <!-- 上传区域 -->
      <div class="upload-section">
        <a-upload-dragger
          :maxCount="1"
          accept=".ppt,.pptx,.PPT,.PPTX"
          :beforeUpload="() => false"
          @change="handleFileChange"
          :fileList="fileList"
          :multiple="false"
        >
          <p class="ant-upload-drag-icon">
            <inbox-outlined />
          </p>
          <p class="ant-upload-text">点击或拖拽上传PPT文件</p>
          <p class="ant-upload-hint">支持 .ppt 和 .pptx 格式文件</p>
          <div v-if="isGeneratingPreview" class="generate-status">
            <LoadingOutlined spin />
            <a-typography-text class="status-text"> 正在生成预览...</a-typography-text>
          </div>
          <div v-if="isGeneratedPreview" class="generate-status success">
            <CheckCircleOutlined />
            <a-typography-text class="status-text success"> 生成预览成功</a-typography-text>
          </div>
        </a-upload-dragger>
      </div>

      <!-- 预览区域 -->
      <div v-if="selectedFile && previewUrls.length > 0" class="preview-section">
        <div class="section-header">
          <eye-outlined />
          <span class="section-title">预览幻灯片</span>
        </div>

        <div class="preview-container">
          <a-spin :spinning="isGeneratingPreview">
            <template #indicator>
              <div class="generating-preview">
                <a-progress
                  :percent="previewProgress"
                  :status="previewProgress >= 100 ? 'success' : 'active'"
                  :show-info="false"
                  :stroke-width="4"
                  :stroke-color="{ from: '#3c67e3', to: '#5ddcff' }"
                />
                <div class="progress-info">
                  <loading-outlined v-if="previewProgress < 100" spin />
                  <check-circle-outlined v-else class="success-icon" />
                  <span>{{
                    previewProgress >= 100 ? '预览生成完成' : `正在生成预览 ${previewProgress}%`
                  }}</span>
                </div>
              </div>
            </template>

            <div class="preview-content">
              <div class="preview-image-container">
                <img
                  v-if="currentPreviewUrl"
                  :src="currentPreviewUrl"
                  alt="PPT预览"
                  class="preview-image"
                />
              </div>

              <div class="preview-controls">
                <a-button
                  type="primary"
                  shape="circle"
                  :disabled="currentSlide <= 0"
                  @click="prevSlide"
                  class="control-button"
                >
                  <template #icon>
                    <left-outlined />
                  </template>
                </a-button>

                <a-typography-text class="slide-counter">
                  {{ currentSlide + 1 }} / {{ totalSlides }}
                </a-typography-text>

                <a-button
                  type="primary"
                  shape="circle"
                  :disabled="currentSlide >= totalSlides - 1"
                  @click="nextSlide"
                  class="control-button"
                >
                  <template #icon>
                    <right-outlined />
                  </template>
                </a-button>
              </div>
            </div>
          </a-spin>
        </div>
      </div>

      <!-- 语音选择区域 -->
      <div v-if="selectedFile" class="voice-section">
        <div class="section-header">
          <sound-outlined />
          <span class="section-title">选择语音</span>
        </div>

        <div class="voice-content">
          <div class="custom-voice-toggle">
            <a-switch
              v-model:checked="customVoice"
              class="custom-audio-switch"
              checked-children="自定义音频"
              un-checked-children="预设音色"
            />
          </div>

          <!-- 预设音色选择 -->
          <div class="voice-options-container" v-if="!customVoice">
            <a-radio-group v-model:value="selectedVoice" class="voice-options">
              <a-radio-button
                v-for="voice in voices"
                :key="voice.id"
                :value="voice.id"
                class="voice-option"
              >
                {{ voice.name }}
              </a-radio-button>
            </a-radio-group>
          </div>

          <!-- 自定义音频选择 -->
          <div class="custom-audio-container" v-if="customVoice">
            <div class="custom-audio-header">
              <span class="section-subtitle">我的自定义音频</span>
              <a-button type="primary" class="upload-audio-btn" @click="showUploadModal = true">
                上传新音频
              </a-button>
            </div>

            <div class="custom-audio-list">
              <a-empty v-if="customVoices.length === 0" description="暂无自定义音频" />
              <div
                v-else
                v-for="audio in customVoices"
                :key="audio.id"
                class="custom-audio-item"
                :class="{ active: selectedVoice === audio.type }"
                @click="selectedVoice = audio.type"
              >
                <div class="custom-audio-inner">
                  <div class="audio-info">
                    <div class="audio-title">{{ audio.title }}</div>
                    <div class="audio-date">
                      {{ new Date(audio.createTime).toLocaleDateString() }}
                    </div>
                  </div>
                  <a-button
                    type="primary"
                    class="voice-preview-btn"
                    shape="circle"
                    @click.stop="playVoiceSample(audio.url)"
                  >
                    <template #icon>
                      <span class="preview-icon">试听</span>
                    </template>
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 转换按钮 -->
      <div class="action-section">
        <a-button
          type="primary"
          size="large"
          :loading="isConverting"
          :disabled="!selectedFile || !selectedVoice || isConverting || !currentPreviewUrl"
          @click="convertPPT"
          class="convert-button"
        >
          <template #icon>
            <thunderbolt-outlined />
          </template>
          {{ isConverting ? '正在转换中...' : '开始转换' }}
        </a-button>

        <div v-if="isConverting" class="conversion-status">
          <div class="hint-text">{{ hintText }}</div>
          <a-progress
            :stroke-color="{
              from: '#3c67e3',
              to: '#5ddcff',
            }"
            :percent="progress"
            :status="status"
          />
        </div>
      </div>

      <!-- 下载结果 -->
      <a-result
        v-if="downloadUrl"
        status="success"
        title="转换成功！"
        sub-title="您的有声PPT已经准备就绪"
        class="result-section"
      >
        <template #extra>
          <a-button type="primary" size="large" @click="handleDownload" class="download-button">
            <template #icon>
              <download-outlined />
            </template>
            下载有声PPT
          </a-button>
        </template>
      </a-result>
    </a-card>
  </div>

  <!-- 上传音频模态框 -->
  <a-modal
    v-model:visible="showUploadModal"
    title="上传自定义音频"
    :footer="null"
    class="upload-audio-modal"
  >
    <div class="upload-form">
      <a-form layout="vertical">
        <a-form-item label="音频标题">
          <a-input v-model:value="audioTitle" placeholder="请输入音频标题" />
        </a-form-item>

        <a-form-item label="音频文件">
          <a-upload
            :maxCount="1"
            accept="audio/*"
            :showUploadList="true"
            :beforeUpload="() => false"
            @change="handleAudioFileChange"
          >
            <a-button class="upload-btn">选择音频文件</a-button>
          </a-upload>
        </a-form-item>

        <a-form-item>
          <div class="upload-actions">
            <a-button @click="showUploadModal = false">取消</a-button>
            <a-button type="primary" :loading="uploadLoading" @click="uploadCustomAudio">
              上传
            </a-button>
          </div>
        </a-form-item>

        <a-progress
          v-if="uploadLoading"
          :percent="uploadProgress"
          :status="uploadProgress >= 100 ? 'success' : 'active'"
          :stroke-color="{ from: '#3c67e3', to: '#5ddcff' }"
        />
      </a-form>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, type Ref, watch } from 'vue'
import { convertPptUsingPost } from '@/api/pptController'
import type { UploadChangeParam } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DownloadOutlined,
  EyeOutlined,
  FileTextOutlined,
  InboxOutlined,
  LeftOutlined,
  LoadingOutlined,
  RightOutlined,
  SoundOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons-vue'
import { getCustomAudioUsingPost } from '@/api/audioCommunityController.ts'


// 接口定义
interface Voice {
  id: number
  name: string
}

const customVoice = ref(false)
// 状态管理
const currentStep = ref(0)
const fileList = ref([])
const selectedFile: Ref<File | null> = ref(null)
const selectedVoice = ref<number | null>(null)
const isConverting = ref(false)
const isGeneratingPreview = ref(false)
const isGeneratedPreview = ref(false)
const downloadUrl = ref('')
const previewUrls: Ref<string[]> = ref([])
const currentSlide = ref(0)
const totalSlides = ref(0)
const previewProgress = ref(0)
const status = ref('active')

// 自定义音频上传相关
const showUploadModal = ref(false)
const uploadLoading = ref(false)
const uploadProgress = ref(0)
const audioFile = ref<File | null>(null)
const audioTitle = ref('')
const currentAudio = ref<HTMLAudioElement | null>(null)
const audioPlaying = ref(false)

// 预设语音列表
const voices: Voice[] = [
  { id: 3, name: '标准女声' },
  { id: 1, name: '男声(康辉)' },
  { id: 4, name: '女声(欧丽娟)' },
  { id: 2, name: '标准男声' },
]

// 计算属性
const currentPreviewUrl = computed(() => previewUrls.value[currentSlide.value] || '')

// 文件处理
const handleFileChange = (info: UploadChangeParam) => {
  const file = info.file.originFileObj || info.file
  console.log('上传的文件:', file) // 添加日志便于调试
  if (!file) {
    message.error('文件上传失败，请重试')
    fileList.value = []
    return
  }

  if (isValidPPTFile(file)) {
    selectedFile.value = file
    fileList.value = [info.file]
    currentStep.value = 1
    generatePreview(file)
  } else {
    message.error(`不支持的文件格式：${file.name}。请上传PPT文件（.ppt或.pptx格式）`)
    fileList.value = []
  }
}

// 文件验证
const isValidPPTFile = (file: File): boolean => {
  const fileName = file.name.toLowerCase()
  return fileName.endsWith('.ppt') || fileName.endsWith('.pptx')
}

const FILE_HASH = ref()
// 预览生成
const generatePreview = async (file: File) => {
  isGeneratingPreview.value = true
  isGeneratedPreview.value = false
  previewProgress.value = 0

  try {
    const formData = new FormData()
    formData.append('file', file)
    const response = await fetch('http://i-2.gpushare.com:24924/upload-ppt', {
      method: 'POST',
      body: formData,
    })
    if (response.status === 200) {
      response.json().then(async (data) => {
        previewUrls.value = data.image_paths
        totalSlides.value = data.total
        FILE_HASH.value = data.hash
        localStorage.setItem('previewUrls', JSON.stringify(previewUrls.value))
        localStorage.setItem('totalSlides', JSON.stringify(totalSlides.value))
        localStorage.setItem('FILE_HASH', FILE_HASH.value)
        console.log('预览生成成功:', FILE_HASH.value)
      })
      previewProgress.value = 100
      currentSlide.value = 0
      currentStep.value = 2
      message.success('预览生成成功')
      isGeneratedPreview.value = true
    }
  } catch (error) {
    previewProgress.value = 0
    message.error('预览生成失败')
    previewUrls.value = []
    totalSlides.value = 0
  } finally {
    isGeneratingPreview.value = false
  }
}

// 幻灯片控制
const prevSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--
  }
}

const nextSlide = () => {
  if (currentSlide.value < totalSlides.value - 1) {
    currentSlide.value++
  }
}
const progress = ref(0)
const hintText = ref('正在发起请求....')
const ws = new WebSocket("ws://1.117.233.91/api/websocket")
ws.onopen = () => {
  console.log('WebSocket connection opened')
}

ws.onerror = (error) => {
  console.error('WebSocket error:', error)
}
ws.onmessage = (event) => {
  const data = JSON.parse(event.data)
  console.log('Received message from server:', data)
  if (data.task === 'ppt') {
    if (data.status === 'progress') {
      progress.value = data.progress
      hintText.value = data.message
    }
    if (data.status === 'completed') {
      status.value = 'success'
      console.log('任务完成')
    }
  }
}
// PPT转换
const convertPPT = async () => {
  if (!selectedFile.value || !selectedVoice.value) return
  isConverting.value = true
  try {
    if (previewUrls.value && previewUrls.value.length > 0) {
      const createAuditoryppt = {
        imageList: previewUrls.value,
        type: selectedVoice.value,
        fileHash: FILE_HASH.value,
      }
      const response = await convertPptUsingPost(createAuditoryppt)
      if (response.data.code === 0) {
        downloadUrl.value = response.data.data || ''
        message.success('转换成功')
      } else {
        message.error('转换失败' + response.data.message)
      }
    }
  } catch (error) {
    message.error('转换失败' + error)
  } finally {
    isConverting.value = false
  }
}

// 下载处理
const handleDownload = () => {
  if (downloadUrl.value) {
    window.open(downloadUrl.value)
  }
}
const customVoices = ref<API.AudioFileVO[]>([])
const fetchData = async () => {
  const res = await getCustomAudioUsingPost()
  if (res.data.data) {
    customVoices.value = res.data.data
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
onMounted(() => {
  fetchData()
})
// 监听文件变化
watch(selectedFile, (newFile) => {
  if (!newFile) {
    previewUrls.value = []
    totalSlides.value = 0
    currentSlide.value = 0
    currentStep.value = 0
  }
})

// 音频预览播放
const playVoiceSample = (sample: string) => {
  // 如果有正在播放的音频，先停止
  if (currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
    audioPlaying.value = false
  }

  // 创建新的音频对象并播放
  const audio = new Audio(sample)
  audio.onended = () => {
    audioPlaying.value = false
    currentAudio.value = null
  }
  audio
    .play()
    .then(() => {
      audioPlaying.value = true
      currentAudio.value = audio
    })
    .catch((err) => {
      message.error('音频播放失败: ' + err.message)
    })
}

// 处理音频文件选择
const handleAudioFileChange = (info: any) => {
  const file = info.file.originFileObj
  if (file) {
    if (file.type.includes('audio')) {
      audioFile.value = file
    } else {
      message.error('请上传音频文件')
    }
  }
}

// 上传自定义音频
const uploadCustomAudio = async () => {
  if (!audioFile.value || !audioTitle.value.trim()) {
    message.error('请选择音频文件并填写标题')
    return
  }

  uploadLoading.value = true
  uploadProgress.value = 0

  try {
    const formData = new FormData()
    formData.append('file', audioFile.value)
    formData.append('title', audioTitle.value)

    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 300)

    const res = await uploadCustomAudioUsingPost(formData)

    clearInterval(progressInterval)
    uploadProgress.value = 100

    if (res.data.code === 0) {
      message.success('上传成功')
      fetchData()
      showUploadModal.value = false
      audioFile.value = null
      audioTitle.value = ''
    } else {
      message.error('上传失败: ' + res.data.message)
    }
  } catch (error) {
    message.error('上传失败: ' + error)
  } finally {
    uploadLoading.value = false
  }
}
</script>

<style scoped>
/* 基础流光特效定义 */
@property --rotate {
  syntax: '<angle>';
  initial-value: 132deg;
  inherits: false;
}

.ppt-converter-container {
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 20px;
}

.ppt-converter {
  width: 100%;
  background: #131a28;
  color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  border: 3px solid transparent;
}

.ppt-converter::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  background: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  border-radius: 13px;
  z-index: -1;
  animation: spin 6s linear infinite;
}

.ppt-converter::after {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  background: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  filter: blur(20px);
  opacity: 0.5;
  z-index: -2;
  border-radius: 13px;
  animation: spin 6s linear infinite;
}

@keyframes spin {
  0% {
    --rotate: 0deg;
  }
  100% {
    --rotate: 360deg;
  }
}

.title-container {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.steps-section {
  margin: 30px 0;
  color: white !important;
}

.upload-section {
  margin: 40px auto;
  width: 80%;
  max-width: 600px;
}

.upload-area {
  background: rgba(28, 37, 55, 0.7) !important;
  border: 2px dashed rgba(255, 255, 255, 0.4) !important;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #ffffff !important;
  background: rgba(35, 45, 65, 0.8) !important;
}

.ant-upload-drag-icon {
  color: #ffffff;
  font-size: 48px;
  margin-bottom: 15px;
}

.ant-upload-text {
  color: #b9b1b1 !important;
  font-size: 18px;
  font-weight: 500;
}

.ant-upload-hint {
  color: rgba(255, 255, 255, 0.8) !important;
  margin-top: 5px;
}

.generate-status {
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #ffffff;
}

.generate-status.success {
  color: #ffffff;
}

.status-text {
  font-size: 16px;
  color: #ffffff;
}

.status-text.success {
  color: #ffffff;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 20px;
  padding: 0 20px;
}

.section-title {
  font-size: 16px;
  color: #ffffff;
}

.preview-section {
  margin: 40px 0;
  background: rgba(25, 35, 50, 0.8);
  padding: 20px 0;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.preview-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffffff, transparent);
}

.preview-container {
  width: 100%;
}

.generating-preview {
  padding: 24px;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.progress-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  color: #ffffff;
}

.success-icon {
  color: #ffffff;
}

.preview-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.preview-image-container {
  width: 90%;
  max-width: 1000px;
  display: flex;
  justify-content: center;
  background: rgba(15, 23, 35, 0.6);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 25px rgba(255, 255, 255, 0.2);
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
}

.slide-counter {
  font-size: 16px;
  color: #ffffff;
  min-width: 60px;
  text-align: center;
}

.control-button {
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
  border: none !important;
  transition: all 0.3s ease;
}

.control-button:hover:not(:disabled) {
  background: linear-gradient(45deg, #5ddcff, #3c67e3) !important;
  transform: scale(1.05);
}

.voice-section {
  margin: 40px 0;
  background: rgba(25, 35, 50, 0.8);
  padding: 20px 0;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.voice-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffffff, transparent);
}

.voice-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 0 20px;
}

.custom-voice-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

/* 自定义音频切换开关 */
.custom-audio-switch {
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
}

:deep(.custom-audio-switch.ant-switch-checked) {
  background: linear-gradient(45deg, #5ddcff, #3c67e3) !important;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ffffff;
  font-size: 15px;
  cursor: pointer;
  user-select: none;
  transition: color 0.3s ease;
}

.toggle-label:hover {
  color: #ffffff;
}

.voice-options-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

/* 自定义音频部分 */
.custom-audio-container {
  width: 100%;
  padding: 8px 0 20px;
}

.custom-audio-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 16px;
}

.section-subtitle {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.upload-audio-btn {
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
  border: none !important;
  box-shadow: 0 0 10px rgba(60, 103, 227, 0.4);
  transition: all 0.3s ease;
}

.upload-audio-btn:hover {
  background: linear-gradient(45deg, #5ddcff, #3c67e3) !important;
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(93, 220, 255, 0.6);
}

.custom-audio-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
  padding: 0 16px;
}

.custom-audio-item {
  position: relative;
  padding: 2px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: rgba(19, 26, 40, 0.8);
  cursor: pointer;
}

.custom-audio-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 8px;
  padding: 2px;
  background: linear-gradient(45deg, transparent, transparent);
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  transition: all 0.3s ease;
}

.custom-audio-item.active::before {
  background: linear-gradient(45deg, #5ddcff, #3c67e3, #4e00c2);
}

.custom-audio-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 6px;
  background: #1a2436;
  height: 100%;
}

.audio-info {
  flex: 1;
  overflow: hidden;
}

.audio-title {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.audio-date {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.custom-audio-item:hover .audio-title,
.custom-audio-item.active .audio-title {
  color: #00baff;
  text-shadow: 0 0 5px rgba(0, 186, 255, 0.5);
}

.voice-preview-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 186, 255, 0.2);
  border: 1px solid rgba(0, 186, 255, 0.4);
  transition: all 0.3s ease;
}

.voice-preview-btn:hover {
  background: rgba(0, 186, 255, 0.4);
  transform: scale(1.1);
}

.preview-icon {
  font-size: 12px;
  color: #fff;
}

/* 上传模态框样式 */
:deep(.upload-audio-modal .ant-modal-content) {
  background: #131a28;
  border: 1px solid rgba(93, 220, 255, 0.3);
  box-shadow: 0 0 20px rgba(93, 220, 255, 0.3);
}

:deep(.upload-audio-modal .ant-modal-header) {
  background: #131a28;
  border-bottom: 1px solid rgba(93, 220, 255, 0.3);
}

:deep(.upload-audio-modal .ant-modal-title) {
  color: #fff;
}

:deep(.upload-audio-modal .ant-modal-close) {
  color: #fff;
}

:deep(.upload-audio-modal .ant-form-item-label > label) {
  color: #fff;
}

.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.upload-btn {
  background: linear-gradient(45deg, rgba(60, 103, 227, 0.3), rgba(93, 220, 255, 0.3)) !important;
  border: 1px solid rgba(93, 220, 255, 0.4) !important;
  color: #fff !important;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: linear-gradient(45deg, rgba(60, 103, 227, 0.5), rgba(93, 220, 255, 0.5)) !important;
  transform: translateY(-2px);
}

.voice-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.voice-option {
  background: rgba(35, 45, 65, 0.8) !important;
  border: 1px solid rgba(255, 255, 255, 0.4) !important;
  color: #ffffff !important;
  transition: all 0.3s ease;
  min-width: 100px;
  text-align: center;
}

.voice-option:hover {
  background: rgba(60, 103, 227, 0.3) !important;
  border-color: #ffffff !important;
}

.voice-option[class*='-checked'] {
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
  border-color: #ffffff !important;
  color: white !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.4);
}

.action-section {
  margin: 40px auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80%;
  max-width: 600px;
}

.convert-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
  border: none !important;
  border-radius: 25px !important;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  color: #ffffff !important;
}

.convert-button:hover:not(:disabled) {
  background: linear-gradient(45deg, #5ddcff, #3c67e3) !important;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
}

.convert-button:disabled {
  opacity: 0.5;
  background: rgba(60, 103, 227, 0.3) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

.conversion-status {
  width: 100%;
  margin-top: 24px;
}

.hint-text {
  font-size: 14px;
  color: #ffffff;
  margin-bottom: 10px;
  text-align: center;
}

.result-section {
  background: rgba(25, 35, 50, 0.6);
  border-radius: 8px;
  padding: 30px;
  margin-top: 30px;
}

.download-button {
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
  border: none !important;
  height: 45px;
  font-size: 16px;
  border-radius: 22.5px !important;
  padding: 0 30px;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  color: #ffffff !important;
}

.download-button:hover {
  background: linear-gradient(45deg, #5ddcff, #3c67e3) !important;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

/* 定制Ant Design组件 */
:deep(.ant-card-head) {
  background: rgba(19, 26, 40, 0.95);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.ant-card-head-title),
:deep(.ant-card-head .ant-space) {
  color: #ffffff;
}

:deep(.ant-card-body) {
  background: #131a28;
}

:deep(.ant-steps-item-title) {
  color: #ffffff !important;
}

:deep(.ant-steps-item-description) {
  color: rgba(255, 255, 255, 0.8) !important;
}

:deep(.ant-steps-item-process .ant-steps-item-icon) {
  background: #3c67e3;
  border-color: #3c67e3;
}

:deep(.ant-steps-item-finish .ant-steps-item-icon) {
  background: transparent;
  border-color: #ffffff;
}

:deep(.ant-steps-item-finish .ant-steps-icon) {
  color: #ffffff;
}

:deep(.ant-steps-item-finish .ant-steps-item-tail::after) {
  background-color: #ffffff;
}

:deep(.ant-result-title),
:deep(.ant-result-subtitle) {
  color: #ffffff;
}

:deep(.ant-progress-inner) {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 修改svg图标颜色 */
:deep(.anticon svg) {
  fill: #ffffff;
}

:deep(.ant-steps-icon) {
  color: #ffffff;
}

:deep(.anticon-inbox svg) {
  fill: #ffffff;
}

@media (min-width: 1600px) {
  .ppt-converter-container {
    max-width: 1600px;
  }
}

@media (max-width: 768px) {
  .ppt-converter-container {
    padding: 0 10px;
  }

  .upload-section,
  .action-section {
    width: 95%;
  }

  .voice-content {
    flex-direction: column;
  }
}

.custom-steps .ant-steps-item-title,
.custom-steps .ant-steps-item-description,
.custom-steps .ant-steps-item-wait .ant-steps-item-icon,
.custom-steps .ant-steps .ant-steps-item-wait .ant-steps-item-icon {
  color: white !important; /* 设置文字颜色 */
}

.ant-steps
  .ant-steps-item:not(.ant-steps-item-active)
  > .ant-steps-item-container[role='button']
  .ant-steps-item-icon
  .ant-steps-icon {
  color: white !important;
}

/* 更改步骤条线的颜色为白色 */
.custom-steps .ant-steps-item-process {
  border-color: white !important;
}

.custom-steps .ant-steps-item-finish .ant-steps-item-icon::before {
  background: white !important;
}

/* 如果有背景色干扰，可以同时设置背景色 */
.custom-steps {
  background-color: transparent !important;
}
</style>
