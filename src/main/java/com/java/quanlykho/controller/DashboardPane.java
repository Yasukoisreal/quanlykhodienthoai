package com.java.quanlykho.controller;

import com.java.quanlykho.model.Product;
import com.java.quanlykho.model.Warehouse;
import com.java.quanlykho.util.FormatUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.function.Consumer;

/**
 * Dashboard view - trang chủ với KPIs, biểu đồ doanh thu, và cảnh báo tồn kho.
 */
public class DashboardPane extends VBox {

    public DashboardPane(ObservableList<Product> products, ObservableList<Warehouse> warehouses,
                         Consumer<String> navigator) {
        setSpacing(20);
        setPadding(new Insets(0));

        // Calculate stats
        int totalQty = products.stream().mapToInt(Product::getStock).sum();
        double totalVal = products.stream().mapToDouble(p -> p.getStock() * p.getPrice()).sum();
        long lowStockCount = products.stream().filter(p -> p.getStock() <= 10).count();
        long overloadWh = warehouses.stream().filter(w -> w.getUsageRatio() >= 0.8).count();

        // === Welcome Banner ===
        VBox banner = new VBox(6);
        banner.getStyleClass().add("welcome-banner");
        Label welcomeTag = new Label("HỆ THỐNG ĐIỀU PHỐI THÔNG MINH");
        welcomeTag.getStyleClass().add("welcome-label");
        Label welcomeTitle = new Label("Xin chào, Quản trị viên!");
        welcomeTitle.getStyleClass().add("welcome-title");
        Label welcomeDesc = new Label("Hôm nay có sự thay đổi nhẹ về tồn khu vực miền nam. Kiểm tra các cảnh báo thấp để điều hối hàng hóa.");
        welcomeDesc.getStyleClass().add("welcome-subtitle");
        welcomeDesc.setWrapText(true);
        banner.getChildren().addAll(welcomeTag, welcomeTitle, welcomeDesc);

        // === KPI Cards ===
        GridPane kpiGrid = new GridPane();
        kpiGrid.setHgap(16);
        kpiGrid.setVgap(16);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            cc.setHgrow(Priority.ALWAYS);
            kpiGrid.getColumnConstraints().add(cc);
        }

        kpiGrid.add(createKpiCard("DOANH SỐ ĐỊNH GIÁ KHO",
            FormatUtil.formatVND(totalVal), "+12.5% so với tuần trước", "📈", "kpi-icon-blue"), 0, 0);
        kpiGrid.add(createKpiCard("TỔNG SỐ LƯỢNG MÁY",
            totalQty + " chiếc", "Được lưu trữ tại " + warehouses.size() + " kho bãi", "📦", "kpi-icon-green"), 1, 0);
        kpiGrid.add(createKpiCard("SẢN PHẨM SẮP HẾT",
            lowStockCount + " máy", "Cần nhập kho bổ sung sớm", "⚠", "kpi-icon-amber"), 2, 0);
        kpiGrid.add(createKpiCard("CẢNH BÁO QUÁ TẢI KHO",
            overloadWh + " khu vực", "Độ lấp đầy vượt ngưỡng 80%", "🛡", "kpi-icon-rose"), 3, 0);

        // === Charts + Alerts Layout ===
        HBox chartsRow = new HBox(20);
        chartsRow.setAlignment(Pos.TOP_LEFT);

        // Bar Chart
        VBox chartCard = new VBox(12);
        chartCard.getStyleClass().add("card");
        chartCard.setPrefWidth(700);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        HBox chartHeader = new HBox();
        chartHeader.setAlignment(Pos.CENTER_LEFT);
        VBox chartHeaderText = new VBox(2);
        Label chartTitle = new Label("Biểu đồ Doanh Số Nhập Kho Gần Đây");
        chartTitle.getStyleClass().add("card-title");
        Label chartSub = new Label("Giá trị luân chuyển hàng hóa ước tính tuần này");
        chartSub.getStyleClass().add("card-subtitle");
        chartHeaderText.getChildren().addAll(chartTitle, chartSub);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label chartBadge = new Label("Toàn hệ thống");
        chartBadge.getStyleClass().addAll("badge", "badge-info");
        chartHeader.getChildren().addAll(chartHeaderText, spacer, chartBadge);

