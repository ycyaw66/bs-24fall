<template>
  <div class="main">
    <HeaderComponent
      :isLoggedIn="isLoggedIn"
      :username="username"
      @logout="handleLogout"
    />
    <el-button type="primary" @click="jumpSearch" class="search-button">返回搜索</el-button>
    <el-card :class="isMobile ? 'profile-card-mobile' : 'profile-card'">
      <!-- 用户头像和用户名 -->
      <div :class="isMobile ? 'header-mobile' : 'header'">
        <img src="../assets/avatar.png" alt="用户头像" :class="isMobile ? 'avatar-mobile' : 'avatar'" />
        <div :class="isMobile ? 'user-info-mobile' : 'user-info'">
          <h2>{{ username }}</h2>
          <p class="email">{{ userInfo.email }}</p>
        </div>
      </div>
      <!-- 个人信息 -->
      <div class="info-section">
        <h3>个人信息</h3>
        <p>电话：{{ userInfo.phone }}</p>
        <p>性别：{{ userInfo.gender }}</p>
        <p>地址：{{ userInfo.address }}</p>
      </div>
      <!-- 操作按钮 -->
      <div :class="footer-buttons">
        <el-button type="primary" @click="openCompleteDialog" class="action-button">完善信息</el-button>
        <el-button type="default" @click="openPasswordDialog" class="action-button">修改密码</el-button>
      </div>
    </el-card>

    <!-- 完善信息弹窗 -->
    <el-dialog
      title="完善信息"
      v-model="showCompleteDialog"
      :width="isMobile ? '90%' : '30%'"
      :align-center="!isMobile"
    >
      <el-form
        :model="modifyForm"
        ref="modifyForm"
        :label-width="isMobile ? 'auto' : '80px'"
        class="complete-form"
      >
        <el-form-item
          label="电话"
          prop="phone_number"
          :style="isMobile ? '' : 'margin-top: 20px'"
        >
          <el-input
            v-model="modifyForm.phone"
            placeholder="电话"
            :style="isMobile ? 'width: 100%' : 'width: 300px'"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="性别"
          prop="gender"
          :style="isMobile ? '' : 'margin-top: 20px'"
        >
          <el-radio-group v-model="modifyForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
            <el-radio label="不愿透露">不愿透露</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="地址"
          prop="address"
          :style="isMobile ? '' : 'margin-top: 20px'"
        >
          <el-input
            v-model="modifyForm.address"
            placeholder="地址"
            :style="isMobile ? 'width: 100%' : 'width: 300px'"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div v-if="!isMobile" :class="dialog-footer">
          <el-button @click="showCompleteDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
        <div v-else :class="dialog-footer-mobile">
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog
      title="修改密码"
      v-model="showPasswordDialog"
      :width="isMobile ? '90%' : '30%'"
      :align-center="!isMobile"
    >
      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordForm"
        :label-width="isMobile ? 'auto' : '80px'"
        class="password-form"
      >
        <el-form-item label="旧密码" prop="password">
          <el-input
            v-model="passwordForm.password"
            placeholder="请输入密码"
            :style="isMobile ? 'width: 100%' : 'width: 300px'"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item
          label="新密码"
          prop="new_password"
          :style="isMobile ? '' : 'margin-top: 20px'"
        >
          <el-input
            v-model="passwordForm.new_password"
            placeholder="6-16位，至少包括字母和数字"
            :style="isMobile ? 'width: 100%' : 'width: 300px'"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item
          label="重复密码"
          prop="repassword"
          :style="isMobile ? '' : 'margin-top: 20px'"
        >
          <el-input
            v-model="passwordForm.repassword"
            placeholder="请再次输入密码"
            :style="isMobile ? 'width: 100%' : 'width: 300px'"
            show-password
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div v-if="!isMobile" :class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
        <div v-else :class="dialog-footer-mobile">
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import HeaderComponent from "@/components/HeaderComponent.vue";
import CryptoJS from 'crypto-js';
import Cookies from "js-cookie";
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  components: {
    HeaderComponent,
  },
  data() {
    const checkPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.new_password) {
        callback(new Error('两次密码不一致'));
      } else {
        callback();
      }
    };
    return {
      username: "", // 示例用户名
      userInfo: {
        email: "",
        phone: "暂未填充",
        gender: "不愿透露",
        address: "暂未填充",
      },
      showCompleteDialog: false,
      showPasswordDialog: false,
      modifyForm: {
        phone: '',
        gender: '',
        address: '',
      },
      passwordForm: {
        password: '',
        new_password: '',
        repassword: '',
      },
      passwordRules: {
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        new_password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{6,16}$/, message: '密码必须为6-16位，且同时包含字母和数字', trigger: 'blur' }
        ],
        repassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur'},
          { validator: checkPassword, message: '两次密码不一致', trigger: 'blur'}
        ]
      },
      isMobile: false,
    };
  },
  mounted() {
    this.getUserInfo();
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
      this.$router.push("/user/login"); // 跳转到登录页
    },
    jumpSearch() {
      this.$router.push("/search"); // 跳转到登录页
    },
    submitForm() {
      // 表单校验
      if (this.showPasswordDialog) {
        this.$refs["passwordForm"].validate((valid) => {
          if (!valid) {
            ElMessage.error("修改失败，请检查修改信息");
            return;
          } else {
            this.handlePassword();
          }
        });
      } else {
        this.handleProfile();
      }
    },
    getUserInfo() {
      const token = Cookies.get('token');
      if (!token) {
        return;
      }
      axios.post("/user/getprofile",
        {
          "authorization": token
        })
        .then(response => {
          if (response.data.code === 0) {
            this.username = response.data.payload.username;
            this.userInfo.email = response.data.payload.email;
            this.userInfo.phone = response.data.payload.phone;
            this.userInfo.gender = response.data.payload.gender;
            this.userInfo.address = response.data.payload.address;
            if (this.userInfo.phone === null) {
              this.userInfo.phone = "暂未填充";
            }
            if (this.userInfo.gender === null || this.userInfo.gender == '0') {
              this.userInfo.gender = "不愿透露";
            }
            if (this.userInfo.gender == '1') {
              this.userInfo.gender = "男";
            }
            if (this.userInfo.gender == '2') {
              this.userInfo.gender = "女";
            }
            if (this.userInfo.address === null) {
              this.userInfo.address = "暂未填充";
            }
          } else {
            ElMessage.error(response.data.err);
            return;
          }
        })
    },
    handlePassword() {
      const encryptedPassword = CryptoJS.SHA256(this.passwordForm.password).toString();
      const encryptedNewPassword = CryptoJS.SHA256(this.passwordForm.new_password).toString();
      axios.post("/user/password",
        {
          "username": this.username,
          "password": encryptedPassword,
          "newpassword": encryptedNewPassword
        })
        .then(response => {
          if (response.data.code === 0) {
            ElMessage.success("修改密码成功");
            this.showPasswordDialog = false;
          } else {
            ElMessage.error(response.data.err);
            return;
          }
        })
        .catch(error => {
          console.log(error);
        })
    },
    handleProfile() {
      const token = Cookies.get('token');
      console.log(this.modifyForm);
      var postgender = "0";
      if (this.modifyForm.gender == "男") {
        postgender = "1";
      }
      if (this.modifyForm.gender == "女") {
        postgender = "2";
      }
      console.log(postgender);
      axios.post("/user/changeprofile",
        {
          "phone": this.modifyForm.phone,
          "gender": postgender,
          "address": this.modifyForm.address,
          "authorization": token
        })
        .then(response => {
          if (response.data.code === 0) {
            ElMessage.success("修改信息成功");
            this.showCompleteDialog = false;
            this.getUserInfo();
          } else {
            ElMessage.error(response.data.err);
            return;
          }
        })
    },
    openCompleteDialog() {
      this.showCompleteDialog = true;
    },
    openPasswordDialog() {
      this.showPasswordDialog = true;
    },
  },
};
</script>

