<template>
  <div class="container">
    <!-- 小彩灯Canvas -->
    <canvas class="lights-canvas"></canvas>
    
    <!-- 原有的背景Canvas彩带效果 -->
    <canvas id="canvas"></canvas>
    
    <!-- 主要内容 -->
    <div class="content">
      <div class="title-container">
        <h1 class="main-title">知音阁
          <div class="aurora">
            <div class="aurora__item"></div>
            <div class="aurora__item"></div>
            <div class="aurora__item"></div>
            <div class="aurora__item"></div>
          </div>
        </h1>
        <p class="subtitle">声临其境，文化共鸣</p>
      </div>
      
      <div class="buttons-container">
        <div class="btn explore-sounds" @click="gotoSounds">探索声音</div>
        <div class="btn enter-library" @click="gotoBooks">进入书库</div>
    </div>
    </div>
    
    <!-- 小彩灯图片资源 -->
    <img class="asset-img" id="light-img" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAMAAADzN3VRAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAACnVBMVEUAAAD40ED40D740Dv40En40Dr40FH40Df40D/40D340Dn40Dz40Dj40AD40Ff40Db40DT40EH40EL40ED40ED40Dv40D/4zz/4zz74zz/4zz/40D74zz/4zz/4zz/40UH40kD40UL40kH4zz/4zz340D34zz740kH41UT410f410j410j410f40D/40D/4zz/40ED40EH400T410f42Er4103410/411D41UT40kH40D/4z0D4zz/41UX410j42E7411L42Ff42Vv42Vz42En41kb40UH4zz/40Dz40ED41UT42Ej4107411b42l743WT432r4323432r410/42En4zz340Dn400H410f42mD432v44nf45IP45Yn45IT44nj432r411b400L40D/4zz/41UT42Er411P432v44oD46Zv47q/48Lj46Zr432n40UH410f42Ff44nf48r/499T4997499T48r744nf43WP40D741EH42Vv4+O34+PP4+O347q745IL432j40UD432345Yj48LX49934+PP4+Pj4993477T45Ib432z411D400D40D/40UH432n45IP47a7432j42Vv410/400H40Dr4zz340UD410b410346Zj48r343WP42Ff410f40D/41kT42Er42l744nv46Zf42l3411L410r41kX40ED410f42E7411b421/45IL45Ib432n421740UH40D340Dz4zz741EP410j410743WT432f432v43mj43GT42Ej41kT4zz740ED41EX411P42Ff42Fv411f410j41kX400P41034107411D42E340D740UH410j41kb41ET400H40D/40ED4z0D4zz74zz340D////+WdN8lAAAA3nRSTlMAAAAAAAAAAAAAAAAAAAAAAAAAAgMCBAcJDAkDCA0SFRsbGxMDBQscJSwwMS0SDAUFFyExQExUVyIYDQULJzlOZHeDhDonFwoDASE5VXGNpbO4tFM5BwEcMJa3zdjb2Mu2cBwDESM+Y7bU4ebo4bUUK3bL6vDy7+rKogIZgPb49ebXsBu02+jx9/jw6Nu0VRsBGbDX5q9/UhkCCRUqS+HqoHMqESI9i9Thi2E9IhEvTG+V1tq0kxsDAQchN1Ggr7SvoDghChUjYXJ/cjgnIUlSVEkFGi8qIhsFFAcICAaJi/ApAAAAAWJLR0Te6W7imwAAAAd0SU1FB+YMHBEUINvSacMAAAIKSURBVCjPY2AgBBgZmZiFRYQZgQBVnEVUTFxCUlJSSlxMlAVJjplVWkZWTl5BUUleWVZGhZkZYZKqmpy6hqaWtpaOhrqunj7MREZGA1lDI2MTUzNzM1MTYwtLK2uIFCOjiI2hrZ29g6OTs5Ojg72Lq5u7Bxs7SMZTXNfL28fXzz8gMMjfzzc4xEI3NIwVrEUyPMLeNzIqOiY2Lj4hMtE+Ikk2mYkRKCOekpqW7peRmZWdk52bmeeXnqYhJ84BkpHKLzAt9C/KKi4pLSvPragsNNXMl/JkZGCqUqvWMqsJiMkuqa2rL2tobKox06qWTOZg4EiWbNY2d25pbWvv6Ozo6u7pde7T6pf04GTgmGAzEahn0uSGsvqO+rIpjVOnTdeaoTaTmYGLfdbsOXMLKyvmzS/rKps/r2jBwrmL5KWAfmViEl+8ZGn6srzlK6Z0T1mxPG/lqtVr1oLdxp0sqbRu/YaNeUWbNm8q2rJ1w/p12yS38wDDh3PHTrldu/dsWLZ33/4DB5dt2HPosO7OMFBwM/KKHjl6bPf64yemOZ88dXz96TPyR6T5+EFByi6ganP07LrV585fmH5x6bqzs21UmZhg0XDJXe7ykjnrtK5cvXZdzv0GMycs6jiZpXdKLs532+Z2M0VSXAQ5KTAKeorcuu1uc+TO3e07hNDSCCMjNyczKz8nN8FkxgAAgH2hXtQzzA4AAAAldEVYdGRhdGU6Y3JlYXRlADIwMjItMTItMjhUMTc6MjA6MzIrMDA6MDA1MNVoAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIyLTEyLTI4VDE3OjIwOjMyKzAwOjAwRG1t1AAAAABJRU5ErkJggg==" />
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import router from '@/router'