        // Build BarChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("VND");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return FormatUtil.formatShortVND(object.doubleValue());
            }
        });

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        barChart.setPrefHeight(280);
        barChart.setCategoryGap(20);
        barChart.setBarGap(2);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};
        double[] values = {45_000_000, 120_000_000, 85_000_000, 160_000_000, 240_000_000, 310_000_000, 195_000_000};
        for (int i = 0; i < days.length; i++) {
            series.getData().add(new XYChart.Data<>(days[i], values[i]));
        }
        barChart.getData().add(series);

        chartCard.getChildren().addAll(chartHeader, barChart);

        // Low Stock Alerts
        VBox alertCard = new VBox(12);
        alertCard.getStyleClass().add("card");
        alertCard.setPrefWidth(320);
        alertCard.setMinWidth(280);

        HBox alertHeader = new HBox();
        alertHeader.setAlignment(Pos.CENTER_LEFT);
        Label alertTitle = new Label("Cảnh báo tồn kho cực thấp");
        alertTitle.getStyleClass().add("card-title");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Label alertBadge = new Label("Ngưỡng ≤ 10");
        alertBadge.getStyleClass().addAll("badge", "badge-warning");
        alertHeader.getChildren().addAll(alertTitle, sp2, alertBadge);
        alertCard.getChildren().add(alertHeader);

        var lowStockProds = products.stream().filter(p -> p.getStock() <= 10).toList();
        if (lowStockProds.isEmpty()) {
            Label noAlert = new Label("✅ Hòa vốn ổn định, không có dòng máy nào dưới mức báo động!");
            noAlert.getStyleClass().add("text-muted");
            noAlert.setWrapText(true);
            noAlert.setAlignment(Pos.CENTER);
            noAlert.setPadding(new Insets(30, 10, 30, 10));
            alertCard.getChildren().add(noAlert);
        } else {
            for (Product p : lowStockProds) {
                HBox alertItem = new HBox(10);
                alertItem.getStyleClass().add("alert-item");
                alertItem.setAlignment(Pos.CENTER_LEFT);

                VBox info = new VBox(2);
                HBox.setHgrow(info, Priority.ALWAYS);
                Label pName = new Label(p.getModelName());
                pName.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
                Label pDetail = new Label("Mã: " + p.getId() + " • " + p.getBrand());
                pDetail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
                info.getChildren().addAll(pName, pDetail);

                VBox stockInfo = new VBox(4);
                stockInfo.setAlignment(Pos.CENTER_RIGHT);
                Label stockBadge = new Label("Chỉ còn " + p.getStock() + " máy");
                stockBadge.getStyleClass().addAll("badge", "badge-warning");
                stockInfo.getChildren().add(stockBadge);

                alertItem.getChildren().addAll(info, stockInfo);
                alertCard.getChildren().add(alertItem);
            }
        }

        chartsRow.getChildren().addAll(chartCard, alertCard);

        // === Brand Distribution PieChart ===
        VBox pieCard = new VBox(12);
        pieCard.getStyleClass().add("card");
        Label pieTitle = new Label("📊 Phân bố tồn kho theo Thương hiệu");
        pieTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 13px;");

        javafx.collections.ObservableList<PieChart.Data> pieData = javafx.collections.FXCollections.observableArrayList();
        java.util.Map<String, Integer> brandMap = new java.util.LinkedHashMap<>();
        for (Product p : products) {
            brandMap.merge(p.getBrand(), p.getStock(), Integer::sum);
        }
        brandMap.forEach((brand, qty) -> pieData.add(new PieChart.Data(brand + " (" + qty + ")", qty)));

        PieChart pieChart = new PieChart(pieData);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(280);
        pieChart.setAnimated(true);

        pieCard.getChildren().addAll(pieTitle, pieChart);

        // === Warehouse usage summary ===
        VBox whSummary = new VBox(12);
        whSummary.getStyleClass().add("card");
        Label whTitle = new Label("📦 Tỷ lệ sử dụng kho bãi");
        whTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 13px;");
        whSummary.getChildren().add(whTitle);

        for (Warehouse w : warehouses) {
            HBox whRow = new HBox(10);
            whRow.setAlignment(Pos.CENTER_LEFT);
            Label whName = new Label(w.getName());
            whName.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
            whName.setPrefWidth(150);
            javafx.scene.control.ProgressBar pb = new javafx.scene.control.ProgressBar(w.getUsageRatio());
            pb.setPrefWidth(200);
            if (w.getUsageRatio() >= 0.8) pb.getStyleClass().add("progress-bar-danger");
            HBox.setHgrow(pb, Priority.ALWAYS);
            Label pct = new Label(String.format("%.0f%%", w.getUsageRatio() * 100));
            pct.setStyle("-fx-font-weight: 700; -fx-text-fill: " + (w.getUsageRatio() >= 0.8 ? "#dc2626" : "#059669") + "; -fx-font-size: 12px;");
            whRow.getChildren().addAll(whName, pb, pct);
            whSummary.getChildren().add(whRow);
        }

        HBox bottomRow = new HBox(20);
        HBox.setHgrow(pieCard, Priority.ALWAYS);
        HBox.setHgrow(whSummary, Priority.ALWAYS);
        bottomRow.getChildren().addAll(pieCard, whSummary);

        getChildren().addAll(banner, kpiGrid, chartsRow, bottomRow);
    }

    private VBox createKpiCard(String label, String value, String change, String icon, String iconClass) {
        VBox card = new VBox(6);
        card.getStyleClass().add("kpi-card");

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(4);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("kpi-label");
        Label val = new Label(value);
        val.getStyleClass().add("kpi-value");
        Label chg = new Label(change);
        chg.getStyleClass().add("kpi-change-positive");
        chg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        textBox.getChildren().addAll(lbl, val, chg);

        StackPane iconPane = new StackPane();
        iconPane.getStyleClass().add(iconClass);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");
        iconPane.getChildren().add(iconLabel);

        row.getChildren().addAll(textBox, iconPane);
        card.getChildren().add(row);
        return card;
    }
}
