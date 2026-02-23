package com.tic.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads test data from Excel (.xlsx) for DataProvider use.
 * Tries classpath resource first (e.g. testdata/LoginData.xlsx), then test-output path.
 */
public final class ExcelDataReader {

    private static final String TEST_OUTPUT_FILE = "test-output/testdata/LoginData.xlsx";

    private ExcelDataReader() {
    }

    /**
     * Returns rows from the given sheet as Object[][] (row 0 = header, data from row 1).
     * File is resolved from classpath (testdata/LoginData.xlsx) or test-output/testdata/LoginData.xlsx.
     */
    public static Object[][] getData(String sheetName) {
        return getData(null, sheetName);
    }

    /**
     * Returns rows from the given sheet. If resourcePath is null, uses default LoginData.xlsx.
     */
    public static Object[][] getData(String resourcePath, String sheetName) {
        String path = resourcePath != null ? resourcePath : "testdata/LoginData.xlsx";
        try (Workbook workbook = openWorkbook(path)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }
            return sheetToArray(sheet);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel sheet '" + sheetName + "': " + e.getMessage(), e);
        }
    }

    private static Workbook openWorkbook(String resourcePath) throws Exception {
        InputStream in = ExcelDataReader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in != null) {
            return new XSSFWorkbook(in);
        }
        Path filePath = Paths.get(TEST_OUTPUT_FILE);
        if (Files.exists(filePath)) {
            return new XSSFWorkbook(Files.newInputStream(filePath));
        }
        throw new IllegalStateException(
                "Excel file not found. Use classpath resource 'testdata/LoginData.xlsx' or run tests once to generate " + TEST_OUTPUT_FILE);
    }

    /** Converts sheet to Object[][], skipping row 0 (header). */
    private static Object[][] sheetToArray(Sheet sheet) {
        List<Object[]> rows = new ArrayList<>();
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();
        for (int i = firstRow + 1; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            List<Object> cells = new ArrayList<>();
            for (Cell cell : row) {
                cells.add(getCellValue(cell));
            }
            if (!cells.isEmpty()) {
                rows.add(cells.toArray());
            }
        }
        return rows.toArray(new Object[0][]);
    }

    private static Object getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double n = cell.getNumericCellValue();
                yield n == (long) n ? (long) n : n;
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }
}
