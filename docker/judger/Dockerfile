# alpine 版本体积小
FROM eclipse-temurin:17-jdk-alpine

# 工作目录
WORKDIR /judger

# 拷贝依赖
COPY libs/*.jar /usr/share/java/libs/

# 设置类路径
# Java 会自动加载 libs/ 目录下所有的 jar
ENV CLASSPATH=".:/usr/share/java/libs/*"

# 验证安装
RUN java -version