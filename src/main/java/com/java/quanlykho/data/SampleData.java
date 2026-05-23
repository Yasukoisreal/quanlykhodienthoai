package com.java.quanlykho.data;

import com.java.quanlykho.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Dữ liệu mẫu tương đương mockDb.ts trong bản web.
 * Cung cấp dữ liệu khởi tạo cho tất cả các module.
 */
public class SampleData {

    public static ObservableList<Product> getProducts() {
        return FXCollections.observableArrayList(
            new Product("SP001", "iPhone 16 Pro Max", "Apple", 34990000, 45,
                List.of("IMEI8890124901", "IMEI8890124902", "IMEI8890124903")),
            new Product("SP002", "Samsung Galaxy S24 Ultra", "Samsung", 29990000, 28,
                List.of("IMEI9901429011", "IMEI9901429012")),
            new Product("SP003", "Xiaomi 14 Ultra", "Xiaomi", 26490000, 12,
                List.of("IMEI7755120199", "IMEI7755120198")),
            new Product("SP004", "iPhone 15 Pro", "Apple", 24990000, 50,
                List.of("IMEI884400291")),
            new Product("SP005", "OPPO Find X7 Ultra", "OPPO", 21500000, 8,
                List.of("IMEI554421900")),
            new Product("SP006", "iPhone 13 128GB", "Apple", 13990000, 65, List.of()),
            new Product("SP007", "Galaxy A55 5G", "Samsung", 9890000, 4, List.of())
        );
    }

    public static ObservableList<Warehouse> getWarehouses() {
        return FXCollections.observableArrayList(
            new Warehouse("K01", "Kho Trung Tâm - Quận 1",
                "12 Đinh Tiên Hoàng, Quận 1, Tp.HCM", 200, 164),
            new Warehouse("K02", "Kho Trung Chuyển - Thủ Đức",
                "482 Song Hành, Tăng Nhơn Phú, Tp. Thủ Đức", 500, 110),
            new Warehouse("K03", "Kho Dự Phòng - Hóc Môn",
                "148 Nguyễn Ảnh Thủ, Hóc Môn, Tp.HCM", 300, 15)
        );
    }

    public static ObservableList<Customer> getCustomers() {
        return FXCollections.observableArrayList(
            new Customer("KH001", "Nguyễn Văn Đạt", "0901234567", "dat.nguyen@gmail.com",
                "Đang hoạt động", 24, 185000000, "Kim cương"),
            new Customer("KH002", "Trần Thị Thu Thảo", "0918765432", "thao.ttt@hotmail.com",
                "Đang hoạt động", 8, 62000000, "Vàng"),
            new Customer("KH003", "Lê Minh Quân", "0977222111", "quan.leminh@yahoo.com",
                "Đang hoạt động", 3, 14000000, "Bạc"),
            new Customer("KH004", "Phạm Hồng Ngọc", "0934500900", "ngocph@gmail.com",
                "Tạm khóa", 0, 0, "Bạc")
        );
    }

    public static ObservableList<Supplier> getSuppliers() {
        return FXCollections.observableArrayList(
            new Supplier("NCC01", "Apple Vietnam Distribution Co.", "1800-1125",
                "orders@apple.vn", "Tòa nhà Petrovietnam, Lê Duẩn, Quận 1, Tp.HCM", "Hợp tác"),
            new Supplier("NCC02", "Samsung Vina Mobile Division Ltd.", "028-382173",
                "contact.samsung@samsung.com", "Khu công nghệ cao Sài Gòn, Quận 9, Tp.HCM", "Hợp tác"),
            new Supplier("NCC03", "Công ty Cổ phần Digiworld (DGW)", "028-392900",
                "mobile@digiworld.com.vn", "195 Nguyễn Văn Trỗi, Phú Nhuận, Tp.HCM", "Hợp tác"),
            new Supplier("NCC04", "Xiaomi Việt Nam Retail Group", "1900-6012",
                "support@xiaomi-vietnam.com", "Số 1 Cù Lao, Phường 2, Phú Nhuận, Tp.HCM", "Tạm ngưng")
        );
    }

    public static ObservableList<Employee> getEmployees() {
        return FXCollections.observableArrayList(
            new Employee("NV001", "admin_smartphone", "Lê Hoàng Tú (Giám Sát)",
                "tu.lehoang@smartphonekho.vn", "Admin", "Đang làm việc", "0903334445"),
            new Employee("NV002", "kho_thao", "Vũ Thị Thảo (Thủ kho)",
                "thao.vu@smartphonekho.vn", "Thủ Kho", "Đang làm việc", "0988556677"),
            new Employee("NV003", "nv_duy", "Nguyễn Nhân Duy",
                "duy.nn@smartphonekho.vn", "Nhân viên", "Đang làm việc", "0911223344"),
            new Employee("NV004", "nv_linh", "Trần Khánh Linh",
                "linh.tk@smartphonekho.vn", "Nhân viên", "Nghỉ phép", "0944556622")
        );
    }

