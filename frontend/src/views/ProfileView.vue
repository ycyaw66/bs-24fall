<template>
  <div class="main">
    <HeaderComponent
      :isLoggedIn="isLoggedIn"
      :username="username"
      @logout="handleLogout"
    />
    <el-button type="primary" @click="jumpSearch" class="search-button">返回搜索</el-button>
    <el-card class="profile-card">
      <!-- 用户头像和用户名 -->
      <div class="header">
        <el-avatar
          src="https://via.placeholder.com/100"
          size="100"
          class="avatar"
        />
        <div class="user-info">
          <h2>{{ this.username }}</h2>
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
      <div class="footer-buttons">
        <el-button type="primary" @click="openCompleteDialog">完善信息</el-button>
        <el-button type="default" @click="openPasswordDialog">修改密码</el-button>
      </div>
    </el-card>

    <!-- 完善信息弹窗 -->
    <el-dialog title="完善信息" v-model="showCompleteDialog" width="30%" align-center>
      <el-form :model="modifyForm" ref="modifyForm" label-width="80px">
        <el-form-item label="电话" prop="phone_number" style="margin-top: 20px">
          <el-input v-model="modifyForm.phone" placeholder="电话" style="width: 300px"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender" style="margin-top: 20px">
          <el-radio-group v-model="modifyForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
            <el-radio label="不愿透露">不愿透露</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地址" prop="address" style="margin-top: 20px">
          <el-input v-model="modifyForm.address" placeholder="地址" style="width: 300px"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="showCompleteDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="showPasswordDialog" width="30%" align-center>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm"  label-width="80px">
        <el-form-item label="旧密码" prop="password">
          <el-input v-model="passwordForm.password" placeholder="请输入密码" style="width: 300px" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="new_password" style="margin-top: 20px">
          <el-input v-model="passwordForm.new_password" placeholder="6-16位，至少包括字母和数字" style="width: 300px" show-password></el-input>
        </el-form-item>
        <el-form-item label="重复密码" prop="repassword" style="margin-top: 20px">
          <el-input v-model="passwordForm.repassword" placeholder="请再次输入密码" style="width: 300px" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
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
      }
    };
  },
  mounted() {
    this.getUserInfo();
  },
  methods: {
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
</style>
