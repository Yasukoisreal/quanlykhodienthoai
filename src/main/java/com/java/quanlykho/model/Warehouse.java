package com.java.quanlykho.model;

import java.io.Serializable;

public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String location;
    private int capacity;
    private int usedSpace;

    public Warehouse() {}

    public Warehouse(String id, String name, String location, int capacity, int usedSpace) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.usedSpace = usedSpace;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getUsedSpace() { return usedSpace; }
    public void setUsedSpace(int usedSpace) { this.usedSpace = usedSpace; }

    public double getUsageRatio() {
        return capacity > 0 ? (double) usedSpace / capacity : 0;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
