# 使用 Python 3.9 官方镜像作为基础镜像
FROM python:3.9-slim

# 安装 OpenJDK 17
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip

# 下载并安装 Google Chrome
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install ./google-chrome-stable_current_amd64.deb -y

# 下载与之匹配的 chromedriver
RUN wget -O /tmp/chromedriver.zip https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.204/linux64/chromedriver-linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    rm /tmp/chromedriver.zip

# 设置 chromedriver 路径到 PATH 环境变量中
ENV PATH="/usr/local/bin:${PATH}"
    
# 定义工作目录
WORKDIR /app

# 将后端项目的 JAR 包复制到容器中
COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# 安装 Python 环境中的依赖
RUN pip install selenium bs4

# 暴露后端服务端口
EXPOSE 8081

# 启动后端服务
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
