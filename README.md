# Laolao Code

Laolao Code 是一个面向编程教学、训练营作业和课程考试场景的在线判题平台。项目采用前后端分离架构，围绕「学习小组 - 题库 - 考试 - 判题 - 报告」组织业务流程，支持在线写码、异步判题、实时通知、人工阅卷和 AI 辅助分析。

## 功能特性

- 用户注册、登录、JWT 鉴权和基于角色的权限控制
- 学习小组创建、加入与小组考试管理
- 公共题库、私有题库、题目复制和测试用例管理
- 考试草稿创建、题目添加、标准答案预校验和正式发布
- Monaco Editor 在线 Java 编程作答
- RocketMQ 异步判题与考试结束任务处理
- Docker 沙箱编译、运行用户代码，并限制 CPU、内存、进程数和网络
- WebSocket 实时推送判题结果
- 支持按测试用例通过情况记录提交状态、得分与执行信息
- 教师侧人工阅卷、调分与考试完成情况查看
- Spring AI + DeepSeek 生成单题讲解、个人考试报告和整场考试分析

## 技术栈

**后端**

- Java 17
- Spring Boot 3.5
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Redis
- RocketMQ 5
- WebSocket
- Spring AI + DeepSeek
- Docker Java API

**前端**

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Tailwind CSS 4
- Reka UI
- Monaco Editor
- Axios

## 系统架构

```text
Vue 3 + Vite
|-- HTTP 调用 Spring Boot API
|-- WebSocket 接收判题结果
`-- Monaco Editor 提供在线 Java 编辑器

Spring Boot
|-- Spring Security + JWT 处理登录态与接口权限
|-- MyBatis-Plus 访问 MySQL
|-- Redis 存储登录 token 等运行数据
|-- RocketMQ 解耦判题、考试发布和考试结束任务
|-- Docker 沙箱编译并运行用户代码
`-- Spring AI 调用 DeepSeek 生成报告
```

典型判题流程：

1. 学员在考试页面提交 Java 代码。
2. 后端创建判题记录，并投递到 `JudgeTopic`。
3. RocketMQ 消费者取出任务，调用 Docker 沙箱编译和运行代码。
4. 系统比对所有测试用例输出，保存 AC / WA / CE / RE / TLE / MLE 等结果。
5. 后端通过 WebSocket 将判题结果推送给前端。

## 目录结构

```text
.
|-- docker/
|   |-- judger/                 # 判题沙箱镜像构建文件
|   `-- rocketmq/               # RocketMQ 本地开发编排
|-- laolao-code/                # Spring Boot 后端
|   |-- src/main/java/com/laolao/
|   |   |-- common/             # 安全、配置、WebSocket、工具类
|   |   |-- controller/         # 接口层
|   |   |-- judge/              # Docker 判题服务
|   |   |-- listener/           # RocketMQ 消费者
|   |   |-- mapper/             # MyBatis Mapper
|   |   |-- pojo/               # Entity / DTO / VO
|   |   `-- service/            # 业务逻辑
|   `-- src/main/resources/
|       |-- application.yaml
|       `-- mapper/
|-- laolao-code-vue/            # Vue 3 前端
|   |-- src/components/
|   |-- src/router/
|   |-- src/stores/
|   `-- src/utils/
`-- laolao_code.sql             # MySQL 初始化脚本和演示数据
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8+
- Redis 6+
- Docker / Docker Desktop
- Maven 3.9+，也可以直接使用后端目录内置的 Maven Wrapper

### 1. 初始化数据库

创建数据库：

```sql
CREATE DATABASE laolao_code DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后导入根目录下的 `laolao_code.sql`。

导入演示数据后，可使用以下账号登录，初始密码均为 `123456`：

| 账号 | 角色 |
| --- | --- |
| `admin` | 管理员 |
| `advisor1` | 导师 / 组管理员 |
| `member1` | 学员 |
| `member2` | 学员 |

### 2. 准备 RocketMQ

在仓库根目录执行：

```bash
docker compose -f docker/rocketmq/docker-compose.yml up -d
```

默认端口：

- NameServer: `9876`
- Proxy: `9080` / `9081`
- Dashboard: `9877`

### 3. 构建判题镜像

后端判题服务会创建名为 `judger` 的 Docker 镜像容器池。首次运行前需要先构建镜像：

```bash
docker build -t judger ./docker/judger
```

### 4. 配置后端

后端默认启用 `dev` profile，公共配置在：

```text
laolao-code/src/main/resources/application.yaml
```

本地私有配置建议放在：

```text
laolao-code/src/main/resources/application-dev.yaml
```

该文件已被 `.gitignore` 忽略，请不要把真实数据库密码、JWT 密钥和 AI Key 提交到 GitHub。可参考下面的本地配置：

```yaml
datasource:
  driver-class-name: com.mysql.cj.jdbc.Driver
  host: localhost
  port: 3306
  database: laolao_code
  username: root
  password: your_mysql_password

redis:
  host: localhost
  port: 6379
  database: 0

ai:
  deepseek:
    api-key: your_deepseek_api_key

jwt:
  user-secret-key: your_jwt_secret_key
  user-ttl: 604800000
```

### 5. 启动后端

Windows:

```bash
cd laolao-code
mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
cd laolao-code
./mvnw spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

### 6. 启动前端

```bash
cd laolao-code-vue
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:5173
```

Vite 开发服务器已配置代理：

- `/api` -> `http://localhost:8080`
- `/ws` -> `ws://localhost:8080`

## 常用命令

后端测试：

```bash
cd laolao-code
./mvnw test
```

前端构建：

```bash
cd laolao-code-vue
npm run build
```

停止 RocketMQ：

```bash
docker compose -f docker/rocketmq/docker-compose.yml down
```

## 角色说明

| 角色值 | 权限标识 | 说明 |
| --- | --- | --- |
| `0` | `ADMIN` | 管理员 |
| `1` | `MANAGER` | 导师 / 组管理员，可创建小组、管理题库、创建考试、阅卷 |
| `2` | `USER` | 学员，可加入小组、参加考试、查看个人报告 |

## 当前限制

- 当前在线判题语言主要面向 Java。
- 判题依赖本机 Docker 服务，后端启动时会初始化判题容器池。
- AI 报告依赖 DeepSeek API Key，未配置时相关能力不可用。
- 仓库暂未提供完整生产环境部署脚本。

## 后续计划

- 增加更多判题语言支持
- 补充接口文档和页面截图
- 增加单元测试与集成测试覆盖
- 提供 Docker Compose 一键启动 MySQL、Redis、RocketMQ、后端与前端
- 将配置进一步迁移到环境变量或统一密钥管理方案
