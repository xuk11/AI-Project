<template>
  <div class="app" :class="{ 'with-sidebar': !route.path.startsWith('/user') && !route.path.startsWith('/read') }">
    <template v-if="route.path.startsWith('/user') || route.path.startsWith('/read')">
      <router-view />
    </template>
    <template v-else>
      <SideMenu />
      <div class="main-content">
        <router-view />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import SideMenu from '@/components/SideMenu.vue'

const route = useRoute()
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow-y: auto; /* 允许垂直滚动 */
}

.app {
  width: 100%;
  min-height: 100vh; /* 使用最小高度而不是固定高度 */
  background-color: #1e2637; /* 与侧边栏背景色匹配 */
  box-sizing: border-box; /* 确保padding不增加总高度 */
  position: relative; /* 建立新的定位上下文 */
  overflow-y: auto; /* 允许内容滚动 */
}

.app.with-sidebar .main-content {
  margin-left: 240px; /* 与侧边栏宽度匹配 */
  padding: 20px;
  min-height: 100vh;
  transition: margin-left 0.3s ease;
}

/* 当侧边栏折叠时的样式 */
@media (max-width: 768px) {
  .app.with-sidebar .main-content {
    margin-left: 80px;
  }
}

/* 当检测到侧边栏折叠的类时调整内容区域 */
.sidebar-collapsed .app.with-sidebar .main-content {
  margin-left: 80px;
}
</style>
