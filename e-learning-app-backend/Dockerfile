# Dùng image Java chính thức
FROM openjdk:17-jdk-slim

# Tạo thư mục app
WORKDIR /app

# Copy file JAR (tên file JAR của bạn)
COPY target/*.jar app.jar

# Chạy ứng dụng
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]