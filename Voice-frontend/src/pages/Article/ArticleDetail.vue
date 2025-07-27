<script setup lang="ts">
import {
  addBookshelfUsingPost,
  getArticleVoByIdUsingGet,
  isMyBookUsingPost,
} from '@/api/articleController.ts'
import { message, type SelectProps } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'
import router from '@/router'
import { getCustomAudioUsingPost, uploadAudioUsingPost } from '@/api/audioCommunityController.ts'

const isAuthor = ref(false)
// 预设音色选项
const Voices = ref<SelectProps['options']>([
  {
    label: '预设音色',
    options: [
      {
        value: 1,
        label: '男声(康辉)',
        sample:
          'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman1.wav',
      },
      {
        value: 2,
        label: '标准男声',
        sample:
          'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman2.wav',
      },
      {
        value: 3,
        label: '标准女声',
        sample:
          'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen1.wav',
      },
      {
        value: 4,
        label: '女声（欧丽娟）',
        sample:
          'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen2.wav',
      },
    ],
  },
])

// 安全获取音色选项的计算属性
const voiceOptions = computed(() => {
  return Voices.value?.[0]?.options || []
})

// 选中的音色
const selectedVoice = ref(1)
// 音频播放控制
const audioPlaying = ref(false)
const currentAudio = ref<HTMLAudioElement | null>(null)

// 自定义音频相关变量
const useCustomAudio = ref(false)
const customAudios = ref<API.AudioCommunityVO[]>([])
const selectedCustomAudio = ref<string | null>(null)
const showUploadModal = ref(false)
const uploadLoading = ref(false)
const uploadProgress = ref(0)
const AudioCommunity = ref<File | null>(null)
const audioTitle = ref('')
const updateMyArticles = () => {
  const url = router.resolve({
    path: '/update/article/' + props.id,
  }).href
  window.open(url)
}
const handleAddToShelf = async () => {
  try {
    const res = await addBookshelfUsingPost({
      articleId: props.id,
    })
    if (res.data.code === 0) {
      message.success('添加到书架成功')
    } else {
      message.error('添加到书架失败: ' + res.data.message)
    }
  } catch (error) {
    message.error('添加到书架失败: ' + error)
  }
}
// 获取自定义音频列表
const fetchCustomAudios = async () => {
  try {
    const res = await getCustomAudioUsingPost()
    if (res.data.code === 0 && res.data.data) {
      customAudios.value = res.data.data
    } else {
      message.error('获取自定义音频失败: ' + res.data.message)
    }
  } catch (error) {
    message.error('获取自定义音频失败: ' + error)
  }
}

// 处理音频文件选择
const handleAudioCommunityChange = (info: any) => {
  const file = info.fileList[0].originFileObj
  console.log(file)
  if (file) {
    if (file.type.includes('audio')) {
      AudioCommunity.value = file
      console.log(AudioCommunity)
    } else {
      message.error('请上传音频文件')
    }
  }
}

