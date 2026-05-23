package com.java.quanlykho.controller;

import com.java.quanlykho.model.*;
import com.java.quanlykho.network.NetworkService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller chính - xử lý sidebar navigation và quản lý dữ liệu chia sẻ.
 * Kết nối TCP Server trên port 8888 để đồng bộ dữ liệu MySQL.
 */
public class MainController {

    @FXML private VBox sidebarNav;
    @FXML private StackPane contentArea;
    @FXML private Label headerTitle;
    @FXML private Label statusBadge;

    // Logged-in user
    private Employee loggedInUser;

    // Shared data
    private ObservableList<Product> products;
    private ObservableList<Warehouse> warehouses;
    private ObservableList<Customer> customers;
    private ObservableList<Supplier> suppliers;
    private ObservableList<Employee> employees;
    private ObservableList<ImportVoucher> vouchers;
    private ObservableList<ExportVoucher> exportVouchers;
    private ObservableList<InventoryCheck> inventoryChecks;
    private ObservableList<RolePermissions> permissions;
    private ObservableList<AuditLog> auditLogs;
    private Attributes attributes;

    // View cache
    private final Map<String, Node> viewCache = new LinkedHashMap<>();

    // Navigation items
    private final String[][] navItems = {
        {"dashboard",       "📊", "Trang Chủ"},
        {"products",        "📱", "Sản Phẩm (Smartphone)"},
        {"attributes",      "⚙", "Thuộc Tính"},
        {"warehouses",      "📦", "Khu Vực Kho"},
        {"vouchers",        "📄", "Phiếu Nhập / Xuất Kho"},
        {"inventory",       "📋", "Kiểm Kê Kho"},
        {"customers",       "👥", "Khách Hàng"},
        {"suppliers",       "🚚", "Nhà Cung Cấp"},
        {"employees",       "👤", "Tài Khoản Nhân Sự"},
        {"permissions",     "🛡", "Bảng Phân Quyền"},
        {"audit",           "📜", "Lịch Sử Thao Tác"},
    };

    private String activeTab = "dashboard";
    private final Map<String, Button> navButtons = new LinkedHashMap<>();

    /**
     * Được gọi bởi MainApp sau khi login thành công.
     */
    public void setLoggedInUser(Employee user) {
        this.loggedInUser = user;
        if (user != null) {
            NetworkService.getInstance().setCurrentUser(user.getUsername());
        }
    }

    @FXML
    public void initialize() {
        // Data sẽ được load sau khi setLoggedInUser → FXML loaded
        // Vì initialize() chạy trước setLoggedInUser, dùng Platform.runLater
        javafx.application.Platform.runLater(this::loadData);
    }

    private void loadData() {
        NetworkService net = NetworkService.getInstance();
        // Connection đã được thiết lập từ LoginPane, kiểm tra lại
        boolean connected = net.isConnected();
        if (!connected) {
            connected = net.connect();
        }

        if (connected) {
            statusBadge.setText("🖥 SQL State: CONNECTED");
            statusBadge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 4 12; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 700;");

            System.out.println("[Client] Đang tải dữ liệu từ MySQL qua TCP...");
            products = FXCollections.observableArrayList(net.getAllProducts());
            warehouses = FXCollections.observableArrayList(net.getAllWarehouses());
            customers = FXCollections.observableArrayList(net.getAllCustomers());
            suppliers = FXCollections.observableArrayList(net.getAllSuppliers());
            employees = FXCollections.observableArrayList(net.getAllEmployees());
            vouchers = FXCollections.observableArrayList(net.getAllVouchers());
            exportVouchers = FXCollections.observableArrayList(net.getAllExportVouchers());
            inventoryChecks = FXCollections.observableArrayList(net.getAllInventoryChecks());
            permissions = FXCollections.observableArrayList(net.getAllPermissions());
            auditLogs = FXCollections.observableArrayList(net.getAllAuditLogs());
            attributes = net.getAllAttributes();
            System.out.println("[Client] Tải dữ liệu thành công! Products=" + products.size()
                + ", Warehouses=" + warehouses.size());
        } else {
            statusBadge.setText("🖥 SQL State: DISCONNECTED");
            statusBadge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 4 12; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 700;");
            products = FXCollections.observableArrayList();
            warehouses = FXCollections.observableArrayList();
            customers = FXCollections.observableArrayList();
            suppliers = FXCollections.observableArrayList();
            employees = FXCollections.observableArrayList();
            vouchers = FXCollections.observableArrayList();
            exportVouchers = FXCollections.observableArrayList();
            inventoryChecks = FXCollections.observableArrayList();
            permissions = FXCollections.observableArrayList();
            auditLogs = FXCollections.observableArrayList();
            attributes = new Attributes();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi kết nối");
            alert.setHeaderText("Không thể kết nối đến TCP Server!");
            alert.setContentText("Ứng dụng sẽ hiển thị dữ liệu trống.");
            alert.showAndWait();
        }

        // Update header with logged-in user
        updateHeaderUser();

        // Build sidebar navigation buttons
        buildSidebar();

        // Show dashboard
        navigateTo("dashboard");
    }

