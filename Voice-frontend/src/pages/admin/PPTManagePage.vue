<template>
  <div id="AuditorypptManagePage">
    <div class="page-header">
      <div class="header-title">
        <FileOutlined class="header-icon" />
        <h2>有声PPT管理</h2>
      </div>
      <div class="header-desc">管理系统中的所有有声PPT文件，支持预览、下载和删除操作</div>
    </div>

    <!-- 搜索表单 -->
<!--    <div class="search-container">-->
<!--      <a-form layout="inline" :model="searchParams" @finish="doSearch">-->
<!--        <a-form-item label="文件名">-->
<!--          <a-input-->
<!--            style="color: white"-->
<!--            v-model:value="searchParams.name"-->
<!--            allow-clear-->
<!--            class="custom-input"-->
<!--          />-->
<!--        </a-form-item>-->
<!--        <a-form-item label="文件类型">-->
<!--          <a-select v-model:value="searchParams.fileType" allow-clear class="custom-select">-->
<!--            <a-select-option value="Auditoryppt/mp4">MP4</a-select-option>-->
<!--            <a-select-option value="Auditoryppt/avi">AVI</a-select-option>-->
<!--            <a-select-option value="Auditoryppt/mov">MOV</a-select-option>-->
<!--          </a-select>-->
<!--        </a-form-item>-->
<!--        <a-form-item>-->
<!--          <a-button type="primary" html-type="submit" class="search-btn">-->
<!--            <template #icon>-->
<!--              <SearchOutlined />-->
<!--            </template>-->
<!--            搜索-->
<!--          </a-button>-->
<!--        </a-form-item>-->
<!--      </a-form>-->
<!--    </div>-->

    <!-- 表格 -->
    <div class="table-container">
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
            <div class="video-preview">
              <video controls>
                <source :src="record.filePath" :type="record.fileType" />
                您的浏览器不支持视频标签。
              </video>
              <div class="preview-overlay" @click="openPreview(record)">
                <PlayCircleOutlined />
              </div>
            </div>
          </template>
          <template v-if="column.dataIndex === 'duration'">
            {{ secondsToHMS(record.duration) }}
          </template>
          <template v-if="column.dataIndex === 'fileType'">
            {{ getFileType(record.fileType) }}
          </template>
          <template v-if="column.dataIndex === 'fileSize'">
            <span class="size-tag">{{ formatFileSize(record.fileSize) }}</span>
          </template>
          <template v-if="column.dataIndex === 'createTime'">
            <span class="time-display">{{ dayjs(record.createTime).format('YYYY-MM-DD') }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" class="action-btn preview-btn" @click="openPreview(record)">
                <template #icon>
                  <EyeOutlined />
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
                title="确定要删除此文件吗?"
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
    <a-modal
      v-model:visible="previewVisible"
      title="视频预览"
      width="800px"
      :footer="null"
      class="preview-modal"
      :destroyOnClose="true"
    >
      <div class="modal-video-container">
        <video v-if="previewUrl" controls autoplay style="width: 100%">
          <source :src="previewUrl" :type="currentAuditoryppt?.fileType" />
        </video>
      </div>
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
  FileOutlined,
  PlayCircleOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import {
  deleteAuditorypptUsingPost,
  listAuditorypptByPageUsingPost,
} from '@/api/auditorypptController.ts'
import dayjs from 'dayjs'

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
  },
  {
    title: '时长',
    dataIndex: 'duration',
    width: 80,
  },
  {
    title: '文件类型',
    dataIndex: 'fileType',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 140,
  },
  {
    title: '操作',
    key: 'action',
    width: 340,
  },
]

// 定义数据
const dataList = ref<API.Auditoryppt[]>([])
const total = ref(0)
const previewVisible = ref(false)
const previewUrl = ref('')
const currentAuditoryppt = ref<API.Auditoryppt>()

// 搜索条件
const searchParams = reactive<API.AuditorypptQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 打开预览
const openPreview = (record: API.Auditoryppt) => {
  if (record.filePath) {
    previewUrl.value = record.filePath
    currentAuditoryppt.value = record
    previewVisible.value = true
  } else {
    message.error('预览地址不存在')
  }
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
    const res = await listAuditorypptByPageUsingPost(searchParams)
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

// 下载视频
const handleDownload = (record: API.Auditoryppt) => {
  if (record.filePath) {
    window.open(record.filePath)
  } else {
    message.error('下载地址不存在')
  }
}

// 删除视频
const doDelete = async (id: number) => {
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
  }
}
const getFileType = (fileType: string) => {
  if (!fileType) return ''
  return fileType.split('/')[1].toUpperCase()
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
#AuditorypptManagePage {
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

.custom-input::-webkit-input-placeholder {
  color: white;
}

.custom-input::-moz-placeholder {
  color: white;
}

.custom-input:-ms-input-placeholder {
  color: white;
}

.custom-input::-ms-input-placeholder {
  color: white;
}

.custom-input::placeholder {
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
  min-width: 200px;
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

.video-preview {
  width: 280px;
  height: 160px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 2px solid rgba(0, 186, 255, 0.3);
}

.video-preview video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.preview-overlay:hover {
  opacity: 1;
}

.preview-overlay .anticon {
  font-size: 48px;
  color: #00baff;
  filter: drop-shadow(0 0 8px rgba(0, 186, 255, 0.8));
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
  background: linear-gradient(45deg, #0063cc, #0099ff) !important;
  border: none;
  box-shadow: 0 0 10px rgba(0, 128, 255, 0.5);
  transition: all 0.3s ease;
}

.download-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(0, 128, 255, 0.7);
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

.size-tag {
  background: rgba(0, 186, 255, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid rgba(0, 186, 255, 0.3);
}

.time-display {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.preview-modal {
  :deep(.ant-modal-content) {
    background: rgba(19, 26, 40, 0.95);
    border: 1px solid rgba(0, 186, 255, 0.3);
    border-radius: 8px;
    backdrop-filter: blur(10px);
  }

  :deep(.ant-modal-header) {
    background: transparent;
    border-bottom: 1px solid rgba(0, 186, 255, 0.2);
  }

  :deep(.ant-modal-title) {
    color: #00baff;
  }

  :deep(.ant-modal-close) {
    color: #fff;
  }

  :deep(.ant-modal-close:hover) {
    color: #00baff;
  }
}

.modal-video-container {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 186, 255, 0.3);
}
</style>
