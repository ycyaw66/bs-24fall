<template>
  <div class="main" style="overflow-y: hidden;">
    <el-main class="main-content">
      <el-card class="forget-card" style="margin-top: -100px;">
        <template #header>
          <div class="container" style="justify-content: space-between;">
            <div style="margin-left: 165px">
              <span>忘记密码</span>
            </div>
            <el-link type="warning" @click="jumpLogin" :underline="false" style="margin-right: 10px;">返回</el-link>
          </div>
        </template>
        <!-- 忘记密码卡片的body -->
        <el-form :model="forgetForm" :rules="forgetRules" ref="forgetForm" label-width="80px">
          <el-form-item label="邮箱" prop="email" style="margin-top: 20px">
            <el-input v-model="forgetForm.email" placeholder="邮箱" style="width: 200px"></el-input>
            <el-button v-if="isCounting" type="warning" style="margin-left: 5px; width: 95px" :disabled="isCounting">{{countDown}}秒后重试</el-button>
            <el-button v-else type="warning" style="margin-left: 5px; width: 95px" @click="getVerificationCode">获取验证码</el-button>
          </el-form-item>
          <el-form-item label="验证码" prop="verificationCode" style="margin-top: 20px">
            <el-input v-model="forgetForm.verificationCode" placeholder="六位数字验证码" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password" style="margin-top: 20px">
            <el-input v-model="forgetForm.password" placeholder="6-16位，至少包括字母和数字" style="width: 300px" show-password></el-input>
          </el-form-item>
          <el-form-item label="重复密码" prop="repassword" style="margin-top: 20px">
            <el-input v-model="forgetForm.repassword" placeholder="请再次输入密码" style="width: 300px" show-password></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="warning" @click="submitForm" style="width: 300px">重置密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

<script>
import CryptoJS from 'crypto-js';
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  data() {
    const checkPassword = (rule, value, callback) => {
      if (value !== this.forgetForm.password) {
        callback(new Error('两次密码不一致'));
      } else {
        callback();
      }
    };
    return {
      // 邮件发送验证码倒计时
      countDown: 30,
      // 是否正在倒计时
      isCounting: false,
      // 倒计时定时器
      countDownTimeout: null,
      uuid: 0,
      forgetForm: {
        email: '',
        verificationCode: '',
        password: '',
        repassword: ''
      },
      forgetRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'change' },
          { pattern: /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/, message: '请输入有效的邮箱地址', trigger: 'blur' }
        ],
        verificationCode: [
          { required: true, message: '请输入验证码', trigger: 'change' },
          { pattern: /^\d{6}$/, message: '请输入有效的验证码', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'change' },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{6,16}$/, message: '密码必须为6-16位，且同时包含字母和数字', trigger: 'blur' }
        ],
        repassword: [
          { required: true, message: '请再次输入密码', trigger: 'change'},
          { validator: checkPassword, message: '两次密码不一致', trigger: 'blur'}
        ]
      }
    };
  },
  methods: {
    jumpLogin() {
      this.$router.push('/user/login');
    },
    submitForm() {
      // 表单校验
      this.$refs["forgetForm"].validate((valid) => {
        if (!valid) {
          ElMessage.error("重置失败，请检查信息");
          return;
        } else {
          this.handleForget();
        }
      });
    },
    handleForget() {
      const encrypted = CryptoJS.SHA256(this.forgetForm.password).toString();
      axios.post("/user/forget",
          {
            "email": this.forgetForm.email,
            "uuid": this.uuid,
            "password": encrypted,
            "code": this.forgetForm.verificationCode
          })
          .then(response => {
            if (response.data.code === 0) {
              ElMessage.success("重置密码成功");
              this.jumpLogin();
            } else {
              ElMessage.error(response.data.err);
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
            "mail": this.forgetForm.email
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
      return pattern.test(this.forgetForm.email);
    }
  }
};
</script>

<style scoped>

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

.forget-card {
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

</style>