package com.java.quanlykho.controller;

import com.java.quanlykho.model.Employee;
import com.java.quanlykho.network.NetworkService;
import com.java.quanlykho.util.ExportUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Employee management view.
 */
public class EmployeePane extends VBox {

    private final ObservableList<Employee> employees;
    private TableView<Employee> table;

    public EmployeePane(ObservableList<Employee> employees) {
        this.employees = employees;
        setSpacing(16);

        // Toolbar
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm tên nhân viên, username...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(300);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnAdd = new Button("➕ Đăng ký Tài Khoản Mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> showAddDialog());

        Button btnExport = new Button("📥 Xuất Excel");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> ExportUtil.exportTableToExcel(table, "nhan_vien", getScene().getWindow()));

        toolbar.getChildren().addAll(searchField, sp, btnExport, btnAdd);

        // Table
        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(0));

        HBox tableHeader = new HBox(8);
        tableHeader.setPadding(new Insets(14, 20, 14, 20));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        Label icon = new Label("👤");
        icon.setStyle("-fx-font-size: 16px;");
        Label title = new Label("Danh bạ nhân viên hệ thống");
        title.getStyleClass().add("card-title");
        tableHeader.getChildren().addAll(icon, title);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(450);

        TableColumn<Employee, String> cId = new TableColumn<>("Mã NV");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cId.setPrefWidth(70);

        TableColumn<Employee, String> cUser = new TableColumn<>("Tài khoản (Username)");
        cUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        cUser.setPrefWidth(150);
        cUser.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: 700; -fx-text-fill: #4338ca; -fx-font-family: 'Consolas';");
            }
        });

        TableColumn<Employee, String> cName = new TableColumn<>("Họ và tên");
        cName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        cName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: 700; -fx-text-fill: #0f172a;");
            }
        });

        TableColumn<Employee, String> cRole = new TableColumn<>("Phân quyền");
        cRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        cRole.setPrefWidth(120);
        cRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label("🔒 " + item);
                    badge.getStyleClass().add("badge");
                    if ("Admin".equals(item)) badge.setStyle("-fx-background-color: #fff1f2; -fx-text-fill: #991b1b; -fx-font-weight: 700; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 4;");
                    else if ("Thủ Kho".equals(item)) badge.setStyle("-fx-background-color: #fffbeb; -fx-text-fill: #92400e; -fx-font-weight: 700; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 4;");
                    else badge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #1e40af; -fx-font-weight: 700; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 4;");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Employee, String> cContact = new TableColumn<>("Liên hệ");
        cContact.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getPhone() + "\n" + d.getValue().getEmail()));
        cContact.setPrefWidth(180);
        cContact.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            }
        });

        TableColumn<Employee, String> cStatus = new TableColumn<>("Trạng thái");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setPrefWidth(120);
        cStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge",
                        "Đang làm việc".equals(item) ? "badge-success" : "badge-brand");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Employee, Void> cAction = new TableColumn<>("Hành động");
        cAction.setPrefWidth(120);
        cAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏");
            private final Button btnDel = new Button("🗑");
            private final HBox box = new HBox(4, btnEdit, btnDel);
            {
                btnEdit.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #0369a1; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 8; -fx-background-radius: 4;");
                btnDel.getStyleClass().add("btn-danger");
                box.setAlignment(Pos.CENTER);
                btnEdit.setOnAction(e -> showEditDialog(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Employee emp = getTableView().getItems().get(getIndex());
                    NetworkService.getInstance().deleteEmployee(emp.getId());
                    employees.remove(emp);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(cId, cUser, cName, cRole, cContact, cStatus, cAction);

        FilteredList<Employee> filtered = new FilteredList<>(employees, p -> true);
        searchField.textProperty().addListener((obs, old, val) -> {
            String s = val.toLowerCase().trim();
            filtered.setPredicate(emp -> s.isEmpty()
                || emp.getFullName().toLowerCase().contains(s)
                || emp.getUsername().contains(s));
        });
        SortedList<Employee> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        tableCard.getChildren().addAll(tableHeader, table);
        getChildren().addAll(toolbar, tableCard);
    }

    private void showAddDialog() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Ghi danh nhân sự bến kho");

        ButtonType saveBtn = new ButtonType("Khai báo cấu hình", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfUser = new TextField();
        tfUser.setPromptText("viết thường, không dấu");
        TextField tfName = new TextField();
        ComboBox<String> cbRole = new ComboBox<>(FXCollections.observableArrayList("Nhân viên", "Thủ Kho", "Admin"));
        cbRole.setValue("Nhân viên");
        TextField tfPhone = new TextField();
        TextField tfEmail = new TextField();

        grid.add(new Label("Tên tài khoản *"), 0, 0);
        grid.add(tfUser, 1, 0);
        grid.add(new Label("Họ và tên nhân sự *"), 0, 1);
        grid.add(tfName, 1, 1);
        grid.add(new Label("Vai trò công việc *"), 0, 2);
        grid.add(cbRole, 1, 2);
        grid.add(new Label("Số điện thoại *"), 0, 3);
        grid.add(tfPhone, 1, 3);
        grid.add(new Label("Địa chỉ Email *"), 0, 4);
        grid.add(tfEmail, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfUser.getText().isBlank() && !tfName.getText().isBlank()) {
                String id = "NV00" + (employees.size() + 1);
                return new Employee(id, tfUser.getText().toLowerCase().trim(), tfName.getText().trim(),
                    tfEmail.getText().trim(), cbRole.getValue(), "Đang làm việc", tfPhone.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(emp -> {
            NetworkService.getInstance().insertEmployee(emp);
            employees.add(emp);
        });
    }

    private void showEditDialog(Employee emp) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin nhân viên: " + emp.getId());

        ButtonType saveBtn = new ButtonType("Lưu thay đổi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));

        TextField tfUser = new TextField(emp.getUsername());
        TextField tfName = new TextField(emp.getFullName());
        ComboBox<String> cbRole = new ComboBox<>(FXCollections.observableArrayList("Nhân viên", "Thủ Kho", "Admin"));
        cbRole.setValue(emp.getRole());
        TextField tfPhone = new TextField(emp.getPhone());
        TextField tfEmail = new TextField(emp.getEmail());
        ComboBox<String> cbStatus = new ComboBox<>(FXCollections.observableArrayList("Đang làm việc", "Nghỉ việc"));
        cbStatus.setValue(emp.getStatus());

        grid.add(new Label("Username *"), 0, 0); grid.add(tfUser, 1, 0);
        grid.add(new Label("Họ và tên *"), 0, 1); grid.add(tfName, 1, 1);
        grid.add(new Label("Vai trò *"), 0, 2); grid.add(cbRole, 1, 2);
        grid.add(new Label("SĐT"), 0, 3); grid.add(tfPhone, 1, 3);
        grid.add(new Label("Email"), 0, 4); grid.add(tfEmail, 1, 4);
        grid.add(new Label("Trạng thái"), 0, 5); grid.add(cbStatus, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank()) {
                emp.setUsername(tfUser.getText().trim());
                emp.setFullName(tfName.getText().trim());
                emp.setRole(cbRole.getValue());
                emp.setPhone(tfPhone.getText().trim());
                emp.setEmail(tfEmail.getText().trim());
                emp.setStatus(cbStatus.getValue());
                return emp;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            NetworkService.getInstance().updateEmployee(updated);
            table.refresh();
        });
    }
}
