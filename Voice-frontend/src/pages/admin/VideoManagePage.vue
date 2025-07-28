<template>
  <div id="VideoManagePage">
    <div class="page-header">
      <div class="header-title">
        <video-camera-outlined class="header-icon" />
        <h2>视频管理</h2>
      </div>
      <div class="header-desc">管理系统中的所有视频，支持预览、下载和删除操作</div>
    </div>

    <!-- 搜索表单 -->
    <!--    <div class="search-container">-->
    <!--      <a-form layout="inline" :model="searchParams" @finish="doSearch">-->
    <!--        <a-form-item label="文件名">-->
    <!--          <a-input-->
    <!--            v-model:value="searchParams.name"-->
    <!--            placeholder="输入文件名"-->
    <!--            allow-clear-->
    <!--            class="custom-input"-->
    <!--          />-->
    <!--        </a-form-item>-->
    <!--        <a-form-item label="文件类型">-->
    <!--          <a-select-->
    <!--            v-model:value="searchParams.fileType"-->
    <!--            placeholder="选择文件类型"-->
    <!--            allow-clear-->
    <!--            class="custom-select"-->
    <!--          >-->
    <!--            <a-select-option value="Video/mp4">MP4</a-select-option>-->
    <!--            <a-select-option value="Video/avi">AVI</a-select-option>-->
    <!--            <a-select-option value="Video/mov">MOV</a-select-option>-->
    <!--          </a-select>-->
    <!--        </a-form-item>-->
    <!--        <a-form-item>-->
    <!--          <a-button type="primary" html-type="submit" class="search-btn">-->
    <!--            <template #icon>-->
    <!--              <search-outlined />-->
    <!--            </template>-->
    <!--            搜索-->
    <!--          </a-button>-->
    <!--        </a-form-item>-->
    <!--      </a-form>-->
    <!--    </div>-->

    <!-- 表格 -->
    <div class="table-container">
      <div class="table-header">
        <h3>视频列表</h3>
      </div>

      <a-table
        :columns="columns"
        :data-source="dataList"
        :pagination="pagination"
        @change="doTableChange"
        class="custom-table"
        :scroll="{ x: 1200 }"
        :row-key="(record) => record.id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'preview'">
            <Video controls style="width: 300px; height: 180px">
              <source :src="record.filePath" :type="record.fileType" />
              您的浏览器不支持视频标签。
            </Video>
          </template>
          <template v-if="column.dataIndex === 'fileSize'">
            {{ formatFileSize(record.fileSize) }}
          </template>
          <template v-if="column.dataIndex === 'duration'">
            {{ secondsToHMS(record.duration) }}
          </template>
          <template v-if="column.dataIndex === 'fileType'">
            {{ getFileType(record.fileType) }}
          </template>
          <template v-if="column.dataIndex === 'createTime'">
            <span class="time-tag">{{ dayjs(record.createTime).format('YYYY-MM-DD') }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                type="primary"
                class="action-btn preview-btn"
                @click="handlePreview(record)"
              >
                <template #icon>
                  <eye-outlined />
                </template>
                预览
              </a-button>
              <a-button
                type="primary"
                class="action-btn download-btn"
                @click="handleDownload(record)"
              >
                <template #icon>
                  <download-outlined />
                </template>
                下载
              </a-button>
              <a-popconfirm
                title="确定要删除此视频吗?"
                ok-text="确定"
                cancel-text="取消"
                @confirm="doDelete(record.id)"
              >
                <a-button type="primary" danger class="action-btn delete-btn">
                  <template #icon>
                    <delete-outlined />
                  </template>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 预览模态框 -->
    <a-modal v-model:visible="previewVisible" title="视频预览" width="800px" :footer="null">
      <Video v-if="previewUrl" controls style="width: 100%">
        <source :src="previewUrl" :type="currentVideo?.fileType" />
      </Video>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  DownloadOutlined,
  EyeOutlined,
  VideoCameraOutlined,
} from '@ant-design/icons-vue'
import { deleteVideoUsingPost, listVideoByPageUsingPost } from '@/api/videoController.ts'
import dayjs from 'dayjs'

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

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    ellipsis: true,
  },
  {
    title: '预览',
    dataIndex: 'preview',
    width: 300,
    fixed: 'left',
  },
  {
    title: '文件大小',
    dataIndex: 'fileSize',
    width: 120,
    sorter: true,
  },
  {
    title: '时长',
    dataIndex: 'duration',
    width: 120,
    sorter: true,
  },
  {
    title: '文件类型',
    dataIndex: 'fileType',
    width: 90,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 140,
  },
  {
    title: '操作',
    key: 'action',
    width: 320,
  },
]

// 定义数据
const dataList = ref<API.Video[]>([])
const total = ref(0)
const previewVisible = ref(false)
const previewUrl = ref('')
const currentVideo = ref<API.Video>()

