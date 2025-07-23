<template>
  <div id="read">
    <div :class="['reading-container', { 'night-mode': isNightMode }]">
      <a-layout :style="{ backgroundColor: themes[currentTheme] }">
        <!-- 左侧辅助功能栏 -->
        <div class="left-sidebar">
          <div class="sidebar-item">反馈</div>
          <div class="sidebar-item">指南</div>
          <div class="sidebar-item">举报</div>
          <div class="sidebar-item">旧版</div>
        </div>
        <!-- 中间内容区 -->
        <a-layout-content
          class="article-content"
          :style="{ width: `${pageWidth}px`, backgroundColor: themes[currentTheme] }"
        >
          <!-- 章节信息 -->
          <div class="chapter-header">
            <h1 class="chapter-title">
              {{ currentChapter?.chapterTitle || article?.title }}
            </h1>
            <div class="chapter-meta">
              <span><book-outlined /> {{ article?.title }} </span>
              <span><user-outlined /> {{ article?.author }}</span>
              <span><font-size-outlined /> {{ currentChapter?.wordCount }}字</span>
              <span><clock-circle-outlined /> {{ article?.updateTime }}</span>
            </div>
          </div>

          <!-- 章节内容 -->
          <div
            class="chapter-content"
            :style="{ fontSize: `${fontSize}px`, fontFamily: fontFamily }"
            @mouseup="handleTextSelection"
          >
            <p
              v-for="(paragraph, index) in article?.content"
              :key="index"
              class="paragraph"
              :class="{ highlight: currentHighlightIndex === index }"
            >
              {{ paragraph }}
            </p>
          </div>

          <!-- 选中文本后的浮动菜单 -->
          <div
            v-if="showSelectionMenu"
            class="selection-menu"
            :style="{ top: selectionMenuPosition.y + 'px', left: selectionMenuPosition.x + 'px' }"
          >
            <a-button
              class="selection-menu-item"
              type="primary"
              @click="openAIChat('analyze', selectedText)"
            >
              <robot-outlined />
              AI赏析
            </a-button>
            <a-button
              class="selection-menu-item"
              type="primary"
              @click="openAIChat('explain', selectedText)"
            >
              <bulb-outlined />
              AI解释
            </a-button>
          </div>

          <!-- 底部内容 -->
          <div class="chapter-footer">
            <!--          <a-pagination-->
            <!--            :total="100"-->
            <!--            :current="currentPage"-->
            <!--            :pageSize="1"-->
            <!--            show-quick-jumper-->
            <!--            @change="onPageChange"-->
            <!--          />-->
          </div>
        </a-layout-content>

        <!-- 右侧功能栏 -->
        <div class="right-sidebar">
          <div class="sidebar-button" @click="showCatalogModal">
            <unordered-list-outlined />
            <div>目录</div>
          </div>
          <div class="sidebar-button" @click="gotoArticleDetail">
            <read-outlined />
            <div>书详情</div>
          </div>
          <div class="sidebar-button">
            <book-outlined />
            <div>加书架</div>
          </div>
          <div class="sidebar-button" @click="openAIChat()">
            <robot-outlined />
            <div>智能对话</div>
          </div>
          <div class="sidebar-button" @click="toggleNightMode">
            <bulb-outlined v-if="!isNightMode" />
            <bulb-filled v-else />
            <div>夜间</div>
          </div>
          <div class="sidebar-button" @click="showSettingsModal">
            <setting-outlined />
            <div>设置</div>
          </div>
          <div class="sidebar-button" @click="startAudioReading">
            <mobile-outlined />
            <div>有声阅读</div>
          </div>
          <div class="sidebar-button" @click="scrollToTop">
            <to-top-outlined />
          </div>
        </div>
      </a-layout>
    </div>

    <!-- 设置模态框 -->
    <a-modal
      v-model:visible="settingsVisible"
      :footer="null"
      :mask-closable="true"
      width="600px"
      :closable="true"
      centered
      title="设置"
    >
      <div class="settings-container">
        <!-- 阅读主题 -->
        <div class="settings-section">
          <div class="settings-label">阅读主题</div>
          <div class="theme-options">
            <div
              v-for="(color, index) in themesArray"
              :key="index"
              :class="['theme-circle', { active: currentTheme === index }]"
              :style="{ backgroundColor: color }"
              @click="setTheme(index)"
            >
              <check-outlined v-if="currentTheme === index" class="check-icon" />
            </div>
          </div>
        </div>

        <!-- 正文字体 -->
        <div class="settings-section">
          <div class="settings-label">正文字体</div>
          <div class="font-options">
            <a-button
              :class="['font-button', { active: fontType === 'heiti' }]"
              @click="setFontType('heiti')"
            >
              黑体
            </a-button>
            <a-button
              :class="['font-button', { active: fontType === 'songti' }]"
              @click="setFontType('songti')"
            >
              宋体
            </a-button>
            <a-button
              :class="['font-button', { active: fontType === 'kaiti' }]"
              @click="setFontType('kaiti')"
            >
              楷体
            </a-button>
          </div>
        </div>

        <!-- 字体大小 -->
        <div class="settings-section">
          <div class="settings-label">字体大小</div>
          <div class="font-size-control">
            <a-button class="size-button" @click="decreaseFontSize">A-</a-button>
            <div class="font-size-value">{{ fontSize }}</div>
            <a-button class="size-button" @click="increaseFontSize">A+</a-button>
          </div>
        </div>

        <!-- 页面宽度 -->
        <div class="settings-section">
          <div class="settings-label">页面宽度</div>
          <div class="width-options">
            <a-button
              v-for="width in widthOptions"
              :key="width"
              :class="['width-button', { active: pageWidth === (width === '自动' ? 1000 : width) }]"
              @click="setPageWidth(width)"
            >
              {{ width }}
            </a-button>
          </div>
        </div>

        <!-- 翻页模式 -->
        <div class="settings-section">
          <div class="settings-label">翻页模式</div>
          <div class="page-mode-options">
            <a-button
              :class="['page-button', { active: pageMode === 'chapter' }]"
              @click="setPageMode('chapter')"
            >
              章节翻页
            </a-button>
            <a-button
              :class="['page-button', { active: pageMode === 'scroll' }]"
              @click="setPageMode('scroll')"
            >
              滚动翻页
            </a-button>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- AI对话模态框 -->
    <a-modal
      v-model:visible="aiChatVisible"
      :footer="null"
      :mask-closable="true"
      width="700px"
      :closable="true"
      centered
      title="AI智能对话"
      class="ai-chat-modal"
      :bodyStyle="{ background: isNightMode ? '#1c1c1b' : '#f8f4e5', padding: '0' }"
    >
      <div class="ai-chat-container">
        <!-- 对话历史 -->
        <div class="chat-history" ref="chatHistoryRef">
          <div
            v-for="(message, index) in chatMessages"
            :key="index"
            :class="['chat-message', message.type]"
          >
            <div class="chat-avatar">
              <robot-outlined v-if="message.type === 'ai'" />
              <user-outlined v-else />
            </div>
            <div class="chat-bubble">
              <div v-if="message.quote" class="chat-quote">{{ message.quote }}</div>
              <div class="chat-content">{{ message.content }}</div>
              <div v-if="message.type === 'ai'" class="chat-actions">
                <a-button type="text" size="small">
                  <copy-outlined />
                  复制
                </a-button>
                <a-button type="text" size="small">
                  <sound-outlined />
                  朗读
                </a-button>
              </div>
            </div>
          </div>
          <div v-if="isAiTyping" class="chat-message ai">
            <div class="chat-avatar">
              <robot-outlined />
            </div>
            <div class="chat-bubble">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <a-input-group compact class="chat-input-container">
            <a-textarea
              v-model:value="userMessage"
              placeholder="请输入您的问题或提示词..."
              :autoSize="{ minRows: 1, maxRows: 4 }"
              class="chat-input"
              @keydown.enter.prevent="sendMessage"
            />
            <a-button
              type="primary"
              class="send-button"
              :disabled="!userMessage.trim()"
              @click="sendMessage"
            >
              <send-outlined />
            </a-button>
          </a-input-group>
          <div class="chat-suggestions">
            <a-button
              v-for="(suggestion, index) in chatSuggestions"
              :key="index"
              class="suggestion-button"
              @click="userMessage = suggestion"
            >
              {{ suggestion }}
            </a-button>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 目录模态框 -->
    <a-modal
      v-model:visible="catalogVisible"
      :footer="null"
      :mask-closable="true"
      width="800px"
      :closable="true"
      centered
      title="目录"
      class="catalog-modal"
    >
      <div class="catalog-header">
        <!--        <div class="catalog-info">{{}} · 共 {{}} 章</div>-->
        <div class="catalog-actions">
          <a-button>
            <shopping-cart-outlined />
            批量订阅
          </a-button>
          <a-button>
            <sort-ascending-outlined />
            倒序
          </a-button>
        </div>
      </div>
      <div class="catalog-content">
        <div class="catalog-columns">
          <div class="catalog-column">
            <div
              v-for="(item, index) in catalogItems.slice(0, catalogItems.length / 2)"
              :key="index"
              class="catalog-item"
              @click="loadChapter(item.id)"
            >
              <span class="chapter-number">{{ item.chapterTitle }}</span>
            </div>
          </div>
          <div class="catalog-column">
            <div
              v-for="(item, index) in catalogItems.slice(catalogItems.length / 2)"
              :key="index + catalogItems.length / 2"
              class="catalog-item"
              @click="loadChapter(item.id)"
            >
              <span class="chapter-number">{{ item.chapterTitle }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <audio ref="audioRef" @ended="handleAudioEnded"></audio>
  </div>
</template>

<script setup lang="ts">
// 添加缺少的 computed 导入
import { computed, nextTick, onMounted, ref } from 'vue'
import {
  BookOutlined,
  BulbFilled,
  BulbOutlined,
  CheckOutlined,
  ClockCircleOutlined,
  CopyOutlined,
  FontSizeOutlined,
  MobileOutlined,
  ReadOutlined,
  RobotOutlined,
  SendOutlined,
  SettingOutlined,
  ShoppingCartOutlined,
  SortAscendingOutlined,
  SoundOutlined,
  ToTopOutlined,
  UnorderedListOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import {
  getArticleVoByIdUsingGet,
  getChapterUsingGet,
  listChapterUsingPost,
} from '@/api/articleController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import { qwenUsingPost } from '@/api/ttsController.ts'

// 设置相关状态
const settingsVisible = ref(false)
const catalogVisible = ref(false)
const currentTheme = ref(2) // 默认为淡黄色
const fontType = ref('heiti') // 默认黑体
const fontSize = ref(18) // 默认字体大小
const pageWidth = ref(1000) // 默认页面宽度
const pageMode = ref('scroll') // 默认滚动翻页
const currentHighlightIndex = ref(-1)
const audioRef = ref(null)

// AI对话相关状态
const aiChatVisible = ref(false)
const userMessage = ref('')
const chatHistoryRef = ref(null)
const isAiTyping = ref(false)
const chatSuggestions = ref([
  '解释一下这段文字的含义',
  '深入分析这段文字的写作手法',
  '这段落中的主要意象有哪些？',
  '作者在这段文字中想表达什么情感？',
])

// 文字选择菜单
const showSelectionMenu = ref(false)
const selectionMenuPosition = ref({ x: 0, y: 0 })
const selectedText = ref('')

interface ChatMessage {
  type: 'user' | 'ai'
  content: string
  quote?: string
}

const chatMessages = ref<ChatMessage[]>([
  {
    type: 'ai',
    content:
      '您好，我是您的AI助手。我可以帮您解析文章内容、回答问题或提供写作建议。请问有什么可以帮您的吗？',
  },
])

// 检测文本选择并显示菜单
const handleTextSelection = () => {
  const selection = window.getSelection()
  if (selection && selection.toString().trim().length > 0) {
    selectedText.value = selection.toString().trim()

    const range = selection.getRangeAt(0)
    const rect = range.getBoundingClientRect()

    selectionMenuPosition.value = {
      x: rect.left + rect.width / 2 - 75, // 水平居中定位
      y: rect.top - 45, // 放在选中文本上方
    }

    showSelectionMenu.value = true
  } else {
    showSelectionMenu.value = false
  }
}

// 点击外部区域时隐藏选择菜单
window.addEventListener('click', (event) => {
  if (showSelectionMenu.value) {
    // 检查点击是否在菜单内，如果不是则关闭菜单
    const targetElement = event.target as HTMLElement
    if (!targetElement.closest('.selection-menu')) {
      showSelectionMenu.value = false
    }
  }
})

// 打开AI对话框
const openAIChat = (type = '', text = '') => {
  aiChatVisible.value = true
  showSelectionMenu.value = false

  if (text) {
    // 根据类型生成不同的预设问题
    if (type === 'analyze') {
      userMessage.value = `请对这段文字进行文学赏析：\n"${text}"`
    } else if (type === 'explain') {
      userMessage.value = `请解释这段文字的含义：\n"${text}"`
    }

    // 自动发送消息
    nextTick(() => {
      sendMessage()
    })
  }
}
// 发送消息
const sendMessage = async () => {
  if (!userMessage.value.trim()) return
  // 添加用户消息
  chatMessages.value.push({
    type: 'user',
    content: userMessage.value,
  })
  // 显示AI正在输入
  isAiTyping.value = true
  // 滚动到底部
  nextTick(() => {
    if (chatHistoryRef.value) {
      chatHistoryRef.value.scrollTop = chatHistoryRef.value.scrollHeight
    }
  })

  const res = await qwenUsingPost({
    message: userMessage.value,
  })
  const AIMessage = ref<ChatMessage>({
    type: 'ai',
    content: '',
  })
  ws.onmessage = (event) => {
    const data = JSON.parse(event.data)
    if (data.task === 'qwen') {
      AIMessage.value.content += data.message
      if (isAiTyping.value) {
        chatMessages.value.push(AIMessage.value)
      }

      isAiTyping.value = false
    }
  }
  // 清空输入框
  userMessage.value = ''
  // 滚动到底部
  nextTick(() => {
    if (chatHistoryRef.value) {
      chatHistoryRef.value.scrollTop = chatHistoryRef.value.scrollHeight
    }
  })
}

const ws = new WebSocket("ws://1.117.233.91/api/websocket")
ws.onopen = () => {
  console.log('WebSocket connection opened')
}

ws.onerror = (error) => {
  console.error('WebSocket error:', error)
}

const generateAIResponse = (message: string): string => {
  if (message.includes('赏析')) {
    return '从您提供的文字中，我能看到作者运用了细腻的描写和丰富的意象。这段文字展现了深刻的情感和思想内涵，通过精准的用词和优美的句式，营造出独特的艺术氛围。作者在文中巧妙地运用了比喻和暗示等修辞手法，使文章内容更加生动形象。从中我们也可以感受到作者对生活的思考和对美的追求。'
  } else if (message.includes('解释')) {
    return '这段文字的核心含义是探讨了人与自然、个体与社会之间的关系。作者通过具体的描述和抽象的思考，引导读者反思生活中的种种现象和问题。文中蕴含的哲理性思考超越了表面的字词意义，触及人类共通的情感和普遍的生存状态。'
  } else if (message.includes('写作手法')) {
    return '这段文字运用了多种写作手法：首先是细腻的描写，通过精确的用词勾勒出鲜活的画面；其次是巧妙的比喻，将抽象概念具象化；还有节奏感强的句式变化，营造出起伏有致的阅读韵律；最后是情景交融的表现手法，使感情与景象自然结合，增强了文章的感染力。'
  } else {
    return '您提出的问题很有深度。从文学分析的角度看，这段内容体现了作者独特的思想观点和丰富的情感世界。通过精心选择的词语和句式，作者成功地传达了内心的感受和对世界的认知。如果您有更具体的问题，我很乐意提供更深入的分析和解答。'
  }
}

// 主题颜色
const themes = {
  0: '#e6e6e6', // 浅灰色
  1: '#f7edd0', // 米色
  2: '#f8f4e5', // 淡黄色
  3: '#e8f4e5', // 淡绿色
  4: '#f5e5e5', // 浅红色
  5: '#000000', // 黑色
}

// 转换为数组便于遍历
const themesArray = Object.values(themes)

// 字体映射
const fontFamilyMap = {
  heiti: 'SimHei, "Microsoft YaHei", sans-serif',
  songti: 'SimSun, "Songti SC", serif',
  kaiti: 'KaiTi, "Kaiti SC", cursive',
}

// 页面宽度选项
const widthOptions = ['自动', 640, 800, 900, 1000, 1280]

// 计算属性：当前字体类型
const fontFamily = computed(() => {
  return fontFamilyMap[fontType.value]
})

// 显示设置模态框
const showSettingsModal = () => {
  settingsVisible.value = true
}

// 设置主题
const setTheme = (index) => {
  currentTheme.value = index
  if (index === 5) {
    // 黑色主题
    isNightMode.value = true
  } else {
    isNightMode.value = false
  }
}

// 设置字体类型
const setFontType = (type) => {
  fontType.value = type
}

// 增加字体大小
const increaseFontSize = () => {
  if (fontSize.value < 24) {
    fontSize.value += 1
  }
}

// 减小字体大小
const decreaseFontSize = () => {
  if (fontSize.value > 14) {
    fontSize.value -= 1
  }
}

// 设置页面宽度
const setPageWidth = (width) => {
  if (width === '自动') {
    pageWidth.value = 1000 // 自动时默认为1000
  } else {
    pageWidth.value = width
  }
}

// 设置翻页模式
const setPageMode = (mode) => {
  pageMode.value = mode
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
}
const gotoArticleDetail = () => {
  const url = router.resolve({
    path: '/article/detail/' + props.id,
  }).href
  window.open(url, '_blank')
}
const article = ref<API.ArticleVO>()
const props = defineProps<{
  id: string | number
  type: string
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
const gptWeights = new Map<string, string>([
  ['1', 'GPT_weights_v2/kh-e25.ckpt'],
  ['2', 'GPT_weights_v2/man-e15.ckpt'],
  ['3', 'GPT_weights_v2/woman-e45.ckpt'],
  ['4', 'GPT_weights_v2/olj-e45.ckpt'],
])
const sovitsWeights = new Map<string, string>([
  ['1', 'SoVITS_weights_v2/kh_e32_s384.pth'],
  ['2', 'SoVITS_weights_v2/man_e24_s264.pth'],
  ['3', 'SoVITS_weights_v2/woman_e8_s96.pth'],
  ['4', 'SoVITS_weights_v2/woman_e8_s96.pth'],
])
const promptTextMap = new Map<string, string>([
  ['1', '自小爱红楼的我，自然也极爱着正定古城里的这座赤造荣国府。'],
  ['2', '多么严密的结构哇，宗谱先生真不愧是写文章的好手'],
  ['3', '在记忆当中大家觉得冬天应该是白雪皑皑的，非常的冷的'],
  ['4', '只要有这么一点点快乐的回忆，它可以永劫轮回，都在所不惜'],
])
const refAudioMap = new Map<string, string>([
  [
    '1',
    'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman1.wav',
  ],
  [
    '2',
    'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman2.wav',
  ],
  [
    '3',
    'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen1.wav',
  ],
  [
    '4',
    'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen2.wav',
  ],
])
const isReading = ref(false)

const startAudioReading = async () => {
  if (isReading.value) return
  isReading.value = true
  currentHighlightIndex.value = 0
  await startStreaming()
}
const processNextParagraph = async () => {
  if (currentHighlightIndex.value >= article.value.content.length) {
    isReading.value = false
    return
  }
  const paragraph = article.value.content[currentHighlightIndex.value]
  try {
    const response = await fetch('http://i-2.gpushare.com:24924/tts', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        text: paragraph,
        text_lang: 'zh',
        ref_audio_path:
          'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman1.wav',
        prompt_text: '自小爱红楼的我，自然也极爱着正定古城里的这座赤造荣国府。',
        prompt_lang: 'zh',
        text_split_method: 'cut0',
        streaming_mode: false,
        media_type: 'wav',
      }),
    })

    // 检查响应状态
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    const audioBlob = await response.blob()
    const audioUrl = URL.createObjectURL(audioBlob)
    const audio = new Audio(audioUrl)
    audio.onended = () => {
      currentHighlightIndex.value++
      processNextParagraph()
    }
    audio.play()
  } catch (error) {
    console.error('音频生成失败:', error)
    isReading.value = false
  }
}