// 检测设备类型
const isMobile = ref(false)

const checkDevice = () => {
  isMobile.value = window.innerWidth <= 768
}

// 导航函数
const gotoBooks = () => {
  const url = router.resolve({
    path: '/article/library',
  }).href
  window.open(url, '_blank')
}

const gotoSounds = () => {
  const url = router.resolve({
    path: '/sound/community',
  }).href
  window.open(url, '_blank')
}

// 小彩灯相关类定义
class Mouse {
  pos: any
  radius: number
  constructor(canvas: HTMLCanvasElement) {
    this.pos = new Vector(-1000, -1000)
    this.radius = 40

    canvas.onmousemove = e => this.pos.setXY(e.clientX, e.clientY)
    canvas.ontouchmove = e => this.pos.setXY(e.touches[0].clientX, e.touches[0].clientY)
    canvas.ontouchcancel = () => this.pos.setXY(-1000, -1000)
    canvas.ontouchend = () => this.pos.setXY(-1000, -1000)
  }
}

class Dot {
  id: number
  pos: any
  oldPos: any
  radius: number
  friction: number
  gravity: any
  mass: number
  pinned: boolean
  distMouse: number
  lightImg: HTMLImageElement
  lightWidth: number
  lightHeight: number

  constructor(x: number, y: number) {
    this.id = Math.random()
    this.pos = new Vector(x, y)
    this.oldPos = new Vector(x, y)
    this.radius = 1
    this.friction = 0.97
    this.gravity = new Vector(0, 0.6)
    this.mass = 1
    this.pinned = false
    this.distMouse = 1000
    this.lightImg = document.querySelector('#light-img') as HTMLImageElement
    this.lightWidth = 15
    this.lightHeight = 15
  }

  update(mouse: Mouse) {
    if (this.pinned) return

    let vel = Vector.sub(this.pos, this.oldPos)
    vel.mult(this.friction)

    this.oldPos.setXY(this.pos.x, this.pos.y)
    vel.add(this.gravity)
    this.pos.add(vel)

    let { x: dx, y: dy } = Vector.sub(this.pos, mouse.pos)
    this.distMouse = Math.sqrt(dx * dx + dy * dy)

    if (this.distMouse > mouse.radius + this.radius) return
    const direction = new Vector(dx / this.distMouse, dy / this.distMouse)

    let force = (mouse.radius - this.distMouse) / mouse.radius
    if (force < 0) force = 0
    if (force < 0.6) {
      this.pos.sub(direction.mult(force).mult(0.0001))
    } else {
      this.pos.setXY(mouse.pos.x, mouse.pos.y)
    }
  }

  drawLight(ctx: CanvasRenderingContext2D) {
    ctx.drawImage(
      this.lightImg,
      this.pos.x - this.lightWidth / 2,
      this.pos.y - this.lightHeight / 2,
      this.lightWidth,
      this.lightHeight
    )
  }
}

class Vector {
  x: number
  y: number

