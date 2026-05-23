-- =============================================================
-- WAREHOUSE_DB - Hệ Thống Quản Lý Kho Smartphone
-- MySQL Schema + Sample Data
-- =============================================================

DROP DATABASE IF EXISTS warehouse_db;
CREATE DATABASE warehouse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE warehouse_db;

-- -----------------------------------------------------------
-- 1. PRODUCTS
-- -----------------------------------------------------------
CREATE TABLE products (
    id VARCHAR(20) PRIMARY KEY,
    model_name VARCHAR(200) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL DEFAULT 0,
    stock INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE product_imeis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(20) NOT NULL,
    imei VARCHAR(50) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 2. WAREHOUSES
-- -----------------------------------------------------------
CREATE TABLE warehouses (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(500),
    capacity INT NOT NULL DEFAULT 0,
    used_space INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 3. CUSTOMERS
-- -----------------------------------------------------------
CREATE TABLE customers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(200),
    status VARCHAR(50) DEFAULT 'Đang hoạt động',
    total_orders INT DEFAULT 0,
    total_spend DOUBLE DEFAULT 0,
    tier VARCHAR(50) DEFAULT 'Bạc'
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 4. SUPPLIERS
-- -----------------------------------------------------------
CREATE TABLE suppliers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(200),
    address VARCHAR(500),
    status VARCHAR(50) DEFAULT 'Hợp tác'
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 5. EMPLOYEES
-- -----------------------------------------------------------
CREATE TABLE employees (
    id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    email VARCHAR(200),
    role VARCHAR(50) DEFAULT 'Nhân viên',
    status VARCHAR(50) DEFAULT 'Đang làm việc',
    phone VARCHAR(20)
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 6. IMPORT VOUCHERS
-- -----------------------------------------------------------
CREATE TABLE import_vouchers (
    id VARCHAR(20) PRIMARY KEY,
    supplier VARCHAR(200) NOT NULL,
    warehouse_id VARCHAR(20),
    created_date VARCHAR(20),
    total_amount DOUBLE DEFAULT 0,
    creator VARCHAR(200),
    status VARCHAR(50) DEFAULT 'Nháp'
) ENGINE=InnoDB;

CREATE TABLE import_voucher_products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voucher_id VARCHAR(20) NOT NULL,
    product_id VARCHAR(20) NOT NULL,
    product_name VARCHAR(200),
    quantity INT NOT NULL DEFAULT 0,
    unit_price DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (voucher_id) REFERENCES import_vouchers(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 7. ATTRIBUTES (key-value store)
-- -----------------------------------------------------------
CREATE TABLE attributes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attr_type VARCHAR(50) NOT NULL,
    attr_value VARCHAR(200) NOT NULL,
    extra VARCHAR(50) DEFAULT NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 8. ROLE PERMISSIONS
-- -----------------------------------------------------------
CREATE TABLE role_permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    module VARCHAR(50) NOT NULL,
    can_view BOOLEAN DEFAULT FALSE,
    can_add BOOLEAN DEFAULT FALSE,
    can_edit BOOLEAN DEFAULT FALSE,
    can_delete BOOLEAN DEFAULT FALSE,
    can_export BOOLEAN DEFAULT FALSE,
    UNIQUE KEY uk_role_module (role, module)
) ENGINE=InnoDB;


-- =============================================================
-- SAMPLE DATA (từ SampleData.java / mockDb.ts)
-- =============================================================

-- Products
INSERT INTO products (id, model_name, brand, price, stock) VALUES
('SP001', 'iPhone 16 Pro Max', 'Apple', 34990000, 45),
('SP002', 'Samsung Galaxy S24 Ultra', 'Samsung', 29990000, 28),
('SP003', 'Xiaomi 14 Ultra', 'Xiaomi', 26490000, 12),
('SP004', 'iPhone 15 Pro', 'Apple', 24990000, 50),
('SP005', 'OPPO Find X7 Ultra', 'OPPO', 21500000, 8),
('SP006', 'iPhone 13 128GB', 'Apple', 13990000, 65),
('SP007', 'Galaxy A55 5G', 'Samsung', 9890000, 4);

-- Product IMEIs
INSERT INTO product_imeis (product_id, imei) VALUES
('SP001', 'IMEI8890124901'), ('SP001', 'IMEI8890124902'), ('SP001', 'IMEI8890124903'),
('SP002', 'IMEI9901429011'), ('SP002', 'IMEI9901429012'),
('SP003', 'IMEI7755120199'), ('SP003', 'IMEI7755120198'),
('SP004', 'IMEI884400291'),
('SP005', 'IMEI554421900');

-- Warehouses
INSERT INTO warehouses (id, name, location, capacity, used_space) VALUES
('K01', 'Kho Trung Tâm - Quận 1', '12 Đinh Tiên Hoàng, Quận 1, Tp.HCM', 200, 164),
('K02', 'Kho Trung Chuyển - Thủ Đức', '482 Song Hành, Tăng Nhơn Phú, Tp. Thủ Đức', 500, 110),
('K03', 'Kho Dự Phòng - Hóc Môn', '148 Nguyễn Ảnh Thủ, Hóc Môn, Tp.HCM', 300, 15);

-- Customers
INSERT INTO customers (id, name, phone, email, status, total_orders, total_spend, tier) VALUES
('KH001', 'Nguyễn Văn Đạt', '0901234567', 'dat.nguyen@gmail.com', 'Đang hoạt động', 24, 185000000, 'Kim cương'),
('KH002', 'Trần Thị Thu Thảo', '0918765432', 'thao.ttt@hotmail.com', 'Đang hoạt động', 8, 62000000, 'Vàng'),
('KH003', 'Lê Minh Quân', '0977222111', 'quan.leminh@yahoo.com', 'Đang hoạt động', 3, 14000000, 'Bạc'),
('KH004', 'Phạm Hồng Ngọc', '0934500900', 'ngocph@gmail.com', 'Tạm khóa', 0, 0, 'Bạc');

-- Suppliers
INSERT INTO suppliers (id, name, phone, email, address, status) VALUES
('NCC01', 'Apple Vietnam Distribution Co.', '1800-1125', 'orders@apple.vn', 'Tòa nhà Petrovietnam, Lê Duẩn, Quận 1, Tp.HCM', 'Hợp tác'),
('NCC02', 'Samsung Vina Mobile Division Ltd.', '028-382173', 'contact.samsung@samsung.com', 'Khu công nghệ cao Sài Gòn, Quận 9, Tp.HCM', 'Hợp tác'),
('NCC03', 'Công ty Cổ phần Digiworld (DGW)', '028-392900', 'mobile@digiworld.com.vn', '195 Nguyễn Văn Trỗi, Phú Nhuận, Tp.HCM', 'Hợp tác'),
('NCC04', 'Xiaomi Việt Nam Retail Group', '1900-6012', 'support@xiaomi-vietnam.com', 'Số 1 Cù Lao, Phường 2, Phú Nhuận, Tp.HCM', 'Tạm ngưng');

-- Employees
INSERT INTO employees (id, username, full_name, email, role, status, phone) VALUES
('NV001', 'admin_smartphone', 'Lê Hoàng Tú (Giám Sát)', 'tu.lehoang@smartphonekho.vn', 'Admin', 'Đang làm việc', '0903334445'),
('NV002', 'kho_thao', 'Vũ Thị Thảo (Thủ kho)', 'thao.vu@smartphonekho.vn', 'Thủ Kho', 'Đang làm việc', '0988556677'),
('NV003', 'nv_duy', 'Nguyễn Nhân Duy', 'duy.nn@smartphonekho.vn', 'Nhân viên', 'Đang làm việc', '0911223344'),
('NV004', 'nv_linh', 'Trần Khánh Linh', 'linh.tk@smartphonekho.vn', 'Nhân viên', 'Nghỉ phép', '0944556622');

-- Import Vouchers
INSERT INTO import_vouchers (id, supplier, warehouse_id, created_date, total_amount, creator, status) VALUES
('PN001', 'Apple Vietnam Distribution Co.', 'K01', '2024-05-18', 459000000, 'Vũ Thị Thảo (Thủ kho)', 'Đã Duyệt'),
('PN002', 'Công ty Cổ phần Digiworld (DGW)', 'K02', '2024-05-20', 100000000, 'Lê Hoàng Tú (Giám Sát)', 'Nháp');

INSERT INTO import_voucher_products (voucher_id, product_id, product_name, quantity, unit_price) VALUES
('PN001', 'SP001', 'iPhone 16 Pro Max', 10, 34000000),
('PN001', 'SP004', 'iPhone 15 Pro', 5, 23800000),
('PN002', 'SP003', 'Xiaomi 14 Ultra', 4, 25000000);

-- Attributes
INSERT INTO attributes (attr_type, attr_value, extra) VALUES
('brand', 'Apple', NULL), ('brand', 'Samsung', NULL), ('brand', 'Xiaomi', NULL),
('brand', 'OPPO', NULL), ('brand', 'Vivo', NULL),
('color', 'Titanium đen', '#1c1c1e'), ('color', 'Titanium tự nhiên', '#b4b2ad'),
('color', 'Trắng ngọc trai', '#f2f2f7'), ('color', 'Midnight xanh', '#1d2b3a'),
('color', 'Vàng Champagne', '#e5d3b3'),
('storage', '128GB', NULL), ('storage', '256GB', NULL), ('storage', '512GB', NULL), ('storage', '1TB', NULL),
('cpu', 'Apple A18 Pro', NULL), ('cpu', 'Snapdragon 8 Gen 4', NULL), ('cpu', 'Dimensity 9400', NULL),
('cpu', 'Apple A17 Pro', NULL), ('cpu', 'Snapdragon 8 Gen 3', NULL),
('screen', 'Super Retina XDR 6.9"', NULL), ('screen', 'Dynamic AMOLED 2X 6.8"', NULL),
('screen', 'AMOLED 120Hz 6.7"', NULL), ('screen', 'IPS LCD 90Hz 6.5"', NULL),
('network', '5G Sub-6GHz', NULL), ('network', '5G Dual-SIM', NULL), ('network', '4G LTE Only / Volte', NULL);

-- Role Permissions - Admin (full access)
INSERT INTO role_permissions (role, module, can_view, can_add, can_edit, can_delete, can_export) VALUES
('Admin', 'dashboard', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'products', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'attributes', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'warehouses', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'vouchers', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'customers', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'suppliers', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'employees', TRUE, TRUE, TRUE, TRUE, TRUE),
('Admin', 'permissions', TRUE, TRUE, TRUE, TRUE, TRUE);

-- Role Permissions - Thủ Kho
INSERT INTO role_permissions (role, module, can_view, can_add, can_edit, can_delete, can_export) VALUES
('Thủ Kho', 'dashboard', TRUE, FALSE, FALSE, FALSE, TRUE),
('Thủ Kho', 'products', TRUE, TRUE, TRUE, FALSE, TRUE),
('Thủ Kho', 'attributes', TRUE, TRUE, FALSE, FALSE, FALSE),
('Thủ Kho', 'warehouses', TRUE, FALSE, FALSE, FALSE, FALSE),
('Thủ Kho', 'vouchers', TRUE, TRUE, TRUE, TRUE, TRUE),
('Thủ Kho', 'customers', TRUE, FALSE, FALSE, FALSE, FALSE),
('Thủ Kho', 'suppliers', TRUE, TRUE, FALSE, FALSE, TRUE),
('Thủ Kho', 'employees', FALSE, FALSE, FALSE, FALSE, FALSE),
('Thủ Kho', 'permissions', FALSE, FALSE, FALSE, FALSE, FALSE);

-- Role Permissions - Nhân viên
INSERT INTO role_permissions (role, module, can_view, can_add, can_edit, can_delete, can_export) VALUES
('Nhân viên', 'dashboard', TRUE, FALSE, FALSE, FALSE, FALSE),
('Nhân viên', 'products', TRUE, FALSE, FALSE, FALSE, TRUE),
('Nhân viên', 'attributes', TRUE, FALSE, FALSE, FALSE, FALSE),
('Nhân viên', 'warehouses', TRUE, FALSE, FALSE, FALSE, FALSE),
('Nhân viên', 'vouchers', TRUE, TRUE, FALSE, FALSE, FALSE),
('Nhân viên', 'customers', TRUE, TRUE, TRUE, FALSE, FALSE),
('Nhân viên', 'suppliers', TRUE, FALSE, FALSE, FALSE, FALSE),
('Nhân viên', 'employees', FALSE, FALSE, FALSE, FALSE, FALSE),
('Nhân viên', 'permissions', FALSE, FALSE, FALSE, FALSE, FALSE);

SELECT '✓ Database warehouse_db created successfully!' AS status;
SELECT CONCAT('  Tables: ', COUNT(*), ' tables created') AS info FROM information_schema.tables WHERE table_schema = 'warehouse_db';
