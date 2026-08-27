# Lab 02

## 1. Phạm vi
Lab tập trung vào `course-service`, được Git history ghi nhận qua hai commit CRUD và search/pagination.

## 2. Thành phần
- Entity `Course` ánh xạ bảng `course`, gồm `id`, tên môn, số tín chỉ, số chỗ tối đa và số chỗ còn lại.
- DTO `CourseDTO` dùng Lombok và Jakarta Validation: tên không rỗng; số tín chỉ và số chỗ tối thiểu là 1.
- `CourseRepository` mở rộng `JpaRepository`, có kiểm tra trùng tên và tìm kiếm theo tên không phân biệt hoa thường.
- `CourseService` xử lý CRUD, tìm kiếm/phân trang và giữ/trả chỗ.
- `GlobalExceptionHandler` trả lỗi 400 cho validation/đối số, 404 khi không tìm thấy và 409 khi xung đột trạng thái.
- Database MySQL `course_db`, port `8082`, JPA `ddl-auto=update`.

## 3. API thực tế

### Tìm kiếm danh sách
- Method: `GET`
- URL: `/courses?keyword={keyword}&page={page}&size={size}`
- Chức năng: tìm kiếm theo tên tùy chọn và phân trang.
- Request: query parameters tùy chọn.
- Response: `Page<CourseDTO>`.

### Xem chi tiết
- Method: `GET`
- URL: `/courses/{id}`
- Chức năng: lấy một môn học.
- Request: `id` trên path.
- Response: `CourseDTO`.

### Tạo môn học
- Method: `POST`
- URL: `/courses`
- Chức năng: tạo môn học; số chỗ còn lại được service khởi tạo theo số chỗ tối đa.
- Request: JSON `CourseDTO` không cần `id`.
- Response: `CourseDTO`, HTTP 201.

### Cập nhật môn học
- Method: `PUT`
- URL: `/courses/{id}`
- Chức năng: cập nhật môn học.
- Request: `id` và JSON hợp lệ.
- Response: `CourseDTO`.

### Xóa môn học
- Method: `DELETE`
- URL: `/courses/{id}`
- Chức năng: xóa môn học.
- Request: `id` trên path.
- Response: HTTP 204.

### Giữ chỗ nội bộ
- Method: `PATCH`
- URL: `/internal/courses/{id}/reserve-seat`
- Chức năng: giảm một chỗ còn lại nếu còn chỗ.
- Request: `id` trên path.
- Response: `CourseDTO`.

### Trả chỗ nội bộ
- Method: `PATCH`
- URL: `/internal/courses/{id}/release-seat`
- Chức năng: tăng một chỗ còn lại.
- Request: `id` trên path.
- Response: `CourseDTO`.

## 4. Cách chạy và kết quả
Từ `course-service`, chạy `mvnw.cmd spring-boot:run` sau khi MySQL có `course_db`. Kết quả của Lab là Course Service có CRUD, validation, xử lý exception, search/pagination và API giữ/trả chỗ.
