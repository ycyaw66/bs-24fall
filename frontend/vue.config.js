const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 5173,
    proxy: 'http://localhost:8081',
  }
})
