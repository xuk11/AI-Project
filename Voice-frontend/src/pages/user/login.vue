<template>
  <div>
    <div class="login-box">
      <h2>登录系统</h2>
      <form>
        <div class="user-box">
          <input type="text" name="" v-model="formState.userAccount" required="" />
          <label>用户名</label>
        </div>
        <div class="user-box">
          <input type="password" name="" v-model="formState.userPassword" required="" />
          <label>密码</label>
        </div>
        <div class="action-area">
          <a-space>
            <a @click="handleSubmit(formState)" class="login-btn">
              <span></span>
              <span></span>
              <span></span>
              <span></span>
              进入系统
            </a>
          </a-space>
          <div class="register-link">
            没有账号？<a @click="goToRegister" class="text-link">立即注册</a>
            <a @click="quickStart" style="margin-left: 24px" class="text-link">快速体验</a>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { userLoginUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

const activeKey = ref('1')
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const loginUserStore = useLoginUserStore()

// 测试账号信息
const testAccount = {
  userAccount: 'qwer',
  userPassword: '12345678',
}

// 使用测试账号登录
const useTestAccount = () => {
  formState.userAccount = testAccount.userAccount
  formState.userPassword = testAccount.userPassword
  // 自动点击登录按钮
  handleSubmit(formState)
}

const handleSubmit = async (values: any) => {
  if (!values.userAccount || !values.userPassword) {
    message.error('请输入用户名和密码')
    return
  }
  const res = await userLoginUsingPost(values)
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    let redirectPath = router.currentRoute.value.query.redirect || '/home'
    if (typeof redirectPath === 'string') {
      redirectPath = decodeURIComponent(redirectPath)
    }
    router.push({
      path: redirectPath,
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}

const goToRegister = () => {
  router.push('/user/register')
}
const quickStart = () => {
  useTestAccount()
}
</script>

<style>
html {
  height: 100%;
}

body {
  margin: 0;
  padding: 0;
  font-family: sans-serif;
  background: linear-gradient(#141e30, #243b55);
}

.login-box {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 400px;
  padding: 40px;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.5);
  box-sizing: border-box;
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.6);
  border-radius: 10px;
}

.login-box h2 {
  margin: 0 0 30px;
  padding: 0;
  color: #fff;
  text-align: center;
}

.login-box .user-box {
  position: relative;
}

.login-box .user-box input {
  width: 100%;
  padding: 10px 0;
  font-size: 16px;
  color: #fff;
  margin-bottom: 30px;
  border: none;
  border-bottom: 1px solid #fff;
  outline: none;
  background: transparent;
}

.login-box .user-box label {
  position: absolute;
  top: 0;
  left: 0;
  padding: 10px 0;
  font-size: 16px;
  color: #fff;
  pointer-events: none;
  transition: 0.5s;
}

.login-box .user-box input:focus ~ label,
.login-box .user-box input:valid ~ label {
  top: -20px;
  left: 0;
  color: #03e9f4;
  font-size: 12px;
}

.action-area {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-box form a.login-btn {
  position: relative;
  display: inline-block;
  padding: 10px 20px;
  color: #03e9f4;
  font-size: 16px;
  text-decoration: none;
  text-transform: uppercase;
  overflow: hidden;
  transition: 0.5s;
  margin-top: 40px;
  letter-spacing: 4px;
  cursor: pointer;
}

.login-box a.login-btn:hover {
  background: #03e9f4;
  color: #fff;
  border-radius: 5px;
  box-shadow:
    0 0 5px #03e9f4,
    0 0 25px #03e9f4,
    0 0 50px #03e9f4,
    0 0 100px #03e9f4;
}

.login-box a.login-btn span {
  position: absolute;
  display: block;
}

.login-box a.login-btn span:nth-child(1) {
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #03e9f4);
  animation: btn-anim1 1s linear infinite;
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

.login-box a.login-btn span:nth-child(2) {
  top: -100%;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, transparent, #03e9f4);
  animation: btn-anim2 1s linear infinite;
  animation-delay: 0.25s;
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

.login-box a.login-btn span:nth-child(3) {
  bottom: 0;
  right: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(270deg, transparent, #03e9f4);
  animation: btn-anim3 1s linear infinite;
  animation-delay: 0.5s;
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

.login-box a.login-btn span:nth-child(4) {
  bottom: -100%;
  left: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(360deg, transparent, #03e9f4);
  animation: btn-anim4 1s linear infinite;
  animation-delay: 0.75s;
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

.register-link {
  margin-top: 25px;
  color: #fff;
  font-size: 14px;
  text-align: center;
}

.text-link {
  color: #03e9f4;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.text-link:hover {
  text-shadow:
    0 0 5px #03e9f4,
    0 0 15px #03e9f4;
}

.test-account {
  margin-top: 20px;
  text-align: center;
}

.test-btn {
  position: relative;
  display: inline-block;
  padding: 8px 16px;
  color: #8bc34a;
  font-size: 14px;
  text-decoration: none;
  overflow: hidden;
  transition: 0.5s;
  letter-spacing: 2px;
  cursor: pointer;
  border: 1px solid #8bc34a;
  border-radius: 5px;
  background: rgba(139, 195, 74, 0.1);
}

.test-btn:hover {
  background: #8bc34a;
  color: #fff;
  border-radius: 5px;
  box-shadow:
    0 0 5px #8bc34a,
    0 0 25px #8bc34a,
    0 0 50px #8bc34a;
}

.test-btn span {
  position: absolute;
  display: block;
}

.test-btn span:nth-child(1) {
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #8bc34a);
  animation: btn-anim1 1s linear infinite;
}

.test-btn span:nth-child(2) {
  top: -100%;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, transparent, #8bc34a);
  animation: btn-anim2 1s linear infinite;
  animation-delay: 0.25s;
}

.test-btn span:nth-child(3) {
  bottom: 0;
  right: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(270deg, transparent, #8bc34a);
  animation: btn-anim3 1s linear infinite;
  animation-delay: 0.5s;
}

.test-btn span:nth-child(4) {
  bottom: -100%;
  left: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(360deg, transparent, #8bc34a);
  animation: btn-anim4 1s linear infinite;
  animation-delay: 0.75s;
}
</style>
