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
import java.util.List;
import java.util.Optional;
import javafx.collections.ListChangeListener;

/**
 * Import/Export voucher management - TabPane with import and export tabs.
 */
public class ImportVoucherPane extends HBox {

    private final ObservableList<Product> products;
    private final ObservableList<Warehouse> warehouses;
    private final ObservableList<Supplier> suppliers;
    private final ObservableList<ImportVoucher> vouchers;
    private final String creator;
    private final ObservableList<ImportVoucherProduct> voucherItems = FXCollections.observableArrayList();

    private ComboBox<String> cbSupplier;
    private ComboBox<String> cbWarehouse;
    private final VBox itemsContainer = new VBox(8);
    private final Label totalLabel = new Label("0 ₫");
    private final VBox pastVouchersContainer = new VBox(10);

    public ImportVoucherPane(ObservableList<Product> products, ObservableList<Warehouse> warehouses,
                       ObservableList<Supplier> suppliers, ObservableList<ImportVoucher> vouchers,
                       String creator) {
        this.products = products;
        this.warehouses = warehouses;
        this.suppliers = suppliers;
        this.vouchers = vouchers;
        this.creator = creator;

        setSpacing(20);
        setPadding(new Insets(0));

        // Left panel (2/3)
        VBox leftPanel = createLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Right panel (1/3)
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
        Label title = new Label("➕ Tạo lập phiếu nhập mới");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label step = new Label("BƯỚC 1");
        step.getStyleClass().addAll("badge", "badge-brand");
        header.getChildren().addAll(title, sp, step);

        // Filters
        GridPane filters = new GridPane();
        filters.setHgap(16);
        filters.setVgap(8);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        cc.setHgrow(Priority.ALWAYS);
        filters.getColumnConstraints().addAll(cc, new ColumnConstraints() {{ setPercentWidth(50); setHgrow(Priority.ALWAYS); }});

        cbSupplier = new ComboBox<>();
        cbSupplier.setMaxWidth(Double.MAX_VALUE);
        Runnable updateCbSupplier = () -> {
            cbSupplier.getItems().clear();
            suppliers.forEach(s -> cbSupplier.getItems().add(s.getName()));
            if (!suppliers.isEmpty() && cbSupplier.getValue() == null) cbSupplier.setValue(suppliers.get(0).getName());
        };
        updateCbSupplier.run();
        suppliers.addListener((ListChangeListener<Supplier>) c -> updateCbSupplier.run());

        cbWarehouse = new ComboBox<>();
        cbWarehouse.setMaxWidth(Double.MAX_VALUE);
        Runnable updateCbWarehouse = () -> {
            cbWarehouse.getItems().clear();
            warehouses.forEach(w -> cbWarehouse.getItems().add(w.getName() + " (còn " + (w.getCapacity() - w.getUsedSpace()) + ")"));
            if (!warehouses.isEmpty() && cbWarehouse.getSelectionModel().getSelectedIndex() < 0) cbWarehouse.getSelectionModel().selectFirst();
        };
        updateCbWarehouse.run();
        warehouses.addListener((ListChangeListener<Warehouse>) c -> updateCbWarehouse.run());

        filters.add(new Label("Đối tác phân phối *"), 0, 0);
        filters.add(cbSupplier, 0, 1);
        filters.add(new Label("Kho tiếp nhận chính *"), 1, 0);
        filters.add(cbWarehouse, 1, 1);

        // Product selection table
        Label selTitle = new Label("DANH SÁCH THIẾT BỊ BẾN KHO");
        selTitle.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 11px;");

        TableView<Product> selTable = new TableView<>(products);
        selTable.setPrefHeight(200);
        selTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Product, String> c1 = new TableColumn<>("Mã");
        c1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        c1.setPrefWidth(70);

        TableColumn<Product, String> c2 = new TableColumn<>("Mô tả kiểu mẫu");
        c2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("modelName"));

