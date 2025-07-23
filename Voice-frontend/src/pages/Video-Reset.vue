<template>
  <div class="voice-changer">
    <h2 class="title">音视频声音置换</h2>

    <!-- 视频上传区域 -->
    <div class="upload-section">
      <div
        class="upload-area"
        @drop.prevent="handleDrop"
        @dragover.prevent
        @click="triggerFileInput"
      >
        <input
          type="file"
          ref="fileInput"
          accept="Video/*,audio/*"
          @change="handleFileChange"
          class="hidden"
        />
        <i class="upload-icon">🎥</i>
        <p>点击或拖拽上传音视频文件</p>
        <div v-if="isGeneratingPreview">
          <div class="loading-spinner"></div>
          <a-typography-text style="font-size: large; color: white">
            正在生成预览...
          </a-typography-text>
        </div>
        <div v-if="isGeneratedPreview">
          <CheckCircleOutlined style="color: green; font-size: large; margin-right: 8px" />
          <a-typography-text type="success" style="font-size: large">
            生成预览成功
          </a-typography-text>
        </div>
        <p v-if="mediaFile" class="selected-file">已选择: {{ mediaFile.name }}</p>
      </div>
    </div>

    <!-- 预览区域 -->
    <div v-if="mediaFile" class="preview-section">
      <h3>预览</h3>
      <VIDEO
        v-if="isVideo"
        ref="VideoPlayer"
        controls
        class="media-player"
        @timeupdate="handleTimeUpdate"
      >
        <source :src="mediaUrl" :type="mediaFile.type" />
        <track
          v-if="subtitlesUrl"
          kind="subtitles"
          srclang="zh"
          :src="subtitlesUrl"
          label="字幕"
          default
        />
      </VIDEO>
      <audio v-else ref="audioPlayer" controls class="media-player" @timeupdate="handleTimeUpdate">
        <source :src="mediaUrl" :type="mediaFile.type" />
      </audio>
    </div>
    <!-- 声音设置区域 -->
    <div class="voice-settings">
      <h3>声音设置</h3>

      <!-- 预设声音选择 -->
      <div class="setting-item">
        <label class="checkbox-label">
          <input type="checkbox" v-model="customVoice" /> 选择自定义声音
        </label>
        <div style="margin-left: 28px" v-if="!customVoice">
          <label>预设声音：</label>
          <a-select v-model:value="selectedVoice" :options="Voices" style="width: 200px"></a-select>
        </div>
        <div style="margin-left: 28px" v-if="customVoice">
          <label>自定义声音</label>
          <a-select
            v-model:value="selectedCustomVoice"
            :options="customOptions"
            style="width: 200px"
          ></a-select>
        </div>
      </div>

      <!-- 音调调节 -->
      <div class="setting-item">
        <label>音调调节：</label>
        <input type="range" v-model="pitch" min="0.5" max="2" step="0.1" class="slider" />
        <span>{{ pitch }}x</span>
      </div>

      <!-- 语速调节 -->
      <div class="setting-item">
        <label>语速调节：</label>
        <input type="range" v-model="speed" min="0.5" max="2" step="0.1" class="slider" />
        <span>{{ speed }}x</span>
      </div>
    </div>

    <!-- 字幕设置 -->
    <div class="subtitle-section">
      <h3>字幕设置</h3>
      <div class="subtitle-controls">
        <label class="checkbox-label">
          <input type="checkbox" v-model="autoGenerateSubtitles" /> 自动生成字幕
        </label>
        <label>
          选择字幕语言
          <a-select
            v-model:value="selectedLanguage"
            :options="language"
            style="width: 200px; margin-left: 24px"
          ></a-select>
        </label>
        <div class="subtitle-upload">
          <label>或上传字幕文件（.srt/.vtt）：</label>
          <input
            type="file"
            @change="handleSubtitleUpload"
            accept=".srt,.vtt"
            class="subtitle-input"
          />
        </div>
      </div>
    </div>

    <!-- 转换按钮 -->
    <button
      @click="startConversion"
      :disabled="!mediaFile || !selectedVoice || isConverting"
      class="convert-btn"
    >
      {{ isConverting ? '处理中...' : '开始转换' }}
    </button>
    <div class="hint-text" v-if="isConverting">{{ hintText }}</div>
    <a-progress
      v-if="isConverting"
      :stroke-color="{
        from: '#108ee9',
        to: '#87d068',
      }"
      :percent="progress"
      :status="status"
    />
    <!-- 下载区域 -->
    <div v-if="convertedUrl" class="download-section">
      <p>转换完成！</p>
      <div class="preview-converted">
        <video v-if="isVideo" controls class="media-player">
          <source :src="convertedUrl" type="video/mp4" />
          <track
            v-if="subtitlesUrl"
            kind="subtitles"
            srclang="zh"
            :src="subtitlesUrl"
            label="字幕"
            default
          />
        </video>
        <audio v-else controls class="media-player">
          <source :src="convertedUrl" type="audio/mp3" />
        </audio>
      </div>
      <a :href="convertedUrl" download class="download-btn">下载转换后的文件</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Ref } from 'vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { getCustomAudioUsingPost } from '@/api/audioCommunityController.ts'
