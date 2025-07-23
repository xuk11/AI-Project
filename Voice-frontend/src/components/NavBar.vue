<template>
  <div class="nav-container">
    <canvas
      id="canvas-nav"
      ref="canvasRef"
      style="position: absolute; z-index: -1; left: 0; width: 100%"
    ></canvas>
    <div class="nav-left">
      <!-- Logo与标题区域 -->
      <div class="logo-container">
        <div class="logo-glow"></div>
        <img src="../assets/logo.png" alt="logo" class="nav-logo" />
      </div>
      <!-- 标题 -->
      <div class="title-container">
        <span class="nav-title">悦音书坊</span>
        <span class="nav-subtitle">AudioBook Platform</span>
      </div>
    </div>
    <div v-if="isMobile" class="mobile-menu-toggle" @click="toggleMobileMenu">
      <MenuOutlined />
    </div>
    <div
      class="nav-main"
      :class="{ 'mobile-nav-main': isMobile, 'mobile-menu-visible': mobileMenuVisible }"
    >
      <span class="nav-span" v-for="(item, index) in menuItems" :key="index">
        <!-- 如果有子菜单，显示下拉菜单 -->
        <a-dropdown
          v-if="item.children && item.children.length"
          placement="bottomCenter"
          :trigger="isMobile ? ['click'] : ['hover']"
        >
          <div
            class="nav-link"
            :class="{
              'nav-active': !$route.meta.noActiveMenu && currentPath.startsWith(
                item.children[0].path.split('/').slice(0, 2).join('/'),
              ),
            }"
          >
            <component :is="item.icon" class="nav-icon" style="color: white" />
            {{ item.name }}
            <down-outlined class="dropdown-icon" />
          </div>
          <template #overlay>
            <a-menu class="nav-dropdown-menu">
              <a-menu-item
                v-for="(child, childIndex) in item.children"
                :key="childIndex"
                @click="isMobile && (mobileMenuVisible = false)"
              >
                <router-link :to="child.path" class="dropdown-link" style="color: white">
                  <component :is="child.icon" class="nav-icon" />
                  {{ child.name }}
                </router-link>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <!-- 普通菜单项 -->
        <router-link
          v-else
          class="nav-link"
          :class="{ 'nav-active': !$route.meta.noActiveMenu && currentIndex === index }"
          @click="toggleNav(index); isMobile && (mobileMenuVisible = false)"
          :to="item.path"
        >
          <!-- 使用 Ant Design Vue 图标 -->
          <component :is="item.icon" class="nav-icon" />
          {{ item.name }}
        </router-link>
      </span>
    </div>
    <div class="nav-right" :class="{ 'mobile-nav-right': isMobile }">
      <div v-if="loginUserStore.loginUser.id" class="user-container">
        <a-dropdown :trigger="isMobile ? ['click'] : ['hover']">
          <a-space class="user-info">
            <div class="avatar-container">
              <a-avatar :src="loginUserStore.loginUser.userAvatar" class="user-avatar" />
              <div class="avatar-glow"></div>
            </div>
            <div class="user-name" v-if="!isMobile">
              <span>{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
              <small class="user-role" v-if="loginUserStore.loginUser.userRole === 'admin'">管理员</small>
              <small class="user-role" v-if="loginUserStore.loginUser.userRole === 'user'">普通用户</small>
            </div>
          </a-space>
          <template #overlay>
            <a-menu class="user-dropdown">
              <a-menu-item @click="doLogout" class="logout-item" style="color: white">
                <LogoutOutlined style="color: white" />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <!-- 登录按钮 -->
      <a class="nav-login-button" href="/user/login" v-else>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        登录
      </a>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { menuItems as configMenuItems } from '@/config/menu' // 按需导入 Ant Design Vue 图标
import {
  BookFilled,
  BookOutlined,
  DownOutlined,
  FileOutlined,
  HomeOutlined,
  LogoutOutlined,
  MenuOutlined,
  SettingOutlined,
  SoundOutlined,
  UserOutlined,
  VideoCameraOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '../stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '../api/userController.ts'
import { message } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
const route = useRoute()

// 设备检测
const isMobile = ref(false)
const mobileMenuVisible = ref(false)

const checkDevice = () => {
  isMobile.value = window.innerWidth <= 768
  // 在设备切换为PC时，确保移动菜单关闭
  if (!isMobile.value) {
    mobileMenuVisible.value = false
  }
}

// 切换移动端菜单显示状态
const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

// 当前路径
const currentPath = computed(() => route.path)

// 菜单项配置 - 使用计算属性根据用户角色过滤菜单
const menuItems = computed(() => {
  // 处理所有菜单项的图标
  const processedMenuItems = configMenuItems.map((item) => {
    let icon
    switch (item.icon) {
      case 'icon-ArtboardCopy9':
        icon = HomeOutlined
        break
      case 'icon-ArtboardCopy10':
        icon = BookOutlined
        break
      case 'icon-ArtboardCopy11':
        icon = VideoCameraOutlined
        break
      case 'icon-ArtboardCopy12':
        icon = SoundOutlined
        break
      case 'icon-admin':
        icon = SettingOutlined
        break
      case 'icon-ppt-manage':
        icon = FileOutlined
        break
      case 'icon-book-manage':
        icon = BookFilled
        break
    }

    // 处理子菜单
    if (item.children) {
      return {
        ...item,
        icon,
        children: item.children.map((child) => {
          let childIcon
          switch (child.icon) {
            case 'icon-ppt-manage':
              childIcon = FileOutlined
              break
            case 'icon-book-manage':
              childIcon = BookFilled
              break
          }
          return { ...child, icon: childIcon }
        }),
      }
    }

    return { ...item, icon }
  })

  // 根据用户角色过滤菜单项
  // 只有管理员才能看到管理后台菜单
  if (loginUserStore.loginUser.userRole !== 'admin') {
    return processedMenuItems.filter(item => item.name !== '管理后台')
  }

  return processedMenuItems
})

// 当前活动菜单索引
const currentIndex = ref(0)
const canvasRef = ref(null)

// 导航配置项和状态
const opt = ref({
  currentIndex: 0,
  canvas: null,
  timer: null,
  tabWidthList: [],
  tabHeight: 0,
  height: 0,
  width: 0,
  pattern: null,
  animating: false,
  distance: 0,
  avgSpeed: 0,
  curDisX: 0,
  nextIndex: 0,
  navMainOffset: 0,
})

// 监听用户角色变化，更新菜单
watch(
  () => loginUserStore.loginUser.userRole,
  () => {
    // 当用户角色发生变化时，重新计算tab位置和重绘canvas
    setTimeout(() => {
      calcTabs();
      handleResize();
    }, 50);
  }
)

// 监听路由变化来更新当前选中的菜单
watch(
  () => route.path,
  (newPath) => {
    // 即使当前路由有noActiveMenu标记，也需要重绘光条
    if (route.meta.noActiveMenu) {
      // 触发重绘，但不更改当前菜单索引
      setTimeout(() => {
        calcTabs();
        handleResize();
      }, 50);
      return;
    }

    let index = -1;

    // 先检查直接匹配
    index = menuItems.value.findIndex((item) => item.path === newPath);

    // 如果没有直接匹配，检查子菜单
    if (index === -1) {
      index = menuItems.value.findIndex((item) => {
        if (item.children && item.children.length) {
          return item.children.some((child) => newPath.startsWith(child.path));
        }
        return false;
      });
    }

    // 如果仍然没有匹配，检查前缀匹配
    if (index === -1) {
      index = menuItems.value.findIndex(
        (item) => newPath.startsWith(item.path) && item.path !== '/',
      );
    }

    // 如果找到匹配项，更新当前索引
    if (index !== -1) {
      currentIndex.value = index;
      opt.value.currentIndex = index;

      // 立即重新计算标签位置
      setTimeout(() => {
        calcTabs();
        handleResize();
      }, 50);
    }
  },
  { immediate: true },
)

// 点击菜单项
const toggleNav = (index) => {
  if (
    typeof index !== 'undefined' &&
    index !== opt.value.currentIndex &&
    opt.value.tabWidthList &&
    opt.value.tabWidthList.length &&
    (!opt.value.animating || index !== opt.value.nextIndex)
  ) {
    opt.value.animating = true
    // 计算移动距离为目标位置与当前位置的差值
    const currentStartX = opt.value.tabWidthList[opt.value.currentIndex]
    const nextStartX = opt.value.tabWidthList[index]
    opt.value.distance = nextStartX - currentStartX

    opt.value.avgSpeed = calcAVGSpeed(opt.value.distance)
    opt.value.curDisX = 0
    opt.value.nextIndex = index
    currentIndex.value = index
  }
}

// 计算平均速度
const calcAVGSpeed = (a) => {
  const j = 0.85,
    k = 10,
    l = 4
  let b = (l * j * a + k * (1 - j) * a) / (k * l * 20)
  return (b = Math.max(Math.abs(b), 2.5) * Math.sign(b))
}

// 获取当前速度
const getCurSpeed = (a, b) => {
  const j = 0.85,
    l = 4,
    k = 10
  return Math.abs(a) > Math.abs(j * b) ? l * opt.value.avgSpeed : k * opt.value.avgSpeed
}

// 计算贝塞尔曲线
const calCurve = (a, b, c, d, e, f) => {
  e.bezierCurveTo(a + f, b, c - f, d, c, d)
}

// 初始化画布
const initCanvas = (canvas, width, height) => {
  const devicePixelRatio = window.devicePixelRatio
  const canvasObj = canvas.getContext('2d')
  canvas.width = width * devicePixelRatio
  canvas.height = height * devicePixelRatio
  canvas.style.width = width + 'px'
  canvas.style.height = height + 'px'
  canvasObj.scale(devicePixelRatio, devicePixelRatio)
}

// 计算标签位置
const calcTabs = () => {
  const a = document.querySelectorAll('.nav-main .nav-span')
  const b = []

  // 获取nav-main的位置信息
  const navContainer = document.querySelector('.nav-container')
  const navMain = document.querySelector('.nav-main')
  const navMainOffsetLeft =
    navMain?.getBoundingClientRect().left - navContainer?.getBoundingClientRect().left || 0

  // 计算每个菜单项的位置
  let c = 0
  Array.prototype.forEach.call(a, (item, index) => {
    // 获取每个菜单项相对于nav-main的位置
    const itemLeft = item.getBoundingClientRect().left - navMain?.getBoundingClientRect().left || 0
    b.push(itemLeft)
  })

  // 添加最后一个菜单项的结束位置
  const lastItem = a[a.length - 1]
  if (lastItem) {
    b.push(
      lastItem.getBoundingClientRect().left -
        navMain?.getBoundingClientRect().left +
        lastItem.offsetWidth,
    )
  } else {
    b.push(c)
  }

  opt.value.tabWidthList = b
  opt.value.tabHeight = a[0]?.offsetHeight || 60
  opt.value.height = 60 // 固定高度为导航栏高度
  opt.value.width = navContainer?.offsetWidth || window.innerWidth
  opt.value.navMainOffset = navMainOffsetLeft
}

// 创建图案
const createPattern = (a) => {
  const b = 140,
    c = 63,
    d = 1
  const e = document.createElement('canvas')
  e.width = b
  e.height = c
  e.style.width = b / d + 'px'
  e.style.height = c / d + 'px'

  const f = e.getContext('2d')
  f.scale(d, d)
  f.lineWidth = 0.4

  for (let g = 3, h = 0.8, j = 1; 30 > j; j++) {
    f.strokeStyle = 'RGBA(22, 120, 160, ' + h + ')'
    f.beginPath()
    f.moveTo(0, j * g)
    f.lineTo(b, j * g)
    f.stroke()
    f.closePath()
    if (10 < j) h -= 0.1
  }

  const i = a.getContext('2d').createPattern(e, 'repeat-x')
  opt.value.pattern = i
}

// 绘制高光效果
const drawHighlight = (a) => {
  const b = opt.value.canvas.getContext('2d')
  const d = 0.3

  b.clearRect(0, 0, 2 * opt.value.width, 2 * opt.value.height)
  b.shadowColor = 'rgba(0, 193, 220, 1)'
  b.shadowBlur = 5
  b.strokeStyle = '#00baff' // 使用更亮的蓝色作为描边颜色
  b.lineWidth = 2 // 增加线宽使光条更明显
  b.fillStyle = 'none'

  draw_path(b, false)

  const e = b.createLinearGradient(0, 0, opt.value.width, opt.value.height)
  const f = a - d

  e.addColorStop(Math.min(1, Math.max(0, 0 + f)), 'rgba(0,0,0,0)')
  e.addColorStop(Math.min(1, Math.max(0, 0 + f + 0.1)), '#8ED6FF')
  e.addColorStop(Math.min(1, 0 + f + d), '#8ED6FF')
  e.addColorStop(Math.min(1, 0 + f + d + 0.1), 'rgba(0,0,0,0)')
  e.addColorStop(1, 'rgba(0,0,0,0)')

  b.lineWidth = 2
  b.strokeStyle = e
  b.fillStyle = 'rgba(0, 186, 255, 0.05)' // 半透明填充颜色

  draw_path(b, true)
}

// 绘制路径
const draw_path = (canvasObj, trueorfalse) => {
  const navindex = opt.value.currentIndex
  const f = opt.value.navMainOffset || 0
  const menuHeight = 60 // 菜单高度
  let l = 0

  // 如果是动画过程中计算偏移
  if (opt.value.animating) {
    const m = getCurSpeed(opt.value.curDisX, opt.value.distance)
    l = Math.min(Math.abs(opt.value.distance), Math.abs(opt.value.curDisX + m)) * Math.sign(m)
  }

  canvasObj.beginPath()

  // 检查当前路由是否有noActiveMenu标记
  if (route.meta.noActiveMenu) {
    // 没有高亮时，绘制一条直线
    const lineY = menuHeight - 2 // 流光条的基本高度位置

    // 创建路径，绘制一个薄矩形区域而不是简单直线
    // 这样可以正确应用填充效果
    canvasObj.moveTo(0, lineY - 1); // 左上角
    canvasObj.lineTo(opt.value.width, lineY - 1); // 右上角
    canvasObj.lineTo(opt.value.width, lineY + 1); // 右下角
    canvasObj.lineTo(0, lineY + 1); // 左下角
    canvasObj.closePath(); // 闭合路径

    canvasObj.stroke();

    if (trueorfalse) {
      // 添加微弱的填充效果
      canvasObj.globalAlpha = 0.1;
      canvasObj.fill();
      canvasObj.globalAlpha = 1.0;
    }

    // 更新动画状态
    if (opt.value.animating && trueorfalse) {
      opt.value.animating = false;
    }

    return;
  }

  // 计算当前菜单项的开始和结束位置
  let startX, endX

  if (opt.value.animating) {
    // 动画中 - 处理从一个菜单到另一个菜单的过渡
    const nextStartX = f + opt.value.tabWidthList[opt.value.nextIndex]
    const nextEndX = f + opt.value.tabWidthList[opt.value.nextIndex + 1]
    const currentStartX = f + opt.value.tabWidthList[opt.value.currentIndex]
    const currentEndX = f + opt.value.tabWidthList[opt.value.currentIndex + 1]

    // 根据动画进度计算当前位置
    const progress = Math.abs(l / opt.value.distance)
    startX = currentStartX + (nextStartX - currentStartX) * progress
    endX = currentEndX + (nextEndX - currentEndX) * progress
  } else {
    // 静止状态 - 直接使用当前菜单项的位置
    startX = f + opt.value.tabWidthList[navindex]
    endX = f + opt.value.tabWidthList[navindex + 1]
  }

  // 定义绘制参数
  const lineY = menuHeight - 2 // 流光条的基本高度位置
  const controlPointOffset = 25 // 控制点偏移量，控制曲线弯曲程度
  const arcHeight = 15 // 弧度高度(菜单项顶部位置) - 调整为更小的值，使光条绕过文字

  // 绘制路径 - 从左向右的流动光条，在当前菜单项处向上弯曲

  // 起点 - 导航栏左侧
  canvasObj.moveTo(0, lineY)

  // 左侧直线部分到当前菜单项前
  canvasObj.lineTo(startX - controlPointOffset, lineY)

  // 左侧上弯曲
  canvasObj.bezierCurveTo(
    startX - controlPointOffset / 2,
    lineY, // 控制点1
    startX,
    lineY - controlPointOffset / 2, // 控制点2
    startX,
    arcHeight, // 终点(菜单项上方)
  )

  // 菜单项顶部横线
  canvasObj.lineTo(endX, arcHeight)

  // 右侧下弯曲
  canvasObj.bezierCurveTo(
    endX,
    lineY - controlPointOffset / 2, // 控制点1
    endX + controlPointOffset / 2,
    lineY, // 控制点2
    endX + controlPointOffset,
    lineY, // 终点(回到基础线)
  )

  // 右侧直线部分到导航栏右侧
  canvasObj.lineTo(opt.value.width, lineY)

  canvasObj.stroke()

  if (trueorfalse) {
    // 添加微弱的填充效果，增强发光感
    canvasObj.globalAlpha = 0.1
    canvasObj.fill()
    canvasObj.globalAlpha = 1.0
  }

  // 更新动画状态
  if (opt.value.animating && trueorfalse) {
    opt.value.curDisX = l
    if (Math.abs(l) >= Math.abs(opt.value.distance)) {
      opt.value.animating = false
      opt.value.currentIndex = opt.value.nextIndex
    }
  }
}

// 绘制画布
const drawCanvas = (a) => {
  drawHighlight(a)

  opt.value.timer = requestAnimationFrame(() => {
    drawCanvas((a + 0.005) % 1.6)
  })
}

// 窗口大小调整处理
const handleResize = () => {
  if (opt.value.timer) {
    cancelAnimationFrame(opt.value.timer)
  }
  calcTabs()
  initCanvas(opt.value.canvas, opt.value.width, opt.value.height)
  drawCanvas(0)
}

// 使用防抖处理窗口大小变化
let resizeTimer
const debouncedResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    handleResize()
  }, 200)
}

