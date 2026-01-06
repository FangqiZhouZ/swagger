# --- 第一阶段：构建 ---
# 明确使用 Java 17 进行编译
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. 调高内存并设置重试机制
ENV MAVEN_OPTS="-Xmx2G -XX:+UseG1GC -Dmaven.wagon.http.retryHandler.count=3"

# 2. 直接复制源码（跳过容易报错的 go-offline 步骤）
COPY pom.xml .
COPY src ./src

# 3. 执行打包
RUN mvn clean package -DskipTests -B

# --- 第二阶段：运行 ---
# 明确使用 Java 17 运行环境
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# 确认 jar 文件名是否正确
COPY --from=build /app/target/authentication-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx1G", "-XX:+UseG1GC", "-jar", "app.jar"]