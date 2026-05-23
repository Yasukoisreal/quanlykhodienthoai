package com.java.quanlykho.controller;

import com.java.quanlykho.model.*;
import com.java.quanlykho.network.NetworkService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kiểm kê kho - tạo phiếu kiểm kê so sánh tồn thực tế vs hệ thống.
 */
public class InventoryPane extends VBox {

    private final ObservableList<Product> products;
    private final ObservableList<Warehouse> warehouses;
    private final ObservableList<InventoryCheck> checks;
    private final String creator;
    private final VBox pastChecksContainer = new VBox(10);

    public InventoryPane(ObservableList<Product> products, ObservableList<Warehouse> warehouses,
                         ObservableList<InventoryCheck> checks, String creator) {
        this.products = products;
        this.warehouses = warehouses;
        this.checks = checks;
        this.creator = creator;
        setSpacing(16);

        TableView<InventoryCheck> table = new TableView<>(checks);

        // Header
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 18px;");
        VBox headerText = new VBox(2);
        Label title = new Label("Kiểm Kê Kho Hàng");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Label sub = new Label("So sánh tồn kho thực tế với hệ thống — phát hiện chênh lệch");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        headerText.getChildren().addAll(title, sub);
        HBox.setHgrow(headerText, Priority.ALWAYS);

        Button btnCreate = new Button("➕ Tạo Phiếu Kiểm Kê");
        btnCreate.getStyleClass().add("btn-primary");
        btnCreate.setOnAction(e -> showCreateDialog());

        Button btnExport = new Button("📥 Xuất Excel");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> com.java.quanlykho.util.ExportUtil.exportTableToExcel(table, "kiem_ke", getScene().getWindow()));

        toolbar.getChildren().addAll(icon, headerText, btnExport, btnCreate);

        // Past checks table
        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(0));

        HBox tableHeader = new HBox(8);
        tableHeader.setPadding(new Insets(14, 20, 14, 20));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        Label tIcon = new Label("📋");
        tIcon.setStyle("-fx-font-size: 16px;");
        Label tTitle = new Label("Danh sách phiếu kiểm kê (" + checks.size() + ")");
        tTitle.getStyleClass().add("card-title");
        tableHeader.getChildren().addAll(tIcon, tTitle);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(400);

        TableColumn<InventoryCheck, String> cId = new TableColumn<>("Mã phiếu");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cId.setPrefWidth(80);

        TableColumn<InventoryCheck, String> cWh = new TableColumn<>("Kho kiểm tra");
        cWh.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));

        TableColumn<InventoryCheck, String> cDate = new TableColumn<>("Ngày kiểm");
        cDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        cDate.setPrefWidth(110);

        TableColumn<InventoryCheck, String> cCreator = new TableColumn<>("Người kiểm");
        cCreator.setCellValueFactory(new PropertyValueFactory<>("creator"));

        TableColumn<InventoryCheck, String> cStatus = new TableColumn<>("Trạng thái");
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
                        "Hoàn tất".equals(item) ? "badge-success" : "badge-warning");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<InventoryCheck, String> cNotes = new TableColumn<>("Ghi chú");
        cNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        table.getColumns().addAll(cId, cWh, cDate, cCreator, cStatus, cNotes);

        tableCard.getChildren().addAll(tableHeader, table);
        getChildren().addAll(toolbar, tableCard);
    }

    private void showCreateDialog() {
        Dialog<InventoryCheck> dialog = new Dialog<>();
        dialog.setTitle("Tạo Phiếu Kiểm Kê");
        dialog.getDialogPane().setPrefWidth(700);

        ButtonType saveBtn = new ButtonType("Lập Phiếu Kiểm Kê", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        ComboBox<String> cbWarehouse = new ComboBox<>();
        warehouses.forEach(w -> cbWarehouse.getItems().add(w.getId() + " - " + w.getName()));
        if (!warehouses.isEmpty()) cbWarehouse.getSelectionModel().selectFirst();
        cbWarehouse.setMaxWidth(Double.MAX_VALUE);

        TextField tfNotes = new TextField();
        tfNotes.setPromptText("Ghi chú (tùy chọn)...");

        // Product check table
        ObservableList<InventoryCheckItem> items = FXCollections.observableArrayList();
        for (Product p : products) {
            items.add(new InventoryCheckItem(p.getId(), p.getModelName(), p.getStock(), p.getStock()));
        }

        TableView<InventoryCheckItem> itemTable = new TableView<>(items);
        itemTable.setPrefHeight(300);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<InventoryCheckItem, String> c1 = new TableColumn<>("Mã SP");
        c1.setCellValueFactory(new PropertyValueFactory<>("productId"));
        c1.setPrefWidth(70);

        TableColumn<InventoryCheckItem, String> c2 = new TableColumn<>("Tên sản phẩm");
        c2.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<InventoryCheckItem, Integer> c3 = new TableColumn<>("Tồn HT");
        c3.setCellValueFactory(new PropertyValueFactory<>("systemQty"));
        c3.setPrefWidth(80);

        TableColumn<InventoryCheckItem, Void> c4 = new TableColumn<>("Tồn thực tế");
        c4.setPrefWidth(100);
        c4.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(0, 99999, 0);
            {
                spinner.setPrefWidth(90);
                spinner.setEditable(true);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    InventoryCheckItem row = getTableView().getItems().get(getIndex());
                    spinner.getValueFactory().setValue(row.getActualQty());
                    spinner.valueProperty().addListener((obs, o, n) -> {
                        row.setActualQty(n);
                        row.setDifference(n - row.getSystemQty());
                        getTableView().refresh();
                    });
                    setGraphic(spinner);
                }
            }
        });

        TableColumn<InventoryCheckItem, Integer> c5 = new TableColumn<>("Chênh lệch");
        c5.setCellValueFactory(new PropertyValueFactory<>("difference"));
        c5.setPrefWidth(90);
        c5.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item > 0 ? "+" + item : String.valueOf(item));
                    setStyle("-fx-font-weight: 700; -fx-text-fill: " +
                        (item == 0 ? "#166534" : "#dc2626") + "; -fx-font-family: 'Consolas';");
                }
            }
        });

        itemTable.getColumns().addAll(c1, c2, c3, c4, c5);

        content.getChildren().addAll(
            new Label("Kho kiểm tra *"), cbWarehouse,
            new Label("Ghi chú"), tfNotes,
            new Label("Chi tiết kiểm kê:"), itemTable
        );
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && cbWarehouse.getValue() != null) {
                int whIdx = cbWarehouse.getSelectionModel().getSelectedIndex();
                Warehouse wh = warehouses.get(whIdx);
                String nextId = "KK00" + (checks.size() + 1);
                return new InventoryCheck(nextId, wh.getId(), wh.getName(),
                    LocalDate.now().toString(), creator, "Hoàn tất", tfNotes.getText().trim(),
                    new ArrayList<>(items));
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ic -> {
            NetworkService.getInstance().insertInventoryCheck(ic);
            checks.add(0, ic);
        });
    }
}
