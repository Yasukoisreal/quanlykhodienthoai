package com.java.quanlykho.model;

import java.io.Serializable;

/**
 * Bản ghi lịch sử thao tác (Audit Log).
 */
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String timestamp;
    private String username;
    private String action;      // INSERT, UPDATE, DELETE, LOGIN
    private String targetTable;
    private String targetId;
    private String detail;

    public AuditLog() {}

    public AuditLog(String username, String action, String targetTable, String targetId, String detail) {
        this.username = username;
        this.action = action;
        this.targetTable = targetTable;
        this.targetId = targetId;
        this.detail = detail;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