// 组件挂载时初始化
onMounted(() => {
  opt.value.canvas = canvasRef.value

  // 检查是否是普通用户访问管理页面，如果是则重定向到首页
  if (
    loginUserStore.loginUser.userRole !== 'admin' &&
    route.path.startsWith('/admin')
  ) {
    router.push('/home');
    message.warning('只有管理员才能访问管理后台');
    return;
  }

  // 根据当前路由设置初始索引
  const path = route.path
  let index = menuItems.value.findIndex((item) => {
    if (item.children && item.children.length) {
      return item.children.some((child) => path.startsWith(child.path))
    }
    return item.path === path
  })

  // 如果没有找到匹配项，尝试前缀匹配
  if (index === -1) {
    index = menuItems.value.findIndex((item) => path.startsWith(item.path) && item.path !== '/')
  }

  if (index !== -1) {
    currentIndex.value = index
    opt.value.currentIndex = index
  }

  // 增加等待时间，确保DOM完全渲染
  setTimeout(() => {
    calcTabs()
    initCanvas(opt.value.canvas, opt.value.width, opt.value.height)
    createPattern(opt.value.canvas)

    // 修改canvas样式，确保它覆盖整个导航栏高度
    if (opt.value.canvas) {
      opt.value.canvas.style.height = '60px'
      opt.value.canvas.style.top = '0'
    }

    drawCanvas(0)

    // 添加额外检查处理延迟布局
    setTimeout(() => {
      calcTabs()
      if (opt.value.canvas) {
        initCanvas(opt.value.canvas, opt.value.width, opt.value.height)
      }
    }, 800)
  }, 300)

  // 添加设备检测
  checkDevice()
  window.addEventListener('resize', debouncedResize)
})

