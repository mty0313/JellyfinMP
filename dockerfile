FROM openjdk:8-jre-slim

# 创建目录
RUN mkdir /jellyfin-mp
WORKDIR /jellyfin-mp

# 创建 resource 目录
RUN mkdir /server-temp

# 复制 Spring Boot JAR 文件和配置文件到容器中
COPY ./target/jellyfinmp-0.0.1-SNAPSHOT.jar /jellyfin-mp
COPY ./src/main/resources/application.yaml /jellyfin-mp
COPY ./src/main/resources/application-online.yaml /jellyfin-mp

# 暴露端口
EXPOSE 8089

# 设置启动命令
CMD ["java", "-Xmx512m", "-Xms512m", "-Duser.timezone=GMT+08", "-jar", "jellyfinmp-0.0.1-SNAPSHOT.jar", "--spring.config.location=/jellyfin-mp/application-online.yaml"]
