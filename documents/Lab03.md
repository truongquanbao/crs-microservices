# Lab 03

## 1. Phạm vi
Lab tập trung vào `registration-service`, port `8083`, database `registration_db`.

## 2. Mô hình dữ liệu
Entity `Registration` lưu `id`, `studentId`, `courseId`, `trangThai` (`DA_DANG_KY` hoặc `DA_HUY`) và `ngayDangKy`. `courseId` chỉ là giá trị số, không dùng quan hệ JPA vì Course nằm ở database/service khác.

## 3. Repository và service
`RegistrationRepository` kiểm tra đăng ký đang hoạt động theo student/course/status. `RegistrationService` chống đăng ký trùng, gọi Course Service để giữ chỗ trước khi lưu; khi hủy, gọi API trả chỗ rồi đổi trạng thái sang `DA_HUY`.

## 4. API thực tế
- Method: `POST`
- URL: `/registrations`
- Chức năng: đăng ký môn học.
- Request: JSON `{"studentId": number, "courseId": number}`.
- Response: `Registration`, HTTP 201.

- Method: `DELETE`
- URL: `/registrations/{id}`
- Chức năng: hủy đăng ký và trả chỗ.
- Request: `id` trên path.
- Response: không có body.

Project hiện không có endpoint đọc danh sách/trạng thái riêng và không sử dụng pagination trong controller này.

## 5. Liên hệ Course Service
`CourseClient` dùng `RestTemplate` gọi `PATCH http://localhost:8082/internal/courses/{id}/reserve-seat` khi đăng ký và endpoint `release-seat` khi hủy. Lỗi hết chỗ, không tồn tại môn hoặc không kết nối được được chuyển thành lỗi nghiệp vụ.

## 6. Exception Handling và giới hạn
Global handler xử lý lỗi validation, môn không tồn tại, đăng ký trùng và đăng ký đã hủy. Quy trình gọi service và lưu database chưa có distributed transaction: nếu giữ chỗ thành công nhưng lưu Registration thất bại, chỗ có thể chưa được hoàn lại.

## 7. Kết quả
Registration Service thực hiện được đăng ký/hủy và đồng bộ số chỗ qua Course Service theo mô hình REST giữa các service.
