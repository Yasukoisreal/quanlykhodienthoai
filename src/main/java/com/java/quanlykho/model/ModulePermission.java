package com.java.quanlykho.model;

import java.io.Serializable;

public class ModulePermission implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean view;
    private boolean add;
    private boolean edit;
    private boolean delete;
    private boolean export;

    public ModulePermission() {}

    public ModulePermission(boolean view, boolean add, boolean edit, boolean delete, boolean export) {
        this.view = view;
        this.add = add;
        this.edit = edit;
        this.delete = delete;
        this.export = export;
    }

    public boolean isView() { return view; }
    public void setView(boolean view) { this.view = view; }

    public boolean isAdd() { return add; }
    public void setAdd(boolean add) { this.add = add; }

    public boolean isEdit() { return edit; }
    public void setEdit(boolean edit) { this.edit = edit; }

    public boolean isDelete() { return delete; }
    public void setDelete(boolean delete) { this.delete = delete; }

    public boolean isExport() { return export; }
    public void setExport(boolean export) { this.export = export; }

    public static ModulePermission fullAccess() {
        return new ModulePermission(true, true, true, true, true);
    }

    public static ModulePermission readOnly() {
        return new ModulePermission(true, false, false, false, false);
    }

    public static ModulePermission noAccess() {
        return new ModulePermission(false, false, false, false, false);
    }
}