async function startStreaming() {
  const paragraph = article.value.content[currentHighlightIndex.value]
  const mediaSource = new MediaSource()
  audioRef.value.src = URL.createObjectURL(mediaSource)
  console.log(1)
  mediaSource.addEventListener('sourceopen', async () => {
    console.log(2)
    const sourceBuffer = mediaSource.addSourceBuffer('audio/mpeg')
    sourceBuffer.mode = 'sequence' // 让音频流保持顺序
    try {
      const response = await fetch('http://i-2.gpushare.com:24924/tts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text: paragraph,
          text_lang: 'zh',
          ref_audio_path: refAudioMap.get(props.type),
          prompt_text: promptTextMap.get(props.type),
          prompt_lang: 'zh',
          text_split_method: 'cut5',
          streaming_mode: true,
          fragment_interval: 0.3,
          media_type: 'mp3',
        }),
      })

      const reader = response.body.getReader()
      let firstChunk = true

      async function processStream({ done, value }) {
        if (done) {
          mediaSource.endOfStream()
          return
        }

        if (firstChunk) {
          firstChunk = false
          audioRef.value.play().catch((error) => {
            console.error('自动播放音频失败:', error)
          })
        }

        // 等待 `sourceBuffer` 处于 `updating == false` 才能追加数据
        if (sourceBuffer.updating) {
          await new Promise((resolve) => {
            sourceBuffer.addEventListener('updateend', resolve, { once: true })
          })
        }

        sourceBuffer.appendBuffer(value)
        reader.read().then(processStream)
      }

      reader.read().then(processStream)
    } catch (error) {
      message.error('播放音频流时发生错误:', error)
    }
  })
}

