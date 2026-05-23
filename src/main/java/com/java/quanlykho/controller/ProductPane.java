package com.java.quanlykho.controller;

import com.java.quanlykho.model.Attributes;
import com.java.quanlykho.model.Product;
import com.java.quanlykho.util.FormatUtil;
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
import javafx.stage.FileChooser;

import com.java.quanlykho.network.NetworkService;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Product management view - CRUD, search, sort, pagination, XML import/export.
 */
public class ProductPane extends VBox {

    private final ObservableList<Product> products;
    private final Attributes attributes;
    private final TableView<Product> table = new TableView<>();
    private final FilteredList<Product> filteredProducts;
    private final TextField searchField;
    private final ComboBox<String> brandFilter;

    public ProductPane(ObservableList<Product> products, Attributes attributes) {
        this.products = products;
        this.attributes = attributes;
        setSpacing(16);

        // === Toolbar ===
        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("card");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm mẫu smartphone, id...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(280);

        brandFilter = new ComboBox<>();
        brandFilter.getItems().add("Tất cả thương hiệu");
        brandFilter.getItems().addAll(attributes.getBrands());
        brandFilter.setValue("Tất cả thương hiệu");
        brandFilter.setPrefWidth(180);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnExport = new Button("📥 Xuất XML");
        btnExport.getStyleClass().add("btn-secondary");
        btnExport.setOnAction(e -> handleExportXML());

        Button btnImport = new Button("📤 Nhập XML");
        btnImport.getStyleClass().add("btn-secondary");
        btnImport.setOnAction(e -> handleImportXML());

        Button btnAdd = new Button("➕ Thêm sản phẩm mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> showAddDialog());

        Button btnExportCsv = new Button("📥 Xuất Excel");
        btnExportCsv.getStyleClass().add("btn-secondary");
        btnExportCsv.setOnAction(e -> com.java.quanlykho.util.ExportUtil.exportTableToExcel(table, "san_pham", getScene().getWindow()));

        toolbar.getChildren().addAll(searchField, brandFilter, spacer, btnExportCsv, btnExport, btnImport, btnAdd);

        // === Table ===
        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(0));

        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(16, 20, 16, 20));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        Label tableTitle = new Label("Danh mục thiết bị điện thoại bến kho");
        tableTitle.getStyleClass().add("card-title");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Label countLabel = new Label();
        countLabel.getStyleClass().add("text-muted");
        tableHeader.getChildren().addAll(tableTitle, sp2, countLabel);


        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("Không tìm thấy dữ liệu nào phù hợp với bộ lọc hiển thị."));
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPrefHeight(450);

        // Columns
        TableColumn<Product, String> colId = new TableColumn<>("Mã");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(80);
        colId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-font-weight: 700; -fx-text-fill: #4338ca; -fx-font-family: 'Consolas';");
                }
            }
        });

        TableColumn<Product, String> colName = new TableColumn<>("Kiểu mẫu / Điện thoại");
        colName.setCellValueFactory(new PropertyValueFactory<>("modelName"));
        colName.setPrefWidth(220);
        colName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a;");
                }
            }
        });

        TableColumn<Product, String> colBrand = new TableColumn<>("Thương hiệu");
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colBrand.setPrefWidth(120);
        colBrand.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge-brand");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Product, String> colPrice = new TableColumn<>("Giá niêm yết");
        colPrice.setCellValueFactory(data ->
            new SimpleStringProperty(FormatUtil.formatVND(data.getValue().getPrice())));
        colPrice.setPrefWidth(150);
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-family: 'Consolas'; -fx-alignment: CENTER-RIGHT;");
                }
            }
        });

        TableColumn<Product, Integer> colStock = new TableColumn<>("Tồn kho");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setPrefWidth(100);
        colStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); }
                else {
                    Label badge = new Label(item + " cái");
                    badge.getStyleClass().add("badge");
                    if (item <= 5) badge.getStyleClass().add("badge-danger");
                    else if (item <= 15) badge.getStyleClass().add("badge-warning");
                    else badge.getStyleClass().add("badge-success");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Product, String> colImei = new TableColumn<>("IMEI");
        colImei.setCellValueFactory(data -> {
            int count = data.getValue().getImeiList().size();
            return new SimpleStringProperty(count > 0 ? count + " mã" : "—");
        });
        colImei.setPrefWidth(80);
        colImei.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, Void> colActions = new TableColumn<>("Hành động");
        colActions.setPrefWidth(120);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏");
            private final Button btnDel = new Button("🗑");
            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnEdit.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                btnDel.getStyleClass().add("btn-danger");
                btnDel.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                btnEdit.setOnAction(e -> showEditDialog(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    HBox box = new HBox(4, btnEdit, btnDel);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colId, colName, colBrand, colPrice, colStock, colImei, colActions);

        // Filtering
        filteredProducts = new FilteredList<>(products, p -> true);
        searchField.textProperty().addListener((obs, old, val) -> applyFilters());
        brandFilter.valueProperty().addListener((obs, old, val) -> applyFilters());

        SortedList<Product> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedProducts);

        // Update count label
        filteredProducts.addListener((javafx.collections.ListChangeListener<Product>) c -> {
            countLabel.setText("Khớp dữ liệu: " + filteredProducts.size() + " dòng");
        });
        countLabel.setText("Khớp dữ liệu: " + filteredProducts.size() + " dòng");

        tableCard.getChildren().addAll(tableHeader, table);
        getChildren().addAll(toolbar, tableCard);
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase().trim();
        String brand = brandFilter.getValue();

        filteredProducts.setPredicate(p -> {
            boolean matchSearch = search.isEmpty()
                || p.getModelName().toLowerCase().contains(search)
                || p.getId().toLowerCase().contains(search);
            boolean matchBrand = "Tất cả thương hiệu".equals(brand) || p.getBrand().equals(brand);
            return matchSearch && matchBrand;
        });
    }

    private void showAddDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Thêm mặt hàng smartphone mới");
        dialog.setHeaderText(null);

        ButtonType saveBtn = new ButtonType("Lưu mặt hàng", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfName = new TextField();
        tfName.setPromptText("Ví dụ: iPhone 16 Pro Max 256GB");
        tfName.setPrefWidth(300);
        ComboBox<String> cbBrand = new ComboBox<>(FXCollections.observableArrayList(attributes.getBrands()));
        cbBrand.setValue(attributes.getBrands().isEmpty() ? "" : attributes.getBrands().get(0));
        TextField tfPrice = new TextField("20000000");
        TextField tfStock = new TextField("10");
        TextField tfImei = new TextField();
        tfImei.setPromptText("Cách nhau bằng dấu phẩy (,)");

        grid.add(new Label("Tên kiểu mẫu thiết bị *"), 0, 0);
        grid.add(tfName, 1, 0, 2, 1);
        grid.add(new Label("Thương hiệu *"), 0, 1);
        grid.add(cbBrand, 1, 1);
        grid.add(new Label("Giá bán gốc (VND) *"), 0, 2);
        grid.add(tfPrice, 1, 2);
        grid.add(new Label("Tồn kho ban đầu *"), 0, 3);
        grid.add(tfStock, 1, 3);
        grid.add(new Label("Danh sách IMEI"), 0, 4);
        grid.add(tfImei, 1, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().trim().isEmpty()) {
                String nextId = "SP00" + (products.size() + 1);
                List<String> imeis = new ArrayList<>();
                if (!tfImei.getText().isBlank()) {
                    for (String s : tfImei.getText().split(",")) {
                        if (!s.trim().isEmpty()) imeis.add(s.trim());
                    }
                }
                return new Product(nextId, tfName.getText().trim(), cbBrand.getValue(),
                    Double.parseDouble(tfPrice.getText()), Integer.parseInt(tfStock.getText()), imeis);
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(p2 -> {
            NetworkService.getInstance().insertProduct(p2);
            products.add(p2);
        });
    }

    private void showEditDialog(Product p) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Sửa đổi thông số Smartphone");
        dialog.setHeaderText("Chỉnh sửa: " + p.getModelName());

        ButtonType saveBtn = new ButtonType("Cập nhật thuộc tính", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfName = new TextField(p.getModelName());
        ComboBox<String> cbBrand = new ComboBox<>(FXCollections.observableArrayList(attributes.getBrands()));
        cbBrand.setValue(p.getBrand());
        TextField tfPrice = new TextField(String.valueOf((long) p.getPrice()));
        TextField tfStock = new TextField(String.valueOf(p.getStock()));
        TextField tfImei = new TextField(String.join(", ", p.getImeiList()));

        grid.add(new Label("Tên kiểu mẫu thiết bị *"), 0, 0);
        grid.add(tfName, 1, 0, 2, 1);
        grid.add(new Label("Thương hiệu *"), 0, 1);
        grid.add(cbBrand, 1, 1);
        grid.add(new Label("Giá bán niêm yết (VND) *"), 0, 2);
        grid.add(tfPrice, 1, 2);
        grid.add(new Label("Hàng tồn kho hiện có *"), 0, 3);
        grid.add(tfStock, 1, 3);
        grid.add(new Label("Danh sách mã IMEI"), 0, 4);
        grid.add(tfImei, 1, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !tfName.getText().trim().isEmpty()) {
                List<String> imeis = new ArrayList<>();
                if (!tfImei.getText().isBlank()) {
                    for (String s : tfImei.getText().split(",")) {
                        if (!s.trim().isEmpty()) imeis.add(s.trim());
                    }
                }
                p.setModelName(tfName.getText().trim());
                p.setBrand(cbBrand.getValue());
                p.setPrice(Double.parseDouble(tfPrice.getText()));
                p.setStock(Integer.parseInt(tfStock.getText()));
                p.setImeiList(imeis);
                return p;
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            NetworkService.getInstance().updateProduct(updated);
            int idx = products.indexOf(updated);
            if (idx >= 0) {
                products.set(idx, updated);
            }
        });
    }

    private void handleDelete(Product p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " + p.getModelName() + "?");
        alert.setContentText("Mã sản phẩm: " + p.getId());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NetworkService.getInstance().deleteProduct(p.getId());
            products.remove(p);
        }
    }

    private void handleExportXML() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Xuất sản phẩm ra XML");
        fc.setInitialFileName("warehousedb-products.xml");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File file = fc.showSaveDialog(getScene().getWindow());
        if (file == null) return;

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<products>\n");
            for (Product p : products) {
                writer.write("  <product id=\"" + p.getId() + "\">\n");
                writer.write("    <name>" + p.getModelName() + "</name>\n");
                writer.write("    <brand>" + p.getBrand() + "</brand>\n");
                writer.write("    <price>" + (long) p.getPrice() + "</price>\n");
                writer.write("    <stock>" + p.getStock() + "</stock>\n");
                writer.write("    <imeis>\n");
                for (String imei : p.getImeiList()) {
                    writer.write("      <imei>" + imei + "</imei>\n");
                }
                writer.write("    </imeis>\n");
                writer.write("  </product>\n");
            }
            writer.write("</products>");
            showInfo("Xuất XML thành công", "Đã lưu " + products.size() + " sản phẩm vào " + file.getName());
        } catch (Exception ex) {
            showError("Lỗi xuất XML", ex.getMessage());
        }
    }

    private void handleImportXML() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Nhập sản phẩm từ XML");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file == null) return;

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList prodNodes = doc.getElementsByTagName("product");
            int count = 0;

            for (int i = 0; i < prodNodes.getLength(); i++) {
                Element el = (Element) prodNodes.item(i);
                String id = el.getAttribute("id");
                if (id.isEmpty()) id = "SP_XML_" + (int)(Math.random() * 1000);
                String name = getTagText(el, "name", "Sản phẩm XML");
                String brand = getTagText(el, "brand", "Apple");
                double price = Double.parseDouble(getTagText(el, "price", "0"));
                int stock = Integer.parseInt(getTagText(el, "stock", "0"));

                List<String> imeis = new ArrayList<>();
                NodeList imeiNodes = el.getElementsByTagName("imei");
                for (int j = 0; j < imeiNodes.getLength(); j++) {
                    String imei = imeiNodes.item(j).getTextContent();
                    if (imei != null && !imei.isBlank()) imeis.add(imei.trim());
                }

                // Update or add
                String finalId = id;
                Product existing = products.stream().filter(p -> p.getId().equals(finalId)).findFirst().orElse(null);
                if (existing != null) {
                    int idx = products.indexOf(existing);
                    products.set(idx, new Product(id, name, brand, price, stock, imeis));
                } else {
                    products.add(new Product(id, name, brand, price, stock, imeis));
                }
                count++;
            }

            showInfo("Nhập XML thành công",
                "Đã nhận diện và nạp thành công " + count + " thiết bị từ tài liệu XML!");
        } catch (Exception ex) {
            showError("Lỗi nhập XML", "Lỗi phân tích cú pháp tệp XML: " + ex.getMessage());
        }
    }

    private String getTagText(Element parent, String tagName, String defaultVal) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0 && nodes.item(0).getTextContent() != null) {
            return nodes.item(0).getTextContent().trim();
        }
        return defaultVal;
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
