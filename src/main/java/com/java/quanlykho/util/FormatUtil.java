package com.java.quanlykho.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Tiện ích định dạng dữ liệu cho ứng dụng.
 */
public class FormatUtil {

    private static final NumberFormat VND_FORMAT;

    static {
        VND_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    /**
     * Định dạng số thành tiền VND.
     * Ví dụ: 34990000 → "34.990.000 ₫"
     */
    public static String formatVND(double amount) {
        return VND_FORMAT.format(amount);
    }

    /**
     * Định dạng số ngắn gọn.
     * Ví dụ: 1500000000 → "1.5 tỷ"
     */
    public static String formatShortVND(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1f tỷ", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.0f triệu", amount / 1_000_000);
        }
        return formatVND(amount);
    }

    /**
     * Định dạng phần trăm.
     */
    public static String formatPercent(double ratio) {
        return String.format("%.0f%%", ratio * 100);
    }
}
