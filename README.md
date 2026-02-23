# Laolao Code - 在线编程考试判题系统

一个基于 Spring Boot 和 Vue 3 的在线编程考试与判题平台，支持代码在线编辑、实时判题、考试管理和学习小组等功能。

## 📋 项目简介

Laolao Code 是一个功能完善的在线编程考试系统，主要面向编程教学和考试场景。系统支持 Java 代码的在线编写、提交、编译和执行，通过 Docker 容器实现安全的代码沙箱环境，确保判题过程的安全性和隔离性。

## ✨ 主要功能

### 🔐 用户管理
- 用户注册与登录
- JWT Token 认证
- 用户信息管理

### 📝 考试管理
- 考试列表查看
- 考试信息查询
- 开始考试（支持断点续考）
- 考试题目展示
- 考试记录管理

### ⚖️ 代码判题
- 在线代码编辑器（Monaco Editor）
- 代码提交与编译
- 多测试用例判题
- 实时判题结果反馈
- 判题记录查询（简单记录和详细记录）
- 支持部分得分（根据通过测试用例数量）

### 👥 学习小组
- 小组列表查看
- 加入学习小组
- 小组详情查看（基础信息和考试信息）

### 🤖 AI 辅助
- 集成 DeepSeek AI API
- 智能代码提示与帮助

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.5.10
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.16
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **容器化**: Docker Java API
- **连接池**: Druid
- **工具库**: Lombok, MapStruct, Apache Commons Lang3

### 前端技术
- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite 7.2.4
- **UI 框架**: Tailwind CSS 4.1.18
- **组件库**: Reka UI
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **代码编辑器**: Monaco Editor
- **HTTP 客户端**: Axios
- **日期处理**: Day.js

## 📁 项目结构

```
laolao-code/
├── laolao-code/              # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/laolao/
│   │   │   │   ├── common/          # 公共模块
│   │   │   │   │   ├── context/     # 用户上下文
│   │   │   │   │   ├── docker/      # Docker 判题服务
│   │   │   │   │   ├── result/      # 统一返回结果
│   │   │   │   │   ├── security/    # 安全配置（JWT）
│   │   │   │   │   └── util/        # 工具类
│   │   │   │   ├── controller/      # 控制器层
│   │   │   │   ├── mapper/          # 数据访问层
│   │   │   │   ├── pojo/            # 实体类、DTO、VO
│   │   │   │   └── service/         # 业务逻辑层
│   │   │   └── resources/
│   │   │       ├── application.yaml        # 主配置文件
│   │   │       ├── application-dev.yaml    # 开发环境配置
│   │   │       └── mapper/                 # MyBatis XML 映射文件
│   │   └── test/                    # 测试代码
│   └── pom.xml                      # Maven 依赖配置
│
├── laolao-code-vue/          # 前端项目
│   ├── src/
│   │   ├── components/       # Vue 组件
│   │   │   ├── common/       # 公共组件
│   │   │   ├── ui/           # UI 组件库
│   │   │   └── user/         # 用户相关组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── utils/            # 工具函数
│   │   └── main.ts           # 入口文件
│   └── package.json           # 前端依赖配置
│
└── laolao_code.sql           # 数据库初始化脚本
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Docker**: 20.10+（用于代码判题）

### 后端部署

1. **克隆项目**
```bash
git clone <repository-url>
cd laolao-code/laolao-code
```

2. **配置数据库**
   - 创建 MySQL 数据库
   - 执行 `laolao_code.sql` 初始化数据库表结构

3. **配置应用**
   - 修改 `src/main/resources/application-dev.yaml`：
     ```yaml
     datasource:
       host: localhost
       port: 3306
       database: laolao_code
       username: your_username
       password: your_password
     
     redis:
       host: localhost
       port: 6379
       database: 2
     
     ai:
       deepseek:
         api-key: your_deepseek_api_key
     
     jwt:
       user-secret-key: your_jwt_secret_key
       user-ttl: 604800000
     ```

4. **启动 Docker**
   - 确保 Docker 服务正在运行
   - 系统会自动拉取 `eclipse-temurin:17-jdk-alpine` 镜像（如果不存在）

5. **编译运行**
```bash
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端部署

1. **进入前端目录**
```bash
cd laolao-code-vue
```

2. **安装依赖**
```bash
npm install
```

3. **配置 API 地址**
   - 修改 `src/utils/myAxios.ts` 中的 `baseURL` 为后端服务地址

4. **启动开发服务器**
```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动（Vite 默认端口）

5. **构建生产版本**
```bash
npm run build
```

## 🔧 核心功能说明

### Docker 判题服务

系统使用 Docker 容器池技术实现高效的代码判题：

- **容器池机制**: 预创建 10 个容器，判题时从池中取出，用完即销毁并补充新容器
- **安全隔离**: 每个容器禁用网络、限制内存（256MB）、限制 CPU（0.5核）、限制进程数（50）
- **资源监控**: 自动记录代码执行时间和内存消耗
- **多测试用例**: 支持多个测试用例依次执行，根据通过数量计算部分得分

### JWT 认证

- 使用 JWT Token 进行用户认证
- Token 有效期可配置（默认 7 天）
- 通过 Spring Security Filter 实现请求拦截和认证

### 考试流程

1. 用户查看考试列表
2. 选择考试并查看考试信息
3. 开始考试（如果已有进行中的考试记录，则继续）
4. 获取考试题目列表
5. 在线编写代码并提交判题
6. 查看判题结果和提交记录

## 📝 API 文档

### 用户相关
- `POST /api/user/sign-up` - 用户注册
- `GET /api/user/info` - 获取用户信息

### 考试相关
- `GET /api/exam` - 获取考试列表
- `GET /api/exam/info` - 获取考试详情
- `POST /api/exam/start` - 开始考试
- `GET /api/exam/begin` - 获取考试题目
- `POST /api/exam/judge` - 提交代码判题

### 判题记录
- `GET /api/submit-record/simple` - 获取简单判题记录
- `GET /api/submit-record/detail` - 获取详细判题记录

### 学习小组
- `GET /api/group` - 获取小组列表
- `POST /api/group/join` - 加入小组
- `GET /api/group/detail-base` - 获取小组基础信息
- `GET /api/group/detail-exam` - 获取小组考试信息

## 🐛 常见问题

### 1. Docker 容器无法创建
- 确保 Docker 服务正在运行
- 检查 Docker 镜像是否存在：`docker images | grep eclipse-temurin`
- 查看应用日志中的错误信息

### 2. 数据库连接失败
- 检查 MySQL 服务是否启动
- 确认数据库配置信息是否正确
- 检查数据库用户权限

### 3. Redis 连接失败
- 确保 Redis 服务正在运行
- 检查 Redis 配置信息

### 4. 主键不自增问题
- 确保数据库表的主键字段设置了 `AUTO_INCREMENT`
- 检查实体类中的 `@TableId` 注解配置
- 确认 MyBatis-Plus 的全局 ID 策略配置

## 📄 许可证

本项目采用 MIT 许可证。

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📮 联系方式

如有问题或建议，请通过 Issue 反馈。
