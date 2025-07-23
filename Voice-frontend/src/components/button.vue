<template>
  <div>
    <button class="sparkles" :style="{ width: width, height: height }">
      <span>{{ buttonText }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 定义一个响应式变量来存储颜色值
const colorValue = ref(260)
// 定义 props

// 定义 props
const props = defineProps({
  buttonText: {
    type: String,
    required: true
  },
  width: {
    type: String,
    default: 'auto'
  },
  height: {
    type: String,
    default: 'auto'
  }
})
// 监听颜色值的变化并更新按钮的颜色
const btn = ref(null)
const onColorChange = () => {
  if (btn.value) {
    btn.value.style.setProperty('--clr', colorValue.value)
  }
}

// 初始设置颜色
const initColor = () => {
  if (btn.value) {
    btn.value.style.setProperty('--clr', colorValue.value)
  }
}

// 在组件挂载时初始化颜色
const onMounted = () => {
  initColor()
}

// 监听颜色值的变化
const watch = (source, callback) => {
  source(() => {
    callback()
  })
}

watch(() => colorValue.value, onColorChange)
</script>

<style scoped>
:root {
  --canvas: 220;
  --bg: hsl(var(--canvas), 15%, 22%);
  --fg: hsl(var(--canvas), 39%, 95%);
  --link: hsl(var(--canvas), 90%, 80%);
  --linkh: hsl(150, 95%, 70%);
  --wgt: 200;
}

body,
html {
  font-family: heebo, sans-serif;
  color: var(--fg);
  background: var(--bg);
  font-weight: var(--wgt);
  padding: 0;
  display: grid;
  place-items: center;
  height: 100%;
}

a {
  color: var(--link);
  text-decoration: none;
  font-weight: 450;
  transition: all 0.3s ease;
}

a:hover,
a:focus,
a:active {
  color: var(--linkh);
}

button {
  background: hsl(var(--canvas), 10%, 10%);
  color: inherit;
  border: none;
  border-radius: 0.5em;
  padding: 0.25em 0.5em;
  font-family: inherit;
  font-size: inherit;
}

.social-icon {
  stroke-width: 1.25;
  stroke: currentColor;
  fill: transparent;
  background: transparent;
  stroke-linecap: round;
  stroke-linejoin: round;
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  z-index: 10;
  animation: iconsLoad 10s ease both 5s;
}

.social-icon path {
  fill: none;
}

.social-icon.twitter {
  right: 40px;
  animation-delay: 4s;
}

.social-icon.codepen {
  position: absolute;
  bottom: 10px;
  right: 80px;
  width: max-content;
  animation-delay: 3s;
}

@keyframes iconsLoad {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0px);
  }
}

.sparkles {
  --clr: 260;
  --shadows: 0%;
  --shadowl: 0%;

  font-size: 2em;
  font-weight: 700;
  letter-spacing: 0.5px;
  border-radius: 5em;
  background: linear-gradient(
    0deg,
    hsla(var(--clr), 100%, 70%) 0%,
    hsla(var(--clr), 100%, 65%) 5%,
    hsla(var(--clr), 80%, 35%) 15%,
    hsla(var(--clr), 10%, 0%) 40%,
    hsla(var(--clr), 25%, 12%) 90%
  );
  background-size: 200% 300%;
  background-position: 0% 0%;
  box-shadow: inset 0 0 2px hsla(var(--clr), 30%, 20%);
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: 1fr;
  place-items: center;
  padding: 0;
  position: relative;
  overflow: hidden;
  transform: translate(0px);

  transition: all 0.5s cubic-bezier(0.77, 0, 0.18, 1);

  box-shadow: 0 -0.5em 0.5em transparent,
  0 0.5em 0.5em transparent,
  0 0.5em 0.5em transparent,
  0 0.5em 0.5em transparent,
  0 0.25em 0.3em -0.2em hsla(var(--clr), 0%, 0%, 0.5),
  0 0.35em 0.75em hsla(var(--clr), 0%, 0%, 0.75);
}

.sparkles::before,
.sparkles::after {
  --gradientPos: 50% 100%;
  content: ' ';
  grid-column: 1;
  grid-row: 1;
  width: 100%;
  height: 100%;
  transition: inherit;
}

.sparkles:before {
  inset: 0;
  position: absolute;
  transform: translate3d(0, 0, 0.01px);
  border-radius: inherit;

  background-image: var(--glitter), var(--glitter), linear-gradient(180deg, black 0%, white 80%);
  background-size: 300px 170px,
  280px 130px,
  200% 200%;
  background-blend-mode: multiply, multiply, overlay;
  background-position: 0px 0px,
  0px 0px,
  var(--gradientPos);
  background-repeat: repeat;

  mix-blend-mode: color-dodge;
  filter: brightness(2) contrast(0.75);
  animation: bubble 20s linear infinite;
  animation-play-state: paused;
  opacity: 0.5;
  box-shadow: inset 0 -8px 10px -7px hsla(var(--clr), 70%, 80%, 0.75);
}

