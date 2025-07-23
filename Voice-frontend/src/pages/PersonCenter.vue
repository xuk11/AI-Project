<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  EyeInvisibleOutlined,
  EyeOutlined,
  PlayCircleOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import {
  deleteAuditorypptUsingPost,
  getMyAuditorypptListUsingPost,
  updateAuditorypptUsingPost,
} from '@/api/auditorypptController.ts'
import {
  deleteVideoUsingPost,
  getMyVideoListUsingPost,
  updateVideoUsingPost,
} from '@/api/videoController.ts'
import { listCreateBooksUsingPost, listMyBooksUsingPost } from '@/api/articleController.ts'
import { useRouter } from 'vue-router'
import {
  deleteAudioCommunityUsingPost,
  listMyAudioCommunityVoByPageUsingPost,
  updateAudioUsingPost,
} from '@/api/audioCommunityController.ts'
import dayjs from 'dayjs'

const router = useRouter()
const value = ref('')

const dataList = ref<API.ArticleVO[]>([])
const myBookList = ref<API.ArticleVO[]>([])
const total = ref(0)
const myBooksTotal = ref(0)
const createBooksTotal = ref(0)
const audiosTotal = ref(0)
const loading = ref(true)
const bookShelfSearchParams = reactive<API.ArticleQueryRequest>({
  current: 1,
  pageSize: 8,
  sortField: 'createTime',
  sortOrder: 'descend',
  searchText: '',
})
const createBookSearchParams = reactive<API.ArticleQueryRequest>({
  current: 1,
  pageSize: 8,
  sortField: 'createTime',
  sortOrder: 'descend',
  searchText: '',
})
const audioSearchParams = reactive<API.ArticleQueryRequest>({
  current: 1,
  pageSize: 8,
  sortField: 'createTime',
  sortOrder: 'descend',
  searchText: '',
})
const bookShelfPagination = computed(() => {
  return {
    current: bookShelfSearchParams.current ?? 1,
    pageSize: bookShelfSearchParams.pageSize ?? 10,
    total: myBooksTotal.value,
    // 切换页号时，会修改搜索参数并获取数据
    onChange: (page: any, pageSize: any) => {
      bookShelfSearchParams.current = page
      bookShelfSearchParams.pageSize = pageSize
      fetchData()
    },
  }
})
const createBookPagination = computed(() => {
  return {
    current: createBookSearchParams.current ?? 1,
    pageSize: createBookSearchParams.pageSize ?? 10,
    total: createBooksTotal.value,
    // 切换页号时，会修改搜索参数并获取数据
    onChange: (page: any, pageSize: any) => {
      createBookSearchParams.current = page
      createBookSearchParams.pageSize = pageSize
      fetchData()
    },
  }
})
const audioPagination = computed(() => {
  return {
    current: audioSearchParams.current ?? 1,
    pageSize: audioSearchParams.pageSize ?? 10,
    total: audiosTotal.value,
    // 切换页号时，会修改搜索参数并获取数据
    onChange: (page: any, pageSize: any) => {
      audioSearchParams.current = page
      audioSearchParams.pageSize = pageSize
      fetchData()
    },
  }
})
const goToArticleDetail = (index: number) => {
  const url = router.resolve({
    path: '/article/detail/' + index,
  }).href
  window.open(url, '_blank')
}
const fetchData = async () => {
  loading.value = true
  const bookShelfRes = await listMyBooksUsingPost(bookShelfSearchParams)
  const createBookRes = await listCreateBooksUsingPost(createBookSearchParams)
  const audioRes = await listMyAudioCommunityVoByPageUsingPost(audioSearchParams)
  if (
    bookShelfRes.data.code === 0 &&
    createBookRes.data.code === 0 &&
    audioRes.data.code === 0 &&
    bookShelfRes.data.data &&
    audioRes.data.data &&
    createBookRes.data.data
  ) {
    dataList.value = bookShelfRes.data.data.records ?? []
    createBooksTotal.value = createBookRes.data.data.total ?? 0
    myBookList.value = createBookRes.data.data.records ?? []
    myBooksTotal.value = bookShelfRes.data.data.total ?? 0
    audioList.value = audioRes.data.data.records ?? []
    audiosTotal.value = audioRes.data.data.total ?? 0
  } else {
    message.error('获取数据失败')
  }
  loading.value = false
}

