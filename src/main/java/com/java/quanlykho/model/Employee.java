package com.java.quanlykho.model;

import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String fullName;
    private String email;
    private String role;    // "Admin" | "Thủ Kho" | "Nhân viên"
    private String status;  // "Đang làm việc" | "Nghỉ phép" | "Đã nghỉ"
    private String phone;

    public Employee() {}

    public Employee(String id, String username, String fullName, String email,
                    String role, String status, String phone) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.phone = phone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
