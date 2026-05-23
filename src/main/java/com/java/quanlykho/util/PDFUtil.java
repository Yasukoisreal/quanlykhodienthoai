package com.java.quanlykho.util;

import com.java.quanlykho.model.ExportVoucher;
import com.java.quanlykho.model.ImportVoucherProduct;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Tiện ích xuất hoá đơn ra file PDF.
 */
public class PDFUtil {

    public static void exportVoucherToPDF(ExportVoucher v, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất Hóa Đơn PDF");
        chooser.setInitialFileName("HoaDon_" + v.getId() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showSaveDialog(owner);

        if (file == null) return;

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Load Windows Arial font to support Vietnamese
            String fontPath = "C:/Windows/Fonts/arial.ttf";
            BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontTitle = new Font(bf, 18, Font.BOLD);
            Font fontHeader = new Font(bf, 12, Font.BOLD);
            Font fontNormal = new Font(bf, 12, Font.NORMAL);

            // Title
            Paragraph title = new Paragraph("HÓA ĐƠN XUẤT KHO", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Details
            document.add(new Paragraph("Mã phiếu: " + v.getId(), fontNormal));
            document.add(new Paragraph("Khách hàng: " + v.getCustomer(), fontNormal));
            document.add(new Paragraph("Ngày lập: " + v.getDate(), fontNormal));
            document.add(new Paragraph("Người lập phiếu: " + v.getCreator(), fontNormal));
            document.add(new Paragraph("Trạng thái: " + v.getStatus(), fontNormal));
            
            Paragraph p = new Paragraph(" ");
            p.setSpacingAfter(10);
            document.add(p);

            // Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 4f, 2f, 2f, 3f});

            String[] headers = {"STT", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            int stt = 1;
            for (ImportVoucherProduct item : v.getProducts()) {
                table.addCell(new Phrase(String.valueOf(stt++), fontNormal));
                table.addCell(new Phrase(item.getModelName(), fontNormal));
                
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), fontNormal));
                qtyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(qtyCell);

                PdfPCell priceCell = new PdfPCell(new Phrase(FormatUtil.formatVND(item.getPrice()), fontNormal));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);

                PdfPCell totalCell = new PdfPCell(new Phrase(FormatUtil.formatVND(item.getSubtotal()), fontNormal));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            document.add(table);

            Paragraph total = new Paragraph("Tổng cộng: " + FormatUtil.formatVND(v.getTotalAmount()), fontTitle);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(20);
            document.add(total);

            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Xuất thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đã lưu hóa đơn PDF vào:\n" + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể xuất file PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
