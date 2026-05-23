package com.java.quanlykho.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RolePermissions implements Serializable {
    private static final long serialVersionUID = 1L;
    private String role; // "Admin" | "Thủ Kho" | "Nhân viên"
    private Map<String, ModulePermission> modules;

    public RolePermissions() {
        this.modules = new LinkedHashMap<>();
    }

    public RolePermissions(String role, Map<String, ModulePermission> modules) {
        this.role = role;
        this.modules = modules != null ? new LinkedHashMap<>(modules) : new LinkedHashMap<>();
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Map<String, ModulePermission> getModules() { return modules; }
    public void setModules(Map<String, ModulePermission> modules) { this.modules = modules; }

    public ModulePermission getPermission(String moduleKey) {
        return modules.getOrDefault(moduleKey, ModulePermission.noAccess());
    }

    public void setPermission(String moduleKey, ModulePermission perm) {
        modules.put(moduleKey, perm);
    }
}
