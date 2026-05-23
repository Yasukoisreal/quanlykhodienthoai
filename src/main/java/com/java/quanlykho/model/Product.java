package com.java.quanlykho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String modelName;
    private String brand;
    private double price;
    private int stock;
    private List<String> imeiList;

    public Product() {
        this.imeiList = new ArrayList<>();
    }

    public Product(String id, String modelName, String brand, double price, int stock, List<String> imeiList) {
        this.id = id;
        this.modelName = modelName;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.imeiList = imeiList != null ? new ArrayList<>(imeiList) : new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public List<String> getImeiList() { return imeiList; }
    public void setImeiList(List<String> imeiList) { this.imeiList = imeiList; }

    @Override
    public String toString() {
        return modelName + " (" + id + ")";
    }
}