// 上传自定义音频
const uploadCustomAudio = async () => {
  if (!AudioCommunity.value || !audioTitle.value.trim()) {
    message.error('请选择音频文件并填写标题')
    return
  }
  uploadLoading.value = true
  try {
    const params = { biz: 'audio', title: audioTitle.value }
    // 上传文件
    const res = await uploadAudioUsingPost(params, {}, AudioCommunity.value, {})
    // 如果请求成功
    if (res.data.code === 0) {
      message.success('文件上传成功')
      await fetchCustomAudios()
    } else {
      // @ts-ignore
      message.error('文件上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('文件上传失败')
  } finally {
    uploadLoading.value = false
    showUploadModal.value = false
  }
}

// 音频播放函数
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

const article = ref<API.ArticleVO>()
const props = defineProps<{
  id: string | number
}>()

const options = {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit',
  hour12: false, // 使用24小时制
}
const handleStartReading = () => {
  // 将选中的音色作为参数传递，支持自定义音频
  let voiceParam

  if (useCustomAudio.value && selectedCustomAudio.value) {
    voiceParam = 5
  } else {
    voiceParam = selectedVoice.value
  }

  const url = router.resolve({
    path: '/read/' + props.id + '/' + voiceParam,
    query: { voice: voiceParam as any },
  }).href
  window.open(url, '_blank')
}
const fetchArticleDetail = async () => {
  try {
    console.log(props.id)
    const res = await getArticleVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      article.value = res.data.data
      const date = new Date(article.value.updateTime)
      article.value.updateTime = new Intl.DateTimeFormat('zh-CN', options).format(date)
    } else {
      message.error('获取文章失败' + res.data.message)
    }
  } catch (error) {
    message.error('获取文章失败' + error)
  }
}
const isMyBook = async () => {
  const res = await isMyBookUsingPost({
    articleId: props.id,
  })
  if (res.data.code === 0) {
    isAuthor.value = res.data.data || false
  } else {
    message.error('获取信息认证失败' + res.data.message)
  }
}
onMounted(() => {
  fetchArticleDetail()
  fetchCustomAudios()
  isMyBook()
})
</script>

<template>
  <a-layout class="article-layout">
    <a-layout-content>
      <!-- 文章头部信息区域 -->
      <a-card class="article-header" :bordered="false">
        <a-row type="flex" align="stretch">
          <a-col :xs="24" :sm="9" :md="8" :lg="7" class="book-cover-col">
            <div class="book-cover-container">
              <img :src="article?.coverImage" alt="封面" class="book-cover" />
              <div class="book-tag"></div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="15" :md="16" :lg="17" class="book-content-col">
            <h1 class="article-title">{{ article?.title }}</h1>
            <div class="article-meta">
              <span>作者：{{ article?.author }}</span>
              <span>更新时间：{{ article?.updateTime }}</span>
            </div>
            <a-card
              :bordered="false"
              title="作品简介"
              style="background-color: #666; color: white; margin-top: 24px"
            >
              <div
                v-for="(paragraph, index) in article?.summary"
                :key="index"
                class="intro-paragraph"
              >
                {{ paragraph }}
              </div>

              <div class="article-tags-small"></div>
            </a-card>

            <!-- 音色选择区域 -->
            <div class="voice-selection-container">
              <div class="voice-selection-title">
                <span>选择听书音色</span>
                <a-switch
                  v-model:checked="useCustomAudio"
                  class="custom-audio-switch"
                  checked-children="自定义音频"
                  un-checked-children="预设音色"
                />
              </div>

              <!-- 预设音色选择 -->
              <div class="voice-options" v-if="!useCustomAudio">
                <div
                  v-for="voice in voiceOptions"
                  :key="voice.value"
                  class="voice-option"
                  :class="{ active: selectedVoice === voice.value }"
                  @click="selectedVoice = voice.value"
                >
                  <div class="voice-option-inner">
                    <div class="voice-name">{{ voice.label }}</div>
                    <a-button
                      type="primary"
                      class="voice-preview-btn"
                      shape="circle"
                      @click.stop="playVoiceSample(voice.sample)"
                    >
                      <template #icon>
                        <span class="preview-icon">试听</span>
                      </template>
                    </a-button>
                  </div>
                </div>
              </div>

              <!-- 自定义音频选择 -->
              <div class="custom-audio-container" v-if="useCustomAudio">
                <div class="custom-audio-header">
                  <span class="section-subtitle">我的自定义音频</span>
                  <a-button type="primary" class="upload-audio-btn" @click="showUploadModal = true">
                    上传新音频
                  </a-button>
                </div>

                <div class="custom-audio-list">
                  <a-empty v-if="customAudios.length === 0" description="暂无自定义音频" />
                  <div
                    v-else
                    v-for="audio in customAudios"
                    :key="audio.id"
                    class="custom-audio-item"
                    :class="{ active: selectedCustomAudio === audio.id }"
                    @click="selectedCustomAudio = audio.id"
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
                        @click.stop="playVoiceSample(audio.filePath)"
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

            <a-row class="action-buttons">
              <a-col :span="6">
                <a-button type="primary" @click="handleStartReading">开始听书</a-button>
              </a-col>
              <a-col :span="6">
                <a-button type="dashed" @click="handleAddToShelf">加入书架</a-button>
              </a-col>
              <a-col :span="6">
                <a-button type="default">互动投票</a-button>
              </a-col>
              <a-col :span="6" v-if="isAuthor">
                <a-button type="primary" @click="updateMyArticles">继续添加内容</a-button>
              </a-col>
            </a-row>
          </a-col>
        </a-row>
      </a-card>
    </a-layout-content>
  </a-layout>

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
          <a-input style="color: black" v-model:value="audioTitle" placeholder="请输入音频标题" />
        </a-form-item>

        <a-form-item label="音频文件">
          <a-upload
            :maxCount="1"
            accept="audio/*"
            :showUploadList="true"
            :beforeUpload="() => false"
            @change="handleAudioCommunityChange"
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

<style scoped>
@property --rotate {
  syntax: '<angle>';
  initial-value: 132deg;
  inherits: false;
}

.article-layout {
  background: linear-gradient(#141e30, #243b55);
  padding: 24px;
  min-height: 100vh;
}

.article-header {
  margin-bottom: 24px;
  background-color: rgba(94, 97, 100, 0.45);
  border-radius: 8px;
}

.book-cover-col {
  padding-right: 24px;
  display: flex;
  flex-direction: column;
}

.book-content-col {
  padding-left: 0;
}

.book-cover-container {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-cover {
  width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.book-tag {
  position: absolute;
  bottom: 10px;
  left: 0;
  background-color: #ff4d4f;
  color: white;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 0 4px 4px 0;
}

.article-title {
  color: white;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 12px;
}

.article-meta {
  margin-bottom: 8px;
  color: white;
}

.article-meta span {
  margin-right: 16px;
}

.latest-chapter {
  margin-bottom: 12px;
  color: #1890ff;
}

.article-tags {
  margin-bottom: 16px;
}

.article-brief {
  font-size: 16px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.article-stats {
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
}

.stat-label {
  color: #666;
}

.action-buttons {
  position: relative;
  margin-top: 20px;
}

.action-buttons .ant-btn {
  width: 90%;
  height: 40px;
  font-size: 15px;
  border-radius: 4px;
}

.action-buttons .ant-btn-primary {
  background: linear-gradient(45deg, #3c67e3, #00baff);
  border: none;
  transition: all 0.3s ease;
}

.action-buttons .ant-btn-primary:hover {
  background: linear-gradient(45deg, #4e00c2, #3c67e3);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(60, 103, 227, 0.4);
}

.intro-paragraph {
  margin-bottom: 12px;
  line-height: 1.8;
  color: white;
}

/* 音色选择区域样式 */
.voice-selection-container {
  margin-top: 24px;
  position: relative;
  padding: 4px;
  border-radius: 10px;
  background: rgba(19, 26, 40, 0.6);
  overflow: hidden;
}

.voice-selection-container::before {
  content: '';
  width: 104%;
  height: 102%;
  border-radius: 10px;
  background-image: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  position: absolute;
  z-index: -1;
  top: -1%;
  left: -2%;
  animation: spin 6s linear infinite;
}

.voice-selection-container::after {
  position: absolute;
  content: '';
  top: calc(100% / 6);
  left: 0;
  right: 0;
  z-index: -1;
  height: 100%;
  width: 100%;
  margin: 0 auto;
  transform: scale(0.8);
  filter: blur(40px);
  background-image: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  opacity: 0.3;
  transition: opacity 0.5s;
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

.voice-selection-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  font-size: 18px;
  padding: 12px 16px;
  text-shadow: 0 0 8px rgba(93, 220, 255, 0.5);
  font-weight: 500;
}

/* 自定义音频切换开关 */
.custom-audio-switch {
  background: linear-gradient(45deg, #3c67e3, #4e00c2) !important;
}

:deep(.custom-audio-switch.ant-switch-checked) {
  background: linear-gradient(45deg, #5ddcff, #3c67e3) !important;
}

/* 自定义音频部分 */
.custom-audio-container {
  padding: 8px 16px 20px;
}

.custom-audio-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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

/* 已有的样式 */
.voice-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 8px 16px 20px;
  justify-content: center;
}

.voice-option {
  flex: 1;
  min-width: 120px;
  max-width: calc(50% - 6px);
  cursor: pointer;
  position: relative;
  padding: 2px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: rgba(19, 26, 40, 0.8);
}

.voice-option::before {
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

.voice-option.active::before {
  background: linear-gradient(45deg, #5ddcff, #3c67e3, #4e00c2);
}

.voice-option-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 6px;
  background: #1a2436;
  height: 100%;
}

.voice-name {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.voice-option:hover .voice-name,
.voice-option.active .voice-name {
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

@media (max-width: 768px) {
  .voice-options {
    flex-direction: column;
  }

  .voice-option {
    max-width: 100%;
  }
}

@media (max-width: 576px) {
  .book-cover-col {
    padding-right: 0;
    margin-bottom: 24px;
  }

  .book-cover-container {
    max-height: 350px;
  }

  .book-cover {
    max-height: 100%;
  }
}

input.ant-input.css-dev-only-do-not-override-b92jn9 {
  background: border-box;
  color: white;
}
</style>
