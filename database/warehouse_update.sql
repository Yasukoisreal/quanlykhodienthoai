-- =============================================================
-- WAREHOUSE_DB UPDATE - Bổ sung chức năng thiếu
-- Chạy file này SAU khi đã chạy warehouse_schema.sql
-- =============================================================

USE warehouse_db;

-- 1. Thêm password_hash vào employees (cho Login)
ALTER TABLE employees ADD COLUMN password_hash VARCHAR(255) DEFAULT '123456';

-- Cập nhật mật khẩu mặc định cho tất cả nhân viên (Tắt safe update tạm thời)
SET SQL_SAFE_UPDATES = 0;
UPDATE employees SET password_hash = '123456';
SET SQL_SAFE_UPDATES = 1;

-- 2. Bảng phiếu xuất kho
CREATE TABLE IF NOT EXISTS export_vouchers (
    id VARCHAR(20) PRIMARY KEY,
    customer VARCHAR(200) NOT NULL,
    warehouse_id VARCHAR(20),
    created_date VARCHAR(20),
    total_amount DOUBLE DEFAULT 0,
    creator VARCHAR(200),
    status VARCHAR(50) DEFAULT 'Nháp'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS export_voucher_products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voucher_id VARCHAR(20) NOT NULL,
    product_id VARCHAR(20) NOT NULL,
    product_name VARCHAR(200),
    quantity INT NOT NULL DEFAULT 0,
    unit_price DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (voucher_id) REFERENCES export_vouchers(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Bảng kiểm kê kho
CREATE TABLE IF NOT EXISTS inventory_checks (
    id VARCHAR(20) PRIMARY KEY,
    warehouse_id VARCHAR(20),
    warehouse_name VARCHAR(200),
    created_date VARCHAR(20),
    creator VARCHAR(200),
    status VARCHAR(50) DEFAULT 'Đang kiểm',
    notes TEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory_check_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    check_id VARCHAR(20) NOT NULL,
    product_id VARCHAR(20) NOT NULL,
    product_name VARCHAR(200),
    system_qty INT DEFAULT 0,
    actual_qty INT DEFAULT 0,
    difference INT DEFAULT 0,
    FOREIGN KEY (check_id) REFERENCES inventory_checks(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Bảng audit log
CREATE TABLE IF NOT EXISTS audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(100),
    action VARCHAR(50),
    target_table VARCHAR(50),
    target_id VARCHAR(50),
    detail TEXT
) ENGINE=InnoDB;

-- Dữ liệu mẫu audit log
INSERT INTO audit_logs (username, action, target_table, target_id, detail) VALUES
('admin_smartphone', 'INSERT', 'products', 'SP001', 'Thêm sản phẩm iPhone 16 Pro Max'),
('admin_smartphone', 'INSERT', 'import_vouchers', 'PN001', 'Tạo phiếu nhập PN001 - Apple Vietnam'),
('kho_thao', 'UPDATE', 'import_vouchers', 'PN001', 'Duyệt phiếu nhập PN001');

SELECT '✓ Database warehouse_db updated successfully!' AS status;
