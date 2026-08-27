# Lab 04

## 1. Phạm vi
Lab tập trung vào `auth-service` và `api-gateway`.

## 2. Kiến trúc
```text
Client
   |
   v
API Gateway :8080
   |
   +------> Auth Service :8081
   |
   +------> Course Service :8082
   |
   +------> Registration Service :8083
```

## 3. Authentication và JWT
`POST /auth/login` của Auth Service nhận username/password, tìm User, kiểm tra mật khẩu bằng BCrypt và sinh JWT có subject, role, thời điểm cấp/hết hạn. Thời hạn hiện cấu hình là 86400000 ms. Auth Service có seeder local cho `admin` và `student1`; mật khẩu mẫu không nên dùng ngoài môi trường học tập.

## 4. Gateway routing thực tế
| Gateway path | Service đích | Path sau rewrite |
|---|---|---|
| `/api/auth/**` | 8081 | `/auth/**` |
| `/api/courses` | 8082 | `/courses` |
| `/api/courses/**` | 8082 | `/courses/**` |
| `/api/registrations` | 8083 | `/registrations` |
| `/api/registrations/**` | 8083 | `/registrations/**` |
| `/api/public/courses` | 8082 | `/courses` |

## 5. Filter và endpoint mở
`AuthHeaderFilter` mở login, public courses và GET `/api/courses/**`; request còn lại phải có header `Authorization`. `ApiKeyFilter` yêu cầu `X-API-KEY` hợp lệ cho `/api/public/courses`. Filter hiện kiểm tra sự hiện diện/đối chiếu header tại Gateway; source Gateway không chứa bước parse và verify chữ ký JWT.

## 6. CORS và cấu hình
CORS cho phép origin `http://localhost:5173`, mọi method và header. Gateway chạy ở `8080`. Secret JWT và partner API key được cấp qua biến môi trường, không ghi vào repository.

## 7. Kết quả
Client có một điểm truy cập thống nhất qua Gateway; các route public/protected và route nội bộ được phân tách theo cấu hình thực tế.