        TableColumn<Product, String> c3 = new TableColumn<>("Giá gốc");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(FormatUtil.formatVND(d.getValue().getPrice())));
        c3.setPrefWidth(130);

        TableColumn<Product, Void> c4 = new TableColumn<>("Bổ sung");
        c4.setPrefWidth(90);
        c4.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("+ Thêm");
            {
                btn.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #0369a1; -fx-font-weight: 700; -fx-font-size: 11px; -fx-border-color: #bfdbfe; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
                btn.setOnAction(e -> addToVoucher(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        selTable.getColumns().addAll(c1, c2, c3, c4);

        // Voucher items
        Label itemsTitle = new Label("CHI TIẾT CÁC MẶT HÀNG TRONG ĐƠN NHẬP");
        itemsTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 11px;");
        itemsTitle.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane itemsScroll = new ScrollPane(itemsContainer);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setPrefHeight(220);
        itemsScroll.setStyle("-fx-background-color: transparent;");

        panel.getChildren().addAll(header, filters, selTitle, selTable,
            new Separator(), itemsTitle, itemsScroll);
        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(16);

        // Preview card
        VBox preview = new VBox(12);
        preview.getStyleClass().add("card");

        HBox previewHeader = new HBox();
        previewHeader.setAlignment(Pos.CENTER_LEFT);
        previewHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 12 0;");
        Label previewTitle = new Label("📄 Xem trước phiếu nhập");
        previewTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label step2 = new Label("BƯỚC 2");
        step2.getStyleClass().addAll("badge", "badge-brand");
        previewHeader.getChildren().addAll(previewTitle, sp, step2);

        VBox details = new VBox(8);
        details.getChildren().addAll(
            createDetailRow("Mã phiếu dự kiến:", "PN00" + (vouchers.size() + 1)),
            createDetailRow("Ngày tạo lập:", LocalDate.now().toString()),
            createDetailRow("Người lập phiếu:", creator)
        );

        VBox totalBox = new VBox(4);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(10, 0, 0, 0));
        Label totalTitle = new Label("TỔNG GIÁ TRỊ ĐƠN NHẬP");
        totalTitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        totalLabel.setStyle("-fx-text-fill: #e11d48; -fx-font-size: 20px; -fx-font-weight: 800; -fx-font-family: 'Consolas';");
        totalBox.getChildren().addAll(totalTitle, totalLabel);

        HBox btnRow = new HBox(12);
        btnRow.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        Button btnDraft = new Button("Lưu bản nháp");
        btnDraft.getStyleClass().add("btn-secondary");
        btnDraft.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnDraft, Priority.ALWAYS);
        btnDraft.setOnAction(e -> submitVoucher("Nháp"));

        Button btnApprove = new Button("Phê Duyệt Nhập Kho");
        btnApprove.getStyleClass().add("btn-dark");
        btnApprove.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnApprove, Priority.ALWAYS);
        btnApprove.setOnAction(e -> submitVoucher("Đã Duyệt"));

        btnRow.getChildren().addAll(btnDraft, btnApprove);

        preview.getChildren().addAll(previewHeader, details, totalBox, btnRow);

        // Past vouchers
        VBox pastCard = new VBox(12);
        pastCard.getStyleClass().add("card");
        Label pastTitle = new Label("📂 Sổ phiếu nhập hiện tại (" + vouchers.size() + ")");
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
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            voucherItems.add(new ImportVoucherProduct(p.getId(), p.getModelName(), 1, p.getPrice() * 0.9));
        }
        refreshItems();
    }

    private void refreshItems() {
        itemsContainer.getChildren().clear();
        if (voucherItems.isEmpty()) {
            Label empty = new Label("Mời kích hoạt bổ sung thiết bị từ bảng phía trên vào phiếu.");
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
                Label priceLbl = new Label("Giá nhập (VND)");
                priceLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 700;");
                Spinner<Integer> priceSpinner = new Spinner<>(0, 999999999, (int) item.getPrice(), 1000000);
                priceSpinner.setPrefWidth(120);
                priceSpinner.valueProperty().addListener((obs, o, n) -> { item.setPrice(n); updateTotal(); });
                priceBox.getChildren().addAll(priceLbl, priceSpinner);

                VBox qtyBox = new VBox(2);
                Label qtyLbl = new Label("Số lượng");
                qtyLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 700;");
                Spinner<Integer> qtySpinner = new Spinner<>(1, 9999, item.getQuantity());
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
            a.setContentText("Vui lòng bổ sung ít nhất 1 mặt hàng vào phiếu nhập!");
            a.showAndWait();
            return;
        }

        String nextId = "PN00" + (vouchers.size() + 1);
        double total = voucherItems.stream().mapToDouble(ImportVoucherProduct::getSubtotal).sum();
        int whIdx = cbWarehouse.getSelectionModel().getSelectedIndex();
        String whId = whIdx >= 0 && whIdx < warehouses.size() ? warehouses.get(whIdx).getId() : "";

        ImportVoucher v = new ImportVoucher(nextId, cbSupplier.getValue(), whId,
            LocalDate.now().toString(), new ArrayList<>(voucherItems), total,
            creator, status);

        NetworkService.getInstance().insertVoucher(v);
        vouchers.add(0, v);

        if ("Đã Duyệt".equals(status)) {
            executeStockEffects(v);
        }

        voucherItems.clear();
        refreshItems();
        refreshPastVouchers();

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Hoàn tất");
        a.setHeaderText(null);
        a.setContentText("Đã hoàn tất lập " + ("Nháp".equals(status) ? "bản nháp" : "phiếu duyệt chính thức") + " mã " + nextId + "!");
        a.showAndWait();
    }

    private void executeStockEffects(ImportVoucher vouch) {
        int totalLoaded = 0;
        for (ImportVoucherProduct vi : vouch.getProducts()) {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(vi.getProductId())) {
                    Product p = products.get(i);
                    p.setStock(p.getStock() + vi.getQuantity());
                    products.set(i, p);
                    NetworkService.getInstance().updateProduct(p);
                    break;
                }
            }
            totalLoaded += vi.getQuantity();
        }
        for (int i = 0; i < warehouses.size(); i++) {
            if (warehouses.get(i).getId().equals(vouch.getWarehouseId())) {
                Warehouse w = warehouses.get(i);
                w.setUsedSpace(Math.min(w.getCapacity(), w.getUsedSpace() + totalLoaded));
                warehouses.set(i, w);
                NetworkService.getInstance().updateWarehouse(w);
                break;
            }
        }
    }

    private void refreshPastVouchers() {
        pastVouchersContainer.getChildren().clear();
        for (ImportVoucher v : vouchers) {
            HBox row = new HBox(10);
            row.getStyleClass().add("voucher-item");
            row.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(4);
            HBox.setHgrow(info, Priority.ALWAYS);
            HBox idRow = new HBox(6);
            idRow.setAlignment(Pos.CENTER_LEFT);
            Label idLbl = new Label(v.getId());
            idLbl.setStyle("-fx-font-weight: 700; -fx-text-fill: #4338ca; -fx-font-family: 'Consolas'; -fx-font-size: 12px;");
            Label statusBadge = new Label(v.getStatus());
            statusBadge.getStyleClass().addAll("badge",
                "Đã Duyệt".equals(v.getStatus()) ? "badge-success" : "badge-warning");
            statusBadge.setStyle(statusBadge.getStyle() + " -fx-font-size: 9px;");
            idRow.getChildren().addAll(idLbl, statusBadge);
            Label dateLbl = new Label(v.getDate() + " • NCC: " + v.getSupplier());
            dateLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
            dateLbl.setWrapText(true);
            info.getChildren().addAll(idRow, dateLbl);

            VBox amountBox = new VBox(4);
            amountBox.setAlignment(Pos.CENTER_RIGHT);
            Label amtLbl = new Label(FormatUtil.formatVND(v.getTotalAmount()));
            amtLbl.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-family: 'Consolas'; -fx-font-size: 12px;");
            amountBox.getChildren().add(amtLbl);

            if ("Nháp".equals(v.getStatus())) {
                Button btnApprove = new Button("\u2713 Duyệt phiếu");
                btnApprove.setStyle("-fx-background-color: transparent; -fx-text-fill: #0284c7; -fx-font-size: 10px; -fx-font-weight: 700; -fx-cursor: hand;");
                btnApprove.setOnAction(e -> {
                    v.setStatus("Đã Duyệt");
                    NetworkService.getInstance().updateVoucher(v);
                    executeStockEffects(v);
                    refreshPastVouchers();
                });
                amountBox.getChildren().add(btnApprove);
            }

            row.getChildren().addAll(info, amountBox);
            pastVouchersContainer.getChildren().add(row);
        }
    }
}
