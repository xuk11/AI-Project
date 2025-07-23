<template>
  <a-flex class="tag-container">
    <div v-for="tag in filteredTags" :key="tag">
      <a-tag :color="tagColorMap[tag]" v-if="tagColorMap[tag]">
        {{ tag }}
      </a-tag>
    </div>
  </a-flex>
</template>

<script setup lang="ts">
import { computed, defineProps } from 'vue';

// 定义标签和颜色的映射关系
const tagColorMap = {
  '中文': 'blue',
  '女声': 'green',
  '舒缓': 'orange',
  '康辉': 'red',
  '欧丽娟': 'cyan',
  '男声': 'magenta',
  '英文': 'lime',
  '粤语': 'purple'
};

// 接收父组件传递的 audio 数据
const props = defineProps<{
  audio: {
    tags: string[];
  };
}>();

// 计算属性：过滤并返回有效的标签
const filteredTags = computed(() => {
  return (props.audio?.tags || []).filter(tag => tagColorMap[tag]);
});
</script>
