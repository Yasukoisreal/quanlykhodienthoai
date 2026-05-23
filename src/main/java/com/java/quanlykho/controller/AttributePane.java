package com.java.quanlykho.controller;

import com.java.quanlykho.model.Attributes;
import com.java.quanlykho.model.Attributes.AttributeColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Attribute management view - quản lý brands, colors, storages, CPUs.
 */
public class AttributePane extends VBox {

    private final Attributes attributes;
    private final Deque<AttributeSnapshot> history = new ArrayDeque<>();
    private final Button btnUndo;

    // FlowPanes for displaying tags
    private final FlowPane brandFlow = new FlowPane(8, 8);
    private final FlowPane colorFlow = new FlowPane(8, 8);
    private final FlowPane storageFlow = new FlowPane(8, 8);
    private final FlowPane cpuFlow = new FlowPane(8, 8);

    public AttributePane(Attributes attributes) {
        this.attributes = attributes;
        setSpacing(16);

        // === Header toolbar ===
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("⚙");
        icon.setStyle("-fx-font-size: 18px;");
        VBox headerText = new VBox(2);
        Label title = new Label("Quản lý Thuộc tính Cấu hình");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Label sub = new Label("Thiết lập các mảng dữ liệu sản phẩm tĩnh cho cơ chế kiểm tra kiểu dữ liệu của hệ thống Java");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        headerText.getChildren().addAll(title, sub);
        HBox.setHgrow(headerText, Priority.ALWAYS);

        btnUndo = new Button("↩ Phục hồi (0)");
        btnUndo.getStyleClass().add("btn-secondary");
        btnUndo.setDisable(true);
        btnUndo.setOnAction(e -> handleUndo());

        toolbar.getChildren().addAll(icon, headerText, btnUndo);

        // === Bento Grid ===
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(33.33);
        cc1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(cc1, new ColumnConstraints() {{ setPercentWidth(33.33); setHgrow(Priority.ALWAYS); }},
            new ColumnConstraints() {{ setPercentWidth(33.33); setHgrow(Priority.ALWAYS); }});

        // Brand card
        grid.add(createBrandCard(), 0, 0);
        // Color card
        grid.add(createColorCard(), 1, 0);
        // Storage card
        grid.add(createStorageCard(), 2, 0);
        // CPU card (spans 3 cols)
        VBox cpuCard = createCpuCard();
        grid.add(cpuCard, 0, 1, 3, 1);

        getChildren().addAll(toolbar, grid);
        refreshAll();
    }

    private VBox createBrandCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("🏷 THƯƠNG HIỆU SMARTPHONE");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 11px;");
        brandFlow.setMinHeight(80);

        HBox addRow = new HBox(8);
        TextField tf = new TextField();
        tf.setPromptText("Nhãn hiệu mới...");
        tf.setPrefWidth(160);
        HBox.setHgrow(tf, Priority.ALWAYS);
        Button btnAdd = new Button("Thêm");
        btnAdd.getStyleClass().add("btn-dark");
        btnAdd.setOnAction(e -> {
            if (!tf.getText().isBlank() && !attributes.getBrands().contains(tf.getText().trim())) {
                pushHistory();
                attributes.getBrands().add(tf.getText().trim());
                tf.clear();
                refreshAll();
            }
        });
        addRow.getChildren().addAll(tf, btnAdd);

