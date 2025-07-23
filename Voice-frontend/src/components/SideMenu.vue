<template>
  <div class="side-menu-container" :class="{ collapsed: isCollapsed }">
    <!-- 背景动效 Canvas -->
    <canvas id="canvas-sidebar" ref="canvasRef" class="sidebar-canvas"></canvas>

    <!-- Logo 区域 -->
    <div class="logo-section">
      <div class="logo-container">
        <div class="logo-glow"></div>
        <img src="../assets/logo.png" alt="logo" class="side-logo" />
      </div>
      <div class="title-container" v-show="!isCollapsed">
        <span class="side-title">知音阁</span>
      </div>
      <div class="collapse-toggle" @click="toggleCollapse">
        <MenuFoldOutlined v-if="!isCollapsed" />
        <MenuUnfoldOutlined v-else />
      </div>
    </div>

    <!-- 主菜单区域 -->
    <div class="menu-section">
      <a-menu
        mode="inline"
        :inline-collapsed="isCollapsed"
        :selected-keys="[currentPath]"
        class="side-menu"
      >
        <template v-for="(item, index) in menuItems" :key="index">
          <!-- 有子菜单的项目 -->
          <a-sub-menu
            v-if="item.children && item.children.length"
            :key="item.children[0].path.split('/').slice(0, 2).join('/')"
          >
            <template #icon>
              <component :is="item.icon" class="menu-icon" />
            </template>
            <template #title>{{ item.name }}</template>

            <a-menu-item
              v-for="(child, childIndex) in item.children"
              :key="child.path"
              @click="() => router.push(child.path)"
            >
              <template #icon>
                <component :is="child.icon" class="menu-icon" />
              </template>
              {{ child.name }}
            </a-menu-item>
          </a-sub-menu>

          <!-- 无子菜单的项目 -->
          <a-menu-item v-else :key="item.path" @click="() => router.push(item.path)">
            <template #icon>
              <component :is="item.icon" class="menu-icon" />
            </template>
            {{ item.name }}
          </a-menu-item>
        </template>
      </a-menu>
    </div>

    <!-- 用户信息区域 -->
    <div class="user-section" v-if="loginUserStore.loginUser.id">
      <a-dropdown placement="topRight">
        <div class="user-info">
          <div class="avatar-container">
            <a-avatar :src="loginUserStore.loginUser.userAvatar" class="user-avatar" />
            <div class="avatar-glow"></div>
          </div>
          <div class="user-name" v-show="!isCollapsed">
            <span>{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
            <small class="user-role" v-if="loginUserStore.loginUser.userRole === 'admin'"
              >管理员</small
            >
            <small class="user-role" v-if="loginUserStore.loginUser.userRole === 'user'"
              >普通用户</small
            >
          </div>
        </div>
        <template #overlay>
          <a-menu class="user-dropdown">
            <a-menu-item @click="doLogout" class="logout-item">
              <LogoutOutlined />
              退出登录
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

    <!-- 登录按钮 -->
    <div class="login-section" v-else>
      <a class="side-login-button" href="/user/login">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <LoginOutlined v-if="isCollapsed" />
        <span v-else>登录</span>
      </a>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { menuItems as configMenuItems } from '@/config/menu'
import {
  BookFilled,
  BookOutlined,
  FileOutlined,
  HomeOutlined,
  LoginOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SettingOutlined,
  SoundOutlined,
  UserOutlined,
  VideoCameraFilled,
  VideoCameraOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '../stores/useLoginUserStore'
import { userLogoutUsingPost } from '../api/userController'
import { message } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
const route = useRoute()
const canvasRef = ref(null)
const isCollapsed = ref(false)

// 当前路径
const currentPath = computed(() => route.path)

// 切换折叠状态
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
  localStorage.setItem('sidebarCollapsed', isCollapsed.value.toString())

  // 添加或移除全局类名，用于主体内容区域响应
  if (isCollapsed.value) {
    document.body.classList.add('sidebar-collapsed')
  } else {
    document.body.classList.remove('sidebar-collapsed')
  }

  // 等待DOM更新后重新绘制canvas
  setTimeout(() => {
    initCanvas()
  }, 300)
}

// 菜单项配置 - 使用计算属性根据用户角色过滤菜单
const menuItems = computed(() => {
  // 处理所有菜单项的图标
  const processedMenuItems = configMenuItems.map((item) => {
    let icon
    switch (item.icon) {
      case 'icon-Home':
        icon = HomeOutlined
        break
      case 'icon-ArticleLibrary':
        icon = BookOutlined
        break
      case 'icon-SoundsCommunity':
        icon = SoundOutlined
        break
      case 'icon-AIWorkshop':
        icon = VideoCameraOutlined
        break
      case 'icon-admin':
        icon = SettingOutlined
        break
      case 'icon-PersonCenter':
        icon = UserOutlined
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
            case 'icon-PPTVideo':
              childIcon = FileOutlined
              break
            case 'icon-BookAdd':
              childIcon = BookFilled
              break
            case 'icon-VideoReset':
              childIcon = VideoCameraFilled
              break
            case 'icon-PPTManage':
              icon = FileOutlined
              break
            case 'icon-BookManage':
              icon = BookFilled
              break
          }
          return { ...child, icon: childIcon }
        }),
      }
    }

    return { ...item, icon }
  })

  // 根据用户角色过滤菜单项
  // 只有管理员才能看到管理枢纽菜单
  if (loginUserStore.loginUser.userRole !== 'admin') {
    return processedMenuItems.filter((item) => item.name !== '管理枢纽')
  }

  return processedMenuItems
})

