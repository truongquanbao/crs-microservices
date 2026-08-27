# CRS Microservices

## 1. Giới thiệu
CRS (Course Registration System) là hệ thống đăng ký học phần theo kiến trúc microservices. Repository hiện bao gồm bốn Spring Boot service và một frontend React. Nội dung được mô tả theo source code và cấu hình đang có.

## 2. Kiến trúc hệ thống
Client gọi vào API Gateway tại `8080`. Gateway định tuyến đến Auth Service (`8081`), Course Service (`8082`) và Registration Service (`8083`). Course Service và Registration Service sử dụng các database MySQL riêng; Registration Service gọi Course Service qua REST để giữ hoặc trả chỗ.

## 3. Các service
- `api-gateway`: Spring Cloud Gateway, routing, CORS, kiểm tra Authorization header và API key cho public partner endpoint.
- `auth-service`: đăng nhập, BCrypt password và sinh JWT.
- `course-service`: CRUD, tìm kiếm/phân trang khóa học và API nội bộ giữ/trả chỗ.
- `registration-service`: đăng ký, hủy đăng ký và gọi Course Service.
- `crs-frontend`: giao diện React hiện gọi `GET /api/courses` để kiểm tra kết nối.

## 4. Công nghệ sử dụng
- Java, Spring Boot, Maven Wrapper, Spring Web, Spring Data JPA, MySQL.
- Spring Cloud Gateway và JWT (JJWT) ở các module tương ứng.
- React, TypeScript, Vite, Axios và React Router DOM trong frontend.

## 5. Cấu trúc repository
```text
api-gateway/          Gateway và route cấu hình trong application.yml
auth-service/         Authentication và database auth_db
course-service/       Course API và database course_db
registration-service/ Registration API và database registration_db
crs-frontend/         React/Vite frontend
documents/            Tài liệu Lab 01 đến Lab 05 và file hệ thống
```

## 6. Port của các service
| Thành phần | Port |
|---|---:|
| API Gateway | 8080 |
| Auth Service | 8081 |
| Course Service | 8082 |
| Registration Service | 8083 |
| Vite dev server | 5173 (mặc định) |

## 7. Yêu cầu môi trường
- JDK và Maven; có thể dùng Maven Wrapper `mvnw.cmd` trong từng service.
- Node.js và npm cho frontend.
- MySQL đang chạy với ba database `auth_db`, `course_db`, `registration_db`.
- Đặt `JWT_SECRET`, `PARTNER_API_KEY` và thông tin database qua biến môi trường trước khi chạy backend.

## 8. Cách chạy backend
Từ từng thư mục backend, chạy trên Windows:
```powershell
.\mvnw.cmd spring-boot:run
```
Có thể build từng module bằng `mvnw.cmd clean install`. Khởi động MySQL và tạo ba database trước; Hibernate đang dùng `ddl-auto=update` để cập nhật schema.

## 9. Cách chạy frontend
```powershell
cd crs-frontend
copy .env.example .env
npm install
npm run dev
```
Frontend dùng `VITE_API_BASE_URL=http://localhost:8080` và gọi API qua Gateway.

## 10. API Gateway
Các route thực tế được khai báo trong `api-gateway/src/main/resources/application.yml`: `/api/auth/**`, `/api/courses`, `/api/courses/**`, `/api/registrations`, `/api/registrations/**` và `/api/public/courses`. Gateway rewrite các path này trước khi chuyển đến service đích.

## 11. Authentication
`POST /api/auth/login` là endpoint mở và Auth Service trả JWT. GET course là public; các request khác qua Gateway cần header `Authorization`. `/api/public/courses` cần thêm `X-API-KEY`. Filter hiện kiểm tra sự hiện diện của header tại Gateway; việc sinh và ký JWT nằm ở Auth Service.

## 12. Documentation
Xem [documents/README.md](documents/README.md) để đọc tài liệu Lab 01-Lab 05 và [documents/SYSTEM_FILES.md](documents/SYSTEM_FILES.md) để hiểu các file hệ thống.

## 13. Các Lab đã hoàn thành
- Lab 01: nền tảng repository và kiến trúc microservices.
- Lab 02: Course Service CRUD, validation, tìm kiếm và phân trang.
- Lab 03: Registration Service, giữ/trả chỗ qua Course Service.
- Lab 04: Auth Service, JWT và API Gateway.
- Lab 05: React/Vite frontend kết nối API Gateway.

Không commit `target/`, `node_modules/`, `dist/`, thư mục IDE hoặc file `.env` chứa dữ liệu local/secret.
