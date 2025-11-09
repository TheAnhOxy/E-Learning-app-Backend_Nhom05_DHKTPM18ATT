# SỬA DÒNG 2 – THAY 17 THÀNH 21
FROM openjdk:21-jdk-slim

# Tạo thư mục app
WORKDIR /app

# Copy file JAR (từ target/)
COPY target/*.jar app.jar

# Chạy ứng dụng
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]