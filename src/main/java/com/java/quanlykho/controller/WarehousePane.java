package com.java.quanlykho.controller;

import com.java.quanlykho.model.Warehouse;
import com.java.quanlykho.network.NetworkService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Optional;

/**
 * Warehouse management view - cards with progress bars.
 */
public class WarehousePane extends VBox {

    private final ObservableList<Warehouse> warehouses;
    private final FlowPane cardContainer;

    public WarehousePane(ObservableList<Warehouse> warehouses) {
        this.warehouses = warehouses;
        setSpacing(16);

        // === Header ===
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 18px;");
        VBox headerText = new VBox(2);
        Label title = new Label("Mặt bằng Hệ thống Kho Điện Thoại");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Label sub = new Label("Giám sát sức chứa, tọa độ địa hình và trạng thái tiếp nhận thiết bị smartphone");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        headerText.getChildren().addAll(title, sub);
        HBox.setHgrow(headerText, Priority.ALWAYS);

        Button btnAdd = new Button("➕ Đăng ký Kho bãi Mới");
        btnAdd.getStyleClass().add("btn-success");
        btnAdd.setOnAction(e -> showAddDialog());

        toolbar.getChildren().addAll(icon, headerText, btnAdd);

        // === Cards Grid ===
        cardContainer = new FlowPane(20, 20);
        cardContainer.setPrefWrapLength(900);

        buildCards();

        // Listen for changes
        warehouses.addListener((javafx.collections.ListChangeListener<Warehouse>) c -> buildCards());

        getChildren().addAll(toolbar, cardContainer);
    }

    private void buildCards() {
        cardContainer.getChildren().clear();
        for (Warehouse w : warehouses) {
            cardContainer.getChildren().add(createWarehouseCard(w));
        }
    }

    private VBox createWarehouseCard(Warehouse w) {
        double ratio = w.getUsageRatio();
        int pct = (int) Math.min(100, Math.round(ratio * 100));
        boolean isFull = ratio >= 0.8;

        VBox card = new VBox(12);
        card.getStyleClass().add("warehouse-card");
        if (isFull) card.getStyleClass().add("warehouse-card-danger");
        card.setPrefWidth(320);
        card.setMinWidth(280);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label idBadge = new Label("Mã kho: " + w.getId());
        idBadge.getStyleClass().add("badge-mono");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        header.getChildren().add(idBadge);
        if (isFull) {
            Label warn = new Label("QUÁ TẢI (BÁO ĐỘNG)");
            warn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 9px; -fx-font-weight: 700;");
            header.getChildren().addAll(sp, warn);
        }

        // Name + location
        VBox info = new VBox(4);
        Label name = new Label(w.getName());
        name.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 13px;");
        name.setWrapText(true);
        Label loc = new Label("📍 " + w.getLocation());
        loc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        loc.setWrapText(true);
        info.getChildren().addAll(name, loc);

        // Progress section
        VBox progressSection = new VBox(8);
        HBox progressHeader = new HBox();
        progressHeader.setAlignment(Pos.CENTER_LEFT);
        Label usageLabel = new Label("Dung lượng sử dụng:");
        usageLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Label usageValue = new Label(w.getUsedSpace() + " / " + w.getCapacity() + " cái (" + pct + "%)");
        usageValue.setStyle("-fx-font-weight: 700; -fx-font-family: 'Consolas'; -fx-font-size: 11px; -fx-text-fill: " + (isFull ? "#dc2626" : "#0f172a") + ";");
        progressHeader.getChildren().addAll(usageLabel, sp2, usageValue);

        ProgressBar pb = new ProgressBar(ratio);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(8);
        if (isFull) pb.getStyleClass().add("progress-bar-danger");

        progressSection.getChildren().addAll(progressHeader, pb);

        // Footer
        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        Label capLabel = new Label("🏗 Sức chứa: " + w.getCapacity() + " tối đa");
        capLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Region sp3 = new Region();
        HBox.setHgrow(sp3, Priority.ALWAYS);

        Button btnEdit = new Button("✏ Sửa");
        btnEdit.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #0369a1; -fx-font-size: 10px; -fx-font-weight: 700; -fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4;");
        btnEdit.setOnAction(e -> showEditDialog(w));

        Button btnDelete = new Button("🗑 Xóa");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setStyle("-fx-font-size: 10px; -fx-padding: 4 10;");
        btnDelete.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa kho");
            alert.setHeaderText("Xóa vĩnh viễn kho: " + w.getName() + "?");
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                NetworkService.getInstance().deleteWarehouse(w.getId());
                warehouses.remove(w);
            }
        });

        Button btnClear = new Button("🔄 Giải phóng");
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 600; -fx-cursor: hand; -fx-padding: 4 8;");
        btnClear.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận giải phóng kho");
            alert.setHeaderText("Bạn có chắc muốn giải phóng toàn bộ kho " + w.getName() + "?");
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                w.setUsedSpace(0);
                NetworkService.getInstance().updateWarehouse(w);
                buildCards();
            }
        });
        footer.getChildren().addAll(capLabel, sp3, btnEdit, btnDelete, btnClear);

        card.getChildren().addAll(header, info, progressSection, footer);
        return card;
    }

    private void showAddDialog() {
        Dialog<Warehouse> dialog = new Dialog<>();
        dialog.setTitle("Đăng kiểm kho hàng mới");

        ButtonType saveBtn = new ButtonType("Tạo địa chỉ kho", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfName = new TextField();
        tfName.setPromptText("Kho Bán Lẻ Chi Nhánh 4");
        TextField tfLoc = new TextField();
        tfLoc.setPromptText("Số 88 Trường Chinh, Q. Tân Bình, Tp.HCM");
        TextField tfCap = new TextField("100");

        grid.add(new Label("Tên kho bãi *"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Địa chỉ tọa lạc *"), 0, 1);
        grid.add(tfLoc, 1, 1);
        grid.add(new Label("Sức chứa tối đa (cái) *"), 0, 2);
        grid.add(tfCap, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank() && !tfLoc.getText().isBlank()) {
                String nextId = "K0" + (warehouses.size() + 1);
                return new Warehouse(nextId, tfName.getText().trim(), tfLoc.getText().trim(),
                    Integer.parseInt(tfCap.getText()), 0);
            }
            return null;
        });

        Optional<Warehouse> result = dialog.showAndWait();
        result.ifPresent(w -> {
            NetworkService.getInstance().insertWarehouse(w);
            warehouses.add(w);
            buildCards();
        });
    }

    private void showEditDialog(Warehouse w) {
        Dialog<Warehouse> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin kho: " + w.getName());

        ButtonType saveBtn = new ButtonType("Lưu thay đổi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));

        TextField tfName = new TextField(w.getName());
        TextField tfLoc = new TextField(w.getLocation());
        TextField tfCap = new TextField(String.valueOf(w.getCapacity()));

        grid.add(new Label("Tên kho bãi *"), 0, 0); grid.add(tfName, 1, 0);
        grid.add(new Label("Địa chỉ tọa lạc *"), 0, 1); grid.add(tfLoc, 1, 1);
        grid.add(new Label("Sức chứa tối đa *"), 0, 2); grid.add(tfCap, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().isBlank()) {
                w.setName(tfName.getText().trim());
                w.setLocation(tfLoc.getText().trim());
                w.setCapacity(Integer.parseInt(tfCap.getText()));
                return w;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            NetworkService.getInstance().updateWarehouse(updated);
            buildCards();
        });
    }
}
