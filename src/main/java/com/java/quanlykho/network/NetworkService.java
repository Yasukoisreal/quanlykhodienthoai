package com.java.quanlykho.network;

import com.java.quanlykho.model.*;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * TCP Client - kết nối đến ServerApp qua port 8888.
 * Cung cấp phương thức tiện ích cho các module CRUD.
 * Singleton pattern.
 */
public class NetworkService {

    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    private static NetworkService instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected = false;
    private String currentUser = "system";

    private NetworkService() {}

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    private void logAudit(String action, String targetTable, String targetId, String detail) {
        if (!connected) return;
        AuditLog log = new AuditLog(currentUser, action, targetTable, targetId, detail);
        sendRequest(new Request("INSERT", "audit_logs", log));
    }

    public static synchronized NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    /**
     * Kết nối đến TCP Server.
     */
    public boolean connect() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            System.out.println("[Client] Kết nối TCP Server thành công: " + HOST + ":" + PORT);
            return true;
        } catch (IOException e) {
            connected = false;
            System.err.println("[Client] Không thể kết nối TCP Server: " + e.getMessage());
            return false;
        }
    }

    public boolean isConnected() { return connected; }

    /**
     * Gửi request và nhận response (synchronous).
     */
    public synchronized Response sendRequest(Request request) {
        if (!connected) {
            return Response.error("Chưa kết nối đến server");
        }
        try {
            out.writeObject(request);
            out.flush();
            out.reset();
            return (Response) in.readObject();
        } catch (Exception e) {
            connected = false;
            return Response.error("Lỗi giao tiếp: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            connected = false;
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("[Client] Ngắt kết nối TCP Server.");
        } catch (IOException ignored) {}
    }

    // =============================================================
    // PRODUCTS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<Product> getAllProducts() {
        Response res = sendRequest(new Request("GET_ALL", "products"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<Product>) res.getData();
        }
        return List.of();
    }

    public boolean insertProduct(Product p) {
        Response res = sendRequest(new Request("INSERT", "products", p));
        if (res.isSuccess()) logAudit("INSERT", "products", p.getId(), "Thêm sản phẩm mới");
        return res.isSuccess();
    }

    public boolean updateProduct(Product p) {
        Response res = sendRequest(new Request("UPDATE", "products", p));
        if (res.isSuccess()) logAudit("UPDATE", "products", p.getId(), "Cập nhật sản phẩm");
        return res.isSuccess();
    }

    public boolean deleteProduct(String id) {
        Response res = sendRequest(new Request("DELETE", "products", id));
        if (res.isSuccess()) logAudit("DELETE", "products", id, "Xóa sản phẩm");
        return res.isSuccess();
    }

    // =============================================================
    // WAREHOUSES
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<Warehouse> getAllWarehouses() {
        Response res = sendRequest(new Request("GET_ALL", "warehouses"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<Warehouse>) res.getData();
        }
        return List.of();
    }

    public boolean insertWarehouse(Warehouse w) {
        Response res = sendRequest(new Request("INSERT", "warehouses", w));
        if (res.isSuccess()) logAudit("INSERT", "warehouses", w.getId(), "Thêm kho bãi mới");
        return res.isSuccess();
    }

    public boolean updateWarehouse(Warehouse w) {
        Response res = sendRequest(new Request("UPDATE", "warehouses", w));
        if (res.isSuccess()) logAudit("UPDATE", "warehouses", w.getId(), "Cập nhật kho bãi");
        return res.isSuccess();
    }

    public boolean deleteWarehouse(String id) {
        Response res = sendRequest(new Request("DELETE", "warehouses", id));
        if (res.isSuccess()) logAudit("DELETE", "warehouses", id, "Xóa kho bãi");
        return res.isSuccess();
    }

    // =============================================================
    // CUSTOMERS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<Customer> getAllCustomers() {
        Response res = sendRequest(new Request("GET_ALL", "customers"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<Customer>) res.getData();
        }
        return List.of();
    }

    public boolean insertCustomer(Customer c) {
        Response res = sendRequest(new Request("INSERT", "customers", c));
        if (res.isSuccess()) logAudit("INSERT", "customers", c.getId(), "Thêm khách hàng");
        return res.isSuccess();
    }

    public boolean deleteCustomer(String id) {
        Response res = sendRequest(new Request("DELETE", "customers", id));
        if (res.isSuccess()) logAudit("DELETE", "customers", id, "Xóa khách hàng");
        return res.isSuccess();
    }

    // =============================================================
    // SUPPLIERS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<Supplier> getAllSuppliers() {
        Response res = sendRequest(new Request("GET_ALL", "suppliers"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<Supplier>) res.getData();
        }
        return List.of();
    }

    public boolean insertSupplier(Supplier s) {
        Response res = sendRequest(new Request("INSERT", "suppliers", s));
        if (res.isSuccess()) logAudit("INSERT", "suppliers", s.getId(), "Thêm nhà cung cấp");
        return res.isSuccess();
    }

    public boolean deleteSupplier(String id) {
        Response res = sendRequest(new Request("DELETE", "suppliers", id));
        if (res.isSuccess()) logAudit("DELETE", "suppliers", id, "Xóa nhà cung cấp");
        return res.isSuccess();
    }

    // =============================================================
    // EMPLOYEES
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<Employee> getAllEmployees() {
        Response res = sendRequest(new Request("GET_ALL", "employees"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<Employee>) res.getData();
        }
        return List.of();
    }

    public boolean insertEmployee(Employee e) {
        Response res = sendRequest(new Request("INSERT", "employees", e));
        if (res.isSuccess()) logAudit("INSERT", "employees", e.getId(), "Thêm nhân viên");
        return res.isSuccess();
    }

    public boolean deleteEmployee(String id) {
        Response res = sendRequest(new Request("DELETE", "employees", id));
        if (res.isSuccess()) logAudit("DELETE", "employees", id, "Xóa nhân viên");
        return res.isSuccess();
    }

    // =============================================================
    // VOUCHERS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<ImportVoucher> getAllVouchers() {
        Response res = sendRequest(new Request("GET_ALL", "vouchers"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<ImportVoucher>) res.getData();
        }
        return List.of();
    }

    public boolean insertVoucher(ImportVoucher v) {
        Response res = sendRequest(new Request("INSERT", "vouchers", v));
        if (res.isSuccess()) logAudit("INSERT", "import_vouchers", v.getId(), "Tạo phiếu nhập");
        return res.isSuccess();
    }

    public boolean updateVoucher(ImportVoucher v) {
        Response res = sendRequest(new Request("UPDATE_STATUS", "vouchers", new String[]{v.getId(), v.getStatus()}));
        if (res.isSuccess()) logAudit("UPDATE", "import_vouchers", v.getId(), "Cập nhật trạng thái phiếu nhập");
        return res.isSuccess();
    }

    public boolean updateVoucherStatus(String id, String status) {
        Response res = sendRequest(new Request("UPDATE_STATUS", "vouchers", new String[]{id, status}));
        if (res.isSuccess()) logAudit("UPDATE", "import_vouchers", id, "Duyệt phiếu nhập");
        return res.isSuccess();
    }

    // =============================================================
    // ATTRIBUTES
    // =============================================================

    public Attributes getAllAttributes() {
        Response res = sendRequest(new Request("GET_ALL", "attributes"));
        if (res.isSuccess() && res.getData() != null) {
            return (Attributes) res.getData();
        }
        return new Attributes();
    }

    public boolean insertAttribute(String type, String value, String extra) {
        Response res = sendRequest(new Request("INSERT", "attributes", new String[]{type, value, extra}));
        if (res.isSuccess()) logAudit("INSERT", "attributes", type + ":" + value, "Thêm thuộc tính");
        return res.isSuccess();
    }

    public boolean deleteAttribute(String type, String value) {
        Response res = sendRequest(new Request("DELETE", "attributes", new String[]{type, value}));
        if (res.isSuccess()) logAudit("DELETE", "attributes", type + ":" + value, "Xóa thuộc tính");
        return res.isSuccess();
    }

    // =============================================================
    // PERMISSIONS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<RolePermissions> getAllPermissions() {
        Response res = sendRequest(new Request("GET_ALL", "permissions"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<RolePermissions>) res.getData();
        }
        return List.of();
    }

    // =============================================================
    // UPDATE METHODS
    // =============================================================

    public boolean updateCustomer(Customer c) {
        Response res = sendRequest(new Request("UPDATE", "customers", c));
        if (res.isSuccess()) logAudit("UPDATE", "customers", c.getId(), "Cập nhật khách hàng");
        return res.isSuccess();
    }

    public boolean updateSupplier(Supplier s) {
        Response res = sendRequest(new Request("UPDATE", "suppliers", s));
        if (res.isSuccess()) logAudit("UPDATE", "suppliers", s.getId(), "Cập nhật nhà cung cấp");
        return res.isSuccess();
    }

    public boolean updateEmployee(Employee e) {
        Response res = sendRequest(new Request("UPDATE", "employees", e));
        if (res.isSuccess()) logAudit("UPDATE", "employees", e.getId(), "Cập nhật nhân viên");
        return res.isSuccess();
    }

    // =============================================================
    // EXPORT VOUCHERS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<ExportVoucher> getAllExportVouchers() {
        Response res = sendRequest(new Request("GET_ALL", "export_vouchers"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<ExportVoucher>) res.getData();
        }
        return List.of();
    }

    public boolean insertExportVoucher(ExportVoucher v) {
        Response res = sendRequest(new Request("INSERT", "export_vouchers", v));
        if (res.isSuccess()) logAudit("INSERT", "export_vouchers", v.getId(), "Tạo phiếu xuất kho");
        return res.isSuccess();
    }

    // =============================================================
    // INVENTORY CHECKS
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<InventoryCheck> getAllInventoryChecks() {
        Response res = sendRequest(new Request("GET_ALL", "inventory_checks"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<InventoryCheck>) res.getData();
        }
        return List.of();
    }

    public boolean insertInventoryCheck(InventoryCheck ic) {
        Response res = sendRequest(new Request("INSERT", "inventory_checks", ic));
        if (res.isSuccess()) logAudit("INSERT", "inventory_checks", ic.getId(), "Kiểm kê kho hàng");
        return res.isSuccess();
    }

    // =============================================================
    // AUDIT LOG
    // =============================================================

    @SuppressWarnings("unchecked")
    public List<AuditLog> getAllAuditLogs() {
        Response res = sendRequest(new Request("GET_ALL", "audit_logs"));
        if (res.isSuccess() && res.getData() != null) {
            return (List<AuditLog>) res.getData();
        }
        return List.of();
    }

    public boolean insertAuditLog(AuditLog log) {
        Response res = sendRequest(new Request("INSERT", "audit_logs", log));
        return res.isSuccess();
    }
}
