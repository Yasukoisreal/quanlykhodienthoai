package com.java.quanlykho.controller;

import com.java.quanlykho.model.AuditLog;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Lịch sử thao tác (Audit Log) - hiển thị log CRUD.
 */
public class AuditLogPane extends VBox {

    public AuditLogPane(ObservableList<AuditLog> logs) {
        setSpacing(16);

        TableView<AuditLog> table = new TableView<>(logs);

        // Header
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📜");
        icon.setStyle("-fx-font-size: 18px;");
        VBox headerText = new VBox(2);
        Label title = new Label("Nhật Ký Hoạt Động Hệ Thống");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Label sub = new Label("Ghi nhận toàn bộ thao tác CRUD (Create/Read/Update/Delete) trên hệ thống");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        headerText.getChildren().addAll(title, sub);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnExport = new Button("📥 Xuất Excel");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> com.java.quanlykho.util.ExportUtil.exportTableToExcel(table, "audit_log", getScene().getWindow()));

        toolbar.getChildren().addAll(icon, headerText, spacer, btnExport);

        // Table
        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(0));

        HBox tableHeader = new HBox(8);
        tableHeader.setPadding(new Insets(14, 20, 14, 20));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        Label tIcon = new Label("📜");
        tIcon.setStyle("-fx-font-size: 16px;");
        Label tTitle = new Label("100 bản ghi gần nhất");
        tTitle.getStyleClass().add("card-title");
        tableHeader.getChildren().addAll(tIcon, tTitle);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(500);

        TableColumn<AuditLog, String> cTime = new TableColumn<>("Thời gian");
        cTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        cTime.setPrefWidth(150);
        cTime.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-family: 'Consolas'; -fx-font-size: 11px; -fx-text-fill: #64748b;");
            }
        });

        TableColumn<AuditLog, String> cUser = new TableColumn<>("Tài khoản");
        cUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        cUser.setPrefWidth(130);
        cUser.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: 700; -fx-text-fill: #0f172a;");
            }
        });

        TableColumn<AuditLog, String> cAction = new TableColumn<>("Hành động");
        cAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        cAction.setPrefWidth(90);
        cAction.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    String cssClass = switch (item) {
                        case "INSERT" -> "badge-success";
                        case "UPDATE" -> "badge-brand";
                        case "DELETE" -> "badge-danger";
                        case "LOGIN" -> "badge-purple";
                        default -> "badge-brand";
                    };
                    badge.getStyleClass().addAll("badge", cssClass);
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<AuditLog, String> cTable = new TableColumn<>("Module");
        cTable.setCellValueFactory(new PropertyValueFactory<>("targetTable"));
        cTable.setPrefWidth(120);

        TableColumn<AuditLog, String> cTargetId = new TableColumn<>("Mã đối tượng");
        cTargetId.setCellValueFactory(new PropertyValueFactory<>("targetId"));
        cTargetId.setPrefWidth(100);
        cTargetId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-family: 'Consolas'; -fx-font-weight: 700; -fx-text-fill: #4338ca;");
            }
        });

        TableColumn<AuditLog, String> cDetail = new TableColumn<>("Chi tiết");
        cDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));

        table.getColumns().addAll(cTime, cUser, cAction, cTable, cTargetId, cDetail);

        tableCard.getChildren().addAll(tableHeader, table);
        getChildren().addAll(toolbar, tableCard);
    }
}
