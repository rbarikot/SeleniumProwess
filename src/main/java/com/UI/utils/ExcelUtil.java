package com.UI.utils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ExcelUtil {
    private static final Logger LOGGER = LoggerUtil.getLogger(ExcelUtil.class);

    private ExcelUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Read data from Excel file
     * @param filePath Path to Excel file
     * @param sheetName Sheet name to read
     * @return List of maps containing row data
     */
    public static List<Map<String, String>> readExcelData(String filePath, String sheetName) {
        List<Map<String, String>> excelData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            int columnCount = headerRow.getLastCellNum();

            // Get header values
            String[] headers = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                Cell cell = headerRow.getCell(i);
                headers[i] = getCellValueAsString(cell);
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < columnCount; j++) {
                        Cell cell = row.getCell(j);
                        rowData.put(headers[j], getCellValueAsString(cell));
                    }
                    excelData.add(rowData);
                }
            }

            LOGGER.info("Successfully read {} rows from Excel file: {}", excelData.size(), filePath);
            return excelData;

        } catch (IOException e) {
            LOGGER.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }

    /**
     * Write data to Excel file
     * @param filePath Path to Excel file
     * @param sheetName Sheet name to write
     * @param headers List of column headers
     * @param data List of row data
     */
    public static void writeExcelData(String filePath, String sheetName,
                                      List<String> headers, List<List<String>> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Create data rows
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                List<String> rowData = data.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData.get(j));
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            LOGGER.info("Successfully wrote {} rows to Excel file: {}", data.size(), filePath);

        } catch (IOException e) {
            LOGGER.error("Error writing Excel file: {}", filePath, e);
            throw new RuntimeException("Error writing Excel file: " + filePath, e);
        }
    }

    /**
     * Get cell value as string
     * @param cell Cell to get value from
     * @return String value of cell
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * Get specific cell value
     * @param filePath Path to Excel file
     * @param sheetName Sheet name
     * @param rowNum Row number (0-based)
     * @param colNum Column number (0-based)
     * @return Cell value as string
     */
    public static String getCellValue(String filePath, String sheetName, int rowNum, int colNum) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            Row row = sheet.getRow(rowNum);
            if (row == null) {
                return "";
            }

            Cell cell = row.getCell(colNum);
            return getCellValueAsString(cell);

        } catch (IOException e) {
            LOGGER.error("Error reading cell value from Excel file: {}", filePath, e);
            throw new RuntimeException("Error reading cell value from Excel file: " + filePath, e);
        }
    }

    /**
     * Get row count
     * @param filePath Path to Excel file
     * @param sheetName Sheet name
     * @return Number of rows
     */
    public static int getRowCount(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            return sheet.getLastRowNum() + 1;

        } catch (IOException e) {
            LOGGER.error("Error getting row count from Excel file: {}", filePath, e);
            throw new RuntimeException("Error getting row count from Excel file: " + filePath, e);
        }
    }

    /**
     * Get column count
     * @param filePath Path to Excel file
     * @param sheetName Sheet name
     * @return Number of columns
     */
    public static int getColumnCount(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return 0;
            }

            return headerRow.getLastCellNum();

        } catch (IOException e) {
            LOGGER.error("Error getting column count from Excel file: {}", filePath, e);
            throw new RuntimeException("Error getting column count from Excel file: " + filePath, e);
        }
    }
}
