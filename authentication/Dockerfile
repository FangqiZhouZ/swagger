# --- 第一阶段：构建 ---
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /app

# 1. 调高内存限额，允许并行构建
# -Xmx2G: 给 Maven 更多堆内存以处理多线程编译
# -Xss256k: 依然保留这个设置，防止线程过多导致内存碎片化报错
ENV MAVEN_OPTS="-Xmx2G -Xss256k -XX:+UseG1GC"

# 2. 利用 Docker 层缓存：先只复制 pom.xml 下载依赖
COPY pom.xml .
# 移除 -T 1，允许使用多核下载（通常网络是瓶颈，多线程更快）
RUN mvn dependency:go-offline -B

# 3. 复制源码并并行构建
COPY src ./src
# -T 1C 代表“每个 CPU 核心启动一个线程”，这是 Maven 官方推荐的提速方式
RUN mvn clean package -DskipTests -B -T 1C -Dsun.zip.disableMemoryMapping=true

# --- 第二阶段：运行 ---
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# 复制生成的 jar
COPY --from=build /app/target/authentication-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# 运行时依然建议保留资源限制，确保容器稳定
ENTRYPOINT ["java", "-Xmx1G", "-XX:+UseG1GC", "-jar", "app.jar"]