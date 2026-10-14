# laolao-code 后端运行镜像
# 构建前先完成打包（本地或 CI）：
# Windows: .\mvnw.cmd clean package -DskipTests
# 产物路径: target/laolao-code-0.0.1-SNAPSHOT.jar
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/laolao-code-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
