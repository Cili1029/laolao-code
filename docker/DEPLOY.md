# Docker Compose 一键部署

面向单机服务器的容器化部署：MySQL、Redis、RocketMQ、后端、Nginx 全部由仓库根目录的 `docker-compose.yml` 编排，服务器上只需安装 Docker。该方案对应的 Spring profile 为 `docker`，本地开发流程不受影响。

## 1. 本地打包

Windows：

```bash
cd laolao-code
mvn clean package -DskipTests
cd ../laolao-code-vue
npm install
npm run build
```

macOS / Linux 将 `mvnw.cmd` 换成 `./mvnw`。产物为 `laolao-code/target/laolao-code-0.0.1-SNAPSHOT.jar` 与 `laolao-code-vue/dist/`。

## 2. 上传服务器

将两个构建产物连同仓库中的部署文件上传到服务器（如 `/opt/laolao`），保持以下结构，编排文件里的相对路径依赖它：

```text
/opt/laolao/
|-- docker-compose.yml
|-- laolao_code.sql
|-- docker/
|   |-- judger/                  # 判题镜像构建文件
|   |-- nginx/laolao.conf        # Nginx 反代配置
|   `-- rmq-proxy.json           # RocketMQ Proxy 配置
|-- laolao-code/
|   |-- Dockerfile
|   |-- .dockerignore
|   `-- target/laolao-code-0.0.1-SNAPSHOT.jar
`-- laolao-code-vue/dist/        # 前端构建产物
```

## 3. 启动

```bash
# 服务器仅需 Docker 与 Compose 插件
apt install -y docker.io docker-compose-v2
systemctl enable --now docker

# 判题镜像构建在宿主机 Docker 上，后端通过挂载 docker.sock 动态创建判题容器
docker build -t judger /opt/laolao/docker/judger

cd /opt/laolao
docker compose up -d --build
```

启动前在本地编辑 `laolao-code/src/main/resources/application-docker.yaml` 填入 DeepSeek API Key，该配置随 jar 打包（已加入 .gitignore，不要提交真实密钥）；数据库密码改动需与 `docker-compose.yml` 中的 `MYSQL_PASSWORD` 保持一致。首次启动会自动执行 `laolao_code.sql` 完成建库建表和演示数据导入。

## 运维命令

```bash
docker compose ps                      # 服务状态
docker compose logs -f backend         # 后端日志
docker compose restart backend         # 重启后端
docker compose up -d --build backend   # 更新 jar 后重建后端
docker compose down                    # 停止；数据在卷中保留，down -v 才清空
```

## 更新版本

代码改动后不需要重装环境，按改动的部分更新即可。

**后端**：重新打包，覆盖 jar 后重建后端镜像和容器：

```bash
# 本地
cd laolao-code
mvnw.cmd clean package -DskipTests
scp target/laolao-code-0.0.1-SNAPSHOT.jar root@服务器IP:/opt/laolao/laolao-code/target/

# 服务器
cd /opt/laolao
docker compose up -d --build backend
```

**前端**：重新构建后覆盖 dist 内的文件（bind mount 实时生效，无需重启 nginx）：

```bash
# 本地
cd laolao-code-vue
npm run build
scp -r dist/* root@服务器IP:/opt/laolao/laolao-code-vue/dist/
```

> ⚠️ **不要 `mv dist dist.bak` 再上传新 dist 目录**。Docker bind mount 绑定的是原始目录的 inode，`mv` 改名后容器仍挂载旧目录，新 dist 容器内不可见，会导致 403/500。正确做法是覆盖 dist 内部的文件（如 `rm -rf dist/* && cp -r 新dist/* dist/`），或上传后执行 `docker compose restart nginx` 重新挂载。

**Nginx 配置**（如 `docker/nginx/laolao.conf`）修改后，上传覆盖并 reload：

```bash
docker compose exec nginx nginx -t && docker compose exec nginx nginx -s reload
```

**配置**：修改本地 `laolao-code/src/main/resources/application-docker.yaml` 后，按后端流程重新打包上传（该配置随 jar 打包，服务器上无法直接改）。

## 说明

- 浏览器访问 `http://服务器IP`，Nginx 容器在 80 端口对外，反代 `/api` 与 `/ws` 到后端容器；防火墙只需放行 22 与 80
- 所有服务均为 `restart: unless-stopped`，Docker 开机自启后整套环境自动恢复
- 2G 内存服务器建议删除编排文件中的 `dashboard` 服务（RocketMQ 控制台）
- 不要在服务器上运行 `docker/docker-compose.yml`（那是本地开发用的 Redis + RocketMQ 编排），RocketMQ 容器名会与根目录总编排冲突

## 排障记录

以下是部署过程中实际踩过的坑，供更新后出现同类问题时参考：

**WebSocket 握手 403 Invalid CORS request**：浏览器握手被 Spring CorsFilter 拒绝。根因是 nginx 的 `/ws/` 块没有 `proxy_set_header Host $host;`，后端收到的 Host 为 `backend:8080` 与浏览器 Origin 不一致被误判为跨域。修复：`/ws/` 块补齐 `Host`、`X-Real-IP`、`X-Forwarded-For`、`X-Forwarded-Proto` 头（`/api/` 块本来就有）。

**判题耗时翻倍 / 正确代码偶发 TLE**：判题容器 `withCpuQuota(50000)` 限制 0.5 核，服务器 cgroup v2 严格节流 Java 冷启动。修复：放宽到 `150000`（1.5 核），java 启动参数加 `-XX:+UseSerialGC -Xss256k`，`timeout` 从 3s 放宽到 5s。本地 Docker Desktop/WSL2 节流不严格所以本地正常，服务器才暴露问题。

**前端更新后 403/500（rewrite cycle）**：用 `mv dist dist.bak` 备份后上传新 dist，bind mount 仍指向旧 inode，容器内目录为空。修复：`docker compose restart nginx` 重新挂载，或以后直接覆盖 dist 内部文件。

**登录失败被提示"登录已过期"**：后端 `failureHandler` 把密码错误返回为 HTTP 401，与未登录的 401 混淆，前端拦截器对所有 401 统一弹"登录已过期"。修复：密码错误改为返回 200 + 业务 code=0，由前端按业务错误提示。

**未登录访问 /api/user/info 弹"未知错误"**：前端 default 分支写死"未知错误"，未读取后端 msg。修复：default 分支优先显示 `error.response.data.msg`；同时登录/注册页收到 401 时静默不弹气泡。
