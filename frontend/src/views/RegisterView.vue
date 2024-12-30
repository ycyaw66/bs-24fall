<template>
  <div class="main" style="overflow-y: hidden;">
    <el-container>
      <el-main v-if="!isMobile" class="main-content">
        <el-card class="register-card" style="margin-top: -50px;">
          <template #header>
            <div class="container">
              <div style="margin-left:5px" >
                <span>用户注册</span>
              </div>
            </div>
          </template>
          <!-- 注册卡片的body -->
          <el-form :model="registerForm" :rules="registerRules" ref="registerForm"  label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="6-16位，仅包含大小写字母和数字" style="width: 300px"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password" style="margin-top: 20px">
              <el-input v-model="registerForm.password" placeholder="6-16位，至少包括字母和数字" style="width: 300px" show-password></el-input> 
            </el-form-item>
            <el-form-item label="重复密码" prop="repassword" style="margin-top: 20px">
              <el-input v-model="registerForm.repassword" placeholder="请再次输入密码" style="width: 300px" show-password></el-input> 
            </el-form-item>
            <el-form-item label="邮箱" prop="email" style="margin-top: 20px">
              <el-input v-model="registerForm.email" placeholder="邮箱" style="width: 200px"></el-input>
                <el-button v-if="isCounting" type="warning" style="margin-left: 5px; width: 95px" :disabled="isCounting">{{countDown}}秒后重试</el-button>
                <el-button v-else type="warning" style="margin-left: 5px; width: 95px" @click="getVerificationCode">获取验证码</el-button>
            </el-form-item>
            <el-form-item label="验证码" prop="verificationCode" style="margin-top: 20px">
              <el-input v-model="registerForm.verificationCode" placeholder="六位数字验证码" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="submitForm" style="width: 300px">注册</el-button>
              <el-divider style="width: 300px"/>
            </el-form-item>
            <el-form-item style="margin-top: -40px">
              已有账号？
              <el-link type="warning" :underline="false" @click="jumpLogin">登录</el-link>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
      <el-main v-else class="main-content-mobile">
        <el-card class="register-card-mobile">
          <img src="../assets/logo.png" alt="Logo" class="logo-mobile" />
          <el-form :model="registerForm" :rules="registerRules" ref="registerForm" label-width="0">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="用户名"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" placeholder="密码" show-password></el-input>
            </el-form-item>
            <el-form-item prop="repassword">
              <el-input v-model="registerForm.repassword" placeholder="重复密码" show-password></el-input>
            </el-form-item>
            <el-form-item prop="email">
              <div class="email-verification-container">
                <el-input v-model="registerForm.email" placeholder="邮箱" class="email-input"></el-input>
                <el-button
                  v-if="isCounting"
                  type="warning"
                  class="verification-button"
                  :disabled="isCounting"
                >{{countDown}}秒后重试</el-button>
                <el-button
                  v-else
                  type="warning"
                  class="verification-button"
                  @click="getVerificationCode"
                >获取验证码</el-button>
              </div>
            </el-form-item>
            <el-form-item prop="verificationCode">
              <el-input v-model="registerForm.verificationCode" placeholder="六位数字验证码" class="mobile-input"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" class="mobile-button" @click="submitForm">注册</el-button>
            </el-form-item>
            <el-form-item>
              已有账号？
              <el-link type="warning" :underline="false" @click="jumpLogin">登录</el-link>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>
 