// 组件卸载前清理
onBeforeUnmount(() => {
  if (opt.value.timer) {
    cancelAnimationFrame(opt.value.timer)
  }
  clearTimeout(resizeTimer)
  window.removeEventListener('resize', debouncedResize)
})
// 用户注销
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.nav-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: rgba(19, 26, 40, 0.95);
  backdrop-filter: blur(10px);
  box-shadow:
    0 2px 15px rgba(0, 0, 0, 0.4),
    0 0 30px rgba(0, 186, 255, 0.1);
  padding: 0 20px;
  transition: all 0.3s ease;
  border-bottom: 1px solid rgba(0, 186, 255, 0.2);
}

.nav-left {
  display: flex;
  align-items: center;
  margin-right: 30px;
  z-index: 2;
}

.logo-container {
  position: relative;
  margin-right: 15px;
}

.logo-glow {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(0, 186, 255, 0.3) 0%, rgba(0, 186, 255, 0) 70%);
  filter: blur(10px);
  border-radius: 50%;
  z-index: -1;
  animation: pulse 3s infinite alternate;
}

@keyframes pulse {
  0% {
    opacity: 0.5;
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1.1);
  }
}

.nav-logo {
  width: 52px;
  height: 70px;
  position: relative;
  filter: drop-shadow(0 0 5px rgba(0, 186, 255, 0.5));
}