  constructor(x: number, y: number) {
    this.x = x || 0
    this.y = y || 0
  }

  static sub(v1: Vector, v2: Vector) {
    return new Vector(v1.x - v2.x, v1.y - v2.y)
  }

  setXY(x: number, y: number) {
    this.x = x
    this.y = y
    return this
  }

  add(v: Vector) {
    this.x += v.x
    this.y += v.y
    return this
  }

  sub(v: Vector) {
    this.x -= v.x
    this.y -= v.y
    return this
  }

  mult(v: number) {
    this.x *= v
    this.y *= v
    return this
  }
}

// 初始化小彩灯
function initLights() {
  const canvas = document.querySelector('.lights-canvas') as HTMLCanvasElement
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  let dots: Dot[] = []
  const mouse = new Mouse(canvas)
  const TOTAL = 20 // 减少灯泡数量，提高性能
  const SPACING = Math.floor(window.innerWidth / TOTAL) // 动态计算间距
  let animationId: number | null = null

  function resize() {
    canvas.width = window.innerWidth
    canvas.height = 150 // 增加高度以确保完全显示
    init()
  }

  function init() {
    dots = []
    // 计算起始偏移，使灯泡均匀分布
    const startOffset = Math.floor(SPACING / 2)
    
    for (let i = 0; i < TOTAL; i++) {
      // 使灯泡沿着正弦曲线排列，看起来像挂着的灯带
      const x = startOffset + i * SPACING
      const y = 60 + Math.sin(i * 0.5) * 15 // 调整正弦波幅度，使灯泡悬挂更明显
      
      const dot = new Dot(x, y)
      dot.lightWidth = 30 // 加大灯泡尺寸
      dot.lightHeight = 30
      dots.push(dot)
    }
    
    // 固定两端和多个中间点，增加稳定性
    dots[0].pinned = true
    dots[dots.length - 1].pinned = true
    
    // 每隔几个灯泡固定一个，增加稳定性
    for (let i = 5; i < dots.length; i += 5) {
      if (i < dots.length) dots[i].pinned = true
    }
  }

  function animate() {
    if (!ctx) return
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    // 画一条电线
    ctx.beginPath()
    ctx.strokeStyle = "rgba(80, 80, 100, 0.6)"
    ctx.lineWidth = 2
    
    for (let i = 0; i < dots.length - 1; i++) {
      ctx.moveTo(dots[i].pos.x, dots[i].pos.y)
      ctx.lineTo(dots[i + 1].pos.x, dots[i + 1].pos.y)
    }
    ctx.stroke()
    
    // 画灯泡
    for (let i = 0; i < dots.length; i++) {
      const dot = dots[i]
      dot.update(mouse)
      
      // 添加灯光效果
      const glow = ctx.createRadialGradient(
        dot.pos.x, dot.pos.y, 2,
        dot.pos.x, dot.pos.y, dot.lightWidth
      )
      
      // 使用科技风格的颜色，匹配页面主题
      // 每个灯泡使用循环互补色，主要在蓝、青、紫色范围内
      const hueBase = 210 // 基础为蓝色
      const hueOffset = (i * 30) % 120 // 在蓝青紫范围内变化
      const hue = hueBase + hueOffset
      
      // 闪烁效果 - 使用时间变量使灯光亮度随时间变化
      const time = Date.now() * 0.001
      const brightness = 0.7 + 0.3 * Math.sin(time + i * 0.5)
      
      glow.addColorStop(0, `hsla(${hue}, 100%, ${70 * brightness}%, 0.9)`)
      glow.addColorStop(0.5, `hsla(${hue}, 85%, ${50 * brightness}%, 0.6)`)
      glow.addColorStop(1, 'rgba(0, 0, 0, 0)')
      
      // 绘制光晕
      ctx.beginPath()
      ctx.fillStyle = glow
      ctx.arc(dot.pos.x, dot.pos.y, dot.lightWidth * 1.2, 0, Math.PI * 2)
      ctx.fill()
      
      // 绘制灯泡中心
      ctx.beginPath()
      ctx.fillStyle = `hsla(${hue}, 100%, ${90 * brightness}%, 0.9)`
      ctx.arc(dot.pos.x, dot.pos.y, dot.lightWidth / 4, 0, Math.PI * 2)
      ctx.fill()
    }

    animationId = requestAnimationFrame(animate)
  }

  // 确保在组件卸载时清理动画
  const cleanup = () => {
    if (animationId) {
      cancelAnimationFrame(animationId)
    }
    window.removeEventListener('resize', resize)
  }

  window.addEventListener('resize', resize)
  resize()
  animate() // 立即开始动画

  return cleanup
}

