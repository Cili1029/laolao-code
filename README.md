# Laolao Code

Laolao Code 是一个面向编程教学与考试场景的在线判题平台，采用前后端分离架构，支持学习小组、题库管理、考试编排、在线写码、异步判题、成绩评阅与 AI 报告生成。

当前仓库包含完整的运行骨架：

- `laolao-code/`：Spring Boot 后端
- `laolao-code-vue/`：Vue 3 前端
- `rocketmq/`：RocketMQ 本地开发编排
- `laolao_code.sql`：数据库初始化脚本

## 项目亮点

- 支持在线 Java 编程考试，前端内置 Monaco Editor
- 使用 Docker 沙箱执行用户代码，隔离运行环境
- 通过 RocketMQ 解耦判题与考试发布流程
- 使用 WebSocket 实时推送判题结果
- 支持学习小组、题库、草稿考试、正式发布、人工阅卷
- 集成 Spring AI + DeepSeek，用于错题分析和考试报告生成

## 适用场景

- 编程课程阶段测验
- 校内机考系统原型
- 编程训练营作业与考试平台
- 在线判题与教学分析实验项目

## 核心功能

### 学员侧

- 注册、登录、查看个人信息
- 加入学习小组
- 查看可参加考试与考试详情
- 在线作答、提交代码、查看每次提交记录
- 查看个人考试报告

### 教师 / 管理员侧

- 创建学习小组
- 管理私有 / 公共题库
- 创建考试草稿并向考试中添加题目
- 先用标准答案校验题目，再发布考试
- 查看考试完成情况
- 对考试进行人工阅卷和调分

### 系统能力

- Java 代码编译与执行
- 多测试用例判题
- 按通过用例数计算部分得分
- 判题结果异步处理与实时通知
- AI 生成题目讲解、个人考试报告、考试整体分析

## 技术栈

### 后端

- Java 17
- Spring Boot 3
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Redis
- RocketMQ 5
- WebSocket
- Spring AI
- Docker Java API

### 前端

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Tailwind CSS 4
- Reka UI
- Monaco Editor
- Axios

## 架构说明

```text
Vue 3 前端
  |- HTTP 调用 Spring Boot API
  |- WebSocket 接收判题结果
  `- Monaco Editor 在线写码

Spring Boot 后端
  |- Spring Security + JWT 鉴权
  |- MyBatis-Plus 访问 MySQL
  |- Redis 存储登录态相关数据
  |- RocketMQ 异步消费判题 / 发布任务
  |- Docker 沙箱执行 Java 代码
  `- Spring AI 调用 DeepSeek 生成报告
```

判题主链路如下：

1. 学员在考试页提交代码。
2. 后端生成判题记录，并把任务投递到 RocketMQ。
3. 消费者从队列中取出任务，调用 Docker 沙箱编译并运行 Java 代码。
4. 后端保存判题结果，并通过 WebSocket 推送给前端。
5. 前端实时更新题目得分和提交结果。

## 目录结构

```text
.
|-- laolao-code/                  # Spring Boot 后端
|   |-- src/main/java/com/laolao/
|   |   |-- common/               # 安全、Docker、WebSocket、工具类
|   |   |-- controller/           # 接口层
|   |   |-- listener/             # RocketMQ 消费者
|   |   |-- mapper/               # MyBatis-Plus Mapper
|   |   |-- pojo/                 # Entity / DTO / VO
|   |   `-- service/              # 业务逻辑
|   `-- src/main/resources/
|       |-- application.yaml
|       |-- application-dev.yaml
|       `-- mapper/
|-- laolao-code-vue/              # Vue 3 前端
|   |-- src/components/
|   |-- src/router/
|   |-- src/stores/
|   `-- src/utils/
|-- rocketmq/                     # RocketMQ docker compose 配置
`-- laolao_code.sql               # MySQL 初始化脚本
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8+
- Redis 6+
- Docker / Docker Desktop
- Maven 3.9+ 或使用仓库自带 Maven Wrapper

### 2. 初始化数据库

创建数据库并导入脚本：

```sql
CREATE DATABASE laolao_code DEFAULT CHARACTER SET utf8mb4;
```

然后执行根目录下的 `laolao_code.sql`。

### 3. 启动 RocketMQ

在仓库根目录执行：

```bash
docker compose -f rocketmq/docker-compose.yml up -d
```

默认会启动：

- NameServer：`9876`
- Proxy：`9080` / `9081`
- Dashboard：`9877`

### 4. 配置后端

后端默认读取：

- [`laolao-code/src/main/resources/application.yaml`](./laolao-code/src/main/resources/application.yaml)
- [`laolao-code/src/main/resources/application-dev.yaml`](./laolao-code/src/main/resources/application-dev.yaml)

本地开发至少需要正确配置：

- MySQL 连接
- Redis 连接
- JWT 密钥
- DeepSeek API Key

建议：

- 不要把真实密钥提交到 GitHub
- 优先改为环境变量注入，或仅在本地保留私有配置

### 5. 启动后端

```bash
cd laolao-code
./mvnw spring-boot:run
```

Windows 也可以使用：

```bash
mvnw.cmd spring-boot:run
```

后端默认端口：`http://localhost:8080`

### 6. 启动前端

```bash
cd laolao-code-vue
npm install
npm run dev
```

前端默认端口：`http://localhost:5173`

前端开发服务器已配置代理：

- `/api` -> `http://localhost:8080`
- `/ws` -> `ws://localhost:8080`

## 关键模块

### 在线判题

判题逻辑位于后端的 Docker 服务中，核心特点：

- 预热容器池，提高判题响应速度
- 禁用容器网络，降低风险
- 限制内存、CPU 和进程数
- 记录执行时间和内存占用
- 当前判题语言为 Java

### 考试发布

教师发布考试时，系统会先校验考试中的题目标准答案是否可以通过测试用例。只有全部校验通过，考试才会进入正式发布状态。

### 实时通知

系统通过 WebSocket 将判题结果实时回推到前端，考试页无需手动刷新即可看到最新结果。

### AI 报告

已接入 Spring AI + DeepSeek，支持：

- 单题错因分析
- 学员个人考试报告
- 教师视角的整场考试分析

## 当前实现特点

- 前后端分离但目录放在同一仓库
- 更偏教学考试平台，而不是通用 OJ
- 题目、考试、学习小组之间有明确业务关联
- 包含异步消息、实时推送和 AI 分析这三条增强能力链路

## 后续可继续完善的方向

- 增加更多判题语言
- 提供容器镜像与一键部署脚本
- 补充单元测试与集成测试
- 增加示例账号、演示数据和页面截图
- 将敏感配置迁移到环境变量或密钥管理方案

## 说明

如果你准备把这个仓库公开到 GitHub，建议在提交前先检查并清理本地开发配置中的数据库密码、JWT 密钥和 AI Key，再补几张系统截图，展示效果会更完整。
