<template>
  <div class="main" style="overflow-y: hidden;">
    <el-header class="title">
      <div style="margin-top: 12px; display: inline-block;">
        <img src="../assets/logo.png"
          style=" margin-right: 20px; height: 40px; vertical-align: middle;" />
        <span style="font-size: large; font-family: 'Microsoft YaHei';
            color: black; font-weight: bold;">比价猫</span>
        <span style="margin-left: 40px; color:rgba(0, 0, 0, 0.2)">浙江大学软件工程基础课程项目</span>
      </div>
    </el-header>
    <div class="logo-container" style="margin-top: 50px">
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
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: "",
    };
  },
  methods: {
    handleSearch() {
      if (this.searchQuery.trim()) {
        console.log(`搜索：${this.searchQuery}`);
        // 添加搜索逻辑，例如导航到搜索结果页或调用 API。
      } else {
        this.$message.warning("请输入搜索内容！");
      }
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
  background-color: #ffffff;
  height: 60px;
}
</style>
