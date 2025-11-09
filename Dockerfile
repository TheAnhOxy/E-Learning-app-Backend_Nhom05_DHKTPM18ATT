# ----- Stage 1: Build ứng dụng (Tạo ra file .jar) -----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Sao chép các tệp cần thiết để build Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# === DÒNG FIX LỖI ===
# Cấp quyền thực thi (execute) cho file mvnw
RUN chmod +x ./mvnw

# Chạy lệnh build Maven bên trong container
# Bước này sẽ TẠO RA thư mục /app/target/*.jar
RUN ./mvnw clean package -DskipTests

# ----- Stage 2: Tạo image cuối cùng để chạy -----
# Sử dụng JRE (nhỏ hơn) để chạy ứng dụng
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Chỉ sao chép tệp .jar đã được build từ Stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]