.sparkles:after {
  background-image: radial-gradient(
    ellipse at center 70%,
    hsla(var(--clr), 100%, 99%, 0.8) 5%,
    hsla(var(--clr), 90%, 80%, 1) 20%,
    transparent 50%,
    transparent 200%
  ),
  linear-gradient(
    90deg,
    hsla(var(--clr), 80%, 10%, 1) -10%,
    transparent 25%,
    transparent 75%,
    hsla(var(--clr), 80%, 10%, 1) 110%
  );
  box-shadow: inset 0 0.25em 0.75em rgba(0, 0, 0, 1),
  inset 0 -0.05em 0.2em rgba(255, 255, 255, 0.4),
  inset 0 -1px 3px hsla(var(--clr), 80%, 50%, 0.75);
  background-blend-mode: darken;
  background-repeat: no-repeat;
  background-size: 180% 80%,
  cover;
  background-position: center 220%;
  mix-blend-mode: hard-light;
  filter: blur(5px);
  opacity: 0;
}

.sparkles:hover {
  --shadows: 90%;
  --shadowl: 80%;
  background-position: 100% 100%;

  transition: all 0.2s cubic-bezier(0.17, 0.84, 0.44, 1);

  box-shadow: 0 -0.2em 1.5em hsla(var(--clr), 90%, 50%, 0.3),
  0 0.5em 2em hsla(var(--clr), 90%, 70%, 0.55),
  0 0.25em 0.3em -0.2em hsla(var(--clr), 0%, 0%, 1),
  0 0.35em 0.75em hsla(var(--clr), 0%, 0%, 1),
  0 0.25em 0.5em -0.3em hsl(var(--clr), 30%, 99%, 1),
  0 0.25em 0.5em hsla(var(--clr), 20%, 30%, 0.35),
  inset 0 -2px 5px -2px rgba(255, 255, 255, 0.5);
}

.sparkles:hover:before {
  --gradientPos: 50% 50%;
  animation-play-state: running;
  filter: brightness(2) contrast(1);
  box-shadow: inset 0 -5px 10px -4px hsla(var(--clr), 70%, 80%, 0.3);
  opacity: 0.8;
}

.sparkles:hover:after {
  opacity: 0.8;
  transform: translateY(0px);
}

.sparkles span {
  grid-column: 1;
  grid-row: 1;
  background-image: linear-gradient(
    rgb(219, 224, 232) 0%,
    rgb(214, 222, 226) 19%,
    rgb(179, 191, 203) 30%,
    rgb(201, 209, 216) 43%,
    hsl(var(--clr), 70%, 70%, 1) 50%,
    hsl(var(--clr), 50%, 85%, 1) 52%,
    rgb(255, 255, 255) 100%
  );
  background-size: 1em 3.45em;
  color: rgb(214, 222, 226);
  -webkit-text-fill-color: transparent;
  -webkit-background-clip: text;
  filter: drop-shadow(0 0 0.05em black) drop-shadow(0 0.025em 0.05em black);
  transition-timing-function: inherit;
  transition-duration: inherit;
  transition-delay: 0s;
  padding: 0.75em 1.5em;
  transform: translateY(0);
}

.sparkles:active {
  transform: translateY(0.075em);
  box-shadow: 0 -0.2em 1.5em hsla(var(--clr), 90%, 50%, 0.4),
  0 0.5em 2em hsla(var(--clr), 90%, 70%, 0.65),
  0 0.15em 0.3em -0.2em hsla(var(--clr), 0%, 0%, 1),
  0 0.25em 0.75em hsla(var(--clr), 0%, 0%, 1),
  0 0.25em 0.5em -0.3em hsl(var(--clr), 30%, 99%, 1),
  0 0.25em 0.5em hsla(var(--clr), 20%, 30%, 0.45),
  inset 0 -2px 5px -2px rgba(255, 255, 255, 0.65);
  transition-duration: 0.1s;
}

.sparkles:active:before,
.sparkles:active:after {
  opacity: 1;
  filter: brightness(3) contrast(0.75);
  animation-duration: 8s;
}

.sparkles:active:after {
  filter: brightness(1.35) contrast(0.8) blur(5px);
}

:root {
  --glitter: url('https://assets.codepen.io/13471/silver-glitter-background.png');
}

@keyframes bubble {
  0% {
    background-position: 0px 340px,
    0px 130px,
    var(--gradientPos);
  }
  100% {
    background-position: 0px 0px,
    0px 0px,
    var(--gradientPos);
  }
}

input {
  position: absolute;
  bottom: 30px;
  width: 300px;
}
</style>