import type { SelectProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { getVideoUrlUsingPost, videoResetUsingPost } from '@/api/videoController.ts'
import { CheckCircleOutlined } from '@ant-design/icons-vue'


const selectedCustomVoice = ref('')
const customOptions = ref<SelectProps['options']>()
const customVoice = ref(false)
const selectedLanguage = ref('zh')
const language = ref<SelectProps['options']>([
  {
    value: 'zh',
    label: '简体中文',
  },
  {
    value: 'en',
    label: '英文',
  },
])
const allVoices = ref<SelectProps['options']>()
const fixedVoices = [
  {
    label: 'Preset',
    options: [
      {
        value: '1',
        label: '男声1',
      },
      {
        value: '2',
        label: '男声2',
      },
      {
        value: '3',
        label: '女声1',
      },
      {
        value: '4',
        label: '女声2',
      },
    ],
  },
]
const Voices = ref<SelectProps['options']>([
  {
    label: 'Preset',
    options: [
      {
        value: 1,
        label: '男声(康辉)',
      },
      {
        value: 2,
        label: '标准男声',
      },
      {
        value: 3,
        label: '标准女声',
      },
      {
        value: 4,
        label: '女声（欧丽娟）',
      },
    ],
  },
])

interface Voice {
  id: number
  name: string
}

interface FileInputElement extends HTMLInputElement {
  files: FileList
}

// 文件相关
const fileInput: Ref<HTMLInputElement | null> = ref(null)
const mediaFile: Ref<File | null> = ref(null)
const mediaUrl: Ref<string> = ref('')
const isConverting = ref(false)
const isGeneratingPreview = ref(false)
const isGeneratedPreview = ref(false)
const convertedUrl: Ref<string> = ref('')
const subtitlesUrl: Ref<string> = ref('')

// 播放器引用
const VideoPlayer: Ref<HTMLVideoElement | null> = ref(null)
const audioPlayer: Ref<HTMLAudioElement | null> = ref(null)

// 声音设置
const selectedVoice: Ref<number> = ref(1)
const pitch = ref(1)
const speed = ref(1)

// 字幕设置
const autoGenerateSubtitles = ref(false)

// 计算是否为视频文件
const isVideo = computed(() => {
  return mediaFile.value?.type.startsWith('video/')
})

// 触发文件选择
const triggerFileInput = (): void => {
  fileInput.value?.click()
}
const progress = ref(0)
const hintText = ref('正在发起请求....')
const status = ref('active')
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
  if (data.task === 'videoReset') {
    if (data.status === 'progress') {
      progress.value = data.progress
      hintText.value = data.message
    }
    if (data.status === 'success') {
      status.value = 'success'
      console.log('任务完成')
    }
  }
}
// 处理文件选择
const handleFileChange = async (event: Event): Promise<void> => {
  isGeneratedPreview.value = false
  isGeneratingPreview.value = true
  const input = event.target as FileInputElement
  const file = input.files[0]
  if (file && (file.type.startsWith('video/') || file.type.startsWith('audio/'))) {
    const res = await getVideoUrlUsingPost({}, file)
    mediaFile.value = file
    if (res.data.code === 0) {
      mediaUrl.value = res.data.data
      isGeneratedPreview.value = true
      console.log(mediaUrl.value)
      message.success('视频上传成功')
    } else {
      message.error('视频上传失败：' + res.data.message)
    }
  } else {
    alert('请上传有效的音视频文件' + file.type)
  }
  isGeneratingPreview.value = false
}

// 处理拖拽上传
const handleDrop = (event: DragEvent): void => {
  const file = event.dataTransfer?.files[0]

  if (file && (file.type.startsWith('video/') || file.type.startsWith('audio/'))) {
    mediaFile.value = file
    mediaUrl.value = URL.createObjectURL(file)
  } else {
    alert('请上传有效的音视频文件')
  }
}

// 处理字幕上传
const handleSubtitleUpload = (event: Event): void => {
  const input = event.target as FileInputElement
  const file = input.files[0]

  if (file && (file.name.endsWith('.srt') || file.name.endsWith('.vtt'))) {
    subtitlesUrl.value = URL.createObjectURL(file)
  } else {
    alert('请上传.srt或.vtt格式的字幕文件')
  }
}

// 处理时间更新
const handleTimeUpdate = (): void => {
  // 这里可以添加字幕同步逻辑
}