// 定义接口
interface BaseFile {
  id: number
  title: string
  thumbnail: string
  createTime: Date
  size: number
}

// 删除视频
const handleDeletePPT = async (id: number) => {
  try {
    const res = await deleteAuditorypptUsingPost({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      await fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    message.error('删除失败')
  } finally {
    await fetchPPTData()
  }
}
const handleDeleteAudio = async (id: number) => {
  try {
    const res = await deleteAudioCommunityUsingPost({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      await fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    message.error('删除失败')
  } finally {
    await fetchData()
  }
}

function secondsToHMS(seconds: number) {
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

interface VideoFile extends BaseFile {
  duration: string
}

interface PPTFile extends BaseFile {
  slideCount: number
}

// 下载视频
const handleDownload = (filePath: string) => {
  if (filePath) {
    window.open(filePath)
  } else {
    message.error('下载地址不存在')
  }
}
const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`
}
// 删除视频
const handleDelete = async (id: number) => {
  try {
    const res = await deleteVideoUsingPost({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      await fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    message.error('删除失败')
  } finally {
    await fetchPPTData()
  }
}
// 状态变量
const activeTab = ref<'bookshelf' | 'mybooks' | 'ppts' | 'myvideos' | 'myaudios'>('bookshelf')
const videoList = ref<API.Video[]>([])
const pptList = ref<API.Auditoryppt[]>([])
const audioList = ref<API.AudioCommunityVO[]>([])
const deleteModalVisible = ref<boolean>(false)
const fileToDelete = ref<VideoFile | PPTFile | null>(null)

// 编辑相关的状态
const editModalVisible = ref<boolean>(false)
const currentEditItem = ref<any>(null)
const editedTitle = ref('')
const isPublic = ref(true)
const editType = ref<'video' | 'audio' | 'ppt'>('video')

// 添加音频播放相关状态
const isPlayingAudio = ref<Record<number, boolean>>({})
const audioRefs = ref<Record<number, HTMLAudioElement>>({})

// 添加到组件生命周期钩子
onBeforeUnmount(() => {
  // 停止所有正在播放的音频
  Object.values(audioRefs.value).forEach((audio) => {
    if (audio && !audio.paused) {
      audio.pause()
    }
  })
})

// 视频缩略图缓存
const videoThumbnails = ref<Record<string, string>>({})

// 从视频中提取第一帧作为缩略图 - 改进提取视频缩略图的函数，同时用于普通视频
const extractVideoThumbnail = (video: any) => {
  // 如果已经有缓存的缩略图，则直接返回
  if (videoThumbnails.value[video.id || '']) {
    return videoThumbnails.value[video.id || '']
  }

  return new Promise<string>((resolve) => {
    const videoEl = document.createElement('video')
    videoEl.crossOrigin = 'anonymous' // 处理跨域问题
    videoEl.src = video.filePath || ''

    // 一旦视频的元数据加载完成，就可以播放
    videoEl.onloadedmetadata = () => {
      // 将视频跳转到 0.1 秒处，避免完全黑屏的首帧
      videoEl.currentTime = 0.1
    }

    // 当视频跳转到指定时间点后，截取当前帧
    videoEl.onseeked = () => {
      // 创建 canvas 元素来绘制视频帧
      const canvas = document.createElement('canvas')
      canvas.width = videoEl.videoWidth
      canvas.height = videoEl.videoHeight

      // 将视频帧绘制到 canvas 上
      const ctx = canvas.getContext('2d')
      if (ctx) {
        ctx.drawImage(videoEl, 0, 0, canvas.width, canvas.height)

        // 将 canvas 转换为图片 URL
        const thumbnailUrl = canvas.toDataURL('image/jpeg')

        // 缓存缩略图
        videoThumbnails.value[video.id || ''] = thumbnailUrl

        // 返回缩略图 URL
        resolve(thumbnailUrl)
      } else {
        resolve('')
      }
    }

    // 处理加载出错的情况
    videoEl.onerror = () => {
      resolve('')
    }
  })
}

// 处理所有视频的缩略图
const processAllThumbnails = async () => {
  // 处理PPT缩略图
  if (pptList.value && pptList.value.length > 0) {
    for (const ppt of pptList.value) {
      await extractVideoThumbnail(ppt)
    }
  }

  // 处理视频缩略图
  if (videoList.value && videoList.value.length > 0) {
    for (const video of videoList.value) {
      await extractVideoThumbnail(video)
    }
  }
}

// 替换原来的processPPTThumbnails函数调用
const fetchPPTData = async () => {
  try {
    const PPTres = await getMyAuditorypptListUsingPost()
    const videoRes = await getMyVideoListUsingPost()
    if (PPTres.data.code === 0 && videoRes.data.code === 0) {
      pptList.value = PPTres.data.data ?? []
      videoList.value = videoRes.data.data ?? []

      // 异步处理所有缩略图
      processAllThumbnails()
    } else {
      message.error('获取数据失败：' + PPTres.data.message)
    }
  } catch (error) {
    message.error('获取数据失败')
  }
}

// 音频播放控制函数
const toggleAudioPlay = (audio, index) => {
  const audioEl = audioRefs.value[audio.id]
  if (!audioEl) return

  // 如果当前音频正在播放，则暂停
  if (isPlayingAudio.value[audio.id]) {
    audioEl.pause()
    isPlayingAudio.value[audio.id] = false
  } else {
    // 暂停所有其他音频
    Object.entries(audioRefs.value).forEach(([id, el]) => {
      if (id !== audio.id.toString() && el && !el.paused) {
        el.pause()
        isPlayingAudio.value[parseInt(id)] = false
      }
    })

    // 播放当前音频
    audioEl.play()
    isPlayingAudio.value[audio.id] = true
  }
}

// 音频事件处理函数
const handleAudioEnded = (audioId) => {
  isPlayingAudio.value[audioId] = false
}

// 模拟数据加载
onMounted(() => {
  fetchPPTData()
  fetchData()
})

// 工具函数
const formatDate = (date: Date): string => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const formatSize = (bytes: number): string => {
  const sizes: string[] = ['B', 'KB', 'MB', 'GB']
  if (bytes === 0) return '0 B'
  const i: number = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)).toString())
  return Math.round(bytes / Math.pow(1024, i)) + ' ' + sizes[i]
}

// 编辑视频或音频信息
const handleEdit = (item: any, type: 'video' | 'audio' | 'ppt'): void => {
  currentEditItem.value = item
  editedTitle.value = item.title || item.name
  isPublic.value = item.isPublic !== false
  editType.value = type
  editModalVisible.value = true
}

// 保存编辑
const saveEdit = async (): Promise<void> => {
  try {
    if (!currentEditItem.value) return

    if (editType.value === 'video') {
      const res = await updateVideoUsingPost({
        id: currentEditItem.value.id,
        title: editedTitle.value,
      })

      if (res.data.code === 0) {
        message.success('视频信息更新成功')
        // 更新本地数据
        currentEditItem.value.title = editedTitle.value
        // 重新获取数据
        await fetchPPTData()
      } else {
        message.error('更新失败：' + res.data.message)
      }
    } else if (editType.value === 'audio') {
      const res = await updateAudioUsingPost({
        id: currentEditItem.value.id,
        title: editedTitle.value,
        isShare: isPublic.value ? 1 : 0,
      })

      if (res.data.code === 0) {
        message.success('音频信息更新成功')
        // 更新本地数据
        currentEditItem.value.title = editedTitle.value
        currentEditItem.value.isPublic = isPublic.value
        // 重新获取数据
        await fetchData()
      } else {
        message.error('更新失败：' + res.data.message)
      }
    } else if (editType.value === 'ppt') {
      const res = await updateAuditorypptUsingPost({
        id: currentEditItem.value.id,
        title: editedTitle.value,
      })
      if (res.data.code === 0) {
        message.success('音频信息更新成功')
        // 更新本地数据
        currentEditItem.value.title = editedTitle.value
        currentEditItem.value.isPublic = isPublic.value
        // 重新获取数据
        await fetchPPTData()
      } else {
        message.error('更新失败：' + res.data.message)
      }
    }
  } catch (error) {
    message.error('保存失败')
  } finally {
    editModalVisible.value = false
  }
}

// 取消编辑
const cancelEdit = (): void => {
  editModalVisible.value = false
  currentEditItem.value = null
}

// 添加视频预览函数
const previewVideoVisible = ref(false)
const currentPreviewVideo = ref('')

const previewVideo = (filePath: string) => {
  currentPreviewVideo.value = filePath
  previewVideoVisible.value = true
}

// 加载PPT列表后处理缩略图
const processPPTThumbnails = async () => {
  if (pptList.value && pptList.value.length > 0) {
    for (const ppt of pptList.value) {
      await extractVideoThumbnail(ppt)
    }
  }
}
</script>

<template>
  <div class="person-center">
    <a-row :gutter="[24, 24]">
      <!-- 页面标题 -->
      <a-col :span="24">
        <div class="page-header">
          <a-typography-title :level="2" style="color: white">
            <user-outlined />
            个人中心
          </a-typography-title>
        </div>
      </a-col>

      <!-- 内容区域 -->
      <a-col :span="24">
        <a-tabs v-model:activeKey="activeTab">
          <!-- 视频文件标签页 -->
          <a-tab-pane key="bookshelf" tab="我的书架" style="color: white; font-size: large">
            <div class="file-list">
              <a-list
                :grid="{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
                :data-source="dataList"
                :pagination="bookShelfPagination"
                :loading="loading"
              >
                <template #renderItem="{ item: article, index }">
                  <a-list-item @click="goToArticleDetail(article?.id)">
                    <!-- 流光特效卡片 -->
                    <div class="magic-card">
                      <div class="card-content">
                        <div class="book-cover">
                          <img :src="article?.coverImage" alt="封面" />
                        </div>
                        <div class="book-info">
                          <h3 class="book-title">{{ article?.title }}</h3>
                          <p class="book-author">作者: {{ article?.author || '未知' }}</p>
                          <p class="book-desc">{{ article.description || '暂无描述' }}</p>
                          <p class="book-date">{{ formatDate(article?.createTime) }}</p>
                        </div>
                      </div>
                    </div>
                  </a-list-item>
                </template>
              </a-list>
            </div>
          </a-tab-pane>
          <a-tab-pane key="mybooks" tab="我创建的书">
            <div class="file-list">
              <a-list
                :grid="{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
                :data-source="myBookList"
                :pagination="createBookPagination"
                :loading="loading"
              >
                <template #renderItem="{ item: article, index }">
                  <a-list-item @click="goToArticleDetail(article?.id)">
                    <!-- 流光特效卡片 -->
                    <div class="magic-card">
                      <div class="card-content">
                        <div class="book-cover">
                          <img :src="article?.coverImage" alt="封面" />
                        </div>
                        <div class="book-info">
                          <h3 class="book-title">{{ article?.title }}</h3>
                          <p class="book-author">作者: {{ article?.author || '未知' }}</p>
                          <p class="book-desc">{{ article.description || '暂无描述' }}</p>
                          <p class="book-date">{{ formatDate(article?.createTime) }}</p>
                        </div>
                      </div>
                    </div>
                  </a-list-item>
                </template>
              </a-list>
            </div>
          </a-tab-pane>
          <!-- 有声PPT标签页 -->
          <a-tab-pane key="ppts" tab="有声PPT" style="color: white; font-size: large">
            <div class="file-list">
              <a-list
                :grid="{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
                :data-source="pptList"
              >
                <template #renderItem="{ item: ppt }">
                  <a-list-item>
                    <!-- 流光特效卡片 -->
                    <div class="magic-card">
                      <div class="card-content">
                        <div class="book-cover ppt-cover" @click="previewVideo(ppt.filePath)">
                          <img
                            :src="
                              videoThumbnails[ppt.id || ''] ||
                              'https://via.placeholder.com/300x180?text=加载中...'
                            "
                            alt="PPT封面"
                            class="ppt-thumbnail"
                          />
                          <div class="play-icon">
                            <play-circle-outlined />
                          </div>
                          <div class="video-duration">{{ secondsToHMS(ppt.duration) }}</div>
                        </div>
                        <div class="book-info">
                          <h3 class="book-title">{{ ppt.name }}</h3>
                          <div class="ppt-actions">
                            <a-tooltip title="下载">
                              <download-outlined @click.stop="handleDownload(ppt.filePath || '')" />
                            </a-tooltip>
                            <a-tooltip title="编辑">
                              <edit-outlined @click.stop="handleEdit(ppt, 'ppt')" />
                            </a-tooltip>
                            <a-tooltip title="删除">
                              <delete-outlined @click.stop="handleDeletePPT(ppt.id || 0)" />
                            </a-tooltip>
                          </div>
                          <p class="book-desc">文件大小：{{ formatFileSize(ppt.fileSize || 0) }}</p>
                          <p class="book-date">
                            {{ dayjs(ppt.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </a-list-item>
                </template>
              </a-list>
            </div>
          </a-tab-pane>
          <!-- 新增：我创建的视频标签页 -->
          <a-tab-pane key="myvideos" tab="我创建的视频" style="color: white; font-size: large">
            <div class="file-list">
              <a-list
                :grid="{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
                :data-source="videoList"
              >
                <template #renderItem="{ item: video }">
                  <a-list-item>
                    <!-- 流光特效卡片 -->
                    <div class="magic-card">
                      <div class="card-content">
                        <div class="book-cover video-cover" @click="previewVideo(video.filePath)">
                          <img
                            :src="
                              videoThumbnails[video.id || ''] ||
                              'https://via.placeholder.com/300x180?text=视频预览'
                            "
                            alt="视频封面"
                            class="video-thumbnail"
                          />
                          <div class="play-icon">
                            <play-circle-outlined />
                          </div>
                          <div class="video-duration">{{ secondsToHMS(video.duration) }}</div>
                        </div>
                        <div class="book-info">
                          <h3 class="book-title">{{ video.title }}</h3>
                          <div class="video-actions">
                            <a-tooltip title="下载">
                              <download-outlined
                                @click.stop="handleDownload(video.filePath || '')"
                              />
                            </a-tooltip>
                            <a-tooltip title="编辑">
                              <edit-outlined @click.stop="handleEdit(video, 'video')" />
                            </a-tooltip>
                            <a-tooltip title="删除">
                              <delete-outlined @click.stop="handleDelete(video.id || 0)" />
                            </a-tooltip>
                          </div>
                          <p class="book-desc">
                            文件大小：{{ formatFileSize(video.fileSize || 0) }}
                          </p>
                          <p class="book-date">
                            {{ dayjs(video.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </a-list-item>
                </template>
              </a-list>
            </div>
          </a-tab-pane>
          <!-- 新增：我创建的音频标签页 -->
          <a-tab-pane key="myaudios" tab="我创建的音频" style="color: white; font-size: large">
            <div class="file-list">
              <a-list
                :grid="{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
                :data-source="audioList"
                :pagination="audioPagination"
              >
                <template #renderItem="{ item: audio }">
                  <a-list-item>
                    <!-- 流光特效卡片 -->
                    <div class="magic-card">
                      <div class="card-content">
                        <div class="book-cover audio-cover">
                          <div class="music-player-container">
                            <div class="song-disc" @click="toggleAudioPlay(audio, index)">
                              <div class="song-disc__bg"></div>
                              <div
                                class="song-disc__cover"
                                :class="{ rotate: isPlayingAudio[audio.id] }"
                              >
                                <div
                                  class="song-img"
                                  :class="{ play: isPlayingAudio[audio.id] }"
                                ></div>
                              </div>
                              <div
                                class="play-btn"
                                :class="{ play: isPlayingAudio[audio.id] }"
                              ></div>
                              <div
                                class="song-needle"
                                :class="{ play: isPlayingAudio[audio.id] }"
                              ></div>
                            </div>
                          </div>
                          <!-- 波形动画，仅在播放时显示 -->
                          <div class="wave-animation" v-if="isPlayingAudio[audio.id]">
                            <span v-for="n in 5" :key="n"></span>
                          </div>
                          <audio
                            class="audio-player"
                            :src="audio.filePath"
                            :ref="
                              (el) => {
                                if (el) audioRefs[audio.id] = el
                              }
                            "
                            @ended="handleAudioEnded(audio.id)"
                          ></audio>
                        </div>
                        <div class="book-info">
                          <h3 class="book-title">{{ audio.title }}</h3>
                          <div class="audio-actions">
                            <a-tooltip title="下载">
                              <download-outlined
                                @click.stop="handleDownload(audio.filePath || '')"
                              />
                            </a-tooltip>
                            <a-tooltip title="编辑">
                              <edit-outlined @click.stop="handleEdit(audio, 'audio')" />
                            </a-tooltip>
                            <a-tooltip title="删除">
                              <delete-outlined @click.stop="handleDeleteAudio(audio.id || 0)" />
                            </a-tooltip>
                            <a-tooltip :title="audio.isShare === 1 ? '公开' : '私密'">
                              <eye-outlined v-if="audio.isShare === 1" />
                              <eye-invisible-outlined v-else />
                            </a-tooltip>
                          </div>
                          <p class="book-desc">
                            文件大小：{{ formatFileSize(audio.fileSize || 0) }}
                          </p>
                          <p class="book-date">
                            {{ dayjs(audio.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </a-list-item>
                </template>
              </a-list>
            </div>
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>

    <!-- 确认删除对话框 -->
    <a-modal
      v-model:visible="deleteModalVisible"
      title="确认删除"
      @ok="confirmDelete"
      @cancel="cancelDelete"
      okText="确认"
      cancelText="取消"
    >
      <p>确定要删除该文件吗？此操作不可恢复。</p>
    </a-modal>

    <!-- 编辑信息对话框 -->
    <a-modal
      v-model:visible="editModalVisible"
      title="编辑信息"
      @ok="saveEdit"
      @cancel="cancelEdit"
      okText="保存"
      cancelText="取消"
    >
      <a-form layout="vertical">
        <a-form-item label="标题">
          <a-input v-model:value="editedTitle" placeholder="请输入标题" />
        </a-form-item>
        <a-form-item v-if="editType === 'audio'" label="权限设置">
          <a-switch v-model:checked="isPublic" checked-children="公开" un-checked-children="私密" />
          <span class="visibility-hint">{{ isPublic ? '他人可见' : '仅自己可见' }}</span>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 视频预览对话框 -->
    <a-modal
      v-model:visible="previewVideoVisible"
      title="视频预览"
      :footer="null"
      :width="800"
      centered
    >
      <div class="video-preview-container">
        <Video controls style="width: 100%">
          <source :src="currentPreviewVideo" type="video/mp4" />
          您的浏览器不支持视频标签。
        </Video>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.person-center {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.file-card {
  transition: all 0.3s;
}

.file-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.video-cover {
  position: relative;
  width: 100%;
  height: 180px;
  background: #0a0f18;
  overflow: hidden;
  cursor: pointer;
  border-radius: 4px;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-cover[data-v-26dce2f4],
.ppt-cover[data-v-26dce2f4] {
  position: relative;
  width: 100%;
  height: 200px;
  /* padding-top: 56.25%; */
  background: #f0f0f0;
  /* overflow: hidden; */
}

.ppt-cover {
  position: relative;
  width: 100%;
  height: 180px;
  background: #0a0f18;
  overflow: hidden;
  cursor: pointer;
  border-radius: 4px;
}

.ppt-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.ppt-cover:hover .ppt-thumbnail {
  transform: scale(1.05);
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 48px;
  opacity: 0.8;
  transition: all 0.3s ease;
}

.ppt-cover:hover .play-icon {
  color: #00baff;
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.1);
}

.video-cover img,
.ppt-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration,
.ppt-slides {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.65);
  color: white;
  border-radius: 4px;
  font-size: 12px;
}

.file-info {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

.file-info p {
  margin-bottom: 4px;
}

/* 卡片流光特效 */
.magic-card {
  position: relative;
  width: 100%;
  height: 350px;
  border-radius: 8px;
  padding: 3px;
  background: #131a28;
  cursor: pointer;
  transition: all 0.3s ease;
}

.magic-card::before {
  content: '';
  width: 104%;
  height: 102%;
  border-radius: 8px;
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
  border-radius: 6px;
  height: 100%;
  padding: 15px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.book-cover {
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 15px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
  overflow: hidden;
}

.book-cover img {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
  background: #0a0f18;
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #fff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 14px;
  color: #00baff;
  margin: 0 0 5px;
}

.book-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 auto;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.book-date {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin: 10px 0 0;
  text-align: right;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .magic-card {
    height: 320px;
  }

  .book-cover {
    height: 150px;
  }
}

/* 覆盖Ant Design的默认样式 */
.article-library .ant-list-item {
  padding: 12px !important;
}

.article-library .ant-pagination {
  margin-top: 30px;
  text-align: center;
}

.article-library .ant-spin-container {
  min-height: 200px;
}

/* 卡片内的操作按钮 */
.ppt-actions {
  display: flex;
  gap: 15px;
  margin: 8px 0;
}

.ppt-actions .anticon {
  font-size: 16px;
  color: #00baff;
  cursor: pointer;
  transition: all 0.3s;
}

.ppt-actions .anticon:hover {
  color: #3c67e3;
  transform: scale(1.2);
}

.video-preview-container {
  width: 100%;
  max-height: 70vh;
  overflow: hidden;
}

/* 视频卡片样式 */
.video-cover {
  position: relative;
  width: 100%;
  height: 180px;
  background: #0a0f18;
  overflow: hidden;
  cursor: pointer;
  border-radius: 4px;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  display: block;
}

.video-cover:hover .video-thumbnail {
  transform: scale(1.05);
}

/* 音频卡片样式 */
.audio-cover {
  position: relative;
  width: 100%;
  height: 180px;
  background: linear-gradient(45deg, #12172d, #1a2142);
  overflow: hidden;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
}

.audio-player {
  width: 100%;
  height: 36px;
  border-radius: 18px;
  background: rgba(0, 0, 0, 0.2);
}

/* 唱片样式（从VoiceLibrary复制而来）*/
.music-player-container {
  position: relative;
  width: 100%;
  height: 75%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.song-disc {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
  cursor: pointer;
  transform: scale(1);
  transition: transform 0.3s;
}

.song-disc:hover {
  transform: scale(1.05);
}

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

.song-disc__cover {
  position: absolute;
  z-index: 1;
  width: 70px;
  height: 70px;
  left: 50%;
  top: 50%;
  margin-left: -35px;
  margin-top: -35px;
  background: url('/cover.jpeg') no-repeat center;
  background-size: cover;
  box-shadow: 0 0 15px rgba(0, 170, 255, 0.6);
  border-radius: 50%;
}

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

.song-disc .play-btn {
  width: 25px;
  height: 25px;
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

.song-needle {
  position: absolute;
  z-index: 3;
  top: -22px;
  width: 40px;
  height: 60px;
  left: 55px;
  background: url('/needle-ip6.png') no-repeat;
  background-size: contain;
  transform: rotate(-30deg);
  transform-origin: 20% 10%;
  transition: transform 0.5s;
  opacity: 0.9;
}

/* 卡片内的操作按钮 */
.video-actions,
.audio-actions,
.ppt-actions {
  display: flex;
  gap: 15px;
  margin: 8px 0;
}

.video-actions .anticon,
.audio-actions .anticon,
.ppt-actions .anticon {
  font-size: 16px;
  color: #00baff;
  cursor: pointer;
  transition: all 0.3s;
}

.video-actions .anticon:hover,
.audio-actions .anticon:hover,
.ppt-actions .anticon:hover {
  color: #3c67e3;
  transform: scale(1.2);
}

.visibility-hint {
  margin-left: 10px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

/* 音频相关样式 */
.song-disc__cover.rotate {
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
</style>