// 搜索条件
const searchParams = reactive<API.VideoQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 格式化文件类型
const getFileType = (fileType: string) => {
  if (!fileType) return ''
  return fileType.split('/')[1].toUpperCase()
}

// 获取文件类型颜色
const getFileTypeColor = (fileType: string) => {
  const colorMap: Record<string, string> = {
    'Video/mp4': 'blue',
    'Video/avi': 'green',
    'Video/mov': 'orange',
  }
  return colorMap[fileType] || 'default'
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`
}

// 获取数据
const fetchData = async () => {
  try {
    const res = await listVideoByPageUsingPost(searchParams)
    if (res.data.code === 0) {
      //@ts-ignore
      dataList.value = res.data.data.records ?? []
      // @ts-ignore
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败：' + res.data.message)
    }
  } catch (error) {
    message.error('获取数据失败')
  }
}

// 预览视频
const handlePreview = (record: API.Video) => {
  currentVideo.value = record
  previewUrl.value = record.filePath || ''
  previewVisible.value = true
}

// 下载视频
const handleDownload = (record: API.Video) => {
  if (record.filePath) {
    window.open(record.filePath)
  } else {
    message.error('下载地址不存在')
  }
}

// 删除视频
const doDelete = async (id: number) => {
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
  }
}

// 分页参数
const pagination = computed(() => ({
  current: searchParams.current,
  pageSize: searchParams.pageSize,
  total: total.value,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
}))

// 表格变化处理
const doTableChange = (pag: any, filters: any, sorter: any) => {
  searchParams.current = pag.current
  searchParams.pageSize = pag.pageSize
  if (sorter.field) {
    searchParams.sortField = sorter.field
    searchParams.sortOrder = sorter.order
  }
  fetchData()
}

// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#VideoManagePage {
  padding: 24px;
  height: 100%;
  width: 100%;
  background: linear-gradient(135deg, #131a28 0%, #131f3d 100%);
  border-radius: 8px;
  overflow: hidden;
  color: #fff;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 1px solid rgba(0, 186, 255, 0.2);
  padding-bottom: 16px;
}

.header-title {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.header-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #00baff;
}

.header-title h2 {
  color: #fff;
  font-size: 24px;
  margin: 0;
}

.header-desc {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.search-container {
  margin-bottom: 24px;
  padding: 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 186, 255, 0.15);
}

.custom-input {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(0, 186, 255, 0.3);
  border-radius: 4px;
  color: white;
}

.custom-input:hover,
.custom-input:focus {
  border-color: #00baff;
  box-shadow: 0 0 5px rgba(0, 186, 255, 0.5);
}

.custom-select {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(0, 186, 255, 0.3);
  border-radius: 4px;
  color: white;
}

.search-btn {
  background: linear-gradient(45deg, #0063cc, #0099ff);
  border: none;
  box-shadow: 0 0 10px rgba(0, 128, 255, 0.5);
  transition: all 0.3s;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(0, 128, 255, 0.7);
  background: linear-gradient(45deg, #0073e6, #00a6ff);
}

.table-container {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid rgba(0, 186, 255, 0.15);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 186, 255, 0.15);
}

.table-header h3 {
  color: #fff;
  margin: 0;
  font-size: 18px;
}

:deep(.custom-table) {
  background: transparent;
}

:deep(.custom-table .ant-table) {
  background: transparent !important;
}

:deep(.custom-table .ant-table-thead > tr > th) {
  background: rgba(0, 186, 255, 0.1);
  color: #fff;
  border-bottom: 1px solid rgba(0, 186, 255, 0.3);
}

:deep(.custom-table .ant-table-tbody > tr > td) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.85);
  background: transparent !important;
}

:deep(.custom-table .ant-table-tbody > tr:hover > td) {
  background: rgba(0, 186, 255, 0.1) !important;
}

:deep(.ant-pagination-item-active) {
  border-color: #00baff;
}

:deep(.ant-pagination-item-active a) {
  color: #00baff;
}

.action-btn {
  margin: 0 4px;
  border-radius: 4px;
  backdrop-filter: blur(5px);
  background: transparent !important;
}

.preview-btn {
  background: linear-gradient(45deg, #7e00cc, #9e00ff) !important;
  border: none;
  box-shadow: 0 0 10px rgba(126, 0, 204, 0.5);
  transition: all 0.3s ease;
}

.preview-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(126, 0, 204, 0.7);
}

.download-btn {
  background: linear-gradient(45deg, #ff8f00, #ffbd45) !important;
  border: none;
  box-shadow: 0 0 10px rgba(255, 143, 0, 0.5);
  transition: all 0.3s ease;
}

.download-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(255, 143, 0, 0.7);
}

.delete-btn {
  background: linear-gradient(45deg, #cc1800, #ff3019) !important;
  border: none;
  box-shadow: 0 0 10px rgba(204, 24, 0, 0.5);
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(204, 24, 0, 0.7);
}

.time-tag {
  background: rgba(0, 186, 255, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  border: none;
}
</style>
