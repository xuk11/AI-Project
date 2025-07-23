<template>
  <!--  声音样本库-->
  <div class="home-vue">
    <!-- 添加动态背景效果 -->
    <div class="cyber-background">
      <div class="grid"></div>
      <div class="glow-1"></div>
      <div class="glow-2"></div>
      <div class="glow-3"></div>
    </div>
    <div class="content-wrapper">
      <a-tabs v-model:activeKey="activeKey" type="card" class="custom-tabs">
        <a-tab-pane key="1" tab="预设声音">
          <a-typography-text class="Title1">普通话</a-typography-text>
          <a-divider />
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
            :data-source="mandarin"
            :loading="loading"
          >
            <template #renderItem="{ item: audio, index }">
              <a-list-item style="padding: 18px">
                <div class="magic-card">
                  <div class="card-content">
                    <div
                      class="audio-cover"
                      :class="{ 'is-playing': isPlayingType(audio.id, audio.type) }"
                    >
                      <!--                      <img :alt="audio.picture" :src="isPlayingType(audio.id, audio.type) ? '../assets/audio_new.gif' : '../assets/audio_new.jpg'" />-->
                      <div class="audio-duration">{{ formatDuration(audio.duration) }}</div>
                      <div class="music-player-container">
                        <div class="song-disc" @click="togglePlay(audio, index, audio.type)">
                          <div class="song-disc__bg"></div>
                          <div
                            class="song-disc__cover"
                            :class="{ rotate: isPlayingType(audio.id, audio.type) }"
                          >
                            <div
                              class="song-img"
                              :class="{ play: isPlayingType(audio.id, audio.type) }"
                            >
                              <div class="default-cover"></div>
                            </div>
                          </div>
                          <div
                            class="play-btn"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                          <div
                            class="song-needle"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                        </div>
                      </div>

                      <!-- 波形动画，仅在播放时显示 -->
                      <div class="wave-animation" v-if="isPlayingType(audio.id, audio.type)">
                        <span v-for="n in 5" :key="n"></span>
                      </div>
                    </div>
                    <div class="audio-info">
                      <h3 class="audio-title">{{ audio.title }}</h3>
                      <p class="audio-author">{{ audio.name }}</p>
                      <div class="audio-meta">
                        <span>创建时间：{{ formatDate(audio.createTime) }}</span>
                        <span>文件大小：{{ formatFileSize(audio.fileSize) }}</span>
                      </div>
                      <p class="audio-desc">{{ audio.description || '暂无描述' }}</p>
                      <div class="audio-controls">
                        <audio
                          class="audio-player"
                          :src="audio.filePath"
                          @play="handlePlay(index, audio.type)"
                          @pause="handlePause(index, audio.type)"
                          @ended="handleEnded(index, audio.type)"
                        ></audio>

                        <div class="audio-actions">
                          <div class="audio-stats">
                            <span class="stat-item"
                              ><PlayCircleOutlined /> {{ audio.playCount || 0 }}</span
                            >

                            <span class="stat-item like-btn" @click.stop="handleLike(audio)">
                              <LikeOutlined :class="{ liked: likedComputed(audio) }" />
                              {{ audio.likeCount || 0 }}
                            </span>
                            <span class="stat-item download-btn" @click.stop="downloadAudio(audio)">
                              <DownloadOutlined /> 下载
                            </span>
                          </div>
                          <div class="audio-tags">
                            <tags :audio="{ tags: audio.tags }" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-list-item>
            </template>
          </a-list>

          <a-typography-text class="Title2">英语</a-typography-text>
          <a-divider />
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
            :data-source="English"
            :loading="loading"
          >
            <template #renderItem="{ item: audio, index }">
              <a-list-item style="padding: 18px">
                <div class="magic-card">
                  <div class="card-content">
                    <div
                      class="audio-cover"
                      :class="{ 'is-playing': isPlayingType(audio.id, audio.type) }"
                    >
                      <!--                      <img :alt="audio.picture" :src="isPlayingType(audio.id, audio.type) ? '../assets/audio_new.gif' : '../assets/audio_new.jpg'" />-->
                      <div class="audio-duration">{{ formatDuration(audio.duration) }}</div>
                      <div class="music-player-container">
                        <div class="song-disc" @click="togglePlay(audio, index, audio.type)">
                          <div class="song-disc__bg"></div>
                          <div
                            class="song-disc__cover"
                            :class="{ rotate: isPlayingType(audio.id, audio.type) }"
                          >
                            <div
                              class="song-img"
                              :class="{ play: isPlayingType(audio.id, audio.type) }"
                            >
                              <div class="default-cover"></div>
                            </div>
                          </div>
                          <div
                            class="play-btn"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                          <div
                            class="song-needle"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                        </div>
                      </div>

                      <!-- 波形动画，仅在播放时显示 -->
                      <div class="wave-animation" v-if="isPlayingType(audio.id, audio.type)">
                        <span v-for="n in 5" :key="n"></span>
                      </div>
                    </div>
                    <div class="audio-info">
                      <h3 class="audio-title">{{ audio.title }}</h3>
                      <p class="audio-author">{{ audio.name }}</p>
                      <div class="audio-meta">
                        <span>创建时间：{{ formatDate(audio.createTime) }}</span>
                        <span>文件大小：{{ formatFileSize(audio.fileSize) }}</span>
                      </div>
                      <p class="audio-desc">{{ audio.description || '暂无描述' }}</p>

                      <div class="audio-controls">
                        <audio
                          class="audio-player"
                          :src="audio.filePath"
                          @play="handlePlay(index, audio.type)"
                          @pause="handlePause(index, audio.type)"
                          @ended="handleEnded(index, audio.type)"
                        ></audio>

                        <div class="audio-actions">
                          <div class="audio-stats">
                            <span class="stat-item"
                              ><PlayCircleOutlined /> {{ audio.playCount || 0 }}</span
                            >
                            <span class="stat-item like-btn" @click.stop="handleLike(audio)">
                              <LikeOutlined :class="{ liked: likedComputed(audio) }" />
                              {{ audio.likeCount || 0 }}
                            </span>
                            <span class="stat-item download-btn" @click.stop="downloadAudio(audio)">
                              <DownloadOutlined /> 下载
                            </span>
                          </div>
                          <div class="audio-tags">
                            <tags :audio="{ tags: audio.tags }" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-list-item>
            </template>
          </a-list>

          <a-typography-text class="Title3">方言</a-typography-text>
          <a-divider />
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
            :data-source="dialect"
            :loading="loading"
          >
            <template #renderItem="{ item: audio, index }">
              <a-list-item style="padding: 18px">
                <div class="magic-card">
                  <div class="card-content">
                    <div
                      class="audio-cover"
                      :class="{ 'is-playing': isPlayingType(audio.id, audio.type) }"
                    >
                      <!--                      <img :alt="audio.picture" :src="isPlayingType(audio.id, audio.type) ? '../assets/audio_new.gif' : '../assets/audio_new.jpg'" />-->
                      <div class="audio-duration">{{ formatDuration(audio.duration) }}</div>
                      <div class="music-player-container">
                        <div class="song-disc" @click="togglePlay(audio, index, audio.type)">
                          <div class="song-disc__bg"></div>
                          <div
                            class="song-disc__cover"
                            :class="{ rotate: isPlayingType(audio.id, audio.type) }"
                          >
                            <div
                              class="song-img"
                              :class="{ play: isPlayingType(audio.id, audio.type) }"
                            >
                              <div class="default-cover"></div>
                            </div>
                          </div>
                          <div
                            class="play-btn"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                          <div
                            class="song-needle"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                        </div>
                      </div>

                      <!-- 波形动画，仅在播放时显示 -->
                      <div class="wave-animation" v-if="isPlayingType(audio.id, audio.type)">
                        <span v-for="n in 5" :key="n"></span>
                      </div>
                    </div>
                    <div class="audio-info">
                      <h3 class="audio-title">{{ audio.title }}</h3>
                      <p class="audio-author">{{ audio.name }}</p>
                      <div class="audio-meta">
                        <span>创建时间：{{ formatDate(audio.createTime) }}</span>
                        <span>文件大小：{{ formatFileSize(audio.fileSize) }}</span>
                      </div>
                      <p class="audio-desc">{{ audio.description || '暂无描述' }}</p>

                      <div class="audio-controls">
                        <audio
                          class="audio-player"
                          :src="audio.filePath"
                          @play="handlePlay(index, audio.type)"
                          @pause="handlePause(index, audio.type)"
                          @ended="handleEnded(index, audio.type)"
                        ></audio>

                        <div class="audio-actions">
                          <div class="audio-stats">
                            <span class="stat-item"
                              ><PlayCircleOutlined /> {{ audio.playCount || 0 }}</span
                            >
                            <span class="stat-item like-btn" @click.stop="handleLike(audio)">
                              <LikeOutlined :class="{ liked: likedComputed(audio) }" />
                              {{ audio.likeCount || 0 }}
                            </span>
                            <span class="stat-item download-btn" @click.stop="downloadAudio(audio)">
                              <DownloadOutlined /> 下载
                            </span>
                          </div>
                          <div class="audio-tags">
                            <tags :audio="{ tags: audio.tags }" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-list-item>
            </template>
          </a-list>
        </a-tab-pane>
        <a-tab-pane key="2" tab="自定义声音">
          <a-typography-text class="Title4">自定义声音</a-typography-text>
          <a-divider />
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
            :data-source="custom"
            :loading="loading"
          >
            <template #renderItem="{ item: audio, index }">
              <a-list-item style="padding: 18px">
                <div class="magic-card">
                  <div class="card-content">
                    <div
                      class="audio-cover"
                      :class="{ 'is-playing': isPlayingType(audio.id, audio.type) }"
                    >
                      <!--                      <img :alt="audio.picture" :src="isPlayingType(audio.id, audio.type) ? '../assets/audio_new.gif' : '../assets/audio_new.jpg'" />-->
                      <div class="audio-duration">{{ formatDuration(audio.duration) }}</div>
                      <div class="music-player-container">
                        <div class="song-disc" @click="togglePlay(audio, index, audio.type)">
                          <div class="song-disc__bg"></div>
                          <div
                            class="song-disc__cover"
                            :class="{ rotate: isPlayingType(audio.id, audio.type) }"
                          >
                            <div
                              class="song-img"
                              :class="{ play: isPlayingType(audio.id, audio.type) }"
                            >
                              <div class="default-cover"></div>
                            </div>
                          </div>
                          <div
                            class="play-btn"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                          <div
                            class="song-needle"
                            :class="{ play: isPlayingType(audio.id, audio.type) }"
                          ></div>
                        </div>
                      </div>

                      <!-- 波形动画，仅在播放时显示 -->
                      <div class="wave-animation" v-if="isPlayingType(audio.id, audio.type)">
                        <span v-for="n in 5" :key="n"></span>
                      </div>
                    </div>
                    <div class="audio-info">
                      <h3 class="audio-title">{{ audio.title }}</h3>
                      <p class="audio-author">{{ audio.name }}</p>
                      <div class="audio-meta">
                        <span>创建时间：{{ formatDate(audio.createTime) }}</span>
                        <span>文件大小：{{ formatFileSize(audio.fileSize) }}</span>
                      </div>
                      <p class="audio-desc">{{ audio.description || '暂无描述' }}</p>

                      <div class="audio-controls">
                        <audio
                          class="audio-player"
                          :src="audio.filePath"
                          @play="handlePlay(index, audio.type)"
                          @pause="handlePause(index, audio.type)"
                          @ended="handleEnded(index, audio.type)"
                        ></audio>

                        <div class="audio-actions">
                          <div class="audio-stats">
                            <span class="stat-item"
                              ><PlayCircleOutlined /> {{ audio.playCount || 0 }}</span
                            >
                            <span class="stat-item like-btn" @click.stop="handleLike(audio)">
                              <LikeOutlined :class="{ liked: likedComputed(audio) }" />
                              {{ audio.likeCount || 0 }}
                            </span>
                            <span class="stat-item download-btn" @click.stop="downloadAudio(audio)">
                              <DownloadOutlined /> 下载
                            </span>
                          </div>
                          <div class="audio-tags">
                            <tags :audio="{ tags: audio.tags }" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-list-item>
            </template>
          </a-list>
          <div class="upload-section">
            <div class="upload-header">
              <h2 class="section-title">上传自定义声音</h2>
              <p class="section-subtitle">上传或录制你的声音，创建个性化音频内容</p>
            </div>

            <a-divider class="glowing-divider" />

            <div class="upload-container">
              <div class="upload-card">
                <div class="upload-card-header">
                  <SoundOutlined class="card-icon" />
                  <h3>上传音频文件</h3>
                </div>
                <a-upload-dragger
                  :disabled="isUploaded"
                  class="cyber-upload"
                  v-model:fileList="fileList"
                  :name="fileName"
                  :multiple="false"
                  :progress="progressConfig"
                  :customRequest="handleUpload"
                  @change="handleChange"
                  @drop="handleDrop"
                  maxCount="3"
                  accept=".wav,.mp3,.flac"
                >
                  <div class="close-btn" v-if="fileList.length > 0">
                    <a-button
                      type="text"
                      shape="circle"
                      :icon="h(CloseOutlined)"
                      @click="closeUploading"
                    />
                  </div>
                  <div class="upload-content">
                    <p class="upload-icon">
                      <UploadOutlined />
                    </p>
                    <p class="upload-text">将音频拖到此处</p>
                    <p class="upload-hint">支持 mp3, wav, flac 格式，最大20MB</p>
                    <a-button class="cyber-btn" type="primary" v-if="fileList.length === 0">
                      <span>选择文件</span>
                    </a-button>
                  </div>
                </a-upload-dragger>
              </div>

              <div class="upload-card recording-card">
                <div class="upload-card-header">
                  <AudioOutlined class="card-icon" />
                  <h3>录制声音</h3>
                </div>
                <div class="record-content">
                  <div class="close-btn" v-if="recordAudioUrl">
                    <a-button
                      type="text"
                      shape="circle"
                      :icon="h(CloseOutlined)"
                      @click="closeRecording"
                    />
                  </div>

                  <div class="record-visual" :class="{ recording: isRecording }">
                    <div class="recording-indicator" v-if="isRecording">
                      <div class="pulse-ring"></div>
                      <div class="mic-icon">
                        <AudioOutlined />
                      </div>
                    </div>
                    <audio
                      v-if="recordAudioUrl"
                      :src="recordAudioUrl"
                      controls
                      class="record-player"
                    ></audio>
                    <div class="record-placeholder" v-if="!recordAudioUrl && !isRecording">
                      <AudioOutlined class="placeholder-icon" />
                      <p>点击下方按钮开始录音</p>
                    </div>
                  </div>

                  <div class="record-controls">
                    <a-button
                      v-if="!isRecording"
                      type="primary"
                      class="cyber-btn record-btn"
                      @click="startRecording"
                    >
                      <AudioOutlined />
                      <span>开始录音</span>
                    </a-button>

                    <a-button
                      v-if="isRecording"
                      danger
                      class="cyber-btn stop-btn"
                      @click="stopRecording"
                    >
                      <BorderOutlined />
                      <span>停止录音</span>
                    </a-button>

                    <a-button
                      v-if="recordAudioUrl"
                      type="primary"
                      class="cyber-btn upload-btn"
                      @click="uploadFile"
                    >
                      <UploadOutlined />
                      <span>上传录音</span>
                    </a-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
    <a-modal v-model:open="open" title="上传录音文件" @ok="handleOk" @cancel="handleCancel">
      <a-form>
        <a-form-item label="音频名称">
          <a-input v-model:value="title" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
