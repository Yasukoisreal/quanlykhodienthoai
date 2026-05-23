package com.java.quanlykho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportVoucher implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String supplier;
    private String warehouseId;
    private String date;
    private List<ImportVoucherProduct> products;
    private double totalAmount;
    private String creator;
    private String status; // "Nháp" | "Đã Duyệt" | "Đã Hủy"

    public ImportVoucher() {
        this.products = new ArrayList<>();
    }

    public ImportVoucher(String id, String supplier, String warehouseId, String date,
                         List<ImportVoucherProduct> products, double totalAmount,
                         String creator, String status) {
        this.id = id;
        this.supplier = supplier;
        this.warehouseId = warehouseId;
        this.date = date;
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.creator = creator;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<ImportVoucherProduct> getProducts() { return products; }
    public void setProducts(List<ImportVoucherProduct> products) { this.products = products; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
