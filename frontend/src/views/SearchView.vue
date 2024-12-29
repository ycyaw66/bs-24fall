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
        v-model="searchQuery"
        placeholder="请输入商品名称"
        clearable
      >
        <template #prepend>
          <el-select v-model="platform" placeholder="选择平台" style="width: 100px">
            <el-option label="京东" value="jd" />
            <el-option label="苏宁易购" value="suning" />
          </el-select>
        </template>
        <template #append>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner"></div>
      <div class="loading-text">搜索中...</div>
    </div>

    <div v-if="itemList.length > 0" class="item-list">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item, index) in itemList" :key="index">
          <el-card shadow="hover" class="item-card">
            <img :src="item.picImg" alt="商品图片" class="item-image" />
            <div class="item-info">
              <h3>{{ item.productTitle }}</h3>
              <p class="price">
                <span class="price-symbol">¥</span>
                <span class="price-integer">{{ formatPrice(item.productPrice).integer }}.</span>
                <span class="price-decimal">{{ formatPrice(item.productPrice).decimal }}</span>
              </p>
            </div>
            <div class="item-links">
              <a @click="openItemDetail(item)" class="view-detail">查看商品详情</a>
              <a :href="item.productLink" target="_blank" class="jump-link">跳转原商品链接</a>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div v-if="itemList.length === 0 && !isLoading && hasSearched" class="no-results-message">
      <p>抱歉，没有找到与关键词相关的商品</p>
    </div>

    <!-- 商品详情弹窗 -->
    <el-dialog
      v-model="showItemDetail"
      title="商品详情"
      width="50%"
    >
      <div class="item-detail">
        <img :src="selectedItem.picImg" alt="商品图片" class="item-detail-image" />
        <div class="item-detail-info">
          <h3>{{ selectedItem.productTitle }}</h3>
          <p class="item-detail-price">
            <span class="price-symbol">¥</span>
            <span class="price-integer">{{ formatPrice(selectedItem.productPrice).integer }}.</span>
            <span class="price-decimal">{{ formatPrice(selectedItem.productPrice).decimal }}</span>
          </p>
        </div>
        <el-button :type="selectedItem.isliked === '0' ? 'primary' : 'danger'" @click="toggleLike(selectedItem)" style="width: 80px">{{ selectedItem.isliked === "0" ? "收藏" : "取消收藏" }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>


<script>
import HeaderComponent from "@/components/HeaderComponent.vue";
import Cookies from "js-cookie";
import axios from "axios";
import { ElMessage } from "element-plus";

export default {
  components: {
    HeaderComponent,
  },
  data() {
    return {
      searchQuery: "",
      isLoggedIn: false, // 登录状态
      itemList: [], // 搜索返回的商品列表
      isLoading: false, // 是否正在加载
      hasSearched: false, // 是否已进行搜索
      platform: "jd", // 默认搜索京东
      showItemDetail: false,
      selectedItem: {
        picImg: "",
        productTitle: "",
        productPrice: "",
        productLink: "",
        isliked: "0" // isliked: 0 表示未收藏，1 表示收藏
      },
    };
  },
  mounted() {
    this.checkLoginStatus();
  },
  methods: {
    formatPrice(price) {
      if (!price) {
        return {
          integer: "0",
          decimal: "00"
        };
      }
      const priceWithoutSymbol = price.replace('¥', '');
      const [integer, decimal] = parseFloat(priceWithoutSymbol).toFixed(2).split('.');
      return {
        integer: integer,
        decimal: decimal
      };
    },
    jumpLogin() {
      this.$router.push("/user/login");
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
    handleSearch() {
      if (!this.isLoggedIn) {
        this.$message.error("请先点击右上角登录！");
        return;
      }
      if (this.searchQuery.trim()) {
        console.log(`搜索：${this.searchQuery}`);
        this.hasSearched = true;
        this.fetchItems();
      } else {
        this.$message.warning("请输入搜索内容！");
      }
    },
    fetchItems() {
      console.log("fetchItems");
      console.log(this.searchQuery);
      this.isLoading = true;
      axios.post("/goods/search",
        {
          "keyword": this.searchQuery,
          "platform": this.platform,
          "authorization": Cookies.get("token")
        })
        .then(response => {
          if (response.data.code === 0) {
            // 如果返回数据成功，更新商品列表
            this.itemList = response.data.payload.goods.filter(item => item.productPrice && item.productPrice.trim() !== '');
            console.log(this.itemList);
          } else {
            ElMessage.error(response.data.err);
          }
        })
        .catch(error => {
          ElMessage.error("出错了，请稍后重试");
          console.error(error);
        })
        .finally(() => {
          this.isLoading = false;
        })
    },
    openItemDetail(item) {
      this.selectedItem = item;
      console.log(this.selectedItem);
      axios.post("/goods/isliked",
        {
          username: this.username,
          goods: item.productLink,
          authorization: Cookies.get("token")
        })
        .then(response => {
          if (response.data.code === 0) {
            console.log(response.data.payload.isliked);
            this.selectedItem.isliked = response.data.payload.isliked;
            this.showItemDetail = true;
          } else {
            ElMessage.error(response.data.err);
          }
        })
        .catch(error => {
          console.error(error);
          this.$message.error("出错了，请稍后重试");
        });
    },
    handleCloseItemDetail() {
      this.showItemDetail = false;
      this.selectedItem = {};
    },
    toggleLike(item) {
      const operation = item.isliked === "0" ? "1" : "0";
      axios.post("/goods/like", 
        {
          username: this.username,
          goods: item.productLink,
          operation: operation,
          authorization: Cookies.get("token")
        })
        .then(response => {
          if (response.data.code === 0) {
            item.isliked = operation;
            const message = item.isliked === "1" ? "收藏成功" : "取消收藏成功";
            this.$message.success(message);
          } else {
            ElMessage.error(response.data.err);
          }
        })
        .catch(error => {
          console.error(error);
          this.$message.error("出错了，请稍后重试");
        });
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
  margin-bottom: 20px;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.item-info {
  text-align: left;
}

.item-info h3 {
  font-size: 18px;
  line-height: 1.5;
  min-height: 3em;
}

.item-info p {
  font-size: 16px;
}

.item-info a {
  font-size: 14px;
  color: #42b983;
  text-decoration: none;
  display: inline-block;
  margin-top: 10px;
}

.item-info h3, .item-info p {
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 限制显示2行文字 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-symbol {
  font-size: 14px;
  color: #ff6600;
}

.price-integer {
  font-size: 24px;
  color: #ff6600;
}

.price-decimal {
  font-size: 14px;
  color: #ff6600;
}

.loading-overlay {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 1);
  z-index: 1000;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 6px solid rgba(0, 0, 0, 0.1);
  border-top-color: #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 10px;
  font-size: 16px;
  color: #333;
}

.no-results-message {
  margin-top: 20px;
  font-size: 16px;
  color: #888;
  text-align: center;
}

.item-detail {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item-detail-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}

.item-detail-info {
  flex: 1;
  margin-left: 20px;
}

.item-detail-price {
  font-size: 20px;
  color: #ff6600;
  margin-top: 10px;
}

.item-links {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.item-links a {
  font-size: 14px;
  color: #42b983;
  text-decoration: none;
}

.item-links .jump-link {
  color: #007bff;
}

.item-links .jump-link:hover {
  text-decoration: underline;
}

.view-detail {
  font-size: 14px;
  color: #42b983;
  text-decoration: none;
  cursor: pointer; /* 鼠标变为手型 */
}

.view-detail:hover {
  text-decoration: underline;
}


</style>