// 数据
import { computed, h, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import type { UploadChangeParam, UploadProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import {
  isLikedUsingGet,
  likeAudioUsingPost,
  listAudioCommunityVoByPageUsingPost,
  playAudioUsingPost,
  uploadAudioUsingPost,
} from '@/api/audioCommunityController.ts'
import {
  AudioOutlined,
  BorderOutlined,
  CloseOutlined,
  DownloadOutlined,
  LikeOutlined,
  PlayCircleOutlined,
  SoundOutlined,
  UploadOutlined,
} from '@ant-design/icons-vue'
import Tags from '@/components/tags.vue'
import { getUuid } from 'ant-design-vue/es/vc-notification/HookNotification'
import RecordRTC, { StereoAudioRecorder } from 'recordrtc'

const uploadModel = ref(false)
const title = ref('未命名' + getUuid())
const open = ref(false)
const mandarinPicture = ref<string[]>([])
const isShare = ref(false)
const likedStatus = ref<{ [key: number]: boolean }>({})

const dataList = ref<API.AudioCommunityVO[]>([])
const mandarin = ref<API.AudioCommunityVO[]>([])
const English = ref<API.AudioCommunityVO[]>([])
const dialect = ref<API.AudioCommunityVO[]>([])
const custom = ref<API.AudioCommunityVO[]>([])
const audioRefs = ref([])
const isPlayingMandarin = ref<boolean[]>(mandarin.value.map((item) => false))
const isPlayingEnglish = ref<boolean[]>(English.value.map((item) => false))
const isPlayingDialect = ref<boolean[]>(dialect.value.map((item) => false))
const isPlayingCustom = ref<boolean[]>(custom.value.map((item) => false))
const isLiked = async (audio: API.AudioCommunityVO) => {
  const res = await isLikedUsingGet({
    id: audio.id,
  })
  if (res.data.code === 0) {
    likedStatus.value[audio.id] = res.data.data
    return res.data.data
  }
  return false
}

const likedComputed = computed(() => {
  return (audio: API.AudioCommunityVO) => {
    console.log(likedStatus.value)
    if (likedStatus.value[audio.id] === undefined) {
      isLiked(audio)
    }
    return likedStatus.value[audio.id] || false
  }
})

const handlePlay = (index: number, type: number) => {
  console.log(index, type)
  switch (type) {
    case 1:
      isPlayingMandarin.value[index] = true
      break
    case 2:
      isPlayingEnglish.value[index] = true
      break
    case 3:
      isPlayingDialect.value[index] = true
      break
    case 4:
      isPlayingCustom.value[index] = true
      break
    default:
      break
  }
}
const handlePause = (index: number, type: number) => {
  switch (type) {
    case 1:
      isPlayingMandarin.value[index] = false
      break
    case 2:
      isPlayingEnglish.value[index] = false
      break
    case 3:
      isPlayingDialect.value[index] = false
      break
    case 4:
      isPlayingCustom.value[index] = false
      break
    default:
      break
  }
}
const activeKey = ref('1')
// 是否正在录制语音
const isRecording = ref(false)
// 使用联合类型，进行媒体录制操作
const mediaRecorder = ref<MediaRecorder | null>(null)
const audioChunks = ref<Blob[]>([])
const audioBlob = ref<Blob | null>(null)
// 录音的 URL
const recordAudioUrl = ref('')
// 上传的 URL
const uploadAudioUrl = ref('')
// 是否已经上传文件
const isUploaded = ref(false)
// 是否正在上传文件
const loading = ref<boolean>(false)
// 在组件的数据部分定义 progress 和 loading 状态
const progress = ref<number>(0)
// 上传文件列表
const fileList = ref([])
//上传文件的名字
const fileName = ref('')
// 合成文本输入框的值
const textValue = ref('')
/**
 * 监听上传文件的状态
 * @param info
 */
const handleChange = (info: UploadChangeParam) => {
  fileName.value = info.file.name
  const status = info.file.status
  if (status !== 'uploading') {
  }
  if (status === 'done') {
    message.success('文件上传成功')
  } else if (status === 'error') {
    message.error('文件上传失败')
  }
}
/**
 * 进度条配置
 */
const progressConfig: UploadProps['progress'] = {
  strokeColor: {
    '0%': '#108ee9',
    '100%': '#87d068',
  },
  strokeWidth: 3,
  // @ts-ignore
  format: (percent) => `${parseFloat(percent.toFixed(2))}%`,
}
/**
 * 使用 watch 来监听 progressPercent 的变化并更新 progressConfig.percent
 */
watch(progress, (newPercent) => {
  if ('percent' in progressConfig) {
    progressConfig.percent = newPercent
  }
})

/**
 * 监听拖拽事件
 * @param e
 */
function handleDrop(e: DragEvent) {
  console.log(e)
}
/**
 * 上传文件
 * @param file
 * @param onProgress
 * @param onError
 * @param onSuccess
 */
const handleUpload = async ({ file, onProgress, onError, onSuccess }: any) => {
  progress.value = 0
  try {
    const params = { biz: 'audio', title: fileName.value }
    // 上传文件
    const res = await uploadAudioUsingPost(params, {}, file, {})
    // 如果请求成功
    // @ts-ignore
    if (res.data.code === 0) {
      // @ts-ignore
      uploadAudioUrl.value = res.data.data
      isUploaded.value = true
      // 调用 onSuccess 来通知 Ant Design 上传已完成
      onSuccess(res.data)
      await fetchData()
    } else {
      // @ts-ignore
      message.error('文件上传失败，' + res.data.message)
      onError(new Error('上传失败'))
    }
  } catch (error) {
    message.error('文件上传失败')
    onError(error)
  } finally {
    loading.value = false
  }
}

// 状态管理
const recorder = ref<RecordRTC | null>(null)
const audioUrl = ref<string | null>(null)

// 开始录音
const startRecording = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        sampleRate: 44100, // 指定采样率
        channelCount: 1, // 单声道
      },
    })

    recorder.value = new RecordRTC(stream, {
      type: 'audio',
      mimeType: 'audio/wav',
      recorderType: StereoAudioRecorder,
      timeSlice: 1000, // 每1秒生成一个数据块
      desiredSampRate: 44100,
      numberOfAudioChannels: 1,
      disableLogs: true, // 禁用调试日志
    })

    recorder.value.startRecording()
    isRecording.value = true
  } catch (error) {
    console.error('麦克风访问失败:', error)
    alert('请允许麦克风权限后重试')
  }
}

