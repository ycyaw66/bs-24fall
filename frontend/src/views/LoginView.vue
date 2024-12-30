<template>
  <div class="main" style="overflow-y: hidden;">
    <el-main v-if="!isMobile" class="main-content">
      <el-card class="login-card">
        <template #header>
          <div class="container">
            <div style="margin-left:5px">
              <span>用户登录</span>
            </div>
          </div>
        </template>
        <el-form :model="loginForm" ref="loginForm" :rules="loginRules" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" style="width: 250px"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password" style="margin-top: 20px">
            <el-input v-model="loginForm.password" placeholder="请输入密码" style="width: 250px" show-password></el-input>
            <el-link type="warning" style="margin-left: 15px; font-size: 13px" :underline="false" @click="jumpForget">忘记密码？</el-link>
          </el-form-item>
          <el-form-item>
            <el-button type="warning" @click="handleLogin" style=" width: 250px">登录</el-button>
            <el-divider style="width: 250px" />
          </el-form-item>
          <el-form-item style="margin-top: -40px">
            没有账号？
            <el-link type="warning" :underline="false" @click="jumpRegister">注册</el-link>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
    <el-main v-else class="main-content-mobile">
      <el-card class="login-card-mobile">
        <template #header>
          <img src="../assets/logo.png" alt="Logo" class="mobile-logo" />
        </template>
        <el-form :model="loginForm" ref="loginForm" :rules="loginRules" label-width="0">
          <el-form-item>
            <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="loginForm.password" placeholder="请输入密码" show-password></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="warning" @click="handleLogin" style="width: 100%">登录</el-button>
          </el-form-item>
          <el-form-item>
            没有账号？<el-link type="warning" :underline="false" @click="jumpRegister">注册</el-link>
            <el-link type="warning" :underline="false" @click="jumpForget" style="margin-left: auto; margin-right: 0">忘记密码？</el-link>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

 
<script>
import CryptoJS from 'crypto-js';
import Cookies from "js-cookie";
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'change' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'change' }
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
    jumpRegister() {
      this.$router.push('/user/register');
    },
    jumpForget() {
      this.$router.push('/user/forget');
    },
    jumpSearch() {
      this.$router.push('/search');
    },
    handleLogin() {
      const encrypted = CryptoJS.SHA256(this.loginForm.password).toString();
      // console.log(encrypted);
      axios.post("/user/login",
        {
          "username": this.loginForm.username,
          "password": encrypted
        })
        .then(response => {
          if (response.data.code === 0) {
            ElMessage.success("登录成功");
            Cookies.set('token', response.data.payload.token);
            this.jumpSearch();
          } else {
            ElMessage.error(response.data.err);
            return;
          }
        })
        .catch(error => {
          console.log(error);
        })
    },
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

.login-card {
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

/* 手机端样式 */
.main-content-mobile {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: #ffffff;
}

.login-card-mobile {
  width: 90%;
  padding: 10px;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
}

.mobile-logo {
  display: block;
  margin: 0 auto;
  width: 100px;
  height: auto;
}

</style>