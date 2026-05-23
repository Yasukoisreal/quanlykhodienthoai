package com.java.quanlykho.controller;

import com.java.quanlykho.model.*;
import com.java.quanlykho.network.NetworkService;
import com.java.quanlykho.util.FormatUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.ListChangeListener;

/**
 * Export voucher management - create, preview, approve.
 */
public class ExportVoucherPane extends HBox {

    private final ObservableList<Product> products;
    private final ObservableList<Warehouse> warehouses;
    private final ObservableList<Customer> customers;
    private final ObservableList<ExportVoucher> exportVouchers;
    private final String creator;
    private final ObservableList<ImportVoucherProduct> voucherItems = FXCollections.observableArrayList();

    private ComboBox<String> cbCustomer;
    private ComboBox<String> cbWarehouse;
    private final VBox itemsContainer = new VBox(8);
    private final Label totalLabel = new Label("0 ₫");
    private final VBox pastVouchersContainer = new VBox(10);

    public ExportVoucherPane(ObservableList<Product> products, ObservableList<Warehouse> warehouses,
                             ObservableList<Customer> customers, ObservableList<ExportVoucher> exportVouchers,
                             String creator) {
        this.products = products;
        this.warehouses = warehouses;
        this.customers = customers;
        this.exportVouchers = exportVouchers;
        this.creator = creator;

        setSpacing(20);
        setPadding(new Insets(0));

        VBox leftPanel = createLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        VBox rightPanel = createRightPanel();
        rightPanel.setPrefWidth(360);
        rightPanel.setMinWidth(320);

        getChildren().addAll(leftPanel, rightPanel);
        refreshItems();
        refreshPastVouchers();
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("card");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 12 0;");
        Label title = new Label("➖ Lập phiếu xuất kho");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label step = new Label("BƯỚC 1");
        step.getStyleClass().addAll("badge", "badge-brand");
        header.getChildren().addAll(title, sp, step);

        // Filters
        GridPane filters = new GridPane();
        filters.setHgap(16);
        filters.setVgap(12);

        VBox boxCustomer = new VBox(4);
        Label lblCustomer = new Label("Khách hàng:");
        lblCustomer.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 11px;");
        cbCustomer = new ComboBox<>();
        Runnable updateCbCustomer = () -> {
            cbCustomer.getItems().clear();
            customers.forEach(c -> cbCustomer.getItems().add(c.getName() + " - " + c.getPhone()));
            if (!customers.isEmpty() && cbCustomer.getValue() == null) cbCustomer.setValue(cbCustomer.getItems().get(0));
        };
        updateCbCustomer.run();
        customers.addListener((ListChangeListener<Customer>) c -> updateCbCustomer.run());
        cbCustomer.setMaxWidth(Double.MAX_VALUE);
        boxCustomer.getChildren().addAll(lblCustomer, cbCustomer);

        VBox boxWh = new VBox(4);
        Label lblWh = new Label("Xuất từ kho:");
        lblWh.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 11px;");
        cbWarehouse = new ComboBox<>();
        Runnable updateCbWarehouse = () -> {
            cbWarehouse.getItems().clear();
            warehouses.forEach(w -> cbWarehouse.getItems().add(w.getName()));
            if (!warehouses.isEmpty() && cbWarehouse.getSelectionModel().getSelectedIndex() < 0) cbWarehouse.getSelectionModel().selectFirst();
        };
        updateCbWarehouse.run();
        warehouses.addListener((ListChangeListener<Warehouse>) c -> updateCbWarehouse.run());
        cbWarehouse.setMaxWidth(Double.MAX_VALUE);
        boxWh.getChildren().addAll(lblWh, cbWarehouse);

        filters.add(boxCustomer, 0, 0);
        filters.add(boxWh, 1, 0);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        filters.getColumnConstraints().addAll(c1, c2);

        // Product Catalog
        VBox catalog = new VBox(8);
        Label lblCat = new Label("Chọn sản phẩm từ danh mục:");
        lblCat.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 11px;");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(160);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

        FlowPane flow = new FlowPane(8, 8);
        flow.setPadding(new Insets(8));
        for (Product p : products) {
            Button btn = new Button(p.getModelName() + " (" + p.getStock() + ")");
            btn.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 11px;");
            if (p.getStock() <= 0) {
                btn.setDisable(true);
            }
            btn.setOnAction(e -> addToVoucher(p));
            flow.getChildren().add(btn);
        }
        scroll.setContent(flow);
        catalog.getChildren().addAll(lblCat, scroll);

        // Selected Items Header
        HBox selectedHeader = new HBox();
        selectedHeader.setAlignment(Pos.CENTER_LEFT);
        selectedHeader.setPadding(new Insets(10, 0, 0, 0));
        Label selTitle = new Label("Danh sách sản phẩm xuất");
        selTitle.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
        selectedHeader.getChildren().add(selTitle);

        ScrollPane itemsScroll = new ScrollPane(itemsContainer);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setPrefHeight(250);
        itemsScroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        panel.getChildren().addAll(header, filters, catalog, selectedHeader, itemsScroll);
        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(16);

        // Preview Card
        VBox preview = new VBox(12);
        preview.getStyleClass().add("card");
        preview.setPrefHeight(350);

        HBox previewHeader = new HBox();
        previewHeader.setAlignment(Pos.CENTER_LEFT);
        previewHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 12 0;");
        Label previewTitle = new Label("📄 Xem trước phiếu xuất");
        previewTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label step2 = new Label("BƯỚC 2");
        step2.getStyleClass().addAll("badge", "badge-brand");
        previewHeader.getChildren().addAll(previewTitle, sp, step2);

        VBox details = new VBox(8);
        details.getChildren().addAll(
            createDetailRow("Mã phiếu dự kiến:", "PX00" + (exportVouchers.size() + 1)),
            createDetailRow("Ngày tạo lập:", LocalDate.now().toString()),
            createDetailRow("Người lập phiếu:", creator)
        );

        VBox totalBox = new VBox(4);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(10, 0, 0, 0));
        Label totalTitle = new Label("TỔNG GIÁ TRỊ ĐƠN XUẤT");
        totalTitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        totalLabel.setStyle("-fx-text-fill: #e11d48; -fx-font-size: 20px; -fx-font-weight: 800; -fx-font-family: 'Consolas';");
        totalBox.getChildren().addAll(totalTitle, totalLabel);