const handleAudioEnded = () => {
  currentHighlightIndex.value++
  startStreaming()
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
      console.log(res.data)
    } else {
      message.error('获取文章失败' + res.data.message)
    }
  } catch (error) {
    message.error('获取文章失败' + error)
  }
}
const chooseModel = (gptWeight: string | undefined, sovitsWeight: string | undefined) => {
  if (gptWeight) {
    const url = `http://i-2.gpushare.com:24924/set_gpt_weights?weights_path=${gptWeight}`
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((response) => response.text())
      .then((data) => {
        console.log('Set GPT weights response:', data)
      })
  }
  if (sovitsWeight) {
    const url = `http://i-2.gpushare.com:24924/set_sovits_weights?weights_path=${sovitsWeight}`
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((response) => response.text())
      .then((data) => {
        console.log('Set SoVITS weights response:', data)
      })
  }
}
onMounted(() => {
  fetchArticleDetail()
  fetchCatalog()
  console.log(props.type)
  const gptWeight = gptWeights.get(props.type)
  const sovitsWeight = sovitsWeights.get(props.type)
  console.log(gptWeight, sovitsWeight)
  chooseModel(gptWeight, sovitsWeight)
})

interface ParagraphContent {
  text: string
  commentCount?: number
}

interface ChapterData {
  number: string
  title: string
  index: string
  bookName: string
  author: string
  wordCount: number
  publishTime: string
  content: ParagraphContent[]
}

