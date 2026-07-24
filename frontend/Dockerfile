# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 配置 npm 镜像源
RUN npm config set registry https://registry.npmmirror.com

# 复制 package.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制源代码
COPY . .

# 构建
RUN npm run build

# 生产阶段
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# 删除默认配置
RUN rm -rf ./*

# 复制构建产物
COPY --from=builder /app/dist .

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
