package com.java.quanlykho.controller;

import com.java.quanlykho.model.ModulePermission;
import com.java.quanlykho.model.RolePermissions;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Permission management view - checkbox matrix per role per module.
 */
public class PermissionPane extends VBox {

    private final ObservableList<RolePermissions> permissions;

    private static final Map<String, String> MODULE_LABELS = new LinkedHashMap<>() {{
        put("dashboard", "Bảng Điều Khiển (Trang chủ)");
        put("products", "Quản Lý Sản Phẩm (Smartphone)");
        put("attributes", "Cấu Hình Thuộc Tính Tĩnh");
        put("warehouses", "Sức Chứa & Quản Lý Kho Bãi");
        put("vouchers", "Danh Mục Phiếu Nhập Hàng");
        put("customers", "Khách Hàng Thương Mại");
        put("suppliers", "Nhà Cung Cấp Chuỗi Cung Ứng");
        put("employees", "Thư Ký & Tài Khoản Nhân Sự");
        put("permissions", "Bảng Phân Quyền Vai Trò");
    }};

    public PermissionPane(ObservableList<RolePermissions> permissions) {
        this.permissions = permissions;
        setSpacing(16);

        // Header
        HBox header = new HBox(12);
        header.getStyleClass().add("card");
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🛡");
        icon.setStyle("-fx-font-size: 18px;");
        VBox headerText = new VBox(2);
        Label title = new Label("Phân Quyền Chi Tiết Module");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Label sub = new Label("Giới hạn định mức truy cập API cho từng bộ phận công tác, can thiệp quy trình bảo mật vai trò tài khoản");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        sub.setWrapText(true);
        headerText.getChildren().addAll(title, sub);

        header.getChildren().addAll(icon, headerText);
        getChildren().add(header);

        // Build a table for each role
        for (RolePermissions rp : permissions) {
            getChildren().add(createRoleSection(rp));
        }
    }

    private VBox createRoleSection(RolePermissions rp) {
        VBox section = new VBox(0);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(0));

        // Role header
        HBox roleHeader = new HBox(8);
        roleHeader.setPadding(new Insets(14, 20, 14, 20));
        roleHeader.setAlignment(Pos.CENTER_LEFT);
        roleHeader.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

        Label lockIcon = new Label("🔒");
        lockIcon.setStyle("-fx-font-size: 14px;");
        Label roleLabel = new Label("Chức danh quyền hạn:");
        roleLabel.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 13px;");
        Label roleBadge = new Label(rp.getRole());
        roleBadge.getStyleClass().add("badge-mono");
        roleBadge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #4338ca; -fx-border-color: #c7d2fe; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: 700;");

        roleHeader.getChildren().addAll(lockIcon, roleLabel, roleBadge);

        // Permission grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setStyle("-fx-background-color: white;");

        // Column constraints
        ColumnConstraints ccModule = new ColumnConstraints();
        ccModule.setPercentWidth(35);
        ccModule.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(ccModule);

        String[] actionLabels = {"Xem (Retrieve)", "Thêm (Create)", "Sửa (Update)", "Xóa (Delete)", "Xuất Dữ Liệu"};
        for (int i = 0; i < actionLabels.length; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(13);
            cc.setHalignment(javafx.geometry.HPos.CENTER);
            grid.getColumnConstraints().add(cc);
        }

        // Header row
        Label hModule = new Label("Tên Module Hệ Thống");
        hModule.setStyle("-fx-font-weight: 700; -fx-text-fill: #64748b; -fx-font-size: 11px; -fx-padding: 10 14;");
        hModule.setMaxWidth(Double.MAX_VALUE);
        grid.add(hModule, 0, 0);

        for (int i = 0; i < actionLabels.length; i++) {
            Label h = new Label(actionLabels[i]);
            h.setStyle("-fx-font-weight: 700; -fx-text-fill: #64748b; -fx-font-size: 11px; -fx-padding: 10 4;");
            h.setAlignment(Pos.CENTER);
            grid.add(h, i + 1, 0);
        }

        // Separator
        Separator sep = new Separator();
        sep.setStyle("-fx-padding: 0;");
        GridPane.setColumnSpan(sep, 6);
        grid.add(sep, 0, 1);

        // Data rows
        int rowIdx = 2;
        for (Map.Entry<String, String> entry : MODULE_LABELS.entrySet()) {
            String moduleKey = entry.getKey();
            String moduleName = entry.getValue();
            ModulePermission perm = rp.getPermission(moduleKey);

            Label modLabel = new Label(moduleName);
            modLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-padding: 8 14;");
            modLabel.setMaxWidth(Double.MAX_VALUE);

            if (rowIdx % 2 == 0) {
                modLabel.setStyle(modLabel.getStyle() + " -fx-background-color: #fafbfc;");
            }

            grid.add(modLabel, 0, rowIdx);

            // Checkboxes
            CheckBox cbView = createPermCheckBox(perm.isView());
            cbView.selectedProperty().addListener((obs, old, val) -> perm.setView(val));

            CheckBox cbAdd = createPermCheckBox(perm.isAdd());
            cbAdd.selectedProperty().addListener((obs, old, val) -> perm.setAdd(val));

            CheckBox cbEdit = createPermCheckBox(perm.isEdit());
            cbEdit.selectedProperty().addListener((obs, old, val) -> perm.setEdit(val));

            CheckBox cbDelete = createPermCheckBox(perm.isDelete());
            cbDelete.selectedProperty().addListener((obs, old, val) -> perm.setDelete(val));

            CheckBox cbExport = createPermCheckBox(perm.isExport());
            cbExport.selectedProperty().addListener((obs, old, val) -> perm.setExport(val));

            grid.add(cbView, 1, rowIdx);
            grid.add(cbAdd, 2, rowIdx);
            grid.add(cbEdit, 3, rowIdx);
            grid.add(cbDelete, 4, rowIdx);
            grid.add(cbExport, 5, rowIdx);

            rowIdx++;
        }

        section.getChildren().addAll(roleHeader, grid);
        return section;
    }

    private CheckBox createPermCheckBox(boolean initial) {
        CheckBox cb = new CheckBox();
        cb.setSelected(initial);
        cb.setStyle("-fx-padding: 8;");
        return cb;
    }
}