    public static ObservableList<ImportVoucher> getVouchers() {
        return FXCollections.observableArrayList(
            new ImportVoucher("PN001", "Apple Vietnam Distribution Co.", "K01", "2024-05-18",
                List.of(
                    new ImportVoucherProduct("SP001", "iPhone 16 Pro Max", 10, 34000000),
                    new ImportVoucherProduct("SP004", "iPhone 15 Pro", 5, 23800000)
                ), 459000000, "Vũ Thị Thảo (Thủ kho)", "Đã Duyệt"),
            new ImportVoucher("PN002", "Công ty Cổ phần Digiworld (DGW)", "K02", "2024-05-20",
                List.of(
                    new ImportVoucherProduct("SP003", "Xiaomi 14 Ultra", 4, 25000000)
                ), 100000000, "Lê Hoàng Tú (Giám Sát)", "Nháp")
        );
    }

    public static Attributes getAttributes() {
        Attributes attrs = new Attributes();
        attrs.setBrands(new ArrayList<>(List.of("Apple", "Samsung", "Xiaomi", "OPPO", "Vivo")));
        attrs.setColors(new ArrayList<>(List.of(
            new Attributes.AttributeColor("Titanium đen", "#1c1c1e"),
            new Attributes.AttributeColor("Titanium tự nhiên", "#b4b2ad"),
            new Attributes.AttributeColor("Trắng ngọc trai", "#f2f2f7"),
            new Attributes.AttributeColor("Midnight xanh", "#1d2b3a"),
            new Attributes.AttributeColor("Vàng Champagne", "#e5d3b3")
        )));
        attrs.setStorages(new ArrayList<>(List.of("128GB", "256GB", "512GB", "1TB")));
        attrs.setCpuList(new ArrayList<>(List.of(
            "Apple A18 Pro", "Snapdragon 8 Gen 4", "Dimensity 9400",
            "Apple A17 Pro", "Snapdragon 8 Gen 3"
        )));
        attrs.setScreenList(new ArrayList<>(List.of(
            "Super Retina XDR 6.9\"", "Dynamic AMOLED 2X 6.8\"",
            "AMOLED 120Hz 6.7\"", "IPS LCD 90Hz 6.5\""
        )));
        attrs.setNetworkList(new ArrayList<>(List.of(
            "5G Sub-6GHz", "5G Dual-SIM", "4G LTE Only / Volte"
        )));
        return attrs;
    }

    public static ObservableList<RolePermissions> getPermissions() {
        // Admin - full access
        Map<String, ModulePermission> adminModules = new LinkedHashMap<>();
        String[] moduleKeys = {"dashboard", "products", "attributes", "warehouses",
                               "vouchers", "customers", "suppliers", "employees", "permissions"};
        for (String key : moduleKeys) {
            adminModules.put(key, ModulePermission.fullAccess());
        }

        // Thủ Kho - limited access
        Map<String, ModulePermission> thuKhoModules = new LinkedHashMap<>();
        thuKhoModules.put("dashboard", new ModulePermission(true, false, false, false, true));
        thuKhoModules.put("products", new ModulePermission(true, true, true, false, true));
        thuKhoModules.put("attributes", new ModulePermission(true, true, false, false, false));
        thuKhoModules.put("warehouses", new ModulePermission(true, false, false, false, false));
        thuKhoModules.put("vouchers", new ModulePermission(true, true, true, true, true));
        thuKhoModules.put("customers", new ModulePermission(true, false, false, false, false));
        thuKhoModules.put("suppliers", new ModulePermission(true, true, false, false, true));
        thuKhoModules.put("employees", ModulePermission.noAccess());
        thuKhoModules.put("permissions", ModulePermission.noAccess());

        // Nhân viên - minimal access
        Map<String, ModulePermission> nhanVienModules = new LinkedHashMap<>();
        nhanVienModules.put("dashboard", new ModulePermission(true, false, false, false, false));
        nhanVienModules.put("products", new ModulePermission(true, false, false, false, true));
        nhanVienModules.put("attributes", new ModulePermission(true, false, false, false, false));
        nhanVienModules.put("warehouses", new ModulePermission(true, false, false, false, false));
        nhanVienModules.put("vouchers", new ModulePermission(true, true, false, false, false));
        nhanVienModules.put("customers", new ModulePermission(true, true, true, false, false));
        nhanVienModules.put("suppliers", new ModulePermission(true, false, false, false, false));
        nhanVienModules.put("employees", ModulePermission.noAccess());
        nhanVienModules.put("permissions", ModulePermission.noAccess());

        return FXCollections.observableArrayList(
            new RolePermissions("Admin", adminModules),
            new RolePermissions("Thủ Kho", thuKhoModules),
            new RolePermissions("Nhân viên", nhanVienModules)
        );
    }
}
