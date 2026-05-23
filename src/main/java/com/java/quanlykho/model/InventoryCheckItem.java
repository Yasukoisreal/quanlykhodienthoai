package com.java.quanlykho.model;

import java.io.Serializable;

/**
 * Dòng chi tiết trong phiếu kiểm kê: so sánh hệ thống vs thực tế.
 */
public class InventoryCheckItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String productName;
    private int systemQty;
    private int actualQty;
    private int difference;

    public InventoryCheckItem() {}

    public InventoryCheckItem(String productId, String productName, int systemQty, int actualQty) {
        this.productId = productId;
        this.productName = productName;
        this.systemQty = systemQty;
        this.actualQty = actualQty;
        this.difference = actualQty - systemQty;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getSystemQty() { return systemQty; }
    public void setSystemQty(int systemQty) { this.systemQty = systemQty; }
    public int getActualQty() { return actualQty; }
    public void setActualQty(int actualQty) { this.actualQty = actualQty; this.difference = actualQty - systemQty; }
    public int getDifference() { return difference; }
    public void setDifference(int difference) { this.difference = difference; }
}
