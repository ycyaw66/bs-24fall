<template>
  <div class="main">
    <HeaderComponent
      :isLoggedIn="isLoggedIn"
      :username="username"
      @logout="handleLogout"
    />
    <el-button type="primary" @click="jumpSearch" class="search-button">返回搜索</el-button>

    <h2 class="page-title">我的收藏</h2>

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

    <!-- 没有收藏商品时的提示 -->
    <div v-if="itemList.length === 0 && hasFetched" class="no-results-message">
      <p>暂无收藏的商品</p>
    </div>

    <el-dialog
      v-model="showItemDetail"
      title="商品详情"
      width="50%"
      :before-close="checkCollections"
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
import { ElMessage } from "element-plus";
import HeaderComponent from "@/components/HeaderComponent.vue";
import axios from "axios";
import Cookies from "js-cookie";

export default {
  components: {
    HeaderComponent,
  },
  data() {
    return {
      username: "",
      isLoggedIn: false,
      itemList: [], // 收藏的商品列表
      hasFetched: false,
      showItemDetail: false,
      selectedItem: {
        picImg: "",
        productTitle: "",
        productPrice: "",
        productLink: "",
        isliked: "0"
      },
    };
  },
  mounted() {
    this.checkLoginStatus();
    this.fetchCollections();
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
    jumpSearch() {
      this.$router.push("/search");
    },
    checkLoginStatus() {
      const token = Cookies.get('token');
      if (!token) {
        this.isLoggedIn = false;
        return;
      }
      axios.post("/user/getprofile", {
        authorization: token,
      })
      .then((response) => {
        if (response.data.code === 0) {
          this.isLoggedIn = true;
          this.username = response.data.payload.username;
        } else {
          ElMessage.error(response.data.err);
        }
      })
      .catch((error) => {
        console.error("获取用户信息失败：", error);
        this.isLoggedIn = false;
      });
    },
    fetchCollections() {
      const token = Cookies.get("token");
      if (!token) {
        ElMessage.error("请先登录");
        this.jumpLogin();
        return;
      }
      axios.post("/goods/userlike", {
        username: this.username,
        authorization: token,
      })
      .then((response) => {
        if (response.data.code === 0) {
          this.itemList = response.data.payload.goods;
          this.hasFetched = true;
          console.log("收藏的商品列表：", this.itemList);
        } else {
          ElMessage.error(response.data.err);
        }
      })
      .catch((error) => {
        console.error(error);
        ElMessage.error("出错了，请稍后重试");
      });
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
            this.fetchCollections();  // 重新渲染页面
          } else {
            ElMessage.error(response.data.err);
          }
        })
        .catch(error => {
          console.error(error);
          this.$message.error("出错了，请稍后重试");
        });
    },
    checkCollections() {
      this.fetchCollections();
      this.showItemDetail = false;
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

.search-button {
  margin-top: 20px;
  align-self: flex-end;
  margin-right: 50px;
}

.page-title {
  font-size: 24px;
  margin-top: 20px;
}

.item-list {
  margin-top: 20px;
  width: 80%;
}

.item-card {
  text-align: center;
  margin-bottom: 20px;
  position: relative;  /* 使按钮能够定位到右下角 */
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
  -webkit-line-clamp: 2;
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

.no-results-message {
  margin-top: 20px;
  font-size: 16px;
  color: #888;
  text-align: center;
}

.remove-favorite-btn {
  position: absolute;
  bottom: 10px;
  right: 10px;
  padding: 6px 12px;
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
  cursor: pointer;
}

.view-detail:hover {
  text-decoration: underline;
}
</style>
