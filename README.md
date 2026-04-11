# EasyTravel - Backend

EasyTravel là một ứng dụng nền tảng cho phép người dùng đặt các dịch vụ liên quan đến du lịch bao gồm tour du lịch và đặt phòng khách sạn.

Đây là repository cho hệ thống backend của EasyTravel, được xây dựng với Spring Boot, và cung cấp API RESTful cho các ứng dụng client (Web/Mobile).

## Các công nghệ được sử dụng

- **Java 17**
- **Spring Boot 3.3.1**
  - Spring Web
  - Spring Data JPA
  - Spring Data REST
  - Spring Security (với JWT)
  - Spring Boot Mail
  - OAuth2 Client
- **Cơ sở dữ liệu**: MySQL
- **Lưu trữ ảnh**: AWS S3
- **Thư viện tiện ích**:
  - Lombok
  - Dotenv (để quản lý biến môi trường)
  - JJWT (JSON Web Token)
- **Công cụ build**: Maven

## Cấu trúc dự án
Dự án được tổ chức theo kiến trúc MVC quen thuộc trong Spring Boot:
- `controller`: Chứa các REST API controllers để xử lý các request HTTP (Admin, User, Tour, Hotel, Booking, Review,...).
- `service`: Chứa các class xử lý logic nghiệp vụ của ứng dụng.
- `dao` (Data Access Object): Các repositories kế thừa từ Spring Data JPA/REST để tương tác với CSDL.
- `entity`: Định nghĩa các entity ánh xạ với các bảng trong cơ sở dữ liệu.
- `dto`: Các đối tượng dùng để truyền tải dữ liệu giữa client và server.
- `security`: Chứa cấu hình bảo mật, xử lý JWT và phân quyền.
- `Config`: Các cấu hình chung của hệ thống (như cấu hình CORS, Spring Data REST,...).
- `exception`: Chứa các lớp Custom Exception (BadRequestException, ResourceNotFoundException, UnauthorizedException,...) và `GlobalExceptionHandler` để xử lý lỗi thống nhất.
- `utils`: Các class tiện ích hỗ trợ (ví dụ: VNPayUtil).

## Các tính năng chính

- **Quản lý người dùng**: Đăng ký, đăng nhập (hỗ trợ JWT), cập nhật thông tin, thay đổi mật khẩu, quên mật khẩu (qua email), phân quyền (Admin, User, Hotel Manager, Tour Guide).
- **Quản lý Tour**: Thêm, sửa, xóa, tìm kiếm tour, sao chép tour, quản lý lịch trình (itinerary), thống kê số lượng đặt tour.
- **Quản lý Khách sạn & Phòng**: Thêm, sửa, xóa khách sạn, phòng, quản lý thông tin bởi Hotel Manager.
- **Đặt chỗ (Booking)**: Xử lý đặt tour, đặt phòng khách sạn.
- **Thanh toán**: Tích hợp thanh toán qua VNPay, hỗ trợ hoàn tiền (refund).
- **Đánh giá & Bình luận**: Người dùng có thể để lại review cho tour và khách sạn, bình luận trên các bài blog.
- **Quản lý Blog**: Đăng bài, quản lý bài viết du lịch.
- **Thông báo**: Gửi thông báo đến người dùng (cá nhân hoặc broadcast), quản lý trạng thái thông báo.
- **Xử lý tác vụ nền (Cron Jobs)**: Tự động dọn dẹp các booking đã quá hạn, gửi email thông báo trước ngày khởi hành tour.
- **Lưu trữ đám mây**: Upload và quản lý hình ảnh trên Amazon S3.

## Hướng dẫn cài đặt và chạy ứng dụng

### Yêu cầu cấu hình môi trường
Trước khi chạy ứng dụng, bạn cần cài đặt:
1. **JDK 17**
2. **MySQL Server** (và tạo một schema cho ứng dụng)
3. **Maven** (có thể sử dụng `mvnw` đi kèm trong source code)
4. Môi trường hỗ trợ file biến môi trường (`.env`)

### Cấu hình biến môi trường
Tạo file `.env` ở thư mục gốc của dự án (ngang hàng với `pom.xml`) và cấu hình các thông số cần thiết:

```env
# Cấu hình Database
DB_URL=jdbc:mysql://localhost:3306/easy_travel
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password

# Cấu hình JWT
JWT_SECRET=your_jwt_secret_key_here_must_be_long_enough

# Cấu hình AWS S3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_REGION=ap-southeast-1
AWS_BUCKET_NAME=your_bucket_name

# Cấu hình Mail
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_app_password

# Cấu hình VNPAY
VNPAY_TMNCODE=your_vnpay_tmncode
VNPAY_HASHSECRET=your_vnpay_hash_secret
VNPAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:8080/payment/vn-pay-callback

# Frontend URL (Dành cho CORS và callback)
FRONTEND_URL=http://localhost:5173
```
*(Lưu ý: Các key cấu hình cụ thể có thể khác tùy thuộc vào `application.properties`/`application.yml` của dự án)*

### Build và chạy dự án

Bạn có thể chạy dự án thông qua command line sử dụng Maven Wrapper:

```bash
# Compile và build dự án
./mvnw clean install

# Chạy ứng dụng
./mvnw spring-boot:run
```

Hoặc bạn có thể mở dự án bằng một IDE (IntelliJ IDEA, Eclipse,...) và chạy class `EasyTravelApplication.java`.

Ứng dụng sẽ mặc định khởi chạy ở port `8080` (trừ khi được cấu hình khác trong file properties).