// 停止录音
const stopRecording = async () => {
  if (!recorder.value) return

  return new Promise<void>((resolve) => {
    recorder.value!.stopRecording(() => {
      audioBlob.value = recorder.value!.getBlob()
      audioUrl.value = URL.createObjectURL(audioBlob.value!)
      isRecording.value = false
      open.value = true
      resolve()
    })
  })
}

// 清理资源
onBeforeUnmount(() => {
  if (audioUrl.value) URL.revokeObjectURL(audioUrl.value)
  if (recorder.value) recorder.value.destroy()
})
/**
 * 关闭录音文件
 */
const closeRecording = () => {
  recordAudioUrl.value = ''
  isRecording.value = false
  fileList.value = []
  message.info('录音已清除')
}
/**
 * 关闭上传文件
 */
const closeUploading = () => {
  uploadAudioUrl.value = ''
  isUploaded.value = false
  fileList.value = []
  fileName.value = ''
  message.info('上传文件已清除')
}
const uploadFile = async () => {
  if (!audioBlob.value) {
    message.error('请先录制音频')
    return
  }
  open.value = true
}
const handleOk = async () => {
  if (!audioBlob.value) {
    message.error('请先录制音频')
    return
  }
  const wavFile = new File([audioBlob.value], `recording_${Date.now()}.wav`, {
    type: 'audio/wav',
  })
  try {
    const response = await uploadAudioUsingPost(
      { biz: 'audio', title: title.value },
      {},
      wavFile,
    )
    if (response.data.code === 0) {
      message.success('上传成功')
    } else {
      message.error('上传失败，' + response.message)
    }
  } catch (error) {
    console.error('上传错误:', error)
  }
  open.value = false
  await fetchData()
}
const total = ref(0)
const audioPlayer = ref<HTMLAudioElement | null>(null)
const isPlaying = ref<boolean[]>(dataList.value.map(() => false))
// 切换播放/暂停
const togglePlay = async (audio: API.AudioCommunityVO, index: number, type: number) => {
  const audioElements = document.querySelectorAll('audio')
  let targetAudio: HTMLAudioElement | null = null

  // 找到对应的音频元素
  audioElements.forEach((el) => {
    if (el.src === audio.filePath) {
      targetAudio = el as HTMLAudioElement
    }
  })

  if (!targetAudio) return

  // 如果当前正在播放，则暂停
  if (isPlayingType(audio.id, type)) {
    targetAudio.pause()
  } else {
    // 暂停其他所有音频
    audioElements.forEach((el) => {
      if (el !== targetAudio && !el.paused) {
        el.pause()
      }
    })

    // 播放当前音频
    targetAudio.play()

    // 更新播放计数（这里仅为演示，实际需要调用API）
    const res = await playAudioUsingPost({
      id: audio.id,
    })
    if (res.data.code === 0) {
      // 更新播放计数
      audio.playCount = res.data.data
    } else {
      message.error('更新播放计数失败，' + res.data.message)
    }
  }
}

