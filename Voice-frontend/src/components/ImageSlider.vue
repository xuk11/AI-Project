<template>
  <div class="container">
    <main>
      <ul class="slider" ref="sliderRef">
        <li
          v-for="(image, index) in images"
          :key="index"
          class="item"
          :style="{ backgroundImage: `url('${image}')` }"
          @click="goToArticleDetail(index)"
        >
          <div class="content">
            <h2 class="title">{{ titles[index] }}</h2>
            <p class="description">
              {{ descriptions[index] }}
            </p>
            <button>查看更多</button>
          </div>
        </li>
      </ul>
      <div class="indicator">
        <span
          v-for="(_, index) in images"
          :key="`ind-${index}`"
          :class="{ active: index === currentIndex }"
        ></span>
      </div>
      <nav class="nav">
        <div class="btn prev" @click="slidePrev">&larr;</div>
        <div class="btn next" @click="slideNext">&rarr;</div>
      </nav>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const goToArticleDetail = (index: number) => {
  index += 3
  const url = router.resolve({
    path: '/article/detail/' + index,
  }).href
  window.open(url, '_blank')
}
// 图片数组
const images = [
  'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Farticle%2FcoverImage%2F2FB9FE22677F571ED1D192AD2A772E13.jpg',
  'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Farticle%2FcoverImage%2F660E57EA79D6B9722C2B76637D237193.jpg',
  'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Farticle%2FcoverImage%2FC85F4522CF0ADE03E9D1714809B6EC87.jpg',
  'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Farticle%2FcoverImage%2FDF2B766A052C29291DC70AC4452B3950.jpg',
  'https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Farticle%2FcoverImage%2FE23453AB187FD0584057A11189EF8476.jpg',
]

// 标题和描述（可以根据需要自定义）
const titles = ['白杨礼赞', '小王子', '我的阿勒泰', '出师表', '云边有个小卖部']

const descriptions = [
  '笔直的树干和倔强的枝丫，带来精神的鼓舞与力量。',
  '澄澈的眼眸和柔软的内心，在星际流浪里探寻爱与责任',
  '多彩的民俗和灿烂的笑容，洋溢着阿勒泰地区的温暖与热情',
  '恳切的言辞与忠诚的赤心，凝聚成北伐兴汉的决心与嘱托。',
  '斑驳的小卖部与轻柔的云朵，牵出少年成长的酸涩与温暖。',
]

const sliderRef = ref<HTMLElement | null>(null)
const currentIndex = ref(0)
let autoplayInterval: number | null = null

// 向后滑动
const slideNext = () => {
  if (sliderRef.value) {
    const items = sliderRef.value.querySelectorAll('.item')
    if (items.length > 0) {
      sliderRef.value.appendChild(items[0])

      // 更新当前索引
      currentIndex.value = (currentIndex.value + 1) % images.length
    }
  }
}

// 向前滑动
const slidePrev = () => {
  if (sliderRef.value) {
    const items = sliderRef.value.querySelectorAll('.item')
    if (items.length > 0) {
      sliderRef.value.prepend(items[items.length - 1])

      // 更新当前索引
      currentIndex.value = (currentIndex.value - 1 + images.length) % images.length
    }
  }
}

// 开始自动轮播
const startAutoplay = () => {
  if (autoplayInterval) return
  autoplayInterval = window.setInterval(() => {
    slideNext()
  }, 3000) // 每3秒切换一次
}

// 停止自动轮播
const stopAutoplay = () => {
  if (autoplayInterval) {
    clearInterval(autoplayInterval)
    autoplayInterval = null
  }
}

onMounted(() => {
  // 初始化当前索引
  currentIndex.value = 1 // 因为第二个元素是显示的主图
  startAutoplay()
})