    private void updateHeaderUser() {
        if (loggedInUser == null) return;
        // Find user name + role labels in FXML and update
        try {
            Label userName = (Label) statusBadge.getScene().lookup("#headerUserName");
            Label userRole = (Label) statusBadge.getScene().lookup("#headerUserRole");
            Label avatarLabel = (Label) statusBadge.getScene().lookup("#avatarLabel");
            if (userName != null) userName.setText(loggedInUser.getFullName() + " (" + loggedInUser.getRole() + ")");
            if (userRole != null) userRole.setText("Tài khoản: " + loggedInUser.getUsername());
            if (avatarLabel != null) {
                String initials = loggedInUser.getFullName().length() >= 2
                    ? loggedInUser.getFullName().substring(0, 2).toUpperCase() : "AD";
                avatarLabel.setText(initials);
            }
        } catch (Exception ignored) {}
    }

    private void buildSidebar() {
        sidebarNav.getChildren().clear();

        RolePermissions rolePerm = null;
        if (loggedInUser != null) {
            String roleName = loggedInUser.getRole();
            rolePerm = permissions.stream().filter(p -> p.getRole().equals(roleName)).findFirst().orElse(null);
        }

        for (String[] item : navItems) {
            String key = item[0];
            String icon = item[1];
            String label = item[2];

            // Filter by role
            if (!key.equals("dashboard") && rolePerm != null) {
                ModulePermission mp = rolePerm.getPermission(key);
                if (!mp.isView()) {
                    continue; // Không có quyền xem, bỏ qua
                }
            }

            Button btn = new Button(icon + "  " + label);
            btn.getStyleClass().add("sidebar-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> navigateTo(key));

            navButtons.put(key, btn);
            sidebarNav.getChildren().add(btn);
        }
    }

    private void navigateTo(String tabKey) {
        activeTab = tabKey;

        // Update header title
        for (String[] item : navItems) {
            if (item[0].equals(tabKey)) {
                headerTitle.setText(item[2]);
                break;
            }
        }

        // Update sidebar button styles
        navButtons.forEach((key, btn) -> {
            btn.getStyleClass().remove("sidebar-btn-active");
            if (key.equals(tabKey)) {
                btn.getStyleClass().add("sidebar-btn-active");
            }
        });

        // Load or create view
        Node view = viewCache.computeIfAbsent(tabKey, this::createView);

        // Swap content
        contentArea.getChildren().setAll(view);
    }

    private Node createView(String tabKey) {
        String creator = loggedInUser != null ? loggedInUser.getFullName() + " (" + loggedInUser.getRole() + ")" : "Admin";
        return switch (tabKey) {
            case "dashboard" -> new DashboardPane(products, warehouses, this::navigateTo);
            case "products" -> new ProductPane(products, attributes);
            case "attributes" -> new AttributePane(attributes);
            case "warehouses" -> new WarehousePane(warehouses);
            case "vouchers" -> new VoucherPane(products, warehouses, suppliers, customers, vouchers, exportVouchers, creator);
            case "inventory" -> new InventoryPane(products, warehouses, inventoryChecks, creator);
            case "customers" -> new CustomerPane(customers);
            case "suppliers" -> new SupplierPane(suppliers);
            case "employees" -> new EmployeePane(employees);
            case "permissions" -> new PermissionPane(permissions);
            case "audit" -> new AuditLogPane(auditLogs);
            default -> createPlaceholder(tabKey);
        };
    }

    private Node createPlaceholder(String tabKey) {
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(javafx.geometry.Pos.CENTER);
        Label lbl = new Label("Module: " + tabKey);
        lbl.setStyle("-fx-font-size: 18px; -fx-text-fill: #94a3b8; -fx-font-weight: 700;");
        placeholder.getChildren().add(lbl);
        return placeholder;
    }
}