// 音频播放结束处理
const handleEnded = (index: number, type: number) => {
  handlePause(index, type)
}

// 搜索条件
const searchParams = reactive<API.AudioCommunityQueryRequest>({
  current: 1,
  pageSize: 50,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  const res = await listAudioCommunityVoByPageUsingPost(searchParams)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    classifyDataList()
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}

// 分类函数
function classifyDataList() {
  // 清空原有的分类数组
  mandarin.value = []
  English.value = []
  dialect.value = []
  custom.value = []

  // 遍历 dataList 并根据 type 属性进行分类
  dataList.value.forEach((item) => {
    switch (item.type) {
      case 1:
        mandarin.value.push(item)
        break
      case 2:
        English.value.push(item)
        break
      case 3:
        dialect.value.push(item)
        break
      case 4:
        custom.value.push(item)
        break
      default:
        console.log(`未知类型: ${item.type}`)
    }
  })
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 添加格式化函数
const formatDate = (date: string | number): string => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}
const handleCancel = () => {
  open.value = false
}
const formatFileSize = (bytes: number): string => {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i)) + ' ' + sizes[i]
}

function formatDuration(seconds: number) {
  // 转换为整数以去掉小数部分
  seconds = Math.floor(seconds)

  // 计算小时数
  let hours = Math.floor(seconds / 3600)
  seconds %= 3600

  // 计算分钟数
  let minutes = Math.floor(seconds / 60)

  // 剩余的秒数
  seconds %= 60

  // 初始化结果数组，仅在有小时时添加小时部分
  let result = []
  if (hours > 0) {
    result.push(hours < 10 ? '0' + hours : hours)
  }

  // 添加分钟和秒数部分，确保它们始终为两位数
  result.push(minutes < 10 ? '0' + minutes : minutes)
  result.push(seconds < 10 ? '0' + seconds : seconds)

  // 返回格式化后的字符串
  return result.join(':')
}

