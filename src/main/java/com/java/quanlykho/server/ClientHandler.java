package com.java.quanlykho.server;

import com.java.quanlykho.model.*;
import com.java.quanlykho.network.Request;
import com.java.quanlykho.network.Response;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Xử lý từng client connection trong một thread riêng.
 * Đọc Request → xử lý CRUD qua DatabaseManager → trả Response.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final DatabaseManager db;

    public ClientHandler(Socket socket, DatabaseManager db) {
        this.socket = socket;
        this.db = db;
    }

    @Override
    public void run() {
        String clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        System.out.println("[Server] Client kết nối: " + clientInfo);

        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            while (true) {
                try {
                    Request request = (Request) in.readObject();
                    System.out.println("[Server] << " + request + " từ " + clientInfo);

                    Response response = handleRequest(request);
                    out.writeObject(response);
                    out.flush();
                    out.reset(); // Clear object cache to avoid stale references

                    System.out.println("[Server] >> " + response);
                } catch (EOFException e) {
                    System.out.println("[Server] Client ngắt kết nối: " + clientInfo);
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("[Server] Lỗi class: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("[Server] Client " + clientInfo + " đã ngắt: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    @SuppressWarnings("unchecked")
    private Response handleRequest(Request req) {
        try {
            String action = req.getAction();
            String table = req.getTable();

            return switch (table) {
                case "products" -> handleProducts(action, req.getData());
                case "warehouses" -> handleWarehouses(action, req.getData());
                case "customers" -> handleCustomers(action, req.getData());
                case "suppliers" -> handleSuppliers(action, req.getData());
                case "employees" -> handleEmployees(action, req.getData());
                case "vouchers" -> handleVouchers(action, req.getData());
                case "export_vouchers" -> handleExportVouchers(action, req.getData());
                case "inventory_checks" -> handleInventoryChecks(action, req.getData());
                case "attributes" -> handleAttributes(action, req.getData());
                case "permissions" -> handlePermissions(action, req.getData());
                case "audit_logs" -> handleAuditLogs(action, req.getData());
                default -> Response.error("Bảng không hợp lệ: " + table);
            };
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Lỗi server: " + e.getMessage());
        }
    }

    // --- Products ---
    private Response handleProducts(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllProducts());
            case "INSERT" -> {
                db.insertProduct((Product) data);
                yield Response.ok("Thêm sản phẩm thành công", null);
            }
            case "UPDATE" -> {
                db.updateProduct((Product) data);
                yield Response.ok("Cập nhật sản phẩm thành công", null);
            }
            case "DELETE" -> {
                db.deleteProduct((String) data);
                yield Response.ok("Xóa sản phẩm thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Warehouses ---
    private Response handleWarehouses(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllWarehouses());
            case "INSERT" -> {
                db.insertWarehouse((Warehouse) data);
                yield Response.ok("Thêm kho thành công", null);
            }
            case "UPDATE" -> {
                db.updateWarehouse((Warehouse) data);
                yield Response.ok("Cập nhật kho thành công", null);
            }
            case "DELETE" -> {
                db.deleteWarehouse((String) data);
                yield Response.ok("Xóa kho thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Customers ---
    private Response handleCustomers(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllCustomers());
            case "INSERT" -> {
                db.insertCustomer((Customer) data);
                yield Response.ok("Thêm khách hàng thành công", null);
            }
            case "UPDATE" -> {
                db.updateCustomer((Customer) data);
                yield Response.ok("Cập nhật khách hàng thành công", null);
            }
            case "DELETE" -> {
                db.deleteCustomer((String) data);
                yield Response.ok("Xóa khách hàng thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Suppliers ---
    private Response handleSuppliers(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllSuppliers());
            case "INSERT" -> {
                db.insertSupplier((Supplier) data);
                yield Response.ok("Thêm nhà cung cấp thành công", null);
            }
            case "UPDATE" -> {
                db.updateSupplier((Supplier) data);
                yield Response.ok("Cập nhật nhà cung cấp thành công", null);
            }
            case "DELETE" -> {
                db.deleteSupplier((String) data);
                yield Response.ok("Xóa nhà cung cấp thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Employees ---
    private Response handleEmployees(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllEmployees());
            case "INSERT" -> {
                db.insertEmployee((Employee) data);
                yield Response.ok("Thêm nhân viên thành công", null);
            }
            case "UPDATE" -> {
                db.updateEmployee((Employee) data);
                yield Response.ok("Cập nhật nhân viên thành công", null);
            }
            case "DELETE" -> {
                db.deleteEmployee((String) data);
                yield Response.ok("Xóa nhân viên thành công", null);
            }
            case "LOGIN" -> {
                String[] creds = (String[]) data;
                Employee emp = db.login(creds[0], creds[1]);
                if (emp != null) {
                    db.insertAuditLog(new AuditLog(creds[0], "LOGIN", "employees", emp.getId(), "Đăng nhập thành công"));
                    yield Response.ok(emp);
                } else {
                    yield Response.error("Sai tên tài khoản hoặc mật khẩu!");
                }
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Vouchers ---
    private Response handleVouchers(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllVouchers());
            case "INSERT" -> {
                db.insertVoucher((ImportVoucher) data);
                yield Response.ok("Tạo phiếu nhập thành công", null);
            }
            case "UPDATE_STATUS" -> {
                // data = String[] {id, status}
                String[] parts = (String[]) data;
                db.updateVoucherStatus(parts[0], parts[1]);
                yield Response.ok("Duyệt phiếu thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Export Vouchers ---
    private Response handleExportVouchers(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllExportVouchers());
            case "INSERT" -> {
                db.insertExportVoucher((ExportVoucher) data);
                yield Response.ok("Tạo phiếu xuất thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Inventory Checks ---
    private Response handleInventoryChecks(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllInventoryChecks());
            case "INSERT" -> {
                db.insertInventoryCheck((InventoryCheck) data);
                yield Response.ok("Tạo phiếu kiểm kê thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Attributes ---
    private Response handleAttributes(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok(db.getAllAttributes());
            case "INSERT" -> {
                // data = String[] {type, value, extra}
                String[] parts = (String[]) data;
                db.insertAttribute(parts[0], parts[1], parts.length > 2 ? parts[2] : null);
                yield Response.ok("Thêm thuộc tính thành công", null);
            }
            case "DELETE" -> {
                // data = String[] {type, value}
                String[] parts = (String[]) data;
                db.deleteAttribute(parts[0], parts[1]);
                yield Response.ok("Xóa thuộc tính thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Permissions ---
    private Response handlePermissions(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllPermissions());
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }

    // --- Audit Logs ---
    private Response handleAuditLogs(String action, Object data) throws Exception {
        return switch (action) {
            case "GET_ALL" -> Response.ok((Serializable) db.getAllAuditLogs());
            case "INSERT" -> {
                db.insertAuditLog((AuditLog) data);
                yield Response.ok("Ghi audit log thành công", null);
            }
            default -> Response.error("Action không hợp lệ: " + action);
        };
    }
}