.title-container {
  display: flex;
  flex-direction: column;
}

.nav-title {
  color: #fff;
  font-size: 20px;
  font-weight: 500;
  text-shadow: 0 0 10px rgba(0, 186, 255, 0.5);
  letter-spacing: 1px;
}

.nav-subtitle {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  letter-spacing: 1px;
  margin-top: -2px;
}

.nav-main {
  z-index: 2;
  display: flex;
  position: relative;
  width: auto;
  flex-grow: 1;
  height: 60px;
  justify-content: center;
}

.nav-right {
  margin-left: 30px;
  z-index: 2;
  min-width: 180px;
  display: flex;
  justify-content: flex-end;
}

.user-container {
  padding: 5px 10px;
  background: rgba(0, 186, 255, 0.1);
  border-radius: 30px;
  border: 1px solid rgba(0, 186, 255, 0.3);
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
}

.user-container:hover {
  background: rgba(0, 186, 255, 0.2);
  box-shadow: 0 0 15px rgba(0, 186, 255, 0.3);
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0 5px;
}

.avatar-container {
  position: relative;
  margin-right: 10px;
}

.user-avatar {
  border: 2px solid rgba(0, 186, 255, 0.5);
  box-shadow: 0 0 10px rgba(0, 186, 255, 0.3);
}

