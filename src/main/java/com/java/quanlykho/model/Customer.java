package com.java.quanlykho.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String phone;
    private String email;
    private String status;      // "Đang hoạt động" | "Tạm khóa"
    private int totalOrders;
    private double totalSpend;
    private String tier;        // "Bạc" | "Vàng" | "Kim cương"

    public Customer() {}

    public Customer(String id, String name, String phone, String email, String status,
                    int totalOrders, double totalSpend, String tier) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.totalOrders = totalOrders;
        this.totalSpend = totalSpend;
        this.tier = tier;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public double getTotalSpend() { return totalSpend; }
    public void setTotalSpend(double totalSpend) { this.totalSpend = totalSpend; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}
