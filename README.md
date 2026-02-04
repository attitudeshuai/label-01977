# 🌸 心情随笔 - 日记记录系统

> 一个现代化的个人日记记录 Web 应用，帮助用户记录日常生活、心情和思考。

## ✨ 功能特性

- 📝 **日记管理** - 创建、编辑、删除、查看日记
- 📅 **日历视图** - 直观展示每日心情和天气
- 😊 **心情标签** - 记录当日心情 (开心/平静/难过/愤怒/焦虑)
- ☀️ **天气记录** - 记录当日天气 (晴/多云/阴/雨/雪)
- ✏️ **富文本编辑** - Tiptap 专业排版工具
- 📊 **数据统计** - 日记总数、本月统计、心情分布
- 🔐 **用户认证** - JWT 安全认证，数据隔离
- 🐳 **Docker 部署** - 一键启动，开箱即用

## 🛠️ 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2 + Spring Security + JWT |
| 数据库 | MySQL 8.0 + Spring Data JPA |
| 前端 | Vue 3 + Vite + Tailwind CSS |
| 富文本 | Tiptap 2 |
| 状态管理 | Pinia |
| 容器化 | Docker + Docker Compose |

## 🚀 快速开始

### 前置要求

- Docker & Docker Compose

### 一键启动

```bash
# 克隆项目
cd daily-journal

# 启动所有服务
docker compose up --build

# 或后台启动
docker compose up --build -d
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 🌐 前端 | http://localhost:3000 |
| 🔌 后端 API | http://localhost:8081/api |
| 💾 MySQL | localhost:3307 |

### 测试账号

系统初始化时自动创建测试账号，可直接登录体验：

| 项目 | 值 |
|------|-----|
| 用户名 | `test` |
| 密码 | `123456` |
| 邮箱 | test@example.com |

> 测试账号预置了 **15 篇** 不同日期的示例日记，涵盖多种心情和天气，方便体验日历视图和统计功能

### 停止服务

```bash
docker compose down
```

### 验证部署

```bash
# 检查容器状态
docker ps | grep daily-journal

# 健康检查
curl http://localhost:8081/api/health

# 测试登录
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

## 📖 API 接口

### 认证接口

| Method | Endpoint | 描述 |
|--------|----------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/auth/me` | 获取当前用户 |

### 日记接口

| Method | Endpoint | 描述 |
|--------|----------|------|
| GET | `/api/journals` | 获取日记列表 |
| GET | `/api/journals/:id` | 获取日记详情 |
| POST | `/api/journals` | 创建日记 |
| PUT | `/api/journals/:id` | 更新日记 |
| DELETE | `/api/journals/:id` | 删除日记 |
| GET | `/api/journals/search?q=` | 搜索日记 |

### 日历接口

| Method | Endpoint | 描述 |
|--------|----------|------|
| GET | `/api/calendar/:year/:month` | 获取月历数据 |

### 统计接口

| Method | Endpoint | 描述 |
|--------|----------|------|
| GET | `/api/stats/overview` | 获取统计概览 |

## 📁 项目结构

```
daily-journal/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/dailyjournal/
│   │       ├── config/      # 配置类
│   │       ├── controller/  # 控制器
│   │       ├── dto/         # 数据传输对象
│   │       ├── entity/      # 实体类
│   │       ├── repository/  # 数据访问层
│   │       ├── security/    # 安全配置
│   │       └── service/     # 业务逻辑层
│   └── Dockerfile
├── frontend/                # Vue 3 前端
│   ├── src/
│   │   ├── components/      # 组件
│   │   ├── composables/     # 组合式函数
│   │   ├── router/          # 路由
│   │   ├── stores/          # 状态管理
│   │   ├── styles/          # 样式
│   │   └── views/           # 页面
│   └── Dockerfile
├── docker/                  # Docker 配置
│   └── mysql/init.sql       # 数据库初始化
├── docs/                    # 文档
│   ├── Requirements.md      # 需求文档
│   ├── DesignSpec.md        # 设计规范
│   └── Roadmap.md           # 开发路线图
└── docker-compose.yml       # Docker 编排
```

## 🎨 设计理念

- **简约大气** - 去除干扰，专注写作
- **温暖可爱** - 柔和色彩，圆润设计
- **专注体验** - 沉浸式写作模式

## 📝 License

MIT License

---

Built with ❤️ by 心情随笔 using Alkaid-SOP