onUnmounted(() => {
  stopAutoplay()
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  height: 80vh;
  display: grid;
  place-items: center;
  overflow: hidden;
  background-color: #1e2637;
}

main {
  position: relative;
  width: 100%;
  height: 100%;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.item {
  width: 200px;
  height: 300px;
  list-style-type: none;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  background-position: center;
  background-size: cover;
  border-radius: 20px;
  box-shadow: 0 20px 30px rgba(255, 255, 255, 0.3) inset;
  transition:
    transform 0.1s,
    left 0.75s,
    top 0.75s,
    width 0.75s,
    height 0.75s;
}

.item:nth-child(1),
.item:nth-child(2) {
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  transform: none;
  border-radius: 0;
  box-shadow: none;
  opacity: 1;
}

.item:nth-child(3) {
  left: 92%;
  width: 180px;
  height: 270px;
  border-radius: 15px;
  transform: translateY(-50%) translateX(-100%);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  z-index: 2;
  opacity: 0.9;
}

.item:nth-child(3):hover {
  transform: translateY(-50%) translateX(-100%) scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
  opacity: 1;
}

.item:nth-child(4),
.item:nth-child(5),
.item:nth-child(6) {
  display: none;
  opacity: 0;
}

.content {
  width: min(30vw, 400px);
  position: absolute;
  top: 50%;
  left: 3rem;
  transform: translateY(-50%);
  font:
    400 0.85rem helvetica,
    sans-serif;
  color: white;
  text-shadow: 0 3px 8px rgba(0, 0, 0, 0.5);
  opacity: 0;
  display: none;
}

.content .title {
  font-family: 'Quicksand', sans-serif;
  text-transform: uppercase;
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.content .description {
  line-height: 1.7;
  margin: 1rem 0 1.5rem;
  font-size: 0.9rem;
  max-width: 700px;
}

.content button {
  width: fit-content;
  background-color: rgba(0, 0, 0, 0.2);
  color: white;
  border: 2px solid white;
  border-radius: 0.25rem;
  padding: 0.75rem 1.5rem;
  cursor: pointer;
  font-size: 1rem;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.content button:hover {
  background-color: white;
  color: #1e2637;
}

.item:nth-of-type(2) .content {
  display: block;
  animation: show 0.75s ease-in-out 0.3s forwards;
}

@keyframes show {
  0% {
    filter: blur(5px);
    transform: translateY(calc(-50% + 75px));
  }
  100% {
    opacity: 1;
    filter: blur(0);
  }
}

.nav {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 5;
  user-select: none;
}

.nav .btn {
  display: inline-block;
  background-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 255, 255, 0.6);
  margin: 0 0.25rem;
  padding: 0.75rem;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.5rem;
  line-height: 1;
  width: 3rem;
  height: 3rem;
  text-align: center;
  transition: all 0.3s ease;
}

.nav .btn:hover {
  background-color: rgba(255, 255, 255, 0.4);
  transform: scale(1.1);
}

.indicator {
  position: absolute;
  bottom: 4rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 5;
}

.indicator span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator span.active {
  background-color: white;
  transform: scale(1.2);
}

@media (width > 650px) and (width < 900px) {
  .content .title {
    font-size: 2rem;
  }

  .content .description {
    font-size: 0.9rem;
  }

  .content button {
    font-size: 0.9rem;
  }

  .item {
    width: 160px;
    height: 270px;
  }

  .item:nth-child(3) {
    left: 90%;
    width: 150px;
    height: 240px;
    transform: translateY(-50%) translateX(-100%);
  }

  .item:nth-child(4),
  .item:nth-child(5),
  .item:nth-child(6) {
    display: none;
  }
}

@media (width < 650px) {
  .content .title {
    font-size: 1.8rem;
  }

  .content .description {
    font-size: 0.8rem;
  }

  .content button {
    font-size: 0.8rem;
  }

  .item {
    width: 130px;
    height: 220px;
  }

  .item:nth-child(3) {
    left: 88%;
    width: 120px;
    height: 200px;
    transform: translateY(-50%) translateX(-100%);
  }

  .item:nth-child(4),
  .item:nth-child(5),
  .item:nth-child(6) {
    display: none;
  }
}
</style>
