<template>
  <div class="add-article-page">
    <div class="magic-container">
      <h1 class="page-title">添加新书籍</h1>

      <a-form :model="formState" :rules="rules" layout="vertical" class="article-form">
        <!-- 书籍名称 -->
        <a-form-item name="title" label="书籍名称">
          <a-input v-model:value="formState.title" placeholder="请输入书籍名称" />
        </a-form-item>

        <!-- 作者 -->
        <a-form-item name="author" label="作者">
          <a-input v-model:value="formState.author" placeholder="请输入作者名称" />
        </a-form-item>

        <!-- 摘要 -->
        <a-form-item name="description" label="摘要（可选）">
          <a-textarea
            v-model:value="formState.description"
            placeholder="请输入书籍摘要"
            :auto-size="{ minRows: 2, maxRows: 4 }"
          />
        </a-form-item>

        <!-- 封面上传 -->
        <a-form-item label="封面图片">
          <div class="cover-upload-container">
            <a-upload
              name="coverImage"
              list-type="picture-card"
              class="cover-uploader"
              :show-upload-list="false"
              :before-upload="beforeUpload"
              @change="handleCoverChange"
              :customRequest="handleUploadCoverImage"
            >
              <div v-if="!coverImage" class="upload-placeholder">
                <div class="upload-icon">
                  <i class="anticon anticon-plus"></i>
                  <div>上传封面</div>
                </div>
              </div>
              <img v-else :src="coverImage" alt="封面" class="cover-preview" />
            </a-upload>
            <div class="cover-hint">建议尺寸：1080 × 1440像素，JPG/PNG格式</div>
          </div>
        </a-form-item>

        <!-- 内容类型选择 -->
        <a-form-item label="内容类型">
          <a-radio-group v-model:value="contentType">
            <a-radio value="text">文本内容</a-radio>
            <a-radio value="image">图片内容</a-radio>
          </a-radio-group>
        </a-form-item>

        <!-- 文本内容 -->
        <a-form-item v-if="contentType === 'text'" name="content" label="内容">
          <a-textarea
            v-model:value="formState.content"
            placeholder="请输入书籍内容"
            :auto-size="{ minRows: 6, maxRows: 12 }"
          />
        </a-form-item>

        <!-- 图片内容 -->
        <a-form-item v-else name="contentImages" label="内容图片">
          <div class="content-upload-container">
            <a-upload
              v-model:file-list="fileList"
              name="contentImages"
              list-type="picture-card"
              :before-upload="beforeUpload"
              @change="handleContentImageChange"
              :customRequest="handleUploadContentImage"
              multiple
            >
              <div v-if="fileList.length < 8" class="upload-icon">
                <i class="anticon anticon-plus"></i>
                <div>上传图片</div>
              </div>
            </a-upload>
            <div class="content-hint">可上传多张图片作为内容，每张不超过2MB，最多8张</div>
          </div>
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
import { reactive, ref } from 'vue'
import type { UploadProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import {
  addArticleUsingPost,
  uploadContentImgUsingPost,
  uploadCoverImgUsingPost,
} from '@/api/articleController.ts'

const router = useRouter()

// 表单数据
const formState = reactive({
  title: '',
  author: '',
  description: '',
  content: '',
  contentImages: [],
})

// 封面图片
const coverImage = ref<string>('')
// 内容图片上传
const contentImages = ref<string[]>([])
const fileList = ref<any[]>([])
const loading = ref<boolean>(false)

// 内容类型选择
const contentType = ref<'text' | 'image'>('text')

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入书籍名称', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者名称', trigger: 'blur' }],
}
/**
 * 上传封面图片
 * @param file
 */
const handleUploadCoverImage = async ({ file }: any) => {
  try {
    const res = await uploadCoverImgUsingPost({}, file)
    if (res.data.code === 0 && res.data.data) {
      coverImage.value = res.data.data
      message.success('上传成功')
    } else {
      message.error('上传失败' + res.data.message)
    }
  } catch (e) {
    message.error('上传失败' + e)
  }
}
/**
 * 上传内容图片
 * @param file
 */
const handleUploadContentImage = async ({ file, onSuccess, onError }: any) => {
  try {
    const res = await uploadContentImgUsingPost({}, file)
    if (res.data.code === 0 && res.data.data) {
      formState.contentImages.push(res.data.data)
      console.log(formState.contentImages)
      onSuccess(res.data.data, file)
    } else {
      onError(new Error(res.data.message || '上传失败'))
      message.error('上传失败' + res.data.message)
    }
  } catch (e) {
    onError(e)
    message.error('上传失败' + e)
  }
}
// 提交表单
const handleSubmit = async () => {
  loading.value = true
  try {
    if (formState.title === '') {
      message.error('请输入书籍名称')
      return
    }
    if (formState.author === '') {
      message.error('请输入作者名称')
      return
    }
    if (!coverImage.value) {
      message.error('请上传封面图片  ')
      return
    }
    if (contentType.value === 'text' && formState.content === '') {
      message.error('请输入书籍内容')
      return
    }
    if (contentType.value === 'image' && formState.contentImages.length === 0) {
      message.error('请上传书籍内容图片')
      return
    }
    const params = ref<API.ArticleAddRequest>({
      title: formState.title,
      author: formState.author,
      summary: formState.description,
      content: formState.content,
      coverImage: coverImage.value,
      contentImages: formState.contentImages,
    })
    const res = await addArticleUsingPost(params.value)
    if (res.data.code === 0) {
      message.success('上传成功，即将跳转至文章列表页面')
      loading.value = false
      setTimeout(() => {
        router.push('/article/library')
      }, 3000)
    } else {
      message.error('上传失败' + res.data.message)
    }
  } catch (e) {
    message.error('上传失败' + e)
  } finally {
    loading.value = false
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/article-library')
}

// 封面图片上传
const handleCoverChange: UploadProps['onChange'] = (info) => {
  console.log('handleCoverChange', info)
  if (info.file.status === 'uploading') {
    return
  }
  if (info.file.status === 'done') {
    // 获取上传的图片URL
    // 注意：实际项目中应该从后端返回的数据中获取URL
    const url = URL.createObjectURL(info.file.originFileObj as Blob)
    coverImage.value = url
    message.success(`${info.file.name} 上传成功`)
  } else if (info.file.status === 'error') {
    message.error(`${info.file.name} 上传失败`)
  }
}

// 图片上传前检查
const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传JPG/PNG格式的图片！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!isLt2M) {
    message.error('图片大小不能超过10MB！')
    return false
  }
  return isJpgOrPng && isLt2M
}

