# Lab 05

## 1. Mục tiêu và công nghệ
`crs-frontend` là ứng dụng Vite dùng React 19 và TypeScript. Axios được dùng để gọi backend; `react-router-dom` có trong package dependency nhưng hiện chưa được sử dụng trong source. Script hiện có gồm `dev`, `build`, `lint` và `preview`.

## 2. Luồng gọi API
```text
React
   |
   v
courseApi.ts
   |
   v
axiosClient.ts
   |
   v
http://localhost:8080
   |
   v
API Gateway
   |
   v
Course Service
   |
   v
Database
```

`src/api/axiosClient.ts` lấy `VITE_API_BASE_URL` từ Vite environment. `src/api/courseApi.ts` gọi `GET /api/courses` với `keyword`, `page`, `size`; `App.tsx` hiển thị trạng thái tải, lỗi hoặc JSON danh sách course.

## 3. TypeScript interfaces
- `types/course.ts`: `Course` và `PagedResponse<T>`.
- `types/auth.ts`: `LoginRequest` và `LoginResponse` gồm token, username, role.
- `types/registration.ts`: `Registration` và `RegistrationRequest`.
- `types/apiError.ts`: cấu trúc lỗi API.

## 4. Cấu hình và cách chạy
`package.json`, `package-lock.json`, `vite.config.ts`, ba file `tsconfig` và `.oxlintrc.json` là các file cấu hình hiện có. Tạo `.env` từ `.env.example` với `VITE_API_BASE_URL=http://localhost:8080`, sau đó chạy `npm install` và `npm run dev`.

## 5. Kết quả
Frontend hiện hoàn thành màn hình kiểm tra kết nối CRS qua Gateway và lấy danh sách course. Các thư mục components, context và pages mới chỉ có `.gitkeep`, chưa có chức năng UI riêng được triển khai.
