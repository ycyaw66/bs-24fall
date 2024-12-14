<template>
  <div class="main">
    <HeaderComponent
      :isLoggedIn="isLoggedIn"
      :username="username"
      @logout="handleLogout"
    />
    <div class="logo-container" style="margin-top: 20px">
      <img src="../assets/logo.png" alt="Logo" class="logo" />
    </div>
    <div class="search-container">
      <el-input
        placeholder="请输入商品名称"
        v-model="searchQuery"
        class="search-box"
        clearable
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>
    <div class="item-list" v-if="itemList.length > 0">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item, index) in itemList" :key="index">
          <el-card shadow="hover" class="item-card">
            <img :src="item.imageUrl" alt="商品图片" class="item-image" />
            <div class="item-info">
              <h3>{{ item.name }}</h3>
              <p>价格: ¥{{ item.price }}</p>
              <p>平台: {{ item.platform }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import HeaderComponent from "@/components/HeaderComponent.vue";

export default {
  components: {
    HeaderComponent,
  },
  data() {
    return {
      searchQuery: "",
      isLoggedIn: false, // 登录状态
      username: "", // 当前登录的用户名
      itemList: [], // 搜索返回的商品列表
    };
  },
  methods: {
    jumpLogin() {
      this.$router.push("/user/login");
    },
    jumpProfile() {
      this.$router.push("/profile");
    },
    handleSearch() {
      if (!this.isLoggedIn) {
        this.$message.error("请先点击右上角登录！");
        return;
      }
      if (this.searchQuery.trim()) {
        console.log(`搜索：${this.searchQuery}`);
        this.fetchItems(); // 模拟调用后端获取商品列表
        // 添加搜索逻辑，例如导航到搜索结果页或调用 API。
      } else {
        this.$message.warning("请输入搜索内容！");
      }
    },
    fetchItems() {
      // 模拟后端返回数据
      this.itemList = [
        { name: "商品A", imageUrl: "https://via.placeholder.com/150", price: 100, platform: "淘宝" },
        { name: "商品B", imageUrl: "https://via.placeholder.com/150", price: 200, platform: "京东" },
        { name: "商品C", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品D", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品E", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品F", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品G", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品H", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品I", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品J", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品K", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
        { name: "商品L", imageUrl: "https://via.placeholder.com/150", price: 150, platform: "淘宝" },
      ];
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
  overflow-y: scroll;
}

.logo-container {
  margin-bottom: 20px;
}

.logo {
  height: 100px;
  width: auto;
}

.search-container {
  width: 80%;
  max-width: 600px;
}

.search-box {
  width: 100%;
  height: 50px;
  font-size: 16px;
}

.title {
  background-color: #f1eeee;
  height: 60px;
  width: 100%;
}

.item-list {
  margin-top: 20px;
  width: 80%;
}

.item-card {
  text-align: center;
}

.item-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  margin-bottom: 10px;
}

.item-info {
  text-align: left;
}


</style>
