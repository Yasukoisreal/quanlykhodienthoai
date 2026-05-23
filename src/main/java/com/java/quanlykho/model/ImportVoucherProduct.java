package com.java.quanlykho.model;

import java.io.Serializable;

public class ImportVoucherProduct implements Serializable {
    private static final long serialVersionUID = 1L;
    private String productId;
    private String modelName;
    private int quantity;
    private double price;

    public ImportVoucherProduct() {}

    public ImportVoucherProduct(String productId, String modelName, int quantity, double price) {
        this.productId = productId;
        this.modelName = modelName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getSubtotal() {
        return quantity * price;
    }
}
