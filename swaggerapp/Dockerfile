# 第一阶段：编译 (使用 Node.js 环境)
FROM node:14.20 AS build
WORKDIR /app

# 1. 安装 Angular CLI
RUN npm install -g @angular/cli@11.0.2

# 2. 复制依赖清单并安装
COPY package*.json ./
RUN npm install

# 3. 复制源码并构建
COPY . .
RUN ng build --prod

# 第二阶段：部署 (使用轻量级 Nginx 服务器)
FROM nginx:alpine

# 4. 从构建阶段复制生成的静态文件到 Nginx
# 注意：Angular 构建后的文件夹名通常在 angular.json 里定义，一般是项目名
COPY --from=build /app/dist/swaggerapp /usr/share/nginx/html

# 5. 暴露 80 端口
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]