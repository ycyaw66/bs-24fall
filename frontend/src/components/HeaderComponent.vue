<template>
  <el-header class="title">
    <div style="margin-top: 12px; display: inline-block;">
      <img src="../assets/logo.png" style="margin-right: 20px; height: 40px; vertical-align: middle;" />
      <span style="font-size: large; font-family: 'Microsoft YaHei'; color: black; font-weight: bold;">比价猫</span>
      <span style="margin-left: 40px; color:rgba(0, 0, 0, 0.2)">浙江大学B/S体系软件设计课程作业</span>
    </div>
    <div style="float: right; margin-top: 18px; margin-right: 20px; font-size: 16px; display: inline-block;">
      <template v-if="isLoggedIn">
        <span>当前用户：{{ username }}</span>
        <el-dropdown>
          <el-button type="text" style="margin-left: 10px; margin-top: -4px; font-size: 16px; color: black">
            <span>▼</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="jumpProfile">个人资料</el-dropdown-item>
              <el-dropdown-item @click="jumpCollection">我的收藏</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-link type="primary" style="font-size: 16px;" :underline="false" @click="jumpLogin">请先登录</el-link>
      </template>
    </div>
  </el-header>
</template>

<script>
import Cookies from "js-cookie";
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      isLoggedIn: false,
      username: "",
    };
  },
  mounted() {
    this.checkLoginStatus();
  },
  methods: {
    jumpLogin() {
      this.$router.push("/user/login");
    },
    jumpCollection() {
      this.$router.push("/collection");
    },
    jumpProfile() {
      this.$router.push("/profile");
    },
    checkLoginStatus() {
      const token = Cookies.get('token');
      if (!token) {
        this.isLoggedIn = false;
        return;
      }
      axios.post("/user/getprofile",
        {
          "authorization": token
        })
        .then(response => {
          if (response.data.code === 0) {
            this.isLoggedIn = true;
            this.username = response.data.payload.username;
          } else {
            ElMessage.error(response.data.err);
            return;
          }
        })
    },
    handleLogout() {
      const token = Cookies.get('token');
      axios.post("/user/logout",
        {
          "authorization": token
        })
        .then(response => {
          if (response.data.code === 0) {
            ElMessage.success("注销成功");
            Cookies.remove('token');
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
  },
};
</script>

<style scoped>
.title {
  background-color: #f1eeee;
  height: 60px;
  width: 100%;
}
</style>
