<template>
  <div class="add-chapter-page">
    <div class="magic-container">
      <h1 class="page-title">添加新章节</h1>

      <a-form :model="formState" :rules="rules" layout="vertical" class="chapter-form">
        <!-- 书本选择 -->
        <a-form-item name="articleId" label="选择书本">
          <a-select
            v-model:value="formState.articleId"
            placeholder="请选择书本"
            show-search
            :options="bookOptions"
            :filter-option="filterOption"
            @change="handleBookChange"
          />
        </a-form-item>

        <!-- 章节标题 -->
        <a-form-item name="chapterTitle" label="章节标题">
          <a-input v-model:value="formState.chapterTitle" placeholder="请输入章节标题" />
        </a-form-item>

        <!-- 章节序号 -->
        <a-form-item name="chapterOrder" label="章节序号">
          <a-input-number
            v-model:value="formState.chapterOrder"
            :min="1"
            placeholder="请输入章节序号"
            style="width: 100%"
          />
        </a-form-item>

        <!-- 章节字数 -->
        <a-form-item name="wordCount" label="章节字数">
          <a-input-number
            v-model:value="formState.wordCount"
            :min="0"
            placeholder="请输入章节字数（可选）"
            style="width: 100%"
          />
        </a-form-item>

        <!-- 是否免费 -->
        <a-form-item name="isFree" label="是否免费">
          <a-switch v-model:checked="formState.isFree" />
        </a-form-item>

        <!-- 章节内容 -->
        <a-form-item name="content" label="章节内容">
          <a-textarea
            v-model:value="formState.content"
            placeholder="请输入章节内容，每个段落将自动分割"
            :auto-size="{ minRows: 10, maxRows: 20 }"
          />
        </a-form-item>

        <!-- 按钮区域 -->
        <div class="form-actions">
          <a-button @click="handleCancel">取消</a-button>
          <a-button type="primary" @click="handleSubmit" :loading="loading" class="submit-button">
            提交
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import {
  addChapterUsingPost,
  getAllArticleUsingPost,
  getArticleVoByIdUsingGet,
  listChapterUsingPost,
} from '@/api/articleController.ts'

const router = useRouter()

// 表单数据
const formState = reactive({
  articleId: undefined,
  chapterTitle: '',
  chapterOrder: 1,
  wordCount: 0,
  isFree: true,
  content: '',
})

// 书本选项
const bookOptions = ref<{ value: number; label: string }[]>([])
const loading = ref<boolean>(false)
const selectedBookTitle = ref<string>('')

// 表单验证规则
const rules = {
  articleId: [{ required: true, message: '请选择书本', trigger: 'change' }],
  chapterTitle: [{ required: true, message: '请输入章节标题', trigger: 'blur' }],
  chapterOrder: [{ required: true, message: '请输入章节序号', trigger: 'blur' }],
  content: [{ required: true, message: '请输入章节内容', trigger: 'blur' }],
}

// 获取所有书本
const fetchAllBooks = async () => {
  try {
    const res = await getAllArticleUsingPost()
    if (res.data.code === 0 && res.data.data) {
      bookOptions.value = res.data.data.map((book: API.Article) => ({
        value: book.id,
        label: `${book.title} (ID: ${book.id})`,
      }))
    } else {
      message.error('获取书本列表失败: ' + res.data.message)
    }
  } catch (error) {
    message.error('获取书本列表失败: ' + error)
  }
}

// 过滤选项
const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 书本变更处理
const handleBookChange = async (value: number) => {
  try {
    // 获取当前书本信息
    const res = await getArticleVoByIdUsingGet({ id: value })
    if (res.data.code === 0 && res.data.data) {
      selectedBookTitle.value = res.data.data.title
    }

    // 获取当前书本的所有章节
    const chaptersRes = await listChapterUsingPost({ articleId: value })
    if (chaptersRes.data.code === 0 && chaptersRes.data.data) {
      // 设置下一个章节序号为当前最大序号+1
      const chapters = chaptersRes.data.data || []
      if (chapters.length > 0) {
        const maxOrder = Math.max(
          ...chapters.map((chapter: API.Chapter) => chapter.chapterOrder || 0),
        )
        formState.chapterOrder = maxOrder + 1
      } else {
        formState.chapterOrder = 1
      }
    }
  } catch (error) {
    console.error('获取书本信息失败:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  // 表单验证
  if (!formState.articleId) {
    message.error('请选择书本')
    return
  }
  if (!formState.chapterTitle) {
    message.error('请输入章节标题')
    return
  }
  if (!formState.content) {
    message.error('请输入章节内容')
    return
  }

  loading.value = true
  try {
    // 将章节内容按段落分割
    const contentArray = formState.content
      .split('\n')
      .filter((paragraph) => paragraph.trim() !== '')

    // 计算字数（如果没有输入）
    if (!formState.wordCount) {
      formState.wordCount = formState.content.replace(/\s/g, '').length
    }

    // 构建请求参数
    const params = ref<API.ChapterAddRequest>({
      articleId: formState.articleId,
      chapterTitle: formState.chapterTitle,
      chapterOrder: formState.chapterOrder,
      wordCount: formState.wordCount,
      isFree: 0,
      content: JSON.stringify(contentArray),
    })
    const res = await addChapterUsingPost(params.value)

    if (res.data.code === 0) {
      message.success('章节添加成功')
    } else {
      message.error('章节添加失败: ' + res.data.message)
    }
  } catch (error) {
    message.error('章节添加失败: ' + error)
  } finally {
    loading.value = false
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/admin/book')
}

// 页面初始化
onMounted(() => {
  fetchAllBooks()
})
</script>

<style scoped>
.add-chapter-page {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.magic-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 24px;
  margin-bottom: 30px;
  color: #333;
  font-weight: 500;
}

.chapter-form {
  max-width: 800px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.submit-button {
  min-width: 100px;
}
</style>
