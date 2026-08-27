# Lab 01

## 1. Mục tiêu
Thiết lập repository CRS theo hướng microservices và tạo nền tảng chạy độc lập cho các backend service. Git history ghi nhận commit đầu tiên triển khai Course Service theo mô hình ba lớp.

## 2. Kiến trúc
Hệ thống được chia thành API Gateway, Auth Service, Course Service, Registration Service và frontend. Mỗi backend là một ứng dụng Spring Boot có Maven Wrapper và cấu hình riêng.

## 3. Công nghệ
Java, Spring Boot, Maven, Spring Web, Spring Data JPA, MySQL và Lombok được sử dụng trong backend; frontend dùng React/Vite/TypeScript.

## 4. Cấu trúc project
Các thư mục service nằm ở root repository. Mỗi backend có `pom.xml`, `mvnw.cmd`, `src/main/java`, `src/main/resources` và test Spring Boot cơ bản.

## 5. Các bước thực hiện
1. Khởi tạo Course Service với entity, DTO, repository, service và controller.
2. Bổ sung các service độc lập cùng cấu hình port/database.
3. Thêm Gateway, authentication và frontend ở các commit tiếp theo.

## 6. File quan trọng
`pom.xml`, `mvnw.cmd`, `src/main/resources/application.properties` hoặc `application.yml`, application class và các controller của từng service.

## 7. Cách chạy
Chuẩn bị JDK, Maven/MySQL và các database được nêu trong README. Từ mỗi service chạy `./mvnw spring-boot:run` trên Unix hoặc `mvnw.cmd spring-boot:run` trên Windows.

## 8. Kết quả
Repository có cấu trúc microservices độc lập, có thể build từng backend bằng Maven Wrapper và mở frontend bằng Vite. Các chức năng chi tiết được trình bày ở Lab 02-Lab 05.