// 夜间模式状态
const isNightMode = ref(false)
// 切换夜间模式
const toggleNightMode = () => {
  isNightMode.value = !isNightMode.value
  if (isNightMode.value) {
    currentTheme.value = 5 // 黑色主题
  } else {
    currentTheme.value = 2 // 淡黄色主题
  }
}
// 目录数据
const catalogItems = ref<API.Chapter[]>([])
// 获取目录数据
const fetchCatalog = async () => {
  const res = await listChapterUsingPost({
    articleId: props.id,
  })
  if (res.data.code === 0 && res.data.data) {
    catalogItems.value = res.data.data
    currentChapter.value = catalogItems.value[0]
    await loadChapter(currentChapter.value?.id)
  } else {
    message.error('获取目录失败: ' + res.data.message)
  }
}
// 显示目录模态框
const showCatalogModal = () => {
  catalogVisible.value = true
}
const currentChapter = ref<API.Chapter>()
// 加载指定章节
const loadChapter = async (chapterId) => {
  try {
    catalogVisible.value = false
    const res = await getChapterUsingGet({
      chapterId: chapterId,
    })
    if (res.data.code === 0 && res.data.data?.content) {
      currentChapter.value = res.data.data
      const contentArray: string[] = JSON.parse(
        res.data.data.content.replace(/[\r\n\s]/g, '') || '[]',
      )
      article.value.content = contentArray
      message.success('章节加载成功')
      // 滚动到顶部
      scrollToTop()
    } else {
    }
  } catch (error) {}
}
</script>