// 开始转换
const startConversion = async (): Promise<void> => {
  isConverting.value = true
  const res = await videoResetUsingPost({
    filePath: mediaUrl.value,
    type: selectedVoice.value,
    language: selectedLanguage.value,
  })
  if (res.data.code === 0) {
    message.success('视频重置成功')
    convertedUrl.value = res.data.data || ''
  } else {
    message.error('视频重置失败：' + res.data.message)
  }
  isConverting.value = false
}
const dataList = ref<API.AudioFileVO[]>([])
// 获取数据
const fetchData = async () => {
  const res = await getCustomAudioUsingPost()
  if (res.data.data) {
    dataList.value = res.data.data ?? []
    const options = reactive<String[]>([])
    dataList.value.forEach((item) => {
      options.push(item.title || '未命名' + item.id)
    })
    // 将工程师数组转换为Select组件所需的格式
    customOptions.value = options.map((option) => ({
      value: 5,
      label: option, // 首字母大写处理
    }))
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.voice-changer {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: rgba(10, 15, 30, 0.8);
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 150, 255, 0.3);
  color: #e1e1ff;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 500;
  color: #4cc9f0;
  text-shadow: 0 0 10px rgba(76, 201, 240, 0.5);
  letter-spacing: 1px;
}

.upload-area {
  border: 2px dashed #3d5af1;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  margin-bottom: 20px;
  background-color: rgba(20, 30, 60, 0.6);
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #4cc9f0;
  box-shadow: 0 0 15px rgba(76, 201, 240, 0.5);
  transform: translateY(-5px);
}

.hidden {
  display: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #4cc9f0;
  text-shadow: 0 0 10px rgba(76, 201, 240, 0.7);
}

.preview-section {
  margin: 20px 0;
  background-color: rgba(20, 30, 60, 0.6);
  padding: 15px;
  border-radius: 12px;
  border: 1px solid #3d5af1;
}

.preview-section h3 {
  color: #4cc9f0;
  margin-bottom: 15px;
  font-weight: 500;
}

.media-player {
  width: 100%;
  max-height: 400px;
  margin: 10px 0;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(76, 201, 240, 0.3);
}

.voice-settings,
.subtitle-section {
  margin: 20px 0;
  padding: 20px;
  background-color: rgba(20, 30, 60, 0.6);
  border: 1px solid #3d5af1;
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(61, 90, 241, 0.2);
}

.voice-settings h3,
.subtitle-section h3 {
  color: #4cc9f0;
  margin-bottom: 15px;
  font-weight: 500;
}

.setting-item {
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 15px;
}

.voice-select {
  width: 200px;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #3d5af1;
  background-color: rgba(30, 40, 70, 0.8);
  color: #e1e1ff;
}

.slider {
  flex: 1;
  height: 6px;
  -webkit-appearance: none;
  background: linear-gradient(90deg, #3d5af1, #4cc9f0);
  border-radius: 10px;
  outline: none;
}

.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #4cc9f0;
  cursor: pointer;
  box-shadow: 0 0 5px rgba(76, 201, 240, 0.8);
}

.subtitle-controls {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.checkbox-label input[type='checkbox'] {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid #3d5af1;
  border-radius: 4px;
  background-color: rgba(30, 40, 70, 0.8);
  cursor: pointer;
  position: relative;
}

.checkbox-label input[type='checkbox']:checked::before {
  content: '✓';
  font-size: 14px;
  color: #4cc9f0;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.subtitle-upload {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.subtitle-input {
  padding: 8px 12px;
  background-color: rgba(30, 40, 70, 0.8);
  border: 1px solid #3d5af1;
  border-radius: 8px;
  color: #e1e1ff;
}

.hint-text {
  font-size: 14px;
  color: #4cc9f0;
  margin-top: 20px;
  margin-bottom: 10px;
  text-align: center;
}

.convert-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #3d5af1, #4cc9f0);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin: 20px 0;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(76, 201, 240, 0.4);
}

.convert-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(76, 201, 240, 0.6);
}

.convert-btn:active:not(:disabled) {
  transform: translateY(1px);
}

.convert-btn:disabled {
  background: linear-gradient(135deg, #3d5af1, #4cc9f0);
  opacity: 0.6;
  cursor: not-allowed;
}

.download-section {
  text-align: center;
  margin-top: 20px;
  padding: 20px;
  background-color: rgba(20, 30, 60, 0.6);
  border-radius: 12px;
  border: 1px solid #3d5af1;
  animation: glow 2s infinite alternate;
}

@keyframes glow {
  from {
    box-shadow: 0 0 10px rgba(76, 201, 240, 0.5);
  }
  to {
    box-shadow:
      0 0 20px rgba(76, 201, 240, 0.8),
      0 0 30px rgba(61, 90, 241, 0.5);
  }
}

.preview-converted {
  margin: 20px 0;
}

.download-btn {
  display: inline-block;
  padding: 12px 24px;
  background: linear-gradient(135deg, #4cc9f0, #3d5af1);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(76, 201, 240, 0.4);
}

.download-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(76, 201, 240, 0.6);
}

.selected-file {
  margin-top: 10px;
  color: #a0c0ff;
  font-style: italic;
}

.loading-spinner {
  display: inline-block;
  width: 24px;
  height: 24px;
  border: 3px solid rgba(76, 201, 240, 0.3);
  border-radius: 50%;
  border-top-color: #4cc9f0;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
