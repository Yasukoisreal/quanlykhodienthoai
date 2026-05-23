package com.java.quanlykho.controller;

import com.java.quanlykho.model.Customer;
import com.java.quanlykho.network.NetworkService;
import com.java.quanlykho.util.ExportUtil;
import com.java.quanlykho.util.FormatUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Optional;

/**
 * Customer management view - table + add/delete.
 */
public class CustomerPane extends VBox {

    private final ObservableList<Customer> customers;
    private TableView<Customer> table;

    public CustomerPane(ObservableList<Customer> customers) {
        this.customers = customers;
        setSpacing(16);

        // Toolbar
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm tên khách hàng hoặc SĐT...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(300);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnAdd = new Button("➕ Đăng ký Khách Hàng mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> showAddDialog());

        Button btnExport = new Button("📥 Xuất Excel");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> ExportUtil.exportTableToExcel(table, "khach_hang", getScene().getWindow()));

        toolbar.getChildren().addAll(searchField, sp, btnExport, btnAdd);

        // Table card
        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(0));

        HBox tableHeader = new HBox(8);
        tableHeader.setPadding(new Insets(14, 20, 14, 20));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        Label icon = new Label("👥");
        icon.setStyle("-fx-font-size: 16px;");
        Label title = new Label("Danh sách khách hàng thương mại");
        title.getStyleClass().add("card-title");
        tableHeader.getChildren().addAll(icon, title);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(450);

        TableColumn<Customer, String> cId = new TableColumn<>("Mã KH");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cId.setPrefWidth(70);
        cId.setCellFactory(col -> createMonoCell());

        TableColumn<Customer, String> cName = new TableColumn<>("Tên khách hàng");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cName.setCellFactory(col -> createBoldCell());

        TableColumn<Customer, String> cContact = new TableColumn<>("Liên hệ");
        cContact.setCellValueFactory(d -> new SimpleStringProperty(
            "📞 " + d.getValue().getPhone() + "\n📧 " + d.getValue().getEmail()));
        cContact.setPrefWidth(200);

        TableColumn<Customer, String> cSpend = new TableColumn<>("Doanh số mua");
        cSpend.setCellValueFactory(d -> new SimpleStringProperty(FormatUtil.formatVND(d.getValue().getTotalSpend())));
        cSpend.setPrefWidth(140);
        cSpend.setCellFactory(col -> createMonoCell());

        TableColumn<Customer, String> cTier = new TableColumn<>("Phân hạng");
        cTier.setCellValueFactory(new PropertyValueFactory<>("tier"));
        cTier.setPrefWidth(100);
        cTier.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    if ("Kim cương".equals(item)) badge.getStyleClass().add("badge-purple");
                    else if ("Vàng".equals(item)) badge.getStyleClass().add("badge-amber");
                    else badge.getStyleClass().add("badge-brand");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Customer, String> cStatus = new TableColumn<>("Trạng thái");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setPrefWidth(110);
        cStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge",
                        "Đang hoạt động".equals(item) ? "badge-success" : "badge-danger");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Customer, Void> cAction = new TableColumn<>("Hành động");
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
                    Customer c = getTableView().getItems().get(getIndex());
                    NetworkService.getInstance().deleteCustomer(c.getId());
                    customers.remove(c);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(cId, cName, cContact, cSpend, cTier, cStatus, cAction);

        // Filtering
        FilteredList<Customer> filtered = new FilteredList<>(customers, p -> true);
        searchField.textProperty().addListener((obs, old, val) -> {
            String s = val.toLowerCase().trim();
            filtered.setPredicate(c -> s.isEmpty()
                || c.getName().toLowerCase().contains(s)
                || c.getPhone().contains(s));
        });
        SortedList<Customer> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        tableCard.getChildren().addAll(tableHeader, table);
        getChildren().addAll(toolbar, tableCard);
    }

    private void showAddDialog() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Ghi danh khách hàng mới");

        ButtonType saveBtn = new ButtonType("Lập hồ sơ", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfName = new TextField();
        TextField tfPhone = new TextField();
        TextField tfEmail = new TextField();

        grid.add(new Label("Họ và tên *"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Số điện thoại liên lạc *"), 0, 1);
        grid.add(tfPhone, 1, 1);
        grid.add(new Label("Thư điện tử (Email) *"), 0, 2);
        grid.add(tfEmail, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank()) {
                String id = "KH00" + (customers.size() + 1);
                return new Customer(id, tfName.getText().trim(), tfPhone.getText().trim(),
                    tfEmail.getText().trim(), "Đang hoạt động", 0, 0, "Bạc");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> {
            NetworkService.getInstance().insertCustomer(c);
            customers.add(c);
        });
    }

    private void showEditDialog(Customer c) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin khách hàng: " + c.getId());

        ButtonType saveBtn = new ButtonType("Lưu thay đổi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));

        TextField tfName = new TextField(c.getName());
        TextField tfPhone = new TextField(c.getPhone());
        TextField tfEmail = new TextField(c.getEmail());
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Đang hoạt động", "Ngưng giao dịch");
        cbStatus.setValue(c.getStatus());
        ComboBox<String> cbTier = new ComboBox<>();
        cbTier.getItems().addAll("Bạc", "Vàng", "Kim cương");
        cbTier.setValue(c.getTier());

        grid.add(new Label("Họ và tên *"), 0, 0); grid.add(tfName, 1, 0);
        grid.add(new Label("SĐT *"), 0, 1); grid.add(tfPhone, 1, 1);
        grid.add(new Label("Email *"), 0, 2); grid.add(tfEmail, 1, 2);
        grid.add(new Label("Trạng thái"), 0, 3); grid.add(cbStatus, 1, 3);
        grid.add(new Label("Phân hạng"), 0, 4); grid.add(cbTier, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank()) {
                c.setName(tfName.getText().trim());
                c.setPhone(tfPhone.getText().trim());
                c.setEmail(tfEmail.getText().trim());
                c.setStatus(cbStatus.getValue());
                c.setTier(cbTier.getValue());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            NetworkService.getInstance().updateCustomer(updated);
            table.refresh();
        });
    }

    private <T> TableCell<T, String> createMonoCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-family: 'Consolas';");
            }
        };
    }

    private <T> TableCell<T, String> createBoldCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: 700; -fx-text-fill: #0f172a;");
            }
        };
    }
}
