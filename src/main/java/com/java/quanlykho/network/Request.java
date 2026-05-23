package com.java.quanlykho.network;

import java.io.Serializable;

/**
 * Request object gửi từ Client → Server qua TCP socket.
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String action;  // GET_ALL, INSERT, UPDATE, DELETE
    private String table;   // products, warehouses, customers, suppliers, employees, vouchers, attributes, permissions
    private Object data;    // DTO object hoặc ID string

    public Request() {}

    public Request(String action, String table) {
        this.action = action;
        this.table = table;
    }

    public Request(String action, String table, Object data) {
        this.action = action;
        this.table = table;
        this.data = data;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @Override
    public String toString() {
        return "Request{" + action + " → " + table + "}";
    }
}
