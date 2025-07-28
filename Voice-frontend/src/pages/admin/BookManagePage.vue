<template>
  <div id="BookManagePage">
    <div class="page-header">
      <div class="header-title">
        <BookFilled class="header-icon" />
        <h2>书籍管理</h2>
      </div>
      <div class="header-desc">管理系统中的所有书籍，支持查看、编辑和删除操作</div>
    </div>

    <!-- 搜索表单 -->
<!--    <div class="search-container">-->
<!--      <a-form layout="inline" :model="searchParams" @finish="doSearch">-->
<!--        <a-form-item label="书籍名称">-->
<!--          <a-input-->
<!--            v-model:value="searchParams.name"-->
<!--            placeholder="输入书籍名称"-->
<!--            allow-clear-->
<!--            class="custom-input"-->
<!--          />-->
<!--        </a-form-item>-->
<!--        <a-form-item label="类型">-->
<!--          <a-select-->
<!--            v-model:value="searchParams.category"-->
<!--            placeholder="选择类型"-->
<!--            allow-clear-->
<!--            class="custom-select"-->
<!--          >-->
<!--            <a-select-option value="小说">小说</a-select-option>-->
<!--            <a-select-option value="教育">教育</a-select-option>-->
<!--            <a-select-option value="历史">历史</a-select-option>-->
<!--            <a-select-option value="科技">科技</a-select-option>-->
<!--            <a-select-option value="文学">文学</a-select-option>-->
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
      <div class="table-header">
        <h3>书籍列表</h3>
        <a-space>
          <a-button type="primary" class="add-chapter-btn" @click="goToAddChapter">
            <template #icon>
              <file-add-outlined />
            </template>
            添加章节
          </a-button>
          <a-button type="primary" class="add-btn" @click="goToAddArticle">
            <template #icon>
              <PlusOutlined />
            </template>
            新增书籍
          </a-button>
        </a-space>
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
          <template v-if="column.dataIndex === 'cover'">
            <div class="book-cover">
              <img :src="record.coverImage || defaultCover" alt="封面" />
            </div>
          </template>
          <template v-if="column.dataIndex === 'category'">
            <a-tag :color="getCategoryColor(record.category)">{{ record.category }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'updateTime'">
            <span class="time-tag">{{ dayjs(record.updateTime).format('YYYY-MM-DD') }}</span>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'warning'">
              {{ record.status === 1 ? '已发布' : '草稿' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" class="action-btn preview-btn" @click="handleView(record)">
                <template #icon>
                  <EyeOutlined />
                </template>
                查看
              </a-button>
              <a-button type="primary" class="action-btn edit-btn" @click="handleEdit(record)">
                <template #icon>
                  <EditOutlined />
                </template>
                编辑
              </a-button>
              <a-popconfirm
                title="确定要删除此书籍吗?"
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import {
  BookFilled,
  DeleteOutlined,
  EditOutlined,
  EyeOutlined,
  FileAddOutlined,
  PlusOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import { deleteArticleUsingPost, listArticleByPageUsingPost } from '@/api/articleController.ts'

const router = useRouter()
const defaultCover = '/src/assets/default-cover.jpg'

const columns = [
  {
    title: '封面',
    dataIndex: 'cover',
    width: 100,
  },
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    ellipsis: true,
  },
  {
    title: '书籍名称',
    dataIndex: 'title',
    with: 120,
    ellipsis: true,
  },
  {
    title: '作者',
    dataIndex: 'author',
    width: 120,
  },
  {
    title: '类型',
    dataIndex: 'category',
    width: 100,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 180,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
  },
  {
    title: '操作',
    key: 'action',
    width: 380,
  },
]

// 定义数据
const dataList = ref<API.Article[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.ArticleQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取分类颜色
const getCategoryColor = (category: string) => {
  const colorMap: Record<string, string> = {
    小说: 'blue',
    教育: 'green',
    历史: 'orange',
    科技: 'purple',
    文学: 'cyan',
  }
  return colorMap[category] || 'default'
}

// 获取数据
const fetchData = async () => {
  try {
    const res = await listArticleByPageUsingPost(searchParams)
    if (res.data.code === 0) {
      dataList.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败：' + res.data.message)
    }
  } catch (error) {
    message.error('获取数据失败')
  }
}

// 查看书籍
const handleView = (record: API.Article) => {
  router.push(`/article/detail/${record.id}`)
}

// 编辑书籍
const handleEdit = (record: API.Article) => {
  router.push(`/update/article/${record.id}`)
}

// 新增书籍
const goToAddArticle = () => {
  router.push('/add/article')
}

// 添加章节
const goToAddChapter = () => {
  router.push('/add/chapter')
}

// 删除书籍
const doDelete = async (id: number) => {
  try {
    const res = await deleteArticleUsingPost({ id })
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
#BookManagePage {
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

.add-btn {
  background: linear-gradient(45deg, #00923f, #00c853);
  border: none;
  box-shadow: 0 0 10px rgba(0, 200, 83, 0.5);
  transition: all 0.3s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(0, 200, 83, 0.7);
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

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  border: 2px solid rgba(0, 186, 255, 0.3);
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

.edit-btn {
  background: linear-gradient(45deg, #ff8f00, #ffbd45) !important;
  border: none;
  box-shadow: 0 0 10px rgba(255, 143, 0, 0.5);
  transition: all 0.3s ease;
}

.edit-btn:hover {
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
