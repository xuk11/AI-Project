<template>
  <div class="article-library">
    <image-slider />
    <!-- 新增搜索框和按钮 -->
    <div class="search-container">
      <!-- 普通搜索框，覆盖样式使其美观 -->
      <a-input-search
        style="width: 400px; margin-left: 100px"
        v-model:value="value"
        placeholder="从书库中进行搜索"
        enter-button
        @search="onSearch"
        class="custom-search-input"
      />
      <!-- 添加书籍按钮 -->
      <div class="glowing-btn-container">
        <a class="glowing-add-button" @click="addArticlePage">
          添加个人书籍
        </a>
      </div>
    </div>
    <a-list
      :grid="{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }"
      :data-source="dataList"
      :pagination="pagination"
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
</template>

<script setup lang="ts">
import ImageSlider from '../../components/ImageSlider.vue'
import { listArticleVoByPageUsingPost } from '@/api/articleController.ts'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const value = ref('')

const dataList = ref<API.ArticleVO[]>([])
const total = ref(0)
const loading = ref(true)
const searchParams = reactive<API.ArticleQueryRequest>({
  current: 1,
  pageSize: 8,
  sortField: 'createTime',
  sortOrder: 'descend',
  searchText: '',
})
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    // 切换页号时，会修改搜索参数并获取数据
    onChange: (page: any, pageSize: any) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
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
// 添加书籍页面跳转
const addArticlePage = () => {
  const url = router.resolve({
    path: '/add/article',
  }).href
  window.open(url, '_blank')
}

// 搜索功能
const onSearch = (searchValue: string) => {
  searchParams.searchText = searchValue
  searchParams.current = 1
  fetchData()
}

// 格式化日期
const formatDate = (timestamp: string | number | undefined) => {
  if (!timestamp) return '未知时间'
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchData = async () => {
  const params = {
    ...searchParams,
    priority: 1,
  }
  loading.value = true
  const res = await listArticleVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* 流光特效所需的CSS变量 */
@property --rotate {
  syntax: '<angle>';
  initial-value: 132deg;
  inherits: false;
}

.article-library {
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 搜索框和按钮容器样式 */
.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  padding: 0 20px;
}

/* 自定义搜索框样式 */
.custom-search-input {
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(93, 220, 255, 0.3);
}

:deep(.custom-search-input .ant-input) {
  background-color: #131a28 !important;
  color: #ffffff !important;
  border: 1px solid rgba(93, 220, 255, 0.3) !important;
  height: 42px;
  padding-left: 15px;
}

:deep(.custom-search-input .ant-input-group-addon) {
  background-color: #131a28 !important;
  border: 1px solid rgba(93, 220, 255, 0.3) !important;
}

:deep(.custom-search-input .ant-input-search-button) {
  background: linear-gradient(
    45deg,
    rgba(60, 103, 227, 0.8) 0%,
    rgba(93, 220, 255, 0.8) 100%
  ) !important;
  border: none !important;
  height: 42px;
  border-radius: 0 30px 30px 0 !important;
  color: white !important;
  box-shadow: none;
  transition: all 0.3s ease;
}

:deep(.custom-search-input .ant-input-search-button:hover) {
  background: linear-gradient(
    45deg,
    rgba(93, 220, 255, 0.8) 0%,
    rgba(60, 103, 227, 0.8) 100%
  ) !important;
  box-shadow: 0 0 15px rgba(93, 220, 255, 0.6);
  transform: translateY(-2px);
}

:deep(.custom-search-input .ant-input-affix-wrapper) {
  background-color: #131a28 !important;
  border-right: none !important;
  border-radius: 30px 0 0 30px !important;
}

:deep(.custom-search-input .ant-input-affix-wrapper:focus),
:deep(.custom-search-input .ant-input-affix-wrapper-focused) {
  box-shadow: none !important;
}

:deep(.custom-search-input .ant-input::placeholder) {
  color: rgba(255, 255, 255, 0.6);
}

/* 添加书籍按钮样式 */
.glowing-btn-container {
  position: relative;
}

.glowing-add-button {
  position: relative;
  display: inline-block;
  padding: 12px 24px;
  color: #ffffff;
  text-decoration: none;
  overflow: hidden;
  background: linear-gradient(
    45deg,
    rgba(60, 103, 227, 0.2) 0%,
    rgba(93, 220, 255, 0.2) 100%
  );
  border-radius: 30px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 1px solid rgba(93, 220, 255, 0.3);
  box-shadow: 0 0 15px rgba(93, 220, 255, 0.3),
              inset 0 0 10px rgba(93, 220, 255, 0.1);
}

.glowing-add-button:hover {
  background: linear-gradient(
    45deg,
    rgba(60, 103, 227, 0.8) 0%,
    rgba(93, 220, 255, 0.8) 100%
  );
  color: #ffffff;
  box-shadow: 0 0 25px rgba(93, 220, 255, 0.5),
              inset 0 0 15px rgba(255, 255, 255, 0.2);
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.glowing-add-button:active {
  transform: translateY(0);
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
</style>