onMounted(() => {
  // 初始检测设备类型
  checkDevice()
  // 监听窗口大小变化
  window.addEventListener('resize', checkDevice)
  
  // 先初始化彩带动画
  initCanvas()
  
  // 在短暂延迟后初始化小彩灯，确保DOM已完全加载
  setTimeout(() => {
    const cleanupLights = initLights()
    
    // 组件卸载时清理
    onBeforeUnmount(() => {
      window.removeEventListener('resize', checkDevice)
      if (cleanupLights) cleanupLights()
      // 清理Canvas相关资源
      if (window.canvasCtx && window.canvasCtx.running) {
        window.canvasCtx.running = false
      }
      document.removeEventListener('mousemove', window.canvasMousemove)
      document.removeEventListener('touchmove', window.canvasMousemove)
      document.removeEventListener('touchstart', window.canvasTouchstart)
      window.removeEventListener('resize', window.canvasResize)
      window.removeEventListener('focus', window.canvasStart)
    })
  }, 300) // 短暂延迟，确保DOM加载完成
})

// Canvas彩带效果初始化
function initCanvas() {
  // 创建请求动画帧的兼容方法
  window.requestAnimFrame = (function(){
    return window.requestAnimationFrame || 
           window.webkitRequestAnimationFrame || 
           window.mozRequestAnimationFrame || 
           function(fn){ window.setTimeout(fn, 1000 / 60) }
  })()

  let ctx,
      hue,
      target = {},
      tendrils = [],
      settings = {}

  settings.debug = false
  settings.friction = 0.5
  settings.trails = 20
  settings.size = 50
  settings.dampening = 0.25
  settings.tension = 0.98

  Math.TWO_PI = Math.PI * 2

  // ========================================================================================
  // Oscillator 振荡器
  // ----------------------------------------------------------------------------------------
  function Oscillator(options) {
    this.init(options || {})
  }

  Oscillator.prototype = (function() {
    let value = 0

    return {
      init: function(options) {
        this.phase = options.phase || 0
        this.offset = options.offset || 0
        this.frequency = options.frequency || 0.001
        this.amplitude = options.amplitude || 1
      },

      update: function() {
        this.phase += this.frequency
        value = this.offset + Math.sin(this.phase) * this.amplitude
        return value
      },

      value: function() {
        return value
      }
    }
  })()

  // ========================================================================================
  // Tendril 卷须
  // ----------------------------------------------------------------------------------------
  function Tendril(options) {
    this.init(options || {})
  }

  Tendril.prototype = (function() {
    function Node() {
      this.x = 0
      this.y = 0
      this.vy = 0
      this.vx = 0
    }

    return {
      init: function(options) {
        this.spring = options.spring + (Math.random() * 0.1) - 0.05
        this.friction = settings.friction + (Math.random() * 0.01) - 0.005
        this.nodes = []

        for (let i = 0, node; i < settings.size; i++) {
          node = new Node()
          node.x = target.x
          node.y = target.y

          this.nodes.push(node)
        }
      },

      update: function() {
        let spring = this.spring,
            node = this.nodes[0]

        node.vx += (target.x - node.x) * spring
        node.vy += (target.y - node.y) * spring

        for (let prev, i = 0, n = this.nodes.length; i < n; i++) {
          node = this.nodes[i]

          if (i > 0) {
            prev = this.nodes[i - 1]

            node.vx += (prev.x - node.x) * spring
            node.vy += (prev.y - node.y) * spring
            node.vx += prev.vx * settings.dampening
            node.vy += prev.vy * settings.dampening
          }

          node.vx *= this.friction
          node.vy *= this.friction
          node.x += node.vx
          node.y += node.vy

          spring *= settings.tension
        }
      },

      draw: function() {
        let x = this.nodes[0].x,
            y = this.nodes[0].y,
            a, b, i, n;

        ctx.beginPath();
        ctx.moveTo(x, y);

        for (i = 1, n = this.nodes.length - 2; i < n; i++) {
          a = this.nodes[i];
          b = this.nodes[i + 1];
          x = (a.x + b.x) * 0.5;
          y = (a.y + b.y) * 0.5;

          ctx.quadraticCurveTo(a.x, a.y, x, y);
        }

        a = this.nodes[i];
        b = this.nodes[i + 1];

        ctx.quadraticCurveTo(a.x, a.y, b.x, b.y);
        ctx.stroke();
        ctx.closePath();
      }
    }
  })()

  // ----------------------------------------------------------------------------------------
  function init(event) {
    document.removeEventListener('mousemove', init)
    document.removeEventListener('touchstart', init)

    document.addEventListener('mousemove', mousemove)
    document.addEventListener('touchmove', mousemove)
    document.addEventListener('touchstart', touchstart)

    // 存储事件处理函数到window，以便清理
    window.canvasMousemove = mousemove
    window.canvasTouchstart = touchstart

    mousemove(event)
    reset()
    loop()
  }

  function reset() {
    tendrils = []

    for (let i = 0; i < settings.trails; i++) {
      tendrils.push(new Tendril({
        spring: 0.45 + 0.025 * (i / settings.trails)
      }))
    }
  }

  function loop() {
    if (!ctx.running) return;

    ctx.globalCompositeOperation = 'source-over';
    ctx.fillStyle = 'rgba(8,5,16,0.4)';
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    ctx.globalCompositeOperation = 'lighter';
    ctx.strokeStyle = 'hsla(' + Math.round(hue.update()) + ',90%,50%,0.25)';
    ctx.lineWidth = 1;

    for (let i = 0, tendril; i < settings.trails; i++) {
      tendril = tendrils[i];
      tendril.update();
      tendril.draw();
    }

    ctx.frame++;
    requestAnimFrame(loop);
  }

  function resize() {
    ctx.canvas.width = window.innerWidth
    ctx.canvas.height = window.innerHeight
  }

  function start() {
    if (!ctx.running) {
      ctx.running = true
      loop()
    }
  }

  function mousemove(event) {
    if (event.touches) {
      target.x = event.touches[0].pageX;
      target.y = event.touches[0].pageY;
    } else {
      target.x = event.clientX;
      target.y = event.clientY;
    }
    
    event.preventDefault();
  }

  function touchstart(event) {
    if (event.touches.length === 1) {
      target.x = event.touches[0].pageX
      target.y = event.touches[0].pageY
    }
  }

  // 初始化Canvas
  setTimeout(() => {
    const canvas = document.getElementById('canvas');
    if (canvas) {
      ctx = canvas.getContext('2d');
      ctx.running = true;
      ctx.frame = 1;
      
      // 创建一个简单的stats对象，以避免错误
      ctx.stats = {
        update: function() {}
      };
      
      hue = new Oscillator({
        phase: Math.random() * Math.TWO_PI,
        amplitude: 85,
        frequency: 0.0015,
        offset: 285
      });
      
      // 存储ctx到window对象，便于清理
      window.canvasCtx = ctx;
      
      // 初始化target位置
      target.x = canvas.width / 2;
      target.y = canvas.height / 2;
      
      // 绑定事件
      document.addEventListener('mousemove', init);
      document.addEventListener('touchstart', init);
      window.addEventListener('resize', resize);
      window.canvasResize = resize;
      window.addEventListener('focus', start);
      window.canvasStart = start;
      
      resize();
      
      // 直接开始动画，不等待用户交互
      init({
        clientX: window.innerWidth / 2,
        clientY: window.innerHeight / 2,
        preventDefault: function() {}
      });
    }
  }, 100);
}
</script>