<style scoped>
.reading-container {
  min-height: 100vh;
  background-color: #f8f4e5; /* 淡黄色背景 */
  position: relative;
  transition: all 0.3s ease;
}

.highlight {
  color: red;
}

/* 选中文本菜单 */
.selection-menu {
  position: absolute;
  background: rgba(25, 25, 25, 0.85);
  border-radius: 6px;
  padding: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  gap: 8px;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.selection-menu-item {
  font-size: 12px;
  height: 30px;
  padding: 0 12px;
  border: none;
  background: linear-gradient(135deg, #3c67e3, #4e00c2);
}

.selection-menu-item:hover {
  background: linear-gradient(135deg, #4e00c2, #3c67e3);
  transform: translateY(-2px);
}

/* AI对话框样式 */
.ai-chat-modal :deep(.ant-modal-content) {
  overflow: hidden;
  border-radius: 10px;
}

.ai-chat-modal :deep(.ant-modal-header) {
  background-color: #3c67e3;
  border-bottom: none;
  padding: 16px 24px;
}

.ai-chat-modal :deep(.ant-modal-title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.ai-chat-modal :deep(.ant-modal-close) {
  color: white;
}

.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 60vh;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-message {
  display: flex;
  gap: 12px;
  max-width: 100%;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-message.user {
  flex-direction: row-reverse;
}

.chat-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #3c67e3;
  color: white;
  flex-shrink: 0;
}

.chat-message.user .chat-avatar {
  background-color: #5a5a5a;
}

.chat-bubble {
  background-color: rgba(60, 103, 227, 0.1);
  border-radius: 12px;
  padding: 12px 16px;
  position: relative;
  max-width: calc(100% - 60px);
}

.chat-message.user .chat-bubble {
  background-color: rgba(90, 90, 90, 0.1);
}

.night-mode .chat-bubble {
  background-color: rgba(60, 103, 227, 0.2);
}

.night-mode .chat-message.user .chat-bubble {
  background-color: rgba(120, 120, 120, 0.2);
}

.chat-quote {
  font-size: 13px;
  color: #666;
  border-left: 3px solid #3c67e3;
  padding-left: 10px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-content {
  font-size: 14px;
  line-height: 1.6;
  color: inherit;
  word-break: break-word;
}

.chat-actions {
  margin-top: 8px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  height: 24px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #3c67e3;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%,
  60%,
  100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-6px);
  }
}

.chat-input-area {
  padding: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.05);
}

.night-mode .chat-input-area {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.chat-input-container {
  display: flex;
  width: 100%;
}

.chat-input {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 20px 0 0 20px;
  padding: 10px 15px;
  resize: none;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-right: none;
  height: 20px;
}

.night-mode .chat-input {
  background-color: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.send-button {
  border-radius: 0 20px 20px 0;
  width: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0095ff, #6891cb);
  border: none;
}

.send-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #0095ff, #6891cb);
}

.chat-suggestions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.suggestion-button {
  font-size: 12px;
  padding: 0 10px;
  height: 28px;
  border-radius: 14px;
  border: 1px solid rgba(60, 103, 227, 0.3);
  color: #3c67e3;
  background: transparent;
}

.night-mode .suggestion-button {
  border-color: rgba(60, 103, 227, 0.5);
  color: #5ddcff;
}

.suggestion-button:hover {
  background-color: rgba(60, 103, 227, 0.1);
  border-color: #3c67e3;
}

/* 夜间模式样式 - 整个页面黑色背景，文字白色 */
.night-mode {
  background-color: #000000;
  color: #ffffff;
}

.night-mode .chapter-header,
.night-mode .chapter-content {
  color: #ffffff;
}

.night-mode .chapter-title {
  color: #ffffff;
}

.night-mode .chapter-meta {
  color: #dddddd;
}

.night-mode .chapter-index {
  color: #bbbbbb;
}

.night-mode .comment-count {
  color: #bbbbbb;
}

.night-mode .sidebar-item,
.night-mode .sidebar-button {
  color: #ffffff;
  background-color: #222222;
}

.night-mode .ant-pagination-item a {
  color: #ffffff;
}

.night-mode .ant-pagination-item {
  background-color: #222222;
  border-color: #444444;
}

.night-mode .ant-pagination-item-active {
  background-color: #1890ff;
  border-color: #1890ff;
}

.night-mode .ant-pagination-options-quick-jumper {
  color: #ffffff;
}

.night-mode .ant-pagination-options-quick-jumper input {
  background-color: #222222;
  border-color: #444444;
  color: #ffffff;
}

.night-mode .article-content {
  width: 1000px;
  margin: 0 auto;
  padding: 20px 40px;
  background-color: #1c1c1b;
}

.article-content {
  width: 1000px;
  margin: 0 auto;
  padding: 20px 40px;
  background-color: transparent;
  transition: all 0.3s ease;
}

.chapter-header {
  margin-bottom: 30px;
  text-align: center;
}

.chapter-title {
  font-size: 24px;
  font-weight: normal;
  margin-bottom: 10px;
}

.chapter-index {
  font-size: 14px;
  color: #888;
  margin-left: 5px;
}

.chapter-meta {
  color: #666;
  font-size: 14px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.chapter-content {
  font-size: 18px;
  line-height: 1.8;
  letter-spacing: 0.05em;
  transition: all 0.3s ease;
}

.paragraph {
  text-indent: 40px;
  margin-bottom: 25px;
  position: relative;
}

.comment-count {
  position: absolute;
  right: -25px;
  color: #888;
  font-size: 12px;
}

.chapter-footer {
  margin-top: 50px;
  text-align: center;
  padding: 20px 0;
}

/* 左侧边栏样式 */
.left-sidebar {
  position: fixed;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar-item {
  width: 40px;
  height: 40px;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.sidebar-item:hover {
  background-color: #f0f0f0;
}

.night-mode .sidebar-item:hover {
  background-color: #333333;
}

/* 右侧边栏样式 */
.right-sidebar {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar-button {
  width: 50px;
  height: 60px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.sidebar-button:hover {
  background-color: #f0f0f0;
}

.night-mode .sidebar-button:hover {
  background-color: #000;
}

.sidebar-button .anticon {
  font-size: 20px;
  margin-bottom: 5px;
}

/* 设置模态框样式 */
.settings-container {
  padding: 10px;
}

.settings-section {
  margin-bottom: 20px;
}

.settings-label {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

/* 主题选择样式 */
.theme-options {
  display: flex;
  gap: 15px;
}

.theme-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.theme-circle.active {
  border: 2px solid #ff4d4f;
}

.check-icon {
  color: #fff;
  font-size: 16px;
}

/* 字体选择样式 */
.font-options {
  display: flex;
  gap: 10px;
}

.font-button {
  flex: 1;
  padding: 10px 0;
}

.font-button.active {
  background-color: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 字体大小控制样式 */
.font-size-control {
  display: flex;
  align-items: center;
}

.size-button {
  width: 60px;
}

.font-size-value {
  flex: 1;
  text-align: center;
  font-size: 18px;
  padding: 0 20px;
}

/* 页面宽度选项样式 */
.width-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.width-button {
  min-width: 60px;
}

.width-button.active {
  background-color: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 翻页模式样式 */
.page-mode-options {
  display: flex;
  gap: 10px;
}

.page-button {
  flex: 1;
}

.page-button.active {
  background-color: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 开关选项样式 */
.toggle-option {
  display: flex;
  align-items: center;
  gap: 15px;
}

.toggle-description {
  font-size: 14px;
  color: #666;
}

/* 目录模态框样式 */
.catalog-modal :deep(.ant-modal-body) {
  padding: 0;
}

.catalog-modal :deep(.ant-modal-header) {
  border-bottom: 1px solid #eee;
}

.catalog-header {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.catalog-info {
  color: #888;
  font-size: 14px;
}

.catalog-actions {
  display: flex;
  gap: 10px;
}

.catalog-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 12px;
}

.catalog-columns {
  display: flex;
  justify-content: space-between;
}

.catalog-column {
  flex: 1;
  padding: 12px;
}

.catalog-item {
  padding: 15px 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 16px;
  color: #333;
}

.catalog-item:hover {
  background-color: #f5f5f5;
  color: #ff4d4f;
}

.night-mode .catalog-item:hover {
  background-color: #222;
  color: #ff4d4f;
}

.night-mode .catalog-header,
.night-mode .catalog-item,
.night-mode .catalog-modal :deep(.ant-modal-header) {
  border-color: #333;
}

.night-mode .catalog-info,
.night-mode .catalog-item {
  color: #ddd;
}

.night-mode .catalog-modal :deep(.ant-modal-content),
.night-mode .catalog-modal :deep(.ant-modal-header) {
  background-color: #1c1c1b;
}

.night-mode .catalog-modal :deep(.ant-modal-title) {
  color: #fff;
}

/* 章节编号样式 */
.chapter-number {
  display: block;
  line-height: 1.5;
}
</style>