        HBox btnRow = new HBox(12);
        btnRow.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        Button btnApprove = new Button("Phê Duyệt Xuất Kho");
        btnApprove.getStyleClass().add("btn-dark");
        btnApprove.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnApprove, Priority.ALWAYS);
        btnApprove.setOnAction(e -> submitVoucher("Đã Duyệt"));

        btnRow.getChildren().addAll(btnApprove);

        preview.getChildren().addAll(previewHeader, details, totalBox, btnRow);

        // Past vouchers
        VBox pastCard = new VBox(12);
        pastCard.getStyleClass().add("card");
        Label pastTitle = new Label("📂 Sổ phiếu xuất hiện tại (" + exportVouchers.size() + ")");
        pastTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 12px;");

        ScrollPane pastScroll = new ScrollPane(pastVouchersContainer);
        pastScroll.setFitToWidth(true);
        pastScroll.setPrefHeight(180);
        pastScroll.setStyle("-fx-background-color: transparent;");

        pastCard.getChildren().addAll(pastTitle, pastScroll);

        panel.getChildren().addAll(preview, pastCard);
        return panel;
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: #f8fafc; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 6 0;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
        row.getChildren().addAll(lbl, sp, val);
        return row;
    }

    private void addToVoucher(Product p) {
        ImportVoucherProduct existing = voucherItems.stream()
            .filter(i -> i.getProductId().equals(p.getId())).findFirst().orElse(null);
        if (existing != null) {
            if (existing.getQuantity() < p.getStock()) {
                existing.setQuantity(existing.getQuantity() + 1);
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Hết hàng");
                a.setHeaderText(null);
                a.setContentText("Số lượng trong kho không đủ!");
                a.showAndWait();
            }
        } else {
            voucherItems.add(new ImportVoucherProduct(p.getId(), p.getModelName(), 1, p.getPrice()));
        }
        refreshItems();
    }

    private void refreshItems() {
        itemsContainer.getChildren().clear();
        if (voucherItems.isEmpty()) {
            Label empty = new Label("Mời chọn sản phẩm từ danh mục.");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-padding: 30;");
            empty.setAlignment(Pos.CENTER);
            itemsContainer.getChildren().add(empty);
        } else {
            for (ImportVoucherProduct item : voucherItems) {
                HBox row = new HBox(12);
                row.getStyleClass().add("voucher-item");
                row.setAlignment(Pos.CENTER_LEFT);

                VBox info = new VBox(2);
                HBox.setHgrow(info, Priority.ALWAYS);
                Label name = new Label(item.getModelName());
                name.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
                Label id = new Label("ID: " + item.getProductId());
                id.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-family: 'Consolas';");
                info.getChildren().addAll(name, id);

                VBox priceBox = new VBox(2);
                Label priceLbl = new Label("Giá bán (VND)");
                priceLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 700;");
                Spinner<Integer> priceSpinner = new Spinner<>(0, 999999999, (int) item.getPrice(), 1000000);
                priceSpinner.setPrefWidth(120);
                priceSpinner.valueProperty().addListener((obs, o, n) -> { item.setPrice(n); updateTotal(); });
                priceBox.getChildren().addAll(priceLbl, priceSpinner);

                Product p = products.stream().filter(pr -> pr.getId().equals(item.getProductId())).findFirst().orElse(null);
                int maxQty = p != null ? p.getStock() : 1;

                VBox qtyBox = new VBox(2);
                Label qtyLbl = new Label("Số lượng");
                qtyLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 700;");
                Spinner<Integer> qtySpinner = new Spinner<>(1, maxQty, item.getQuantity());
                qtySpinner.setPrefWidth(80);
                qtySpinner.valueProperty().addListener((obs, o, n) -> { item.setQuantity(n); updateTotal(); });
                qtyBox.getChildren().addAll(qtyLbl, qtySpinner);

                VBox subtotalBox = new VBox(2);
                subtotalBox.setAlignment(Pos.CENTER_RIGHT);
                Label stLbl = new Label("Thành tiền");
                stLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
                Label stVal = new Label(FormatUtil.formatVND(item.getSubtotal()));
                stVal.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-family: 'Consolas'; -fx-font-size: 12px;");
                subtotalBox.getChildren().addAll(stLbl, stVal);

                Button btnRemove = new Button("🗑");
                btnRemove.getStyleClass().add("btn-danger");
                btnRemove.setOnAction(e -> { voucherItems.remove(item); refreshItems(); });

                row.getChildren().addAll(info, priceBox, qtyBox, subtotalBox, btnRemove);
                itemsContainer.getChildren().add(row);
            }
        }
        updateTotal();
    }

    private void updateTotal() {
        double total = voucherItems.stream().mapToDouble(ImportVoucherProduct::getSubtotal).sum();
        totalLabel.setText(FormatUtil.formatVND(total));
    }

    private void submitVoucher(String status) {
        if (voucherItems.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Chưa đủ dữ liệu");
            a.setHeaderText(null);
            a.setContentText("Vui lòng bổ sung ít nhất 1 mặt hàng vào phiếu xuất!");
            a.showAndWait();
            return;
        }

        String nextId = "PX00" + (exportVouchers.size() + 1);
        double total = voucherItems.stream().mapToDouble(ImportVoucherProduct::getSubtotal).sum();
        int whIdx = cbWarehouse.getSelectionModel().getSelectedIndex();
        String whId = whIdx >= 0 && whIdx < warehouses.size() ? warehouses.get(whIdx).getId() : "";

        ExportVoucher v = new ExportVoucher(nextId, cbCustomer.getValue(), whId,
            LocalDate.now().toString(), new ArrayList<>(voucherItems), total,
            creator, status);

        NetworkService.getInstance().insertExportVoucher(v);
        exportVouchers.add(0, v);

        if ("Đã Duyệt".equals(status)) {
            executeStockEffects(v);
        }

        voucherItems.clear();
        refreshItems();
        refreshPastVouchers();

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Thành công");
        a.setHeaderText("Đã lập phiếu xuất kho " + v.getId());
        a.showAndWait();
    }

    private void executeStockEffects(ExportVoucher v) {
        int totalUnloaded = 0;
        for (ImportVoucherProduct item : v.getProducts()) {
            Product p = products.stream().filter(pr -> pr.getId().equals(item.getProductId())).findFirst().orElse(null);
            if (p != null) {
                p.setStock(Math.max(0, p.getStock() - item.getQuantity()));
                NetworkService.getInstance().updateProduct(p);
            }
            totalUnloaded += item.getQuantity();
        }
        for (int i = 0; i < warehouses.size(); i++) {
            if (warehouses.get(i).getId().equals(v.getWarehouseId())) {
                Warehouse w = warehouses.get(i);
                w.setUsedSpace(Math.max(0, w.getUsedSpace() - totalUnloaded));
                warehouses.set(i, w);
                NetworkService.getInstance().updateWarehouse(w);
                break;
            }
        }
    }

    private void refreshPastVouchers() {
        pastVouchersContainer.getChildren().clear();
        if (exportVouchers.isEmpty()) {
            Label empty = new Label("Chưa có phiếu xuất nào.");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            pastVouchersContainer.getChildren().add(empty);
            return;
        }

        int count = 0;
        for (ExportVoucher v : exportVouchers) {
            if (count++ >= 5) break;

            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8 0; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

            VBox info = new VBox(2);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label id = new Label(v.getId() + " • " + v.getDate());
            id.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 11px;");
            Label supp = new Label(v.getCustomer());
            supp.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
            info.getChildren().addAll(id, supp);

            Label badge = new Label(v.getStatus());
            badge.getStyleClass().addAll("badge",
                "Đã Duyệt".equals(v.getStatus()) ? "badge-success" : "badge-warning");
            badge.setStyle("-fx-font-size: 9px; -fx-padding: 2 6;");

            Button btnPdf = new Button("In Hóa Đơn");
            btnPdf.getStyleClass().add("btn-secondary");
            btnPdf.setStyle("-fx-font-size: 10px; -fx-padding: 4 8;");
            btnPdf.setOnAction(e -> com.java.quanlykho.util.PDFUtil.exportVoucherToPDF(v, getScene().getWindow()));

            row.getChildren().addAll(info, badge, btnPdf);
            pastVouchersContainer.getChildren().add(row);
        }
    }
}
