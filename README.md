# 洗衣店管理系统

一个前后端分离的洗衣店管理系统，支持管理员、顾客、店家三种角色，提供店铺管理、洗衣服务、订单管理、交流区等功能。

## 🛠 技术栈

- **Frontend**: Vue 2 + Vue Router + Vuex + Element UI + Axios
- **Backend**: Spring Boot 3 + MyBatis Plus + JWT
- **Database**: MySQL 8.0
- **DevOps**: Docker + Docker Compose + Nginx

## 🚀 启动指南 (How to Run)

### 前置要求

- 确保 Docker Desktop 已安装并启动

### 一键启动

1. 在项目根目录执行：

```bash
docker compose up --build
```

2. 等待所有容器启动完成（首次构建可能需要几分钟）

3. 访问系统

## 🔗 服务地址 (Services)

| 服务 | 地址 |
|------|------|
| 前台首页 | http://localhost:3000 |
| 后台管理 | http://localhost:3000/#/admin |
| API 文档 (Swagger) | http://localhost:8000/api/swagger-ui.html |
| 数据库 | localhost:3306 |

## 🧪 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 顾客 | customer1 | 123456 |
| 顾客 | customer2 | 123456 |
| 店家 | shop1 | 123456 |
| 店家 | shop2 | 123456 |

## 📋 功能模块

### 后台功能

#### 管理员角色
- 首页（数据统计）
- 个人中心
- 顾客管理
- 店家管理
- 店铺信息管理
- 衣服类型管理
- 洗衣信息管理
- 订单信息管理
- 订单进度管理
- 交流区管理
- 系统公告管理
- 在线客服

#### 顾客角色
- 首页
- 个人中心
- 店铺信息浏览
- 洗衣服务下单
- 订单信息管理
- 订单进度查看
- 在线客服

#### 店家角色
- 首页
- 个人中心
- 我的店铺管理
- 衣服类型管理
- 洗衣信息管理
- 订单信息管理
- 订单进度管理

### 前台功能

- 首页（响应式布局）
- 店铺信息浏览
- 洗衣服务详情
- 交流区（发帖、评论、点赞）
- 系统公告
- 在线客服
- 用户登录/注册

## 📁 项目结构

```
taskId209/
├── backend/                    # 后端项目
│   ├── src/main/java/com/laundry/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── exception/         # 异常处理
│   │   ├── interceptor/       # 拦截器
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── service/           # 服务层
│   │   └── util/              # 工具类
│   ├── src/main/resources/
│   │   └── application.yml    # 配置文件
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── assets/            # 静态资源
│   │   ├── router/            # 路由配置
│   │   ├── store/             # Vuex 状态管理
│   │   └── views/             # 页面组件
│   │       ├── admin/         # 后台管理页面
│   │       └── front/         # 前台页面
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── mysql/
│   └── init.sql               # 数据库初始化脚本
├── docker-compose.yml         # Docker 编排配置
└── README.md
```

## 🗃️ 数据库设计

| 表名 | 描述 |
|------|------|
| user | 用户表（管理员、顾客、店家） |
| shop | 店铺信息表 |
| clothes_type | 衣服类型表 |
| laundry_info | 洗衣信息表 |
| order_info | 订单信息表 |
| order_progress | 订单进度表 |
| discussion | 交流区帖子表 |
| comment | 评论表 |
| announcement | 系统公告表 |
| customer_service | 在线客服消息表 |

## 🔧 开发说明

### 本地开发

**后端开发：**
```bash
cd backend
mvn spring-boot:run
```

**前端开发：**
```bash
cd frontend
npm install
npm run serve
```

### 环境变量

后端环境变量：
- `MYSQL_HOST`: 数据库主机（默认: localhost）
- `MYSQL_PORT`: 数据库端口（默认: 3306）
- `MYSQL_DATABASE`: 数据库名（默认: laundry_db）
- `MYSQL_USER`: 数据库用户（默认: root）
- `MYSQL_PASSWORD`: 数据库密码（默认: root123）

## 📝 API 说明

系统采用 RESTful API 设计，主要接口：

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/user/info` - 获取当前用户信息
- `GET /api/shop/page` - 分页查询店铺
- `GET /api/laundry-info/page` - 分页查询洗衣服务
- `POST /api/order` - 创建订单
- `GET /api/order/page` - 分页查询订单
- `GET /api/front/*` - 前台公开接口（无需登录）

完整 API 文档请访问 Swagger UI：http://localhost:8080/api/swagger-ui.html

## 🎨 界面预览

系统采用现代化 UI 设计：
- 渐变色主题
- 卡片式布局
- 响应式适配
- 流畅的动画效果

## ⚠️ 注意事项

1. 首次启动时，数据库会自动初始化并填充演示数据
2. 系统已配置国内 npm 和 Maven 镜像源，加速依赖下载
3. 前端通过 Nginx 反向代理访问后端 API
4. JWT Token 有效期为 24 小时

## 📄 License

MIT License
