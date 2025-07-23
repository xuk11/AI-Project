<template>
  <div>
    <div class="register-box">
      <h2>用户注册</h2>
      <form>
        <div class="user-box">
          <input type="text" name="" v-model="formState.userAccount" required="" />
          <label>用户名</label>
        </div>
        <div class="user-box">
          <input type="password" name="" v-model="formState.userPassword" required="" @input="updatePasswordStrength" />
          <label>密码</label>
          <div v-if="passwordStrength" :class="passwordStrengthClass" class="password-strength">
            {{ passwordStrength }}
          </div>
        </div>
        <div class="user-box">
          <input type="password" name="" v-model="formState.checkPassword" required="" />
          <label>确认密码</label>
        </div>
        <div class="action-area">
          <a @click="handleSubmit(formState)" class="register-btn">
            <span></span>
            <span></span>
            <span></span>
            <span></span>
            注册账号
          </a>
          <div class="login-link">
            已有账号？<a @click="goToLogin" class="text-link">立即登录</a>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { userRegisterUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

interface RegisterForm extends API.UserRegisterRequest {
  checkPassword: string
}

const formState = reactive<RegisterForm>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const passwordStrength = ref('')
const passwordStrengthClass = ref('')

const updatePasswordStrength = (event: Event) => {
  const password = (event.target as HTMLInputElement).value
  if (password.length < 4) {
    passwordStrength.value = '非常弱'
    passwordStrengthClass.value = 'weak'
  } else if (password.length < 8) {
    passwordStrength.value = '弱'
    passwordStrengthClass.value = 'weak'
  } else if (/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(password)) {
    passwordStrength.value = '中等'
    passwordStrengthClass.value = 'medium'
  } else if (/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/.test(password)) {
    passwordStrength.value = '强'
    passwordStrengthClass.value = 'strong'
  } else {
    passwordStrength.value = '弱'
    passwordStrengthClass.value = 'weak'
  }
}

const handleSubmit = async (values: RegisterForm) => {
  // 表单验证
  if (!values.userAccount || !values.userPassword || !values.checkPassword) {
    message.error('请填写完整信息')
    return
  }
  if (values.userPassword !== values.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  if (values.userAccount.length < 4) {
    message.error('用户名长度不能少于4位')
    return
  }
  if (values.userPassword.length < 8) {
    message.error('密码长度不能少于8位')
    return
  }

  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/
  if (!passwordRegex.test(values.userPassword)) {
    message.error('密码需包含字母、数字和特殊字符')
    return
  }

  try {
    // 调用注册API
    const res = await userRegisterUsingPost({
      userAccount: values.userAccount,
      userPassword: values.userPassword,
      checkPassword: values.checkPassword,
    })

    if (res.data.code === 0) {
      message.success('注册成功，请登录')
      router.push('/user/login')
    } else {
      message.error('注册失败，' + res.data.message)
    }
  } catch (error) {
    message.error('注册失败，请稍后再试')
    console.error(error)
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
html {
  height: 100%;
}

body {
  margin: 0;
  padding: 0;
  font-family: sans-serif;
  background: linear-gradient(#141e30, #243b55);
}

.register-box {
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

.register-box h2 {
  margin: 0 0 30px;
  padding: 0;
  color: #fff;
  text-align: center;
}

.register-box .user-box {
  position: relative;
}

.register-box .user-box input {
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

.register-box .user-box label {
  position: absolute;
  top: 0;
  left: 0;
  padding: 10px 0;
  font-size: 16px;
  color: #fff;
  pointer-events: none;
  transition: 0.5s;
}

.register-box .user-box input:focus ~ label,
.register-box .user-box input:valid ~ label {
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

.register-box form a.register-btn {
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

.register-box a.register-btn:hover {
  background: #03e9f4;
  color: #fff;
  border-radius: 5px;
  box-shadow:
    0 0 5px #03e9f4,
    0 0 25px #03e9f4,
    0 0 50px #03e9f4,
    0 0 100px #03e9f4;
}

.register-box a.register-btn span {
  position: absolute;
  display: block;
}

.register-box a.register-btn span:nth-child(1) {
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

.register-box a.register-btn span:nth-child(2) {
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

.register-box a.register-btn span:nth-child(3) {
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

.register-box a.register-btn span:nth-child(4) {
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

.login-link {
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

.password-strength {
  position: absolute;
  bottom: 10px;
  left: 0;
  font-size: 12px;
}

.weak {
  color: red;
}

.medium {
  color: orange;
}

.strong {
  color: green;
}
</style>