// 在 <script setup> 部分
const handleContentImageChange: UploadProps['onChange'] = ({ file, fileList: newFileList }) => {
  fileList.value = newFileList

  if (file.status === 'done') {
    // 获取上传成功的图片URL
    const url = URL.createObjectURL(file.originFileObj as Blob)
    // 确保不重复添加同一张图片
    if (!contentImages.value.includes(url)) {
      contentImages.value.push(url)
      console.log('contentImages.value:', contentImages.value)
    }
    message.success(`${file.name} 上传成功`)
  } else if (file.status === 'error') {
    message.error(`${file.name} 上传失败`)
  }
}
</script>

<style scoped>
/* 流光特效变量 */
@property --rotate {
  syntax: '<angle>';
  initial-value: 132deg;
  inherits: false;
}

.add-article-page {
  font-family: Arial, sans-serif;
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.magic-container {
  position: relative;
  width: 100%;
  padding: 4px;
  border-radius: 12px;
  background: #131a28;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
  overflow: hidden;
}

/* 流光边框效果 */
.magic-container::before {
  content: '';
  width: 104%;
  height: 102%;
  border-radius: 12px;
  background-image: linear-gradient(var(--rotate), #5ddcff, #3c67e3 43%, #4e00c2);
  position: absolute;
  z-index: -1;
  top: -1%;
  left: -2%;
  animation: spin 6s linear infinite;
}

.magic-container::after {
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
  filter: blur(60px);
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

.page-title {
  text-align: center;
  color: #fff;
  font-size: 28px;
  margin: 0;
  padding: 30px 0;
  text-shadow: 0 0 10px rgba(93, 220, 255, 0.6);
  letter-spacing: 1px;
}

.article-form {
  padding: 20px 40px 40px;
  background: #131a28;
  border-radius: 8px;
}

/* 自定义表单样式 */
:deep(.ant-form-item-label > label) {
  color: #fff;
  font-size: 15px;
}

:deep(.ant-input),
:deep(.ant-input-textarea) {
  background-color: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(93, 220, 255, 0.3);
  color: #fff;
  border-radius: 4px;
  transition: all 0.3s ease;
}

:deep(.ant-input:hover),
:deep(.ant-input-textarea:hover) {
  border-color: rgba(93, 220, 255, 0.6);
  box-shadow: 0 0 5px rgba(93, 220, 255, 0.3);
}

:deep(.ant-input:focus),
:deep(.ant-input-textarea:focus) {
  border-color: #3c67e3;
  box-shadow: 0 0 8px rgba(93, 220, 255, 0.5);
}

/* 上传组件样式 */
.cover-upload-container,
.content-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 15px;
  width: 100%;
}

:deep(.ant-upload-select) {
  background-color: rgba(0, 0, 0, 0.2) !important;
  border: 1px dashed rgba(93, 220, 255, 0.5) !important;
  border-radius: 4px;
  overflow: hidden;
}

.cover-uploader {
  width: 200px;
  height: 250px;
}

:deep(.ant-upload-list) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-start;
  width: 100%;
}

:deep(.ant-upload-list-picture-card-container),
:deep(.ant-upload-select-picture-card) {
  width: 120px !important;
  height: 120px !important;
  margin: 0 !important;
}

:deep(.ant-upload-list-item-thumbnail),
:deep(.ant-upload-list-item-thumbnail img) {
  object-fit: cover !important;
  width: 100% !important;
  height: 100% !important;
}

:deep(.ant-upload-select) .upload-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.upload-icon {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: rgba(255, 255, 255, 0.6);
  height: 100%;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-hint,
.content-hint {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  align-self: flex-start;
}

/* 单选按钮样式 */
:deep(.ant-radio-wrapper) {
  color: #fff;
}

:deep(.ant-radio-inner) {
  background-color: rgba(0, 0, 0, 0.2);
  border-color: rgba(93, 220, 255, 0.5);
}

:deep(.ant-radio-checked .ant-radio-inner) {
  border-color: #00baff;
  background-color: #00baff;
}

/* 按钮样式 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

:deep(.ant-btn) {
  border-radius: 4px;
  height: 40px;
  padding: 0 25px;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(45deg, #3c67e3, #00baff);
  border: none;
  transition: all 0.3s ease;
}

:deep(.ant-btn-primary:hover) {
  background: linear-gradient(45deg, #4e00c2, #3c67e3);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(60, 103, 227, 0.4);
}

:deep(.ant-btn-default) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
}

:deep(.ant-btn-default:hover) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .add-article-page {
    padding: 20px 10px;
  }

  .article-form {
    padding: 15px 20px 30px;
  }

  .page-title {
    font-size: 24px;
    padding: 20px 0;
  }
}
</style>
