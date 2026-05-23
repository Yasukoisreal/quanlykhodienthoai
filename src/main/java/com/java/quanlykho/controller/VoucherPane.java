package com.java.quanlykho.controller;

import com.java.quanlykho.model.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Wrapper pane for Import and Export vouchers using a TabPane.
 */
public class VoucherPane extends VBox {

    public VoucherPane(ObservableList<Product> products, ObservableList<Warehouse> warehouses,
                       ObservableList<Supplier> suppliers, ObservableList<Customer> customers,
                       ObservableList<ImportVoucher> vouchers, ObservableList<ExportVoucher> exportVouchers,
                       String creator) {
        
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab tabImport = new Tab("📥 Phiếu Nhập Kho");
        tabImport.setClosable(false);
        tabImport.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-padding: 6 16;");
        tabImport.setContent(new ImportVoucherPane(products, warehouses, suppliers, vouchers, creator));

        Tab tabExport = new Tab("📤 Phiếu Xuất Kho");
        tabExport.setClosable(false);
        tabExport.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-padding: 6 16;");
        tabExport.setContent(new ExportVoucherPane(products, warehouses, customers, exportVouchers, creator));

        tabPane.getTabs().addAll(tabImport, tabExport);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getChildren().add(tabPane);
    }
}