.avatar-glow {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  background: rgba(0, 186, 255, 0.2);
  filter: blur(5px);
  z-index: -1;
}

.user-name {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.user-name span {
  color: white;
  font-size: 14px;
}

.user-role {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.user-dropdown {
  background-color: rgba(19, 26, 40, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 186, 255, 0.3);
  border-radius: 5px;
  overflow: hidden;
}

.logout-item {
  color: white !important;
  display: flex;
  align-items: center;
}

.logout-item:hover {
  background-color: rgba(0, 186, 255, 0.2) !important;
}

.nav-main .nav-span .nav-active,
.nav-main .nav-span .nav-link:hover {
  color: #00baff !important;
  text-shadow: 0 0 5px rgba(0, 186, 255, 0.5);
}

.nav-main .nav-span {
  height: 60px;
  display: flex;
  align-items: center;
}

.nav-main .nav-span .nav-link {
  text-decoration: none !important;
  display: flex;
  align-items: center;
  color: #fff;
  width: auto;
  min-width: 120px;
  height: 60px;
  line-height: 60px;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  padding: 0 25px;
  transition: color 0.3s ease;
}

.nav-main .nav-icon {
  padding-right: 10px;
  font-size: 18px;
  vertical-align: middle;
}

.nav-login-button {
  position: relative;
  display: inline-block;
  padding: 8px 18px;
  color: #fff;
  text-decoration: none;
  overflow: hidden;
  transition: 0.5s;
  letter-spacing: 2px;
  cursor: pointer;
  font-size: 14px;
  background: transparent;
  border: none;
  border-radius: 5px;
}

.nav-login-button:hover {
  background: #00baff;
  color: #131a28;
  box-shadow:
    0 0 5px #00baff,
    0 0 25px #00baff,
    0 0 50px #00baff,
    0 0 200px #00baff;
  -webkit-box-reflect: below 1px linear-gradient(transparent, #0005);
  transform: translateY(-2px);
}

.nav-login-button span {
  position: absolute;
  display: block;
}

.nav-login-button span:nth-child(1) {
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00baff);
  animation: animate1 1s linear infinite;
}

@keyframes animate1 {
  0% {
    left: -100%;
  }
  50%,
  100% {
    left: 100%;
  }
}

.nav-login-button span:nth-child(2) {
  top: -100%;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, transparent, #00baff);
  animation: animate2 1s linear infinite;
  animation-delay: 0.25s;
}

@keyframes animate2 {
  0% {
    top: -100%;
  }
  50%,
  100% {
    top: 100%;
  }
}

.nav-login-button span:nth-child(3) {
  bottom: 0;
  right: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(270deg, transparent, #00baff);
  animation: animate3 1s linear infinite;
  animation-delay: 0.5s;
}

@keyframes animate3 {
  0% {
    right: -100%;
  }
  50%,
  100% {
    right: 100%;
  }
}

.nav-login-button span:nth-child(4) {
  bottom: -100%;
  left: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(360deg, transparent, #00baff);
  animation: animate4 1s linear infinite;
  animation-delay: 0.75s;
}

@keyframes animate4 {
  0% {
    bottom: -100%;
  }
  50%,
  100% {
    bottom: 100%;
  }
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .nav-container {
    padding: 0 10px;
  }

  .nav-main .nav-span .nav-link {
    min-width: auto;
    padding: 0 15px;
  }

  .nav-title {
    font-size: 16px;
  }

  .nav-subtitle {
    display: none;
  }

  .user-role {
    display: none;
  }
}

.nav-dropdown-menu {
  background-color: rgba(19, 26, 40, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 186, 255, 0.3);
  border-radius: 5px;
  overflow: hidden;
  min-width: 140px;
  box-shadow:
    0 5px 20px rgba(0, 0, 0, 0.5),
    0 0 15px rgba(0, 186, 255, 0.2);
}

.nav-dropdown-menu .ant-dropdown-menu-item {
  padding: 8px 16px;
  color: white;
}

.nav-dropdown-menu .ant-dropdown-menu-item:hover {
  background-color: rgba(0, 186, 255, 0.2);
}

.dropdown-link {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
}

.dropdown-link:hover {
  color: #00baff;
}

.dropdown-icon {
  margin-left: 5px;
  font-size: 12px;
}

.nav-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
  width: auto;
  min-width: 120px;
  height: 60px;
  line-height: 60px;
  font-size: 14px;
  text-align: left;
  padding: 0 25px;
  transition: color 0.3s ease;
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: #00baff;
  transition: all 0.3s ease;
  transform: translateX(-50%);
  opacity: 0;
}

.nav-link:hover::after,
.nav-active::after {
  width: 70%;
  opacity: 1;
  box-shadow: 0 0 10px rgba(0, 186, 255, 0.7);
}

.nav-link:hover {
  color: #00baff !important;
  text-shadow: 0 0 5px rgba(0, 186, 255, 0.5);
}

/* 增强移动端响应式样式 */
.mobile-menu-toggle {
  display: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  z-index: 1010;
  padding: 10px;
}

/* 移动端样式增强 */
@media (max-width: 768px) {
  .nav-container {
    padding: 0 10px;
  }

  .mobile-menu-toggle {
    display: block;
    margin-left: auto;
  }

  .nav-main {
    position: fixed;
    top: 60px;
    left: 0;
    width: 100%;
    height: 0;
    flex-direction: column;
    background-color: rgba(19, 26, 40, 0.95);
    backdrop-filter: blur(10px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
    overflow: hidden;
    transition: height 0.3s ease;
    z-index: 1000;
    padding: 0;
  }

  .mobile-nav-main {
    height: 0;
    opacity: 0;
    visibility: hidden;
  }

  .mobile-menu-visible {
    height: calc(100vh - 60px);
    opacity: 1;
    visibility: visible;
    padding: 10px 0;
    overflow-y: auto;
  }

  .nav-main .nav-span {
    width: 100%;
    height: auto;
    margin: 5px 0;
  }

  .nav-main .nav-span .nav-link {
    width: 100%;
    height: 50px;
    line-height: 50px;
    padding: 0 20px;
    justify-content: flex-start;
  }

  .nav-title {
    font-size: 18px;
  }

  .nav-subtitle {
    display: none;
  }

  .mobile-nav-right {
    min-width: auto;
  }

  .nav-right .user-container {
    padding: 5px;
  }

  .user-name {
    display: none;
  }

  .nav-login-button {
    padding: 5px 12px;
    font-size: 12px;
  }
}

/* 小型手机设备 */
@media (max-width: 480px) {
  .nav-logo {
    width: 42px;
    height: 56px;
  }

  .nav-title {
    font-size: 16px;
  }

  .nav-main .nav-span .nav-link {
    font-size: 14px;
    min-width: unset;
    padding: 0 15px;
  }
}
</style>
