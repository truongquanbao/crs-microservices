# Lab 06

## 1. Mục tiêu
Hoàn thiện giao diện danh sách môn học trong `crs-frontend`, thay thế màn hình kiểm tra kết nối tạm của Lab 05. Giao diện gọi `GET /api/courses` qua API Gateway, hỗ trợ tìm kiếm, phân trang và xử lý đủ bốn trạng thái `loading`, `success`, `empty`, `error`.

## 2. Cấu trúc frontend
- `src/api/useCourses.ts`: custom hook quản lý request, dữ liệu, tổng số trang, lỗi và thao tác tải lại.
- `src/components/SearchBox.tsx`: ô tìm kiếm có debounce 400 ms và tự hủy timer cũ.
- `src/components/CourseList.tsx`: hiển thị trạng thái tải, lỗi, rỗng hoặc bảng môn học.
- `src/components/Pagination.tsx`: chuyển trang, đánh dấu trang hiện tại và ẩn khi chỉ có một trang.
- `src/App.tsx`: phối hợp tìm kiếm, danh sách, phân trang; tìm kiếm mới luôn đặt `page = 0`.

## 3. Luồng gọi API
```text
App.tsx
   |
   v
useCourses.ts
   |
   v
courseApi.ts -> axiosClient.ts
   |
   v
API Gateway :8080
   |
   v
Course Service :8082 -> course_db
```

Request sử dụng các tham số `keyword`, `page`, `size`. Hook dùng `AbortController` để hủy request cũ khi từ khóa hoặc trang thay đổi.

## 4. Bốn trạng thái giao diện
- `loading`: hiện thông báo đang tải danh sách.
- `success`: hiện bảng tên môn học, số tín chỉ và số chỗ còn lại.
- `empty`: hiện thông báo không tìm thấy môn học phù hợp.
- `error`: hiện thông báo lỗi kết nối hoặc message từ backend và nút thử lại.

Số chỗ còn lại được tô đỏ khi bằng `0`. Pagination chỉ xuất hiện khi API trả về từ hai trang trở lên.

## 5. Kiểm thử
1. Mở trang khi Gateway và Course Service đang chạy để kiểm tra trạng thái tải và bảng dữ liệu.
2. Nhập từ khóa không tồn tại, chờ khoảng 400 ms để kiểm tra trạng thái rỗng.
3. Xóa từ khóa để danh sách trở lại và trang được đặt về `0`.
4. Thêm đủ dữ liệu để có nhiều trang rồi kiểm tra các nút chuyển trang.
5. Tắt Gateway và bấm thử lại để kiểm tra trạng thái lỗi; bật Gateway rồi tải lại.

## 6. Cách chạy
```powershell
cd crs-frontend
npm run dev
```

Frontend dùng `VITE_API_BASE_URL=http://localhost:8080`. Trước khi mở giao diện, cần chạy `course-service` ở cổng `8082` và `api-gateway` ở cổng `8080`.

## 7. Kết quả
Lab 06 đã có giao diện danh sách môn học tách component đúng trách nhiệm, tìm kiếm có debounce, phân trang và xử lý đầy đủ Loading/Success/Empty/Error. Số bản ghi hiển thị phụ thuộc dữ liệu hiện có trong `course_db`; ảnh minh họa nhiều môn không phải dữ liệu được tự động tạo bởi frontend.
