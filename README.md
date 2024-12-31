# bs-24fall

#### 项目运行指南：

项目已经打包成 Docker，只需安装并打开 Docker Desktop，然后在项目根目录下运行命令 `docker-compose up --build -d`，即可创建相应容器运行项目。

运行后可访问 `localhost:5173` 进入网站的登陆界面。

#### 后端打包指南：

后端使用 Maven 管理 SpringBoot 项目，如需打包后端，需安装 Maven 后，在后端根目录下运行 `mvn clean package -DskipTests` 打包项目，打包好的文件位于 `/target` 目录下。

后端的数据库配置依赖于容器内的数据库，不能独立运行，修改代码后需重新打包并将打包文件置于后端根目录下，再次执行上述 `docker-compose` 命令重新部署。

#### 前端运行指南：

前端使用 Node.js 进行管理，如需独立运行前端，在前端根目录下先执行 `npm install` 安装所需依赖，然后执行 `npm run serve` 即可使用开发模式运行前端。