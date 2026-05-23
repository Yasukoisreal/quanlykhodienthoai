package com.java.quanlykho.util;

import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

/**
 * Tiện ích xuất dữ liệu ra file Excel (.xlsx).
 */
public class ExportUtil {

    public static <T> void exportTableToExcel(TableView<T> table, String defaultName, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất dữ liệu Excel");
        chooser.setInitialFileName(defaultName + ".xlsx");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = chooser.showSaveDialog(owner);

        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Data");
            
            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < table.getColumns().size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumns().get(i).getText());
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIndex = 1;
            for (T item : table.getItems()) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < table.getColumns().size(); i++) {
                    Cell cell = row.createCell(i);
                    Object cellData = table.getColumns().get(i).getCellData(item);
                    String val = cellData != null ? cellData.toString() : "";
                    cell.setCellValue(val);
                }
            }

            // Auto-size columns
            for (int i = 0; i < table.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Xuất thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đã xuất " + table.getItems().size() + " dòng ra file:\n" + file.getAbsolutePath());
            alert.showAndWait();
        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi xuất file");
            alert.setHeaderText(null);
            alert.setContentText("Không thể xuất file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void exportToExcel(String defaultName, String[] headers, List<String[]> rows, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất dữ liệu Excel");
        chooser.setInitialFileName(defaultName + ".xlsx");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = chooser.showSaveDialog(owner);

        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Data");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Rows
            int rowIndex = 1;
            for (String[] rowData : rows) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    String val = rowData[i] != null ? rowData[i] : "";
                    cell.setCellValue(val);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Xuất thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đã xuất " + rows.size() + " dòng ra file:\n" + file.getAbsolutePath());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