<style scoped>

.main {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  min-height: 100%;
  height: auto;
  background-color: #ffffff;
  overflow-y: hidden;
}

.profile-card {
  margin-top: 5%;
  width: 30%;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 20px;
}

.user-info {
  text-align: left;
}

.user-info h2 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.user-info .email {
  margin: 5px 0 0 0;
  font-size: 14px;
  color: gray;
}

.info-section {
  text-align: left;
}

.info-section h3 {
  margin-bottom: 10px;
  font-size: 18px;
  font-weight: bold;
}

.info-section p {
  margin-bottom: 10px;
  font-size: 13px;
}

.title {
  background-color: #f1eeee;
  height: 60px;
  width: 100%;
}

.search-button {
  margin-top: 20px;
  margin-left: auto;
  margin-right: 50px;
}

/* 手机端样式 */
.profile-card-mobile {
  margin-top: 5%;
  width: 80%;
  padding: 10px;
  text-align: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.header-mobile {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 10px;
}

.footer-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  align-items: center;
}

.dialog-footer-mobile {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}

.avatar-mobile {
  width: 40%;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 5px 0;
}

.user-info-mobile {
  display: flex;
  flex-direction: column;
  align-items: center; /* 用户信息居中 */
  text-align: center;  /* 确保文本居中 */
  margin-top: 10px; /* 增加间距 */
}

.user-info-mobile h2 {
  font-size: 18px;
  font-weight: bold;
  margin: 5px 0;
}

.user-info-mobile .email {
  font-size: 14px;
  color: gray;
  margin: 5px 0;
}

.action-button {
  width: 30%; /* 设置按钮宽度一致 */
}

</style>
