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
COPY ./src/main/resources/sql/setup.sql /jellyfin-mp

# 暴露端口
EXPOSE 8089

ENV DATABASE_URL localhost:3306
ENV DATABASE_USER user
ENV DATABASE_PASS pass

# 设置启动命令
CMD ["java", "-Xmx512m", "-Xms512m", "-Duser.timezone=GMT+08", "-jar", "jellyfinmp-0.0.1-SNAPSHOT.jar", \
 "--spring.config.location=/jellyfin-mp/application-online.yaml", \
 "--spring.datasource.url=jdbc:mysql://${DATABASE_URL}/jellyfinmp?useUnicode=true&useSSL=false&characterEncoding=utf8", \
 "--spring.datasource.username=${DATABASE_USER}", \
 "--spring.datasource.password=${DATABASE_PASS}" \
 ]
