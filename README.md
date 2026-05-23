# Smart Depot — Hệ Thống Quản Lý Kho Smartphone ERP 📱

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-blue?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

**Smart Depot** là hệ thống phần mềm quản trị kho hàng (Warehouse Management System) chuyên nghiệp dành riêng cho các chuỗi phân phối điện thoại di động (Smartphone). Được xây dựng dựa trên kiến trúc **Client-Server (TCP Socket)** kết hợp với giao diện **JavaFX** hiện đại, hệ thống mang đến giải pháp quản lý mạnh mẽ, an toàn và đồng bộ theo thời gian thực.

---

## 🌟 Tính Năng Nổi Bật

* **Kiến trúc Client-Server đồng bộ**: Client và Server giao tiếp thông qua TCP Sockets chuyên dụng, giúp nhiều máy trạm có thể cùng truy cập và cập nhật dữ liệu kho một cách đồng nhất.
* **Bảng điều khiển (Dashboard) thông minh**: Cung cấp cái nhìn tổng quan về doanh thu, tình trạng xuất nhập tồn kho, lượng hàng hóa phân bổ theo từng khu vực.
* **Quản trị Vòng đời Phiếu Nhập / Xuất Kho**: 
  * Quản lý phiếu nháp, trình phê duyệt phiếu.
  * Tự động cộng/trừ số lượng hàng tồn và không gian lưu trữ thực tế tại các Kho sau khi duyệt phiếu.
  * **In Hoá Đơn PDF**: Xuất nhanh các phiếu xuất kho dưới định dạng file PDF chuyên nghiệp phục vụ in ấn.
* **Phân quyền người dùng (Role-Based Access Control)**: Kiểm soát chặt chẽ những Module hiển thị dựa trên vai trò của nhân viên (Admin, Thủ kho, Nhân viên bán hàng...).
* **Truy Xuất Lịch Sử Hoạt Động (Audit Logs)**: Mọi thao tác Thêm/Sửa/Xóa dữ liệu đều được hệ thống ghi nhận chi tiết theo từng user, đảm bảo tính minh bạch tuyệt đối.
* **Trích Xuất Dữ Liệu Nhanh Chóng**: Hỗ trợ xuất trực tiếp mọi bảng dữ liệu (Sản phẩm, Khách hàng, Nhân viên,...) sang định dạng **Excel (.xlsx)** thông qua thư viện Apache POI.

---

## 🛠 Công Nghệ Sử Dụng

* **Ngôn ngữ**: Java 21
* **Giao diện người dùng (UI)**: JavaFX 21 (Vanilla CSS)
* **Cơ sở dữ liệu**: MySQL 8.x
* **Quản lý Dependencies / Build tool**: Apache Maven
* **Thư viện bên thứ ba**:
  * `mysql-connector-j`: Giao tiếp CSDL.
  * `Apache POI`: Hỗ trợ Export Excel (.xlsx).
  * `OpenPDF`: Hỗ trợ Export Hoá đơn PDF.

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### 1. Chuẩn bị Cơ sở dữ liệu (MySQL)
* Đảm bảo máy tính của bạn đã cài đặt và chạy **MySQL Server** (Cổng mặc định: 3306).
* Mở MySQL Client hoặc công cụ quản trị (như DBeaver, Navicat, MySQL Workbench).
* Lần lượt chạy 2 script SQL có trong thư mục `database/` theo thứ tự sau:
  1. `warehouse_schema.sql` (Khởi tạo Database, cấu trúc các Bảng và dữ liệu mẫu).
  2. `warehouse_update.sql` (Cập nhật Password cho các tài khoản mẫu).
* *Lưu ý: Mật khẩu kết nối Database đang được set mặc định là rỗng `""` trong mã nguồn Server. Vui lòng cấu hình lại username/password trong class `DatabaseManager.java` nếu DB của bạn có đặt mật khẩu.*

### 2. Cài đặt Dependencies
Dự án được quản lý bằng Maven. Để tải tất cả thư viện cần thiết, hãy chạy lệnh sau ở thư mục gốc của dự án:
```bash
mvn clean install
```

### 3. Khởi chạy Ứng dụng
Bởi vì đây là một ứng dụng Client - Server, bạn cần bắt buộc phải chạy Server trước để tiếp nhận các luồng kết nối TCP.

**BƯỚC 1: BẬT SERVER**
Chạy class chứa hàm `main` của Server:
```
com.java.quanlykho.server.ServerApp
```
*Server sẽ khởi động và lắng nghe kết nối tại cổng mặc định là `8888`.*

**BƯỚC 2: BẬT CLIENT (GIAO DIỆN NGƯỜI DÙNG)**
Tiếp theo, bạn chạy class `main` của Client:
```
com.java.quanlykho.MainApp
```

### 4. Đăng nhập hệ thống
Bạn có thể test phần mềm bằng các tài khoản phân quyền đã được chuẩn bị sẵn:
* **Quyền Admin (Toàn quyền):**
  * Username: `admin_smartphone`
  * Password: `123456`
* **Quyền Thủ Kho:**
  * Username: `kho_thao`
  * Password: `123456`
* **Quyền Nhân viên (Bán hàng):**
  * Username: `nv_duy`
  * Password: `123456`

---

## 📂 Cấu Trúc Mã Nguồn

```text
src/main/
├── java/com/java/quanlykho/
│   ├── controller/      # Các Controller điều khiển giao diện (Login, Dashboard, Vouchers...)
│   ├── model/           # Các đối tượng DTO (Product, ImportVoucher, AuditLog...)
│   ├── network/         # Tầng kết nối TCP Socket (Request, Response, NetworkService)
│   ├── server/          # Mã nguồn Server (ServerApp, ClientHandler, DatabaseManager)
│   ├── util/            # Các tiện ích chung (FormatUtil, ExportUtil, PDFUtil)
│   └── MainApp.java     # Lớp Main của UI Client
├── resources/
│   └── css/             # Stylesheet chứa giao diện tùy biến cho JavaFX
└── database/
    ├── warehouse_schema.sql  # Script tạo DB ban đầu
    └── warehouse_update.sql  # Script vá lỗi và bổ sung tính năng DB
```

---
*Phát triển và hoàn thiện bởi AI Coding Assistant* ✨
