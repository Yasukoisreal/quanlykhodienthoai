package com.java.quanlykho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Phiếu kiểm kê kho - so sánh tồn kho thực tế vs hệ thống.
 */
public class InventoryCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String warehouseId;
    private String warehouseName;
    private String date;
    private String creator;
    private String status; // "Đang kiểm" | "Hoàn tất"
    private String notes;
    private List<InventoryCheckItem> items;

    public InventoryCheck() { this.items = new ArrayList<>(); }

    public InventoryCheck(String id, String warehouseId, String warehouseName, String date,
                          String creator, String status, String notes, List<InventoryCheckItem> items) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.date = date;
        this.creator = creator;
        this.status = status;
        this.notes = notes;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<InventoryCheckItem> getItems() { return items; }
    public void setItems(List<InventoryCheckItem> items) { this.items = items; }
}