<script>
import CryptoJS from 'crypto-js';
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  data() {
    const checkPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次密码不一致'));
      } else {
        callback();
      }
    };
    return {
      // 邮件发送验证码倒计时
      countDown: 60,
      // 是否正在倒计时
      isCounting: false,
      // 倒计时定时器
      countDownTimeout: null,
      uuid: 0,
      registerForm: {
        username: '',
        password: '',
        repassword: '',
        email: '',
        verificationCode: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'change' },
          { pattern: /^[a-zA-Z0-9]{6,16}$/, message: '用户名必须为6-16位，仅包含大小写字母和数字', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'change' },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{6,16}$/, message: '密码必须为6-16位，且同时包含字母和数字', trigger: 'blur' }
        ],
        repassword: [
          { required: true, message: '请再次输入密码', trigger: 'change'},
          { validator: checkPassword, message: '两次密码不一致', trigger: 'blur'}
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'change' },
          { pattern: /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/, message: '请输入有效的邮箱地址', trigger: 'blur' }
        ],
        verificationCode: [
          { required: true, message: '请输入验证码', trigger: 'change' },
          { pattern: /^\d{6}$/, message: '请输入有效的验证码', trigger: 'blur' }
        ]
      },
      isMobile: false,
    };
  },
  mounted() {
    this.checkIsMobile();
    window.addEventListener("resize", this.checkIsMobile);
    console.log(this.isMobile);
  },
  beforeUnmount() {
    window.removeEventListener("resize", this.checkIsMobile);
  },
  methods: {
    checkIsMobile() {
      this.isMobile = (window.innerWidth <= 768);
    },
    jumpLogin() {
      this.$router.push('/user/login');
    },
    submitForm() {
      // 表单校验
      this.$refs["registerForm"].validate((valid) => {
        if (!valid) {
          ElMessage.error("注册失败，请检查注册信息");
          console.log("校验不通过");
          return;
        } else {
          console.log("校验通过");
          this.handleRegister();
        }
      });
    },
    handleRegister() {
      const encrypted = CryptoJS.SHA256(this.registerForm.password).toString();
      console.log(this.uuid);
      axios.post("/user/register",
      {
        "username": this.registerForm.username,
        "password": encrypted,
        "email": this.registerForm.email,
        "code": this.registerForm.verificationCode,
        "uuid": this.uuid
      })
      .then(response => {
        console.log(response.data);
        if (response.data.code === 0) {
          ElMessage.success("注册成功");
          this.jumpLogin();
        } else {
          ElMessage.error(response.data.err);
          return;
        }
      })
      .catch(error => {
        console.log(error);
      })
    },
    getVerificationCode() {
      if (!this.isEmailValid) {
        ElMessage.error("请输入有效的邮箱地址");
        return;
      }
      console.log("发送成功");
      this.countDown = 30;
      this.isCounting = true;
      this.doCountdown();
      axios.post("/user/sendmail",
        {
          "mail": this.registerForm.email
        })
        .then(response => {
          if (response.data.code === 0) {
            this.uuid = response.data.payload.uuid;
          } else {
            ElMessage.error(response.data.err);
          }
        })
        .catch(error => {
          console.log(error);
        })
    },
    doCountdown() {
      this.countDownTimeout = setTimeout(() => {
        if (this.countDown <= 0) {
          this.isCounting = false;
          clearTimeout(this.countDownTimeout);
        } else {
          this.countDown--;
          this.doCountdown();
        }
      },1000)
    }
  },
  computed: {
    isEmailValid() {
      const pattern = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
      return pattern.test(this.registerForm.email);
    }
  }
};
</script>

<style scoped>
/* PC端样式 */
.main {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  min-height: 100%;
  height: auto;
  background-color: #dcdcdc;
}

.register-card {
  width: 450px;
  margin-left: auto;
  margin-right: 50px;
  font-size: 20px;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.main-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-image: url('../assets/background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.title {
  background-color: #ffffff;
  height: 60px;
}

/* 手机端样式 */
.main-content-mobile {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: #ffffff;
}

.register-card-mobile {
  width: 90%;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logo-mobile {
  display: block;
  margin: 0 auto 20px;
  max-width: 120px;
}

.mobile-button {
  width: 100%;
}

.email-verification-container {
  display: flex;
  align-items: center;
}

.email-input {
  flex: 1;
  margin-right: 5px;
}

.verification-button {
  flex-shrink: 0;
  width: 95px;
}
</style>