# 使用 Node.js 镜像作为基础镜像
FROM node:20 AS build

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制项目文件
COPY . .

# 构建 Vue 项目
RUN npm run build

# 使用 Nginx 镜像来部署静态文件
FROM nginx:alpine

# 删除默认的 Nginx 配置
RUN rm -rf /usr/share/nginx/html/*

# 复制构建好的文件到 Nginx 默认的网页目录
COPY --from=build /app/dist /usr/share/nginx/html

# 暴露端口 80
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
