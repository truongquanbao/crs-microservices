# Các file hệ thống của CRS Microservices

## 1. Maven
Mỗi backend có `pom.xml` và Maven Wrapper (`mvnw`, `mvnw.cmd`). `pom.xml` quản lý version Java/Spring Boot, dependency và quy trình build.

- `course-service`: Spring Web, Spring Data JPA, MySQL Driver, Validation và Lombok.
- `registration-service`: Spring Web, Spring Data JPA, MySQL Driver, Validation và Lombok.
- `auth-service`: Spring Web, Spring Data JPA, MySQL Driver, Validation, Spring Security, JJWT và Lombok.
- `api-gateway`: Spring Cloud Gateway, Spring Boot Actuator và Lombok.

## 2. Spring Boot Configuration
Các file thực tế là `application.properties` ở course/auth/registration và `application.yml` ở api-gateway.

- Course Service chạy `8082`, dùng `course_db`.
- Auth Service chạy `8081`, dùng `auth_db`, JWT expiration 86400000 ms.
- Registration Service chạy `8083`, dùng `registration_db`, gọi Course Service tại `http://localhost:8082`.
- Gateway chạy `8080`, khai báo route, rewrite path, CORS và management endpoint.
- JPA của ba service dùng `ddl-auto=update` và `show-sql=true`.

Thông tin secret không được ghi trong tài liệu. Đặt `JWT_SECRET` và `PARTNER_API_KEY` qua environment trước khi chạy Gateway/Auth. Thông tin database local cũng nên được cấu hình ngoài source khi dùng môi trường thật.

## 3. Frontend Configuration
- `package.json`: scripts và dependency React, Axios, React Router DOM, TypeScript, Vite, plugin React và oxlint.
- `package-lock.json`: khóa phiên bản dependency để clone và cài đặt tái lập.
- `vite.config.ts`: bật React plugin cho Vite.
- `tsconfig.json`, `tsconfig.app.json`, `tsconfig.node.json`: cấu hình TypeScript cho app và Vite config.
- `.oxlintrc.json`: cấu hình oxlint hiện có.
- `.env.example`: mẫu biến `VITE_API_BASE_URL`; `.env` local không đưa lên Git.

## 4. Environment
Frontend dùng `VITE_API_BASE_URL=http://localhost:8080`. Backend dùng `JWT_SECRET` và `PARTNER_API_KEY` cho các giá trị bí mật đã được externalize khỏi file cấu hình. Không đặt password, token, API key hoặc secret thật trong `.env.example`.

## 5. Git
Root `.gitignore` loại trừ IDE (`.idea/`, `.vscode/`), `target/`, `node_modules/`, `dist/`, class/log, file OS và `.env`; vẫn cho phép `.env.example`. Các `.gitignore` trong module tiếp tục giữ rule Maven/Vite hiện có.

## 6. Build Output
`target/` là output Maven, `node_modules/` là dependency cài local và `dist/` là output build Vite. Các thư mục này có thể tái tạo từ source và lock/config files nên không commit.

## 7. Khả năng clone và chạy
Các Maven Wrapper, `pom.xml`, source/config backend, `package.json`, `package-lock.json`, TypeScript/Vite config và `.env.example` đã có. Người clone cần cài JDK, Node.js/npm, khởi động MySQL, tạo ba database và cung cấp environment variables. Có thể chạy build backend bằng `mvnw.cmd clean install` trong từng service và frontend bằng `npm install`, `npm run dev`.
