package com.java.quanlykho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Attributes implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> brands;
    private List<AttributeColor> colors;
    private List<String> storages;
    private List<String> cpuList;
    private List<String> screenList;
    private List<String> networkList;

    public Attributes() {
        this.brands = new ArrayList<>();
        this.colors = new ArrayList<>();
        this.storages = new ArrayList<>();
        this.cpuList = new ArrayList<>();
        this.screenList = new ArrayList<>();
        this.networkList = new ArrayList<>();
    }

    public List<String> getBrands() { return brands; }
    public void setBrands(List<String> brands) { this.brands = brands; }

    public List<AttributeColor> getColors() { return colors; }
    public void setColors(List<AttributeColor> colors) { this.colors = colors; }

    public List<String> getStorages() { return storages; }
    public void setStorages(List<String> storages) { this.storages = storages; }

    public List<String> getCpuList() { return cpuList; }
    public void setCpuList(List<String> cpuList) { this.cpuList = cpuList; }

    public List<String> getScreenList() { return screenList; }
    public void setScreenList(List<String> screenList) { this.screenList = screenList; }

    public List<String> getNetworkList() { return networkList; }
    public void setNetworkList(List<String> networkList) { this.networkList = networkList; }

    /**
     * Inner class representing a named color with hex value.
     */
    public static class AttributeColor implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String hex;

        public AttributeColor() {}

        public AttributeColor(String name, String hex) {
            this.name = name;
            this.hex = hex;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getHex() { return hex; }
        public void setHex(String hex) { this.hex = hex; }
    }
}
