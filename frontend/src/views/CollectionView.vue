<template>
  <div class="main">
    <HeaderComponent
      :isLoggedIn="isLoggedIn"
      :username="username"
      @logout="handleLogout"
    />
    <el-button type="primary" @click="jumpSearch" class="search-button">返回搜索</el-button>

    <div class="collection-container">
      <h2>我的收藏</h2>
      <div v-if="collections.length > 0" class="product-list">
        <div v-for="(product, index) in collections" :key="index" class="product-card">
          <img :src="product.image" alt="product image" class="product-image" />
          <div class="product-info">
            <h3 class="product-title">{{ product.name }}</h3>
            <p class="product-price">价格: ¥{{ product.price }}</p>
            <el-button type="danger" @click="removeFromCollection(product.id)">移除收藏</el-button>
          </div>
        </div>
      </div>
      <div v-else class="no-collection">
        <p>暂无收藏的商品</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from "element-plus";
import HeaderComponent from "@/components/HeaderComponent.vue";

export default {
  components: {
    HeaderComponent,
  },
  data() {
    return {
      username: "张三", // 示例用户名
      isLoggedIn: false, // 登录状态
      collections: [], // 收藏的商品列表
    };
  },
  mounted() {
    this.checkLoginStatus();
    this.fetchCollections(); // 获取收藏的商品
  },
  methods: {
    jumpLogin() {
      this.$router.push("/user/login"); // 跳转到登录页
    },
    jumpSearch() {
      this.$router.push("/search"); // 跳转到搜索页
    },
    checkLoginStatus() {
      const user = "ycyaw"; // 测试用，假设用户已登录
      if (user) {
        this.isLoggedIn = true;
        this.username = user;
      } else {
        this.isLoggedIn = false;
      }
    },
    handleLogout() {
      this.isLoggedIn = false;
      this.jumpLogin();
    },
    fetchCollections() {
      // 示例数据，实际情况中调用后端 API 获取收藏数据
      this.collections = [
        { id: 1, name: "商品1", price: 100, image: "https://via.placeholder.com/150" },
        { id: 2, name: "商品2", price: 200, image: "https://via.placeholder.com/150" },
      ];
    },
    removeFromCollection(productId) {
      // 示例处理，实际情况中调用后端 API
      this.collections = this.collections.filter((product) => product.id !== productId);
      ElMessage.success("商品已移除收藏");
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

.collection-container {
  width: 80%;
  margin-top: 20px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.product-card {
  width: 200px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 10px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  margin-top: 10px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.product-price {
  color: #ff5722;
  margin-bottom: 10px;
}

.no-collection {
  text-align: center;
  font-size: 16px;
  color: gray;
}

.search-button {
  margin-top: 20px;
  align-self: flex-end;
  margin-right: 50px;
}
</style>