// 退出登录
const doLogout = async () => {
  try {
    await userLogoutUsingPost()
    loginUserStore.setLoginUser({})
    message.success('退出登录成功')
    router.push('/user/login')
  } catch (error) {
    message.error('退出登录失败')
  }
}

// Canvas动效相关
let ctx
let particles = []
const particleCount = 40
const colors = ['#3498db', '#00bcd4', '#4CAF50', '#8e44ad']

class Particle {
  constructor(x, y, size, color, speed) {
    this.x = x
    this.y = y
    this.size = size
    this.color = color
    this.speed = speed
    this.direction = Math.random() * Math.PI * 2
    this.velocity = {
      x: Math.cos(this.direction) * this.speed,
      y: Math.sin(this.direction) * this.speed,
    }
    this.alpha = 0.8
  }

  update(width, height) {
    this.x += this.velocity.x
    this.y += this.velocity.y

    if (this.x < 0 || this.x > width) {
      this.velocity.x = -this.velocity.x
    }

    if (this.y < 0 || this.y > height) {
      this.velocity.y = -this.velocity.y
    }
  }

  draw() {
    ctx.globalAlpha = this.alpha
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fillStyle = this.color
    ctx.fill()
    ctx.closePath()
    ctx.globalAlpha = 1
  }
}

// 初始化Canvas
const initCanvas = () => {
  if (!canvasRef.value) return

  const canvas = canvasRef.value
  ctx = canvas.getContext('2d')

  // 设置Canvas大小
  canvas.width = canvas.offsetWidth
  canvas.height = canvas.offsetHeight

  // 创建粒子
  particles = []
  for (let i = 0; i < particleCount; i++) {
    const size = Math.random() * 2 + 1
    const color = colors[Math.floor(Math.random() * colors.length)]
    const x = Math.random() * canvas.width
    const y = Math.random() * canvas.height
    const speed = Math.random() * 0.5 + 0.2
    particles.push(new Particle(x, y, size, color, speed))
  }

  // 开始动画
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  animate()
}

// 动画循环
let animationFrameId
const animate = () => {
  animationFrameId = requestAnimationFrame(animate)

  if (!canvasRef.value) return

  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)

  // 绘制连接线
  ctx.lineWidth = 0.3
  ctx.strokeStyle = 'rgba(102, 204, 255, 0.2)'

  for (let i = 0; i < particles.length; i++) {
    particles[i].update(canvasRef.value.width, canvasRef.value.height)
    particles[i].draw()

    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < 80) {
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.stroke()
      }
    }
  }
}

// 组件挂载
onMounted(() => {
  // 从本地存储中读取折叠状态
  const savedCollapsed = localStorage.getItem('sidebarCollapsed')
  if (savedCollapsed !== null) {
    isCollapsed.value = savedCollapsed === 'true'
    // 根据初始折叠状态设置全局类名
    if (isCollapsed.value) {
      document.body.classList.add('sidebar-collapsed')
    } else {
      document.body.classList.remove('sidebar-collapsed')
    }
  }

  // 初始化canvas
  initCanvas()

  // 监听窗口大小变化
  window.addEventListener('resize', initCanvas)
})

// 组件卸载
onBeforeUnmount(() => {
  window.removeEventListener('resize', initCanvas)
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  // 确保在组件卸载时移除类名
  document.body.classList.remove('sidebar-collapsed')
})
</script>

<style scoped>
.side-menu-container {
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
  width: 240px;
  background-color: #1a1f2e;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  transition: width 0.3s ease;
  overflow: hidden;
}

.side-menu-container.collapsed {
  width: 80px;
}

.sidebar-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.logo-section {
  padding: 16px;
  display: flex;
  align-items: center;
  height: 70px;
  position: relative;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(90deg, rgba(0, 170, 255, 0.1), transparent);
}

