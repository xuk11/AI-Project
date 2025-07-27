<template>
  <div>
    <a-form layout="vertical" :model="TTSResult" style="max-width: 80vw; margin-left: 10vw">
      <a-tabs v-model:activeKey="activeKey">
        <a-tab-pane key="1" tab="直接文本转语音">
          <a-form-item label="请输入文本">
            <a-space align="start">
              <a-textarea
                v-model:value="TTSResult.text"
                show-count
                style="width: 65vw; font-size: large"
                :maxlength="1000"
                :auto-size="{ minRows: 2, maxRows: 10 }"
              />
              <a-select
                v-model:value="selectedLanguage"
                placeholder="请选择文本语言"
                :options="language"
                style="width: 100%; margin-left: 24px"
              ></a-select>
            </a-space>
          </a-form-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="AI对话转语音">
          <a-form-item label="输入一段提示词">
            <a-textarea
              v-model:value="promptText"
              show-count
              style="width: 65vw"
              :maxlength="1000"
              :auto-size="{ minRows: 5, maxRows: 10 }"
            />
          </a-form-item>
          <a-form-item>
            <a-button
              :loading="isGenerating"
              @click="handleGenerate"
              type="primary"
              style="margin-left: 200px; margin-top: 24px; width: 500px"
              >生成回复
            </a-button>
          </a-form-item>
          <a-form-item label="AI生成的回复">
            <a-space align="start">
              <a-textarea
                v-model:value="generateText"
                show-count
                style="width: 65vw"
                :maxlength="1000"
                :auto-size="{ minRows: 10, maxRows: 20 }"
              />
              <a-select
                v-model:value="selectedLanguage"
                placeholder="请选择文本语言"
                :options="language"
                style="width: 100%; margin-left: 24px"
              ></a-select>
            </a-space>
          </a-form-item>
        </a-tab-pane>
      </a-tabs>
      <a-form-item label="请选择音频">
        <a-space align="center">
          <a-checkbox @change="handleCheckboxChange" v-model:checked="isCustomAudio"
            >上传或录制音频
          </a-checkbox>
          <a-select
            :disabled="isCustomAudio"
            v-model:value="selectedAudio"
            placeholder="选择预设参考音频"
            :options="presetAudioOptions"
            style="width: 100%; margin-left: 12px; min-width: 150px"
          ></a-select>
          <a-select
            :disabled="isCustomAudio"
            v-model:value="selectedCustomAudio"
            placeholder="选择自定义音频"
            :options="customOptions"
            style="width: 100%; margin-left: 56px; min-width: 150px"
          ></a-select>
        </a-space>

        <a-space align="center" direction="horizontal" v-if="isCustomAudio">
          <a-upload-dragger
            :disabled="isUploaded"
            style="width: 32vw"
            v-model:fileList="fileList"
            :name="fileName"
            :multiple="false"
            :customRequest="handleUpload"
            @change="handleChange"
            @drop="handleDrop"
            accept=".wav,.mp3,.flac"
          >
            <!--        关闭按钮      -->
            <div style="position: absolute; top: 10px; right: 10px">
              <a-button plain shape="circle" :icon="h(CloseOutlined)" @click="closeUploading" />
            </div>
            <div v-if="!isUploaded">
              <p class="ant-upload-drag-icon">
                <UploadOutlined />
              </p>
              <p class="ant-upload-text">将音频拖到此处</p>
              <p class="ant-upload-hint">-或-</p>
              <p class="ant-upload-text">点击上传</p>
            </div>
            <div v-else class="audio-player">
              <audio
                v-if="uploadAudioUrl"
                :src="uploadAudioUrl"
                controls
                style="width: 30vw; margin-top: 7vh"
              ></audio>
            </div>
          </a-upload-dragger>
          <a-card
            style="
              width: 32vw;
              height: 28vh;
              margin-left: 30px;
              margin-top: 12px;
              margin-right: 10vw;
              background: rgba(232, 228, 228, 0.33);
            "
          >
            <!-- 关闭按钮 -->
            <div style="position: absolute; top: 10px; right: 10px">
              <a-button plain shape="circle" :icon="h(CloseOutlined)" @click="closeRecording" />
            </div>
            <div
              style="
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                height: 100%;
              "
            >
              <audio
                v-if="recordAudioUrl"
                :src="recordAudioUrl"
                controls
                style="width: 30vw; margin-top: 7vh"
              ></audio>
            </div>
            <!-- 录音控制 -->
            <div style="position: absolute; bottom: 20px; left: 30px">
              <a-space v-if="!isRecording">
                <a-button
                  type="primary"
                  shape="circle"
                  :icon="h(AudioOutlined)"
                  @click="startRecording"
                />
                <a-typography-text>开始录音</a-typography-text>
              </a-space>
              <a-space v-if="isRecording">
                <a-button :icon="h(BorderOutlined)" type="primary" danger @click="stopRecording" />
                <a-typography-text>停止录音</a-typography-text>
              </a-space>
            </div>
          </a-card>
        </a-space>
      </a-form-item>
      <a-form-item label="请输入参考音频对应的文本">
        <a-space align="start">
          <a-textarea
            v-model:value="TTSResult.promptText"
            show-count
            placeholder="请输入参考音频对应的文本"
            style="width: 65vw"
            :maxlength="1000"
            :auto-size="{ minRows: 1, maxRows: 5 }"
          />
          <a-select
            v-model:value="selectedPromptLanguage"
            placeholder="参考文本的语言"
            :options="language"
            style="width: 100%; margin-left: 24px"
          ></a-select>
        </a-space>
      </a-form-item>
      <a-collapse v-model:activeKey="activeKey2" expand-icon-position="start">
        <a-collapse-panel key="1" header="高级设置" class="setting">
          <a-space align="center">
            <a-form-item label="Top K">
              <a-space align="center">
                <a-slider v-model:value="TTSResult.TopK" style="width: 400px" :min="1" :max="10" />
                <a-input-number
                  v-model:value="TTSResult.TopK"
                  :min="1"
                  :max="5"
                  style="margin-left: 16px"
                />
              </a-space>
            </a-form-item>
            <a-form-item label="Top P" style="margin-left: 70px">
              <a-space>
                <a-slider
                  v-model:value="TTSResult.TopP"
                  style="width: 400px"
                  :min="0"
                  :max="1"
                  :step="0.01"
                />
                <a-input-number
                  v-model:value="TTSResult.TopP"
                  :min="0"
                  :max="1"
                  :step="0.01"
                  style="margin-left: 16px"
                />
              </a-space>
            </a-form-item>
          </a-space>
          <a-space align="center">
            <a-form-item label="temperature">
              <a-space align="center">
                <a-slider
                  v-model:value="TTSResult.temperature"
                  style="width: 400px"
                  :min="0.1"
                  :max="2"
                  :step="0.01"
                />
                <a-input-number
                  v-model:value="TTSResult.temperature"
                  :min="0.1"
                  :max="2"
                  :step="0.01"
                  style="margin-left: 16px"
                />
              </a-space>
            </a-form-item>
            <a-form-item label="批处理阈值" style="margin-left: 70px">
              <a-space>
                <a-slider
                  v-model:value="TTSResult.batchThreshold"
                  style="width: 400px"
                  :min="0"
                  :max="1"
                  :step="0.01"
                />
                <a-input-number
                  v-model:value="TTSResult.batchThreshold"
                  :min="0"
                  :max="1"
                  :step="0.01"
                  style="margin-left: 16px"
                />
              </a-space>
            </a-form-item>
          </a-space>
          <a-space>
            <a-form-item label="语速因子">
              <a-space>
                <a-slider
                  v-model:value="TTSResult.speed_factor"
                  style="width: 400px"
                  :min="0.5"
                  :max="2"
                  :step="0.01"
                />
                <a-input-number
                  v-model:value="TTSResult.speed_factor"
                  :min="0.5"
                  :max="2"
                  :step="0.01"
                  style="margin-left: 16px"
                />
              </a-space>
            </a-form-item>
            <a-form-item label="惩罚系数" style="margin-left: 70px">
              <a-space>
                <a-slider
                  v-model:value="TTSResult.repetition_penalty"
                  style="width: 400px"
                  :min="1"
                  :max="2"
                  :step="0.01"
                />
                <a-input-number
                  v-model:value="TTSResult.repetition_penalty"
                  :min="0.5"
                  :max="2"
                  :step="0.01"
                  style="margin-left: 16px"
                />
              </a-space>
            </a-form-item>
          </a-space>
          <a-row>
            <a-col :span="6">
              <a-form-item label="批处理大小">
                <a-input-number
                  v-model:value="TTSResult.batchSize"
                  :min="1"
                  :max="50"
                  :step="1"
                  style="width: 80%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="文本切割方法">
                <a-select
                  v-model:value="TTSResult.selectedCut"
                  placeholder="请选择参考文本的语言"
                  :options="cutMethod"
                  style="width: 80%"
                ></a-select>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="随机种子">
                <a-input-number v-model:value="TTSResult.seed" style="width: 80%" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-button @click="rollDice" type="link">
                <img
                  v-if="rolling"
                  src="../../assets/touzi.gif"
                  alt=""
                  style="width: 100px; margin-bottom: 20px"
                />
                <img
                  v-if="!rolling"
                  src="../../assets/touzi.png"
                  alt=""
                  style="width: 100px; margin-bottom: 20px"
                />
              </a-button>
            </a-col>
          </a-row>
          <a-form-item>
            <a-row>
              <a-col :span="6">
                <a-checkbox v-model:checked="TTSResult.split_bucket">启用批次分桶</a-checkbox>
              </a-col>
              <a-col :span="6">
                <div v-if="TTSResult.streaming_mode">
                  <a-checkbox v-model:checked="TTSResult.parallel_infer" disabled>
                    并行推理
                  </a-checkbox>
                </div>
                <div v-else>
                  <a-checkbox
                    v-model:checked="TTSResult.parallel_infer"
                    @change="
                      () => {
                        if (TTSResult.parallel_infer) TTSResult.streaming_mode = false
                      }
                    "
                  >
                    并行推理
                  </a-checkbox>
                </div>
              </a-col>
              <a-col :span="6">
                <div v-if="TTSResult.parallel_infer">
                  <a-checkbox disabled v-model:checked="TTSResult.streaming_mode"
                    >流式响应模式
                  </a-checkbox>
                </div>
                <div v-else>
                  <a-checkbox
                    v-model:checked="TTSResult.streaming_mode"
                    @change="
                      () => {
                        if (TTSResult.streaming_mode) TTSResult.parallel_infer = false
                      }
                    "
                    >流式响应模式
                  </a-checkbox>
                </div>
              </a-col>
            </a-row>
          </a-form-item>
        </a-collapse-panel>
      </a-collapse>
      <a-form-item>
        <!--            <a-button-->
        <!--              v-if="isGenerating"-->
        <!--              @click="startStreaming"-->
        <!--              disabled-->
        <!--              style="-->
        <!--                margin-left: 25%;-->
        <!--                margin-top: 24px;-->
        <!--                width: 50%;-->
        <!--                background: linear-gradient(45deg, #cfc5e1, #b7bee8);-->
        <!--              "-->
        <!--            >-->
        <!--              <a-spin />-->
        <!--            </a-button>-->
        <!--            <a-button-->
        <!--              v-if="!isGenerating"-->
        <!--              @click="startStreaming"-->
        <!--              style="-->
        <!--                margin-left: 25%;-->
        <!--                margin-top: 24px;-->
        <!--                width: 50%;-->
        <!--                background: linear-gradient(45deg, #cfc5e1, #b7bee8);-->
        <!--              "-->
        <!--              >生成音频-->
        <!--            </a-button>-->
        <a-button
          :loading="isGenerating"
          @click="startStreaming"
          type="primary"
          style="margin-left: 25%; margin-top: 24px; width: 50%"
          >生成音频
        </a-button>
      </a-form-item>
      <a-form-item>
        <audio
          ref="audioPlayer"
          :src="audioSrc"
          controls
          style="margin-left: 25%; margin-bottom: 32px; width: 600px"
        ></audio>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, ref, watch } from 'vue'
import { message, type SelectProps, type UploadChangeParam } from 'ant-design-vue'
import { AudioOutlined, BorderOutlined, CloseOutlined, UploadOutlined } from '@ant-design/icons-vue'
import { uploadFileUsingPost } from '@/api/fileController.ts'
import { qwenUsingPost } from '@/api/ttsController.ts'
import { getCustomAudioUsingPost } from '@/api/audioCommunityController.ts'
import RecordRTC, { StereoAudioRecorder } from 'recordrtc'
 // 是否正在录制语音
const rolling = ref(false)
const rollDice = () => {
  rolling.value = true
  setTimeout(() => {
    TTSResult.value.seed = Math.floor(Math.random() * 6) + 1
    rolling.value = false
  }, 1000)
}
const promptText = ref('')
const isGenerating = ref(false)
const activeKey2 = ref()
// 是否正在录制语音
const isRecording = ref(false)
// 使用联合类型，进行媒体录制操作
const mediaRecorder = ref<MediaRecorder | null>(null)
const audioChunks = ref<Blob[]>([])
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
const isCustomAudio = ref(false)
const TTSResult = ref({
  text: '同学们，今天我们将一起探索新的知识，开启充满惊喜的学习之旅。',
  promptText: '',
  TopK: 5,
  refAudio: '',
  TopP: 1,
  temperature: 1,
  batchThreshold: 0.75,
  speed_factor: 1.0,
  batchSize: 30,
  selectedCut: 'cut0',
  seed: -1,
  streaming_mode: true,
  parallel_infer: false,
  split_bucket: true,
  repetition_penalty: 1.35,
})
const selectedLanguage = ref()
const selectedPromptLanguage = ref()
const selectedAudio = ref()
const selectedCustomAudio = ref()
const gptWeights = new Map<number, string>([
  [1, 'GPT_weights_v2/kh-e25.ckpt'],
  [2, 'GPT_weights_v2/man-e15.ckpt'],
  [3, 'GPT_weights_v2/woman-e45.ckpt'],
  [4, 'GPT_weights_v2/olj-e45.ckpt'],
])
const sovitsWeights = new Map<number, string>([
  [1, 'SoVITS_weights_v2/kh_e32_s384.pth'],
  [2, 'SoVITS_weights_v2/man_e24_s264.pth'],
  [3, 'SoVITS_weights_v2/woman_e8_s96.pth'],
  [4, 'SoVITS_weights_v2/woman_e8_s96.pth'],
])
const promptTextMap = new Map<number, string>([
  [1, '自小爱红楼的我，自然也极爱着正定古城里的这座赤造荣国府。'],
  [2, '多么严密的结构哇，宗谱先生真不愧是写文章的好手'],
  [3, '在记忆当中大家觉得冬天应该是白雪皑皑的，非常的冷的'],
  [4, '只要有这么一点点快乐的回忆，它可以永劫轮回，都在所不惜'],
])
const presetAudioMap = new Map<number, { value: string; label: string }>([
  [
    1,
    {
      value:
        'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman1.wav',
      label: '男声(康辉)',
    },
  ],
  [
    2,
    {
      value:
        'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman2.wav',
      label: '标准男声',
    },
  ],
  [
    3,
    {
      value:
        'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen1.wav',
      label: '标准女声',
    },
  ],
  [
    4,
    {
      value:
        'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen2.wav',
      label: '女声(欧丽娟)',
    },
  ],
])
const presetAudioOptions = Array.from(presetAudioMap.entries()).map(([key, { value, label }]) => ({
  value: key,
  label,
}))
const chooseModel = (gptWeight: string, sovitsWeight: string) => {
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
watch(selectedCustomAudio, (newValue) => {
  if (newValue) {
    selectedAudio.value = null
    TTSResult.value.promptText = ''
    TTSResult.value.refAudio = newValue
    selectedPromptLanguage.value = ''
    const gptWeight = 'GPT_weights_v2/zbn-e35.ckpt'
    const sovitsWeight = 'SoVITS_weights_v2/zbn_e48_s768.pth'
    chooseModel(gptWeight, sovitsWeight)
  }
})
watch(selectedAudio, (newValue) => {
  if (newValue) {
    selectedCustomAudio.value = false
    const promptText = promptTextMap.get(newValue)
    const newVar = presetAudioMap.get(newValue)
    const gptWeight = gptWeights.get(newValue)
    const sovitsWeight = sovitsWeights.get(newValue)
    if (newVar) {
      TTSResult.value.refAudio = newVar.value
    }
    if (promptText) {
      TTSResult.value.promptText = promptText
      selectedPromptLanguage.value = 'zh'
    }
    if (gptWeight && sovitsWeight) {
      chooseModel(gptWeight, sovitsWeight)
    }
  }
})

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
const cutMethod = ref<SelectProps['options']>([
  {
    value: 'cut0',
    label: 'cut0',
  },
  {
    value: 'cut1',
    label: 'cut1',
  },
  {
    value: 'cut2',
    label: 'cut2',
  },
  {
    value: 'cut3',
    label: 'cut3',
  },
  {
    value: 'cut4',
    label: 'cut4',
  },
  {
    value: 'cut5',
    label: 'cut5',
  },
])
const activeKey = ref('1')
const audioPlayer = ref(null)
const handleCheckboxChange = () => {
  if (isCustomAudio.value) {
  }
}
const isFinished = ref(false)
const generateText = ref('')
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
  if (data.task === 'qwen') {
    generateText.value += data.message
  }
}
const customOptions = ref<SelectProps['options']>()
/**
 * 获取自定义音频
 */
const fetchData = async () => {
  const customVoices = ref<API.AudioFileVO[]>([])
  const res = await getCustomAudioUsingPost()
  if (res.data.data) {
    customVoices.value = res.data.data ?? []
    customOptions.value = customVoices.value.map((option) => ({
      value: option.filePath,
      label: option.title || '未命名' + option.id,
    }))
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
onMounted(() => {
  fetchData()
})
/**
 * 生成AI回复
 */
const handleGenerate = async () => {
  if (!promptText.value || promptText.value === '') {
    message.error('请输入提示词')
    return
  }
  const res = await qwenUsingPost({
    message: promptText.value,
  })
}

const audioSrc = ref('')

async function startStreaming() {
  const mediaSource = new MediaSource()
  audioPlayer.value.src = URL.createObjectURL(mediaSource)
  mediaSource.addEventListener('sourceopen', async () => {
    const sourceBuffer = mediaSource.addSourceBuffer('audio/mpeg')
    sourceBuffer.mode = 'sequence' // 让音频流保持顺序
    try {
      const response = await fetch('http://i-2.gpushare.com:24924/tts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text: activeKey.value === '1' ? TTSResult.value.text : generateText.value,
          text_lang: selectedLanguage.value,
          ref_audio_path: selectedAudio.value,
          prompt_text: TTSResult.value.promptText,
          prompt_lang: 'zh',
          text_split_method: TTSResult.value.selectedCut,
          streaming_mode: TTSResult.value.streaming_mode,
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
          console.log('收到第一段音频流，开始播放')
          console.timeEnd('总耗时') // 计算第一段数据到达的时间
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
    } finally {
      isGenerating.value = false
    }
  })
}

/**
 * 监听拖拽事件
 * @param e
 */
function handleDrop(e: DragEvent) {
  console.log(e)
}

/**
 * 监听上传文件的状态
 * @param info
 */
const handleChange = (info: UploadChangeParam) => {
  const status = info.file.status
  if (status !== 'uploading') {
  }
  if (status === 'done') {
    fileName.value = info.file.name
    message.success('文件上传成功')
  } else if (status === 'error') {
    message.error('文件上传失败')
  }
}
/**
 * 上传文件
 * @param file
 * @param onProgress
 */
const handleUpload = async ({ file, onSuccess, onError }: any) => {
  loading.value = true
  progress.value = 0
  try {
    const params = { biz: 'audio_file' }
    // 上传文件
    const res = await uploadFileUsingPost(params, {}, file)
    // 如果请求成功
    // @ts-ignore
    if (res.data.code === 0) {
      // @ts-ignore
      uploadAudioUrl.value = res.data.data
      console.log(uploadAudioUrl.value)
      isUploaded.value = true
      // 调用 onSuccess 来通知 Ant Design 上传已完成
      onSuccess(res.data)
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
watch(recordAudioUrl, (newValue) => {
  if (newValue) {
    closeUploading()
  }
})
watch(uploadAudioUrl, (newValue) => {
  if (newValue) {
    closeRecording()
  }
})
// 开始录音
// 状态管理
const recorder = ref<RecordRTC | null>(null)
const audioUrl = ref<string | null>(null)
const audioBlob = ref<Blob | null>(null)
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
      handleOk()
      resolve()
    })
  })
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
    const response = await uploadFileUsingPost({ biz: 'audio_file' }, {}, wavFile)
    if (response.data.code === 0) {
      recordAudioUrl.value = response.data.data
      message.success('上传成功')
    } else {
      message.error('上传失败，' + response.message)
    }
  } catch (error) {
    console.error('上传错误:', error)
  }
}
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
</script>
<style>
.ant-form-item-label > label {
  background: linear-gradient(45deg, #673ab7, #3f51b5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold; /* 加粗字体 */
}

.setting {
  font-weight: bold; /* 加粗字体 */
}

.button {
  background-color: #4caf50; /* 绿色背景 */
  border: none; /* 去掉边框 */
  color: white; /* 白色文字 */
  padding: 15px 32px; /* 内边距 */
  text-align: center; /* 文字居中 */
  text-decoration: none; /* 去掉下划线 */
  display: inline-block; /* 行内块元素 */
  font-size: 16px; /* 字体大小 */
  margin: 4px 2px; /* 外边距 */
  cursor: pointer; /* 鼠标悬停时显示为手型 */
  border-radius: 12px; /* 圆角边框 */
  transition: background-color 0.3s; /* 背景颜色过渡效果 */
}

.button:hover {
  background-color: #45a049; /* 悬停时的背景颜色 */
}
</style>
