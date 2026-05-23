package com.java.quanlykho.controller;

import com.java.quanlykho.model.Supplier;
import com.java.quanlykho.network.NetworkService;
import com.java.quanlykho.util.ExportUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Supplier management view.
 */
public class SupplierPane extends VBox {

    private final ObservableList<Supplier> suppliers;
    private TableView<Supplier> table;

    public SupplierPane(ObservableList<Supplier> suppliers) {
        this.suppliers = suppliers;
        setSpacing(16);

        // Toolbar
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm tên phân phối, SĐT...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(300);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnAdd = new Button("➕ Đăng ký Nhà Phân Phối mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> showAddDialog());

        Button btnExport = new Button("📥 Xuất Excel");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> ExportUtil.exportTableToExcel(table, "nha_cung_cap", getScene().getWindow()));

        toolbar.getChildren().addAll(searchField, sp, btnExport, btnAdd);

        // Table
        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(0));

        HBox tableHeader = new HBox(8);
        tableHeader.setPadding(new Insets(14, 20, 14, 20));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        Label icon = new Label("🚚");
        icon.setStyle("-fx-font-size: 16px;");
        Label title = new Label("Mạng lưới nhà cung cấp thiết bị");
        title.getStyleClass().add("card-title");
        tableHeader.getChildren().addAll(icon, title);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(450);

        TableColumn<Supplier, String> cId = new TableColumn<>("Mã NCC");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cId.setPrefWidth(80);

        TableColumn<Supplier, String> cName = new TableColumn<>("Đối tác / Đại diện");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: 700; -fx-text-fill: #0f172a;");
            }
        });

        TableColumn<Supplier, String> cContact = new TableColumn<>("Thông tin liên lạc");
        cContact.setCellValueFactory(d -> new SimpleStringProperty(
            "📞 " + d.getValue().getPhone() + "\n📧 " + d.getValue().getEmail()));
        cContact.setPrefWidth(200);

        TableColumn<Supplier, String> cAddr = new TableColumn<>("Địa chỉ văn phòng");
        cAddr.setCellValueFactory(d -> new SimpleStringProperty("📍 " + d.getValue().getAddress()));
        cAddr.setPrefWidth(250);
        cAddr.setCellFactory(col -> {
            TableCell<Supplier, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item);
                    setWrapText(true);
                    setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                }
            };
            return cell;
        });

        TableColumn<Supplier, String> cStatus = new TableColumn<>("Trạng thái");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setPrefWidth(100);
        cStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge",
                        "Hợp tác".equals(item) ? "badge-success" : "badge-danger");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Supplier, Void> cAction = new TableColumn<>("Hành động");
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
                    Supplier s = getTableView().getItems().get(getIndex());
                    NetworkService.getInstance().deleteSupplier(s.getId());
                    suppliers.remove(s);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(cId, cName, cContact, cAddr, cStatus, cAction);

        FilteredList<Supplier> filtered = new FilteredList<>(suppliers, p -> true);
        searchField.textProperty().addListener((obs, old, val) -> {
            String s = val.toLowerCase().trim();
            filtered.setPredicate(sup -> s.isEmpty() || sup.getName().toLowerCase().contains(s));
        });
        SortedList<Supplier> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        tableCard.getChildren().addAll(tableHeader, table);
        getChildren().addAll(toolbar, tableCard);
    }

    private void showAddDialog() {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle("Đăng kiểm nhà cung cấp");

        ButtonType saveBtn = new ButtonType("Đăng ký", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfName = new TextField();
        TextField tfPhone = new TextField();
        TextField tfEmail = new TextField();
        TextField tfAddr = new TextField();

        grid.add(new Label("Họ tên Nhà Phân Phối *"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Số điện thoại liên lạc *"), 0, 1);
        grid.add(tfPhone, 1, 1);
        grid.add(new Label("Thư điện tử (Email) *"), 0, 2);
        grid.add(tfEmail, 1, 2);
        grid.add(new Label("Địa chỉ văn phòng *"), 0, 3);
        grid.add(tfAddr, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank()) {
                String id = "NCC0" + (suppliers.size() + 1);
                return new Supplier(id, tfName.getText().trim(), tfPhone.getText().trim(),
                    tfEmail.getText().trim(), tfAddr.getText().trim(), "Hợp tác");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(s -> {
            NetworkService.getInstance().insertSupplier(s);
            suppliers.add(s);
        });
    }

    private void showEditDialog(Supplier s) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin NCC: " + s.getId());

        ButtonType saveBtn = new ButtonType("Lưu thay đổi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));

        TextField tfName = new TextField(s.getName());
        TextField tfPhone = new TextField(s.getPhone());
        TextField tfEmail = new TextField(s.getEmail());
        TextField tfAddr = new TextField(s.getAddress());
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Hợp tác", "Ngưng hợp tác");
        cbStatus.setValue(s.getStatus());

        grid.add(new Label("Tên NCC *"), 0, 0); grid.add(tfName, 1, 0);
        grid.add(new Label("SĐT *"), 0, 1); grid.add(tfPhone, 1, 1);
        grid.add(new Label("Email *"), 0, 2); grid.add(tfEmail, 1, 2);
        grid.add(new Label("Địa chỉ *"), 0, 3); grid.add(tfAddr, 1, 3);
        grid.add(new Label("Trạng thái"), 0, 4); grid.add(cbStatus, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank()) {
                s.setName(tfName.getText().trim());
                s.setPhone(tfPhone.getText().trim());
                s.setEmail(tfEmail.getText().trim());
                s.setAddress(tfAddr.getText().trim());
                s.setStatus(cbStatus.getValue());
                return s;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            NetworkService.getInstance().updateSupplier(updated);
            table.refresh();
        });
    }
}