        card.getChildren().addAll(title, brandFlow, addRow);
        return card;
    }

    private VBox createColorCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("📱 MÀU SẮC THIẾT KẾ");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 11px;");
        colorFlow.setMinHeight(80);

        HBox nameRow = new HBox(8);
        TextField tfName = new TextField();
        tfName.setPromptText("Tên màu...");
        HBox.setHgrow(tfName, Priority.ALWAYS);
        ColorPicker picker = new ColorPicker(Color.web("#3b82f6"));
        picker.setPrefWidth(50);
        nameRow.getChildren().addAll(tfName, picker);

        Button btnAdd = new Button("Thêm màu sắc mới");
        btnAdd.getStyleClass().add("btn-dark");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> {
            if (!tfName.getText().isBlank()) {
                pushHistory();
                String hex = String.format("#%02x%02x%02x",
                    (int)(picker.getValue().getRed()*255),
                    (int)(picker.getValue().getGreen()*255),
                    (int)(picker.getValue().getBlue()*255));
                attributes.getColors().add(new AttributeColor(tfName.getText().trim(), hex));
                tfName.clear();
                refreshAll();
            }
        });

        card.getChildren().addAll(title, colorFlow, nameRow, btnAdd);
        return card;
    }

    private VBox createStorageCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("💾 DUNG LƯỢNG LƯU TRỮ");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 11px;");
        storageFlow.setMinHeight(80);

        HBox addRow = new HBox(8);
        TextField tf = new TextField();
        tf.setPromptText("Ví dụ: 2TB...");
        HBox.setHgrow(tf, Priority.ALWAYS);
        Button btnAdd = new Button("Thêm");
        btnAdd.getStyleClass().add("btn-dark");
        btnAdd.setOnAction(e -> {
            if (!tf.getText().isBlank() && !attributes.getStorages().contains(tf.getText().trim())) {
                pushHistory();
                attributes.getStorages().add(tf.getText().trim());
                tf.clear();
                refreshAll();
            }
        });
        addRow.getChildren().addAll(tf, btnAdd);

        card.getChildren().addAll(title, storageFlow, addRow);
        return card;
    }

    private VBox createCpuCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("🔲 VI XỬ LÝ & VI MẠCH TƯƠNG THÍCH");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 11px;");
        cpuFlow.setMinHeight(50);

        HBox addRow = new HBox(12);
        TextField tf = new TextField();
        tf.setPromptText("Nhập dòng CPU mới (ví dụ: Apple A19 Bionic)...");
        tf.setPrefWidth(350);
        HBox.setHgrow(tf, Priority.ALWAYS);
        Button btnAdd = new Button("Khai báo CPU mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> {
            if (!tf.getText().isBlank() && !attributes.getCpuList().contains(tf.getText().trim())) {
                pushHistory();
                attributes.getCpuList().add(tf.getText().trim());
                tf.clear();
                refreshAll();
            }
        });
        addRow.getChildren().addAll(tf, btnAdd);

        card.getChildren().addAll(title, cpuFlow, addRow);
        return card;
    }

    private void refreshAll() {
        refreshFlow(brandFlow, attributes.getBrands(), "brand");
        refreshColorFlow();
        refreshFlow(storageFlow, attributes.getStorages(), "storage");
        refreshFlow(cpuFlow, attributes.getCpuList(), "cpu");
        btnUndo.setText("↩ Phục hồi (" + history.size() + ")");
        btnUndo.setDisable(history.isEmpty());
    }

    private void refreshFlow(FlowPane flow, List<String> items, String type) {
        flow.getChildren().clear();
        for (String item : items) {
            HBox tag = new HBox(6);
            tag.getStyleClass().add("badge-brand");
            tag.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(item);
            lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #334155;");
            Button del = new Button("✕");
            del.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 0 2;");
            del.setOnAction(e -> {
                pushHistory();
                items.remove(item);
                refreshAll();
            });
            tag.getChildren().addAll(lbl, del);
            flow.getChildren().add(tag);
        }
    }

    private void refreshColorFlow() {
        colorFlow.getChildren().clear();
        for (AttributeColor c : attributes.getColors()) {
            HBox tag = new HBox(6);
            tag.getStyleClass().add("badge-brand");
            tag.setAlignment(Pos.CENTER_LEFT);

            Circle dot = new Circle(5);
            try { dot.setFill(Color.web(c.getHex())); }
            catch (Exception ex) { dot.setFill(Color.GRAY); }
            dot.setStroke(Color.web("#cbd5e1"));

            Label lbl = new Label(c.getName());
            lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #334155;");
            Button del = new Button("✕");
            del.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 0 2;");
            del.setOnAction(e -> {
                pushHistory();
                attributes.getColors().remove(c);
                refreshAll();
            });
            tag.getChildren().addAll(dot, lbl, del);
            colorFlow.getChildren().add(tag);
        }
    }

    // === Undo system ===
    private void pushHistory() {
        history.push(new AttributeSnapshot(attributes));
    }

    private void handleUndo() {
        if (history.isEmpty()) return;
        AttributeSnapshot snap = history.pop();
        snap.restore(attributes);
        refreshAll();
    }

    /**
     * Deep copy snapshot for undo.
     */
    private static class AttributeSnapshot {
        final List<String> brands;
        final List<AttributeColor> colors;
        final List<String> storages;
        final List<String> cpus;

        AttributeSnapshot(Attributes a) {
            this.brands = new ArrayList<>(a.getBrands());
            this.colors = new ArrayList<>();
            for (AttributeColor c : a.getColors()) {
                this.colors.add(new AttributeColor(c.getName(), c.getHex()));
            }
            this.storages = new ArrayList<>(a.getStorages());
            this.cpus = new ArrayList<>(a.getCpuList());
        }

        void restore(Attributes a) {
            a.setBrands(new ArrayList<>(brands));
            a.setColors(new ArrayList<>());
            for (AttributeColor c : colors) {
                a.getColors().add(new AttributeColor(c.getName(), c.getHex()));
            }
            a.setStorages(new ArrayList<>(storages));
            a.setCpuList(new ArrayList<>(cpus));
        }
    }
}