<style scoped>
/* 导入字体 */
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@400;800&display=swap");

:root {
  --bg: #000000;
  --clr-1: #00c2ff;
  --clr-2: #33ff8c;
  --clr-3: #ffc640;
  --clr-4: #e54cff;
  --blur: 1rem;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  color: #fff;
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  font-family: "Inter", "Microsoft YaHei", sans-serif;
}

#canvas {
  position: absolute;
  z-index: 1;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgb(8, 5, 16);
}

.content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  text-align: center;
}

.title-container {
  margin-bottom: 4rem;
}

.main-title {
  font-size: 5rem;
  font-weight: 800;
  letter-spacing: 0.1em;
  position: relative;
  margin-bottom: 1rem;
  color: white; /* 直接使用白色文字 */
  text-shadow: 
    0 0 5px rgba(0, 194, 255, 0.8),
    0 0 10px rgba(0, 194, 255, 0.5),
    0 0 15px rgba(0, 194, 255, 0.3); /* 添加明亮的霓虹灯效果 */
  animation: textPulse 3s infinite;
}

/* 为主标题添加脉冲动画 */
@keyframes textPulse {
  0% {
    text-shadow: 
      0 0 5px rgba(0, 194, 255, 0.8),
      0 0 10px rgba(0, 194, 255, 0.5),
      0 0 15px rgba(0, 194, 255, 0.3);
  }
  50% {
    text-shadow: 
      0 0 10px rgba(0, 194, 255, 1),
      0 0 20px rgba(0, 194, 255, 0.8),
      0 0 30px rgba(0, 194, 255, 0.6),
      0 0 40px rgba(0, 194, 255, 0.4);
  }
  100% {
    text-shadow: 
      0 0 5px rgba(0, 194, 255, 0.8),
      0 0 10px rgba(0, 194, 255, 0.5),
      0 0 15px rgba(0, 194, 255, 0.3);
  }
}