.logo-container {
  position: relative;
  width: 48px;
  height: 48px;
  margin-right: 14px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: visible;
  display: flex;
  justify-content: center;
  align-items: center;
}

.side-logo {
  width: 90%;
  height: 90%;
  object-fit: contain;
  position: relative;
  z-index: 2;
  filter: drop-shadow(0 0 5px rgba(0, 170, 255, 0.9));
}

.logo-glow {
  position: absolute;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(0, 170, 255, 0.7) 0%, rgba(0, 170, 255, 0) 70%);
  border-radius: 12px;
  animation: pulse 2s infinite;
  z-index: 1;
  box-shadow: 0 0 20px rgba(0, 170, 255, 0.8);
  left: -10%;
  top: -10%;
}

.title-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex-grow: 1;
}

.side-title {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
  line-height: 1.2;
  text-shadow: 0 0 6px rgba(0, 170, 255, 0.8);
  letter-spacing: 1px;
}

.side-subtitle {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1;
}

.collapse-toggle {
  margin-left: auto;
  width: 24px;
  height: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  transition: background 0.3s;
}

.collapse-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
}

.menu-section {
  flex-grow: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-top: 10px;
}

/* 自定义菜单样式 */
:deep(.side-menu) {
  background: transparent;
  border-right: none;
}

:deep(.side-menu .ant-menu-item),
:deep(.side-menu .ant-menu-submenu-title) {
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  height: 50px;
  line-height: 50px;
  border-radius: 0;
}

:deep(.side-menu .ant-menu-item:hover),
:deep(.side-menu .ant-menu-submenu-title:hover) {
  color: #fff;
  background: linear-gradient(90deg, rgba(0, 170, 255, 0.2), transparent);
}

:deep(.side-menu .ant-menu-item-selected) {
  color: #fff;
  background: linear-gradient(90deg, rgba(0, 170, 255, 0.4), transparent);
}

:deep(.side-menu .ant-menu-item-selected::after) {
  border-right: 3px solid #00aaff;
  box-shadow: 0 0 8px #00aaff;
}

:deep(.side-menu .menu-icon) {
  margin-right: 10px;
  font-size: 18px;
  vertical-align: middle;
}

.user-section,
.login-section {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
}

.avatar-container {
  position: relative;
  margin-right: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border: 2px solid rgba(0, 170, 255, 0.7);
  position: relative;
  z-index: 2;
}

.avatar-glow {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background: radial-gradient(circle, rgba(0, 170, 255, 0.4) 0%, rgba(0, 170, 255, 0) 70%);
  border-radius: 50%;
  animation: pulse 2s infinite;
  z-index: 1;
}

.user-name {
  display: flex;
  flex-direction: column;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.7);
}

:deep(.user-dropdown) {
  background-color: #1a1f2e;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

:deep(.logout-item) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.logout-item:hover) {
  color: #fff;
  background: rgba(0, 170, 255, 0.2);
}

.side-login-button {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 170, 255, 0.1);
  color: #fff;
  padding: 10px;
  text-align: center;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s;
  text-decoration: none;
}

.side-login-button span {
  position: absolute;
  display: block;
}

.side-login-button span:nth-child(1) {
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00aaff);
  animation: btn-anim1 2s linear infinite;
}

.side-login-button span:nth-child(2) {
  top: -100%;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, transparent, #00aaff);
  animation: btn-anim2 2s linear infinite;
  animation-delay: 0.5s;
}

.side-login-button span:nth-child(3) {
  bottom: 0;
  right: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(270deg, transparent, #00aaff);
  animation: btn-anim3 2s linear infinite;
  animation-delay: 1s;
}

.side-login-button span:nth-child(4) {
  bottom: -100%;
  left: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(360deg, transparent, #00aaff);
  animation: btn-anim4 2s linear infinite;
  animation-delay: 1.5s;
}

.side-login-button:hover {
  background: rgba(0, 170, 255, 0.2);
  color: #fff;
  box-shadow:
    0 0 5px #00aaff,
    0 0 25px #00aaff;
}

@keyframes btn-anim1 {
  0% {
    left: -100%;
  }
  50%,
  100% {
    left: 100%;
  }
}

@keyframes btn-anim2 {
  0% {
    top: -100%;
  }
  50%,
  100% {
    top: 100%;
  }
}

@keyframes btn-anim3 {
  0% {
    right: -100%;
  }
  50%,
  100% {
    right: 100%;
  }
}

@keyframes btn-anim4 {
  0% {
    bottom: -100%;
  }
  50%,
  100% {
    bottom: 100%;
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.3;
  }
  100% {
    transform: scale(0.95);
    opacity: 0.7;
  }
}
</style>