// 添加点赞和下载功能
const handleLike = (audio: API.AudioCommunityVO) => {
  const res = likeAudioUsingPost({
    id: audio.id,
  })
  res.then((res) => {
    if (res.data.code === 0) {
      audio.likeCount = res.data.data
      likedStatus.value[audio.id] = !likedStatus.value[audio.id]
    } else {
      message.error('点赞失败，' + res.data.message)
    }
  })
}

const downloadAudio = (audio: API.AudioCommunityVO) => {
  if (!audio.filePath) {
    message.warning('文件路径不存在')
    return
  }

  try {
    const link = document.createElement('a')
    link.href = audio.filePath
    link.download = audio.title || '声音文件.mp3'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    message.success('下载完成')
  } catch (error) {
    console.error('下载失败:', error)
    message.error('下载失败')
  }
}

// 检查指定类型和ID的音频是否在播放
const isPlayingType = (id: number, type: number) => {
  switch (type) {
    case 1:
      return isPlayingMandarin.value.some(
        (playing, idx) => playing && mandarin.value[idx]?.id === id,
      )
    case 2:
      return isPlayingEnglish.value.some((playing, idx) => playing && English.value[idx]?.id === id)
    case 3:
      return isPlayingDialect.value.some((playing, idx) => playing && dialect.value[idx]?.id === id)
    case 4:
      return isPlayingCustom.value.some((playing, idx) => playing && custom.value[idx]?.id === id)
    default:
      return false
  }
}
</script>
<style scoped>
/* 添加页面容器样式 */
.page-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  padding: 24px;
}