.subtitle {
  font-size: 1.5rem;
  letter-spacing: 0.2em;
  opacity: 1; /* 不透明度设为1使文字更清晰 */
  color: white; /* 直接使用白色 */
  text-shadow: 0 0 8px rgba(51, 255, 140, 0.7); /* 使用绿色霓虹灯效果 */
  animation: subtitleGlow 4s infinite alternate;
}

@keyframes subtitleGlow {
  0% {
    text-shadow: 0 0 8px rgba(51, 255, 140, 0.7);
  }
  50% {
    text-shadow: 0 0 12px rgba(51, 255, 140, 0.9);
  }
  100% {
    text-shadow: 0 0 8px rgba(51, 255, 140, 0.7);
  }
}

.aurora {
  position: absolute;
  top: -100%;
  left: -100%;
  width: 300%;
  height: 300%;
  z-index: -1;
  mix-blend-mode: color-dodge;
  pointer-events: none;
}

.aurora__item {
  overflow: hidden;
  position: absolute;
  width: 60vw;
  height: 60vw;
  border-radius: 37% 29% 27% 27% / 28% 25% 41% 37%;
  filter: blur(var(--blur));
  mix-blend-mode: overlay;
}

.aurora__item:nth-of-type(1) {
  background-color: var(--clr-1);
  top: -50%;
  animation: aurora-border 6s ease-in-out infinite,
    aurora-1 12s ease-in-out infinite alternate;
}

.aurora__item:nth-of-type(2) {
  background-color: var(--clr-3);
  right: 0;
  top: 0;
  animation: aurora-border 6s ease-in-out infinite,
    aurora-2 12s ease-in-out infinite alternate;
}

.aurora__item:nth-of-type(3) {
  background-color: var(--clr-2);
  left: 0;
  bottom: 0;
  animation: aurora-border 6s ease-in-out infinite,
    aurora-3 8s ease-in-out infinite alternate;
}

.aurora__item:nth-of-type(4) {
  background-color: var(--clr-4);
  right: 0;
  bottom: -50%;
  animation: aurora-border 6s ease-in-out infinite,
    aurora-4 24s ease-in-out infinite alternate;
}

.buttons-container {
  display: flex;
  gap: 2rem;
  animation: fadeIn 2s ease-out 1s both;
}

.btn {
  padding: 0.8rem 2rem;
  border: none; /* 移除边框 */
  background-color: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 1.2rem;
  letter-spacing: 0.1em;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.2s ease, box-shadow 0.3s ease;
  backdrop-filter: blur(5px);
  position: relative;
  overflow: hidden;
  z-index: 2;
  box-shadow: 
    0 0 5px rgba(0, 194, 255, 0.5),
    0 0 10px rgba(0, 194, 255, 0.3);
}

