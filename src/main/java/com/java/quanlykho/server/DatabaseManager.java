package com.java.quanlykho.server;

import com.java.quanlykho.model.*;

import java.sql.*;
import java.util.*;

/**
 * Quản lý kết nối JDBC và thao tác CRUD đến MySQL warehouse_db.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/warehouse_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "123456";

    private Connection connection;

    public DatabaseManager() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("[DB] Kết nối MySQL thành công: warehouse_db");
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Đã đóng kết nối MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =============================================================
    // PRODUCTS
    // =============================================================

    public List<Product> getAllProducts() throws SQLException {
        ensureConnection();
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getString("id"),
                    rs.getString("model_name"),
                    rs.getString("brand"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    getImeisByProductId(rs.getString("id"))
                );
                list.add(p);
            }
        }
        return list;
    }

    private List<String> getImeisByProductId(String productId) throws SQLException {
        List<String> imeis = new ArrayList<>();
        String sql = "SELECT imei FROM product_imeis WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imeis.add(rs.getString("imei"));
                }
            }
        }
        return imeis;
    }

    public void insertProduct(Product p) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO products (id, model_name, brand, price, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getModelName());
            ps.setString(3, p.getBrand());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.executeUpdate();
        }
        // Insert IMEIs
        if (p.getImeiList() != null && !p.getImeiList().isEmpty()) {
            String imeiSql = "INSERT INTO product_imeis (product_id, imei) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(imeiSql)) {
                for (String imei : p.getImeiList()) {
                    ps.setString(1, p.getId());
                    ps.setString(2, imei);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    public void updateProduct(Product p) throws SQLException {
        ensureConnection();
        String sql = "UPDATE products SET model_name=?, brand=?, price=?, stock=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getModelName());
            ps.setString(2, p.getBrand());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getId());
            ps.executeUpdate();
        }
        // Refresh IMEIs
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM product_imeis WHERE product_id=?")) {
            ps.setString(1, p.getId());
            ps.executeUpdate();
        }
        if (p.getImeiList() != null && !p.getImeiList().isEmpty()) {
            String imeiSql = "INSERT INTO product_imeis (product_id, imei) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(imeiSql)) {
                for (String imei : p.getImeiList()) {
                    ps.setString(1, p.getId());
                    ps.setString(2, imei);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    public void deleteProduct(String id) throws SQLException {
        ensureConnection();
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // WAREHOUSES
    // =============================================================

    public List<Warehouse> getAllWarehouses() throws SQLException {
        ensureConnection();
        List<Warehouse> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM warehouses ORDER BY id")) {
            while (rs.next()) {
                list.add(new Warehouse(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getInt("used_space")
                ));
            }
        }
        return list;
    }

    public void insertWarehouse(Warehouse w) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO warehouses (id, name, location, capacity, used_space) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, w.getId());
            ps.setString(2, w.getName());
            ps.setString(3, w.getLocation());
            ps.setInt(4, w.getCapacity());
            ps.setInt(5, w.getUsedSpace());
            ps.executeUpdate();
        }
    }

    public void updateWarehouse(Warehouse w) throws SQLException {
        ensureConnection();
        String sql = "UPDATE warehouses SET name=?, location=?, capacity=?, used_space=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, w.getName());
            ps.setString(2, w.getLocation());
            ps.setInt(3, w.getCapacity());
            ps.setInt(4, w.getUsedSpace());
            ps.setString(5, w.getId());
            ps.executeUpdate();
        }
    }

    public void deleteWarehouse(String id) throws SQLException {
        ensureConnection();
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM warehouses WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // CUSTOMERS
    // =============================================================

    public List<Customer> getAllCustomers() throws SQLException {
        ensureConnection();
        List<Customer> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers ORDER BY id")) {
            while (rs.next()) {
                list.add(new Customer(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("status"),
                    rs.getInt("total_orders"),
                    rs.getDouble("total_spend"),
                    rs.getString("tier")
                ));
            }
        }
        return list;
    }

    public void insertCustomer(Customer c) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO customers (id, name, phone, email, status, total_orders, total_spend, tier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getStatus());
            ps.setInt(6, c.getTotalOrders());
            ps.setDouble(7, c.getTotalSpend());
            ps.setString(8, c.getTier());
            ps.executeUpdate();
        }
    }

    public void deleteCustomer(String id) throws SQLException {
        ensureConnection();
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM customers WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // SUPPLIERS
    // =============================================================

    public List<Supplier> getAllSuppliers() throws SQLException {
        ensureConnection();
        List<Supplier> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM suppliers ORDER BY id")) {
            while (rs.next()) {
                list.add(new Supplier(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("status")
                ));
            }
        }
        return list;
    }

    public void insertSupplier(Supplier s) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO suppliers (id, name, phone, email, address, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getStatus());
            ps.executeUpdate();
        }
    }

    public void deleteSupplier(String id) throws SQLException {
        ensureConnection();
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM suppliers WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // EMPLOYEES
    // =============================================================

    public List<Employee> getAllEmployees() throws SQLException {
        ensureConnection();
        List<Employee> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees ORDER BY id")) {
            while (rs.next()) {
                list.add(new Employee(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getString("phone")
                ));
            }
        }
        return list;
    }

    public void insertEmployee(Employee e) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO employees (id, username, full_name, email, role, status, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, e.getId());
            ps.setString(2, e.getUsername());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getRole());
            ps.setString(6, e.getStatus());
            ps.setString(7, e.getPhone());
            ps.executeUpdate();
        }
    }

    public void deleteEmployee(String id) throws SQLException {
        ensureConnection();
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM employees WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // IMPORT VOUCHERS
    // =============================================================

    public List<ImportVoucher> getAllVouchers() throws SQLException {
        ensureConnection();
        List<ImportVoucher> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM import_vouchers ORDER BY id")) {
            while (rs.next()) {
                String vid = rs.getString("id");
                List<ImportVoucherProduct> products = getVoucherProducts(vid);
                list.add(new ImportVoucher(
                    vid,
                    rs.getString("supplier"),
                    rs.getString("warehouse_id"),
                    rs.getString("created_date"),
                    products,
                    rs.getDouble("total_amount"),
                    rs.getString("creator"),
                    rs.getString("status")
                ));
            }
        }
        return list;
    }

    private List<ImportVoucherProduct> getVoucherProducts(String voucherId) throws SQLException {
        List<ImportVoucherProduct> list = new ArrayList<>();
        String sql = "SELECT * FROM import_voucher_products WHERE voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ImportVoucherProduct(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                    ));
                }
            }
        }
        return list;
    }

    public void insertVoucher(ImportVoucher v) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO import_vouchers (id, supplier, warehouse_id, created_date, total_amount, creator, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, v.getId());
            ps.setString(2, v.getSupplier());
            ps.setString(3, v.getWarehouseId());
            ps.setString(4, v.getDate());
            ps.setDouble(5, v.getTotalAmount());
            ps.setString(6, v.getCreator());
            ps.setString(7, v.getStatus());
            ps.executeUpdate();
        }
        // Insert voucher products
        if (v.getProducts() != null) {
            String vpSql = "INSERT INTO import_voucher_products (voucher_id, product_id, product_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(vpSql)) {
                for (ImportVoucherProduct vp : v.getProducts()) {
                    ps.setString(1, v.getId());
                    ps.setString(2, vp.getProductId());
                    ps.setString(3, vp.getModelName());
                    ps.setInt(4, vp.getQuantity());
                    ps.setDouble(5, vp.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    public void updateVoucherStatus(String id, String status) throws SQLException {
        ensureConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE import_vouchers SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // ATTRIBUTES
    // =============================================================

    public Attributes getAllAttributes() throws SQLException {
        ensureConnection();
        Attributes attrs = new Attributes();
        List<String> brands = new ArrayList<>();
        List<Attributes.AttributeColor> colors = new ArrayList<>();
        List<String> storages = new ArrayList<>();
        List<String> cpus = new ArrayList<>();
        List<String> screens = new ArrayList<>();
        List<String> networks = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM attributes ORDER BY id")) {
            while (rs.next()) {
                String type = rs.getString("attr_type");
                String value = rs.getString("attr_value");
                String extra = rs.getString("extra");
                switch (type) {
                    case "brand" -> brands.add(value);
                    case "color" -> colors.add(new Attributes.AttributeColor(value, extra));
                    case "storage" -> storages.add(value);
                    case "cpu" -> cpus.add(value);
                    case "screen" -> screens.add(value);
                    case "network" -> networks.add(value);
                }
            }
        }
        attrs.setBrands(brands);
        attrs.setColors(colors);
        attrs.setStorages(storages);
        attrs.setCpuList(cpus);
        attrs.setScreenList(screens);
        attrs.setNetworkList(networks);
        return attrs;
    }

    public void insertAttribute(String type, String value, String extra) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO attributes (attr_type, attr_value, extra) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, value);
            ps.setString(3, extra);
            ps.executeUpdate();
        }
    }

    public void deleteAttribute(String type, String value) throws SQLException {
        ensureConnection();
        String sql = "DELETE FROM attributes WHERE attr_type=? AND attr_value=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }

    // =============================================================
    // ROLE PERMISSIONS
    // =============================================================

    public List<RolePermissions> getAllPermissions() throws SQLException {
        ensureConnection();
        Map<String, Map<String, ModulePermission>> roleMap = new LinkedHashMap<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM role_permissions ORDER BY role, module")) {
            while (rs.next()) {
                String role = rs.getString("role");
                String module = rs.getString("module");
                ModulePermission mp = new ModulePermission(
                    rs.getBoolean("can_view"),
                    rs.getBoolean("can_add"),
                    rs.getBoolean("can_edit"),
                    rs.getBoolean("can_delete"),
                    rs.getBoolean("can_export")
                );
                roleMap.computeIfAbsent(role, k -> new LinkedHashMap<>()).put(module, mp);
            }
        }

        List<RolePermissions> list = new ArrayList<>();
        for (var entry : roleMap.entrySet()) {
            list.add(new RolePermissions(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    // =============================================================
    // LOGIN / AUTHENTICATION
    // =============================================================

    public Employee login(String username, String password) throws SQLException {
        ensureConnection();
        String sql = "SELECT * FROM employees WHERE username=? AND password_hash=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getString("id"), rs.getString("username"),
                        rs.getString("full_name"), rs.getString("email"),
                        rs.getString("role"), rs.getString("status"),
                        rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }

    // =============================================================
    // UPDATE METHODS (Customer, Supplier, Employee)
    // =============================================================

    public void updateCustomer(Customer c) throws SQLException {
        ensureConnection();
        String sql = "UPDATE customers SET name=?, phone=?, email=?, status=?, total_orders=?, total_spend=?, tier=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getStatus());
            ps.setInt(5, c.getTotalOrders());
            ps.setDouble(6, c.getTotalSpend());
            ps.setString(7, c.getTier());
            ps.setString(8, c.getId());
            ps.executeUpdate();
        }
    }

    public void updateSupplier(Supplier s) throws SQLException {
        ensureConnection();
        String sql = "UPDATE suppliers SET name=?, phone=?, email=?, address=?, status=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getPhone());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getAddress());
            ps.setString(5, s.getStatus());
            ps.setString(6, s.getId());
            ps.executeUpdate();
        }
    }

    public void updateEmployee(Employee e) throws SQLException {
        ensureConnection();
        String sql = "UPDATE employees SET username=?, full_name=?, email=?, role=?, status=?, phone=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, e.getUsername());
            ps.setString(2, e.getFullName());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getRole());
            ps.setString(5, e.getStatus());
            ps.setString(6, e.getPhone());
            ps.setString(7, e.getId());
            ps.executeUpdate();
        }
    }

    // =============================================================
    // EXPORT VOUCHERS
    // =============================================================

    public List<ExportVoucher> getAllExportVouchers() throws SQLException {
        ensureConnection();
        List<ExportVoucher> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM export_vouchers ORDER BY id")) {
            while (rs.next()) {
                String vid = rs.getString("id");
                List<ImportVoucherProduct> products = getExportVoucherProducts(vid);
                list.add(new ExportVoucher(vid, rs.getString("customer"),
                    rs.getString("warehouse_id"), rs.getString("created_date"),
                    products, rs.getDouble("total_amount"),
                    rs.getString("creator"), rs.getString("status")));
            }
        }
        return list;
    }

    private List<ImportVoucherProduct> getExportVoucherProducts(String voucherId) throws SQLException {
        List<ImportVoucherProduct> list = new ArrayList<>();
        String sql = "SELECT * FROM export_voucher_products WHERE voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ImportVoucherProduct(
                        rs.getString("product_id"), rs.getString("product_name"),
                        rs.getInt("quantity"), rs.getDouble("unit_price")));
                }
            }
        }
        return list;
    }

    public void insertExportVoucher(ExportVoucher v) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO export_vouchers (id, customer, warehouse_id, created_date, total_amount, creator, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, v.getId());
            ps.setString(2, v.getCustomer());
            ps.setString(3, v.getWarehouseId());
            ps.setString(4, v.getDate());
            ps.setDouble(5, v.getTotalAmount());
            ps.setString(6, v.getCreator());
            ps.setString(7, v.getStatus());
            ps.executeUpdate();
        }
        if (v.getProducts() != null) {
            String vpSql = "INSERT INTO export_voucher_products (voucher_id, product_id, product_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(vpSql)) {
                for (ImportVoucherProduct vp : v.getProducts()) {
                    ps.setString(1, v.getId());
                    ps.setString(2, vp.getProductId());
                    ps.setString(3, vp.getModelName());
                    ps.setInt(4, vp.getQuantity());
                    ps.setDouble(5, vp.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    // =============================================================
    // INVENTORY CHECKS
    // =============================================================

    public List<InventoryCheck> getAllInventoryChecks() throws SQLException {
        ensureConnection();
        List<InventoryCheck> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM inventory_checks ORDER BY id DESC")) {
            while (rs.next()) {
                String cid = rs.getString("id");
                list.add(new InventoryCheck(cid, rs.getString("warehouse_id"),
                    rs.getString("warehouse_name"), rs.getString("created_date"),
                    rs.getString("creator"), rs.getString("status"),
                    rs.getString("notes"), getInventoryCheckItems(cid)));
            }
        }
        return list;
    }

    private List<InventoryCheckItem> getInventoryCheckItems(String checkId) throws SQLException {
        List<InventoryCheckItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory_check_items WHERE check_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, checkId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventoryCheckItem item = new InventoryCheckItem(
                        rs.getString("product_id"), rs.getString("product_name"),
                        rs.getInt("system_qty"), rs.getInt("actual_qty"));
                    item.setDifference(rs.getInt("difference"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void insertInventoryCheck(InventoryCheck ic) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO inventory_checks (id, warehouse_id, warehouse_name, created_date, creator, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ic.getId());
            ps.setString(2, ic.getWarehouseId());
            ps.setString(3, ic.getWarehouseName());
            ps.setString(4, ic.getDate());
            ps.setString(5, ic.getCreator());
            ps.setString(6, ic.getStatus());
            ps.setString(7, ic.getNotes());
            ps.executeUpdate();
        }
        if (ic.getItems() != null) {
            String itemSql = "INSERT INTO inventory_check_items (check_id, product_id, product_name, system_qty, actual_qty, difference) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(itemSql)) {
                for (InventoryCheckItem item : ic.getItems()) {
                    ps.setString(1, ic.getId());
                    ps.setString(2, item.getProductId());
                    ps.setString(3, item.getProductName());
                    ps.setInt(4, item.getSystemQty());
                    ps.setInt(5, item.getActualQty());
                    ps.setInt(6, item.getDifference());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    // =============================================================
    // AUDIT LOG
    // =============================================================

    public void insertAuditLog(AuditLog log) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO audit_logs (username, action, target_table, target_id, detail) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, log.getUsername());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getTargetTable());
            ps.setString(4, log.getTargetId());
            ps.setString(5, log.getDetail());
            ps.executeUpdate();
        }
    }

    public List<AuditLog> getAllAuditLogs() throws SQLException {
        ensureConnection();
        List<AuditLog> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM audit_logs ORDER BY id DESC LIMIT 100")) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setTimestamp(rs.getString("timestamp"));
                log.setUsername(rs.getString("username"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                log.setTargetId(rs.getString("target_id"));
                log.setDetail(rs.getString("detail"));
                list.add(log);
            }
        }
        return list;
    }
}