.home-vue {
}

.content-wrapper {
  max-width: 1600px;
  margin: 0 auto;
  background: rgba(19, 26, 40, 0.85);
  border-radius: 16px;
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.3),
    0 0 40px rgba(0, 170, 255, 0.1);
  padding: 24px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(0, 170, 255, 0.2);
}

/* 自定义标签页样式 */
.custom-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 24px;
}

.custom-tabs :deep(.ant-tabs-tab) {
  border-radius: 8px 8px 0 0;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  border: none;
  padding: 12px 24px;
}

.custom-tabs :deep(.ant-tabs-tab-active) {
  background: #1890ff !important;
}

.custom-tabs :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: white !important;
  font-weight: bold;
}

/* 卡片样式优化 */
.homeCard {
  border-radius: 12px;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  width: 100%;
}

.homeCard:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 标题样式优化 */
.Title1,
.Title2,
.Title3,
.Title4 {
  font-family: 'Shadows Into Light', cursive;
  font-size: 32px;
  font-weight: bold;
  padding: 8px 0;
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.Title1 {
  background: linear-gradient(45deg, #2196f3, #4caf50);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.Title2 {
  background: linear-gradient(45deg, #ff9800, #f44336);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.Title3 {
  background: linear-gradient(45deg, #9c27b0, #2196f3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.Title4 {
  background: linear-gradient(45deg, #673ab7, #3f51b5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 分割线样式 */
.ant-divider {
  margin: 16px 0 24px;
  background: linear-gradient(to right, transparent, rgba(0, 0, 0, 0.15), transparent);
}

/* 音频播放器样式 */
audio {
  width: 100%;
  height: 40px;
  margin: 12px 0;
  border-radius: 20px;
}

/* 标签样式优化 */
.ant-tag {
  border-radius: 4px;
  padding: 4px 8px;
  margin: 4px;
  font-weight: 500;
}

/* 上传区域样式 */
.upload-audio {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 2px dashed #d9d9d9;
  transition: all 0.3s;
}

.upload-audio:hover {
  border-color: #1890ff;
  background: rgba(255, 255, 255, 0.95);
}

/* 录音区域样式 */
.recording-area {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 列表布局优化 */
.ant-list-item {
  padding: 12px !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-container {
    padding: 12px;
  }

  .content-wrapper {
    padding: 16px;
  }

  .Title1,
  .Title2,
  .Title3,
  .Title4 {
    font-size: 24px;
  }
}

.audio-cover {
  position: relative;
  background: linear-gradient(270deg, #b3d9ff, #e6f2ff, #ffffff);
  background-size: 600% 600%;
  animation: gradientAnimation 10s ease infinite;
  width: 100%;
  height: 250px;
  border-radius: 12px;
  overflow: hidden;
}

.audio-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.audio-duration {
  position: absolute;
  bottom: 10px;
  right: 10px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 20px;
  font-size: 12px;
  z-index: 2;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}

.file-info {
  margin: 12px 0;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.65);
}

.file-info p {
  margin-bottom: 4px;
  line-height: 1.5;
}

.audio-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 auto;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.audio-player {
  width: 100%;
  height: 36px;
  margin: 12px 0;
  border-radius: 18px;
}

.tag-container {
  margin-top: 8px;
  flex-wrap: wrap;
  gap: 4px;
}

.ant-tag {
  margin: 0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

/* 卡片标题样式 */
:deep(.ant-card-meta-title) {
  font-size: 16px;
  margin-bottom: 8px !important;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@keyframes gradientAnimation {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 导入流光特效所需的CSS变量 */
@property --rotate {
  syntax: '<angle>';
  initial-value: 132deg;
  inherits: false;
}

.home-vue {
  font-family: 'Roboto', sans-serif;
}

/* 自定义标签页样式 */
.custom-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 30px;
}

.custom-tabs :deep(.ant-tabs-tab) {
  border-radius: 8px 8px 0 0;
  transition: all 0.3s ease;
  background: rgba(20, 30, 50, 0.5);
  border: 1px solid rgba(0, 170, 255, 0.3);
  padding: 12px 24px;
}

.custom-tabs :deep(.ant-tabs-tab-active) {
  background: linear-gradient(90deg, rgba(0, 170, 255, 0.8), rgba(0, 120, 215, 0.8)) !important;
  border: 1px solid rgba(0, 200, 255, 0.5) !important;
}

.custom-tabs :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: white !important;
  font-weight: bold;
  text-shadow: 0 0 10px rgba(0, 200, 255, 0.5);
}

.custom-tabs :deep(.ant-tabs-ink-bar) {
  background: #00aaff;
  box-shadow: 0 0 10px rgba(0, 170, 255, 0.7);
}

/* 魔法卡片样式 - 流光特效 */
.magic-card {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 12px;
  padding: 3px;
  background: #131a28;
  cursor: pointer;
  transition: all 0.3s ease;
}

.magic-card::before {
  content: '';
  width: 104%;
  height: 102%;
  border-radius: 12px;
  background-image: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  position: absolute;
  z-index: -1;
  top: -1%;
  left: -2%;
  animation: spin 2.5s linear infinite;
}

.magic-card::after {
  position: absolute;
  content: '';
  top: 60px;
  left: 0;
  right: 0;
  z-index: -1;
  height: 100%;
  width: 100%;
  margin: 0 auto;
  transform: scale(0.8);
  filter: blur(40px);
  background-image: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  opacity: 0.6;
  transition: opacity 0.5s;
  animation: spin 2.5s linear infinite;
}

.magic-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
}

.magic-card:hover::after {
  opacity: 0.8;
  transform: scale(0.85);
}

@keyframes spin {
  0% {
    --rotate: 0deg;
  }
  100% {
    --rotate: 360deg;
  }
}

/* 卡片内容样式 */
.card-content {
  background: #131a28;
  border-radius: 10px;
  height: 100%;
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.audio-cover {
  height: 200px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(45deg, #12172d, #1a2142);
  transition: all 0.5s ease;
}

.audio-cover.is-playing {
  background: linear-gradient(45deg, #092444, #0c4a8c);
}

.audio-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.7;
  transition: all 0.5s;
}

.audio-cover.is-playing img {
  opacity: 0.9;
  transform: scale(1.05);
  filter: brightness(1.2);
}

.audio-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.audio-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #fff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.audio-author {
  font-size: 14px;
  color: #00baff;
  margin: 0 0 5px;
}

.audio-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 8px;
}

.audio-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 auto;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.play-btn {
  /* 重置播放按钮样式 */
  width: auto;
  height: auto;
  background: none;
  border: none;
  box-shadow: none;
}

.audio-duration {
  position: absolute;
  bottom: 10px;
  right: 10px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 20px;
  font-size: 12px;
  z-index: 2;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}

.audio-controls {
  margin-top: 10px;
}

.audio-player {
  width: 100%;
  height: 36px;
  margin: 10px 0;
  border-radius: 18px;
}

.audio-actions {
  margin-top: 10px;
}

.audio-stats {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 4px;
}

.like-btn,
.download-btn {
  cursor: pointer;
}

.like-btn:hover,
.download-btn:hover {
  color: #00aaff;
  background: rgba(0, 170, 255, 0.1);
}

.liked {
  color: #ff4d4f;
}

/* 波形动画 */
.wave-animation {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  position: absolute;
  bottom: 10px;
  left: 10px;
  z-index: 2;
}

.wave-animation span {
  display: inline-block;
  width: 3px;
  height: 100%;
  margin-right: 4px;
  background-color: rgba(0, 170, 255, 0.7);
  border-radius: 3px;
  animation: wave 1.2s infinite ease-in-out;
  box-shadow: 0 0 5px rgba(0, 170, 255, 0.5);
}

.wave-animation span:nth-child(1) {
  animation-delay: 0s;
}

.wave-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.wave-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

.wave-animation span:nth-child(4) {
  animation-delay: 0.6s;
}

.wave-animation span:nth-child(5) {
  animation-delay: 0.8s;
}

@keyframes wave {
  0%,
  100% {
    height: 8px;
  }
  50% {
    height: 20px;
  }
}

/* 上传区域样式 */
.upload-section {
  padding: 30px;
  background: rgba(20, 30, 50, 0.5);
  border-radius: 15px;
  border: 1px solid rgba(0, 170, 255, 0.2);
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.3);
  margin-bottom: 30px;
}

.upload-header {
  text-align: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 24px;
  color: #fff;
  font-weight: 600;
  margin-bottom: 10px;
  background: linear-gradient(90deg, #5ddcff, #3c67e3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 5px rgba(0, 170, 255, 0.3);
}

.section-subtitle {
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
  margin: 0;
}

.glowing-divider {
  height: 2px;
  background: linear-gradient(90deg, transparent, #00aaff, transparent);
  border: none;
  margin: 30px 0;
  box-shadow: 0 0 10px rgba(0, 170, 255, 0.5);
}

.upload-container {
  display: flex;
  gap: 30px;
  margin-top: 30px;
  flex-wrap: wrap;
}

.upload-card {
  flex: 1;
  min-width: 300px;
  background: rgba(15, 23, 42, 0.7);
  border-radius: 15px;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(0, 170, 255, 0.2);
}

.upload-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 170, 255, 0.3);
  border-color: rgba(0, 170, 255, 0.4);
}

.upload-card-header {
  background: linear-gradient(90deg, rgba(0, 170, 255, 0.2), rgba(60, 103, 227, 0.2));
  padding: 15px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid rgba(0, 170, 255, 0.2);
}

.card-icon {
  font-size: 20px;
  color: #00aaff;
}

.upload-card-header h3 {
  margin: 0;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.cyber-upload {
  height: 200px;
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}

.upload-icon {
  font-size: 40px;
  color: #00aaff;
  margin-bottom: 15px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.upload-text {
  font-size: 18px;
  color: #fff;
  margin-bottom: 10px;
}

.upload-hint {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 20px;
}

.cyber-btn {
  background: linear-gradient(90deg, #00aaff, #3c67e3);
  border: none;
  height: 40px;
  padding: 0 20px;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(0, 170, 255, 0.3);
  transition: all 0.3s ease;
}

.cyber-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.1) 100%
  );
  transform: rotate(45deg);
  pointer-events: none;
  transition: all 0.6s ease;
  z-index: 1;
}

.cyber-btn:hover {
  background: linear-gradient(90deg, #3c67e3, #00aaff);
  box-shadow: 0 0 20px rgba(0, 170, 255, 0.5);
  transform: translateY(-2px);
}

.cyber-btn:hover::before {
  left: 100%;
}

.cyber-btn span {
  position: relative;
  z-index: 2;
}

.record-content {
  padding: 20px;
  height: 300px;
  display: flex;
  flex-direction: column;
}

.record-visual {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  background: rgba(10, 18, 30, 0.5);
  margin-bottom: 20px;
  position: relative;
  border: 1px solid rgba(0, 170, 255, 0.1);
  transition: all 0.3s ease;
}

.record-visual.recording {
  border-color: rgba(255, 0, 0, 0.3);
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.2);
}

.recording-indicator {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pulse-ring {
  border-radius: 50%;
  height: 100%;
  width: 100%;
  position: absolute;
  animation: pulse 2s ease-out infinite;
  background-color: rgba(255, 0, 0, 0.3);
  z-index: 1;
}

@keyframes pulse {
  0% {
    transform: scale(0.3);
    opacity: 0.5;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

.mic-icon {
  background-color: #ff4d4f;
  color: white;
  border-radius: 50%;
  height: 40px;
  width: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
  animation: blink 1s ease-in-out infinite alternate;
}

@keyframes blink {
  0% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}

.record-player {
  width: 90%;
  height: 40px;
  border-radius: 20px;
}

.record-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.5);
}

.placeholder-icon {
  font-size: 30px;
  opacity: 0.5;
}

.record-controls {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.record-btn {
  background: linear-gradient(90deg, #00aaff, #3c67e3);
}

.stop-btn {
  background: linear-gradient(90deg, #ff4d4f, #ff7875);
}

.upload-btn {
  background: linear-gradient(90deg, #52c41a, #73d13d);
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 3;
}

.close-btn .ant-btn {
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.close-btn .ant-btn:hover {
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.1);
}

/* 分割线样式 */
.ant-divider {
  margin: 16px 0 24px;
  background: linear-gradient(to right, transparent, rgba(0, 170, 255, 0.3), transparent);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .upload-container {
    flex-direction: column;
  }

  .upload-card {
    width: 100%;
    margin-bottom: 20px;
  }

  .audio-stats {
    flex-wrap: wrap;
  }
}

/* 动态背景特效 */
.cyber-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
  background: linear-gradient(135deg, #0a1128 0%, #131a28 50%, #1a2142 100%);
}

.grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(rgba(0, 170, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 170, 255, 0.1) 1px, transparent 1px);
  background-size: 40px 40px;
  background-position: center center;
  animation: gridMove 20s linear infinite;
  opacity: 0.4;
  perspective: 1000px;
  transform-style: preserve-3d;
}

.glow-1,
.glow-2,
.glow-3 {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.glow-1 {
  top: -150px;
  left: -100px;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(0, 170, 255, 0.5) 0%, rgba(0, 0, 0, 0) 70%);
  animation: floatGlow1 25s ease-in-out infinite alternate;
}

.glow-2 {
  bottom: -200px;
  right: -150px;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(93, 220, 255, 0.5) 0%, rgba(0, 0, 0, 0) 70%);
  animation: floatGlow2 20s ease-in-out infinite alternate;
}

.glow-3 {
  top: 40%;
  left: 30%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(60, 103, 227, 0.5) 0%, rgba(0, 0, 0, 0) 70%);
  animation: floatGlow3 30s ease-in-out infinite alternate;
}

@keyframes gridMove {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 40px 40px;
  }
}

@keyframes floatGlow1 {
  0% {
    transform: translateY(0) translateX(0);
  }
  100% {
    transform: translateY(100px) translateX(50px);
  }
}

@keyframes floatGlow2 {
  0% {
    transform: translateY(0) translateX(0);
  }
  100% {
    transform: translateY(-100px) translateX(-80px);
  }
}

@keyframes floatGlow3 {
  0% {
    transform: translateY(0) translateX(0) scale(1);
  }
  100% {
    transform: translateY(80px) translateX(-30px) scale(1.2);
  }
}

/* 音乐播放器旋转特效样式 */
.music-player-container {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

/* 唱片样式 */
.song-disc {
  position: relative;
  width: 150px;
  height: 150px;
  margin: 0 auto;
  cursor: pointer;
  transform: scale(0.9);
  transition: transform 0.3s;
}

.song-disc:hover {
  transform: scale(1);
}

/* 唱片背景 */
.song-disc__bg {
  position: absolute;
  z-index: 2;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: url('/disc-ip6.png') no-repeat center;
  background-size: contain;
  opacity: 0.9;
}

/* 唱片封面 */
.song-disc__cover {
  background: url('/cover.jpeg') no-repeat center;
  position: absolute;
  z-index: 1;
  width: 90px;
  height: 90px;
  left: 50%;
  top: 50%;
  margin-left: -45px;
  margin-top: -45px;
  box-shadow: 0 0 20px rgba(0, 170, 255, 0.4);
  transition: transform 0.5s ease;
  border-radius: 50%;
  background-size: cover;
}

/* 默认图片样式 */
.song-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('/cover.jpeg') no-repeat center;
  background-size: cover;
}

.default-cover {
  display: none;
}

.default-cover::before,
.default-cover::after {
  display: none;
}

/* 播放时的旋转动画 */
.song-img.play {
  animation: circle 10s linear infinite;
}

@keyframes circle {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 播放按钮 */
.song-disc .play-btn {
  width: 45px;
  height: 45px;
  position: absolute;
  z-index: 10;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background: url('/play.png') no-repeat center;
  background-size: contain;
  opacity: 0.9;
  transition: all 0.3s ease;
}

.song-disc .play-btn.play {
  display: none;
}

/* 唱针样式 */
.song-needle {
  position: absolute;
  z-index: 3;
  top: -35px;
  width: 50px;
  height: 85px;
  left: 70px;
  background: url('/needle-ip6.png') no-repeat;
  background-size: contain;
  transform: rotate(-30deg);
  transform-origin: 20% 10%;
  transition: transform 0.5s;
  opacity: 0.8;
}

.song-needle.play {
  transform: rotate(0);
}

/* 播放时的背景发光效果 */
.audio-cover.is-playing::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(0, 170, 255, 0.2) 0%, transparent 70%);
  animation: pulse-glow 2s ease-in-out infinite alternate;
  z-index: 1;
}

@keyframes pulse-glow {
  0% {
    opacity: 0.3;
  }
  100% {
    opacity: 0.8;
  }
}

/* 脉冲动画 */
@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.5;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