.btn:after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 1px solid rgba(0, 194, 255, 0.6);
  border-radius: 5px;
  box-shadow: 
    0 0 5px rgba(0, 194, 255, 0.5),
    0 0 10px rgba(0, 194, 255, 0.3);
  animation: borderPulse 2s infinite;
}

.explore-sounds:after {
  border-color: rgba(51, 255, 140, 0.6);
  box-shadow: 
    0 0 5px rgba(51, 255, 140, 0.5),
    0 0 10px rgba(51, 255, 140, 0.3);
}

@keyframes borderPulse {
  0% {
    box-shadow: 
      0 0 5px rgba(0, 194, 255, 0.5),
      0 0 10px rgba(0, 194, 255, 0.3);
  }
  50% {
    box-shadow: 
      0 0 10px rgba(0, 194, 255, 0.7),
      0 0 15px rgba(0, 194, 255, 0.5),
      0 0 20px rgba(0, 194, 255, 0.3);
  }
  100% {
    box-shadow: 
      0 0 5px rgba(0, 194, 255, 0.5),
      0 0 10px rgba(0, 194, 255, 0.3);
  }
}

.btn:hover {
  background-color: rgba(0, 0, 0, 0.6);
  transform: translateY(-3px);
  box-shadow: 
    0 7px 20px rgba(0, 0, 0, 0.3),
    0 0 15px var(--clr-1),
    0 0 15px var(--clr-3);
}

.btn:active {
  transform: translateY(1px);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes aurora-1 {
  0% {
    top: 0;
    right: 0;
  }
  50% {
    top: 100%;
    right: 75%;
  }
  75% {
    top: 100%;
    right: 25%;
  }
  100% {
    top: 0;
    right: 0;
  }
}

@keyframes aurora-2 {
  0% {
    top: -50%;
    left: 0%;
  }
  60% {
    top: 100%;
    left: 75%;
  }
  85% {
    top: 100%;
    left: 25%;
  }
  100% {
    top: -50%;
    left: 0%;
  }
}

@keyframes aurora-3 {
  0% {
    bottom: 0;
    left: 0;
  }
  40% {
    bottom: 100%;
    left: 75%;
  }
  65% {
    bottom: 40%;
    left: 50%;
  }
  100% {
    bottom: 0;
    left: 0;
  }
}

@keyframes aurora-4 {
  0% {
    bottom: -50%;
    right: 0;
  }
  50% {
    bottom: 0%;
    right: 40%;
  }
  90% {
    bottom: 50%;
    right: 25%;
  }
  100% {
    bottom: -50%;
    right: 0;
  }
}

@keyframes aurora-border {
  0% {
    border-radius: 37% 29% 27% 27% / 28% 25% 41% 37%;
  }
  25% {
    border-radius: 47% 29% 39% 49% / 61% 19% 66% 26%;
  }
  50% {
    border-radius: 57% 23% 47% 72% / 63% 17% 66% 33%;
  }
  75% {
    border-radius: 28% 49% 29% 100% / 93% 20% 64% 25%;
  }
  100% {
    border-radius: 37% 29% 27% 27% / 28% 25% 41% 37%;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-title {
    font-size: 3rem;
  }
  
  .subtitle {
    font-size: 1rem;
  }
  
  .buttons-container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .btn {
    padding: 0.7rem 1.5rem;
    font-size: 1rem;
  }
}

/* 小型手机设备 */
@media (max-width: 480px) {
  .main-title {
    font-size: 2.2rem;
  }
  
  .title-container {
    margin-bottom: 2.5rem;
  }
  
  .btn {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
  }
}

.lights-canvas {
  position: fixed; /* 改为fixed以确保始终固定在页面顶部 */
  top: 0;
  left: 0;
  width: 100%;
  height: 150px; /* 增加高度 */
  z-index: 10; /* 提高z-index优先级 */
  pointer-events: none;
  /* 添加暗蓝色渐变背景，符合科技风格 */
  background: linear-gradient(to bottom, 
                rgba(5, 15, 30, 0.2) 0%, 
                rgba(5, 15, 30, 0.1) 50%, 
                rgba(0, 0, 0, 0) 100%);
}

.asset-img {
  display: none;
}
</style>
