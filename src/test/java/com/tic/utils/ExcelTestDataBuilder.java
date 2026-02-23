package com.tic.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builds the sample LoginData.xlsx in test-output/testdata/ so DataProviders have an Excel file
 * when no file is in src/test/resources/testdata/.
 */
public final class ExcelTestDataBuilder {

    private static final String OUTPUT_DIR = "test-output/testdata";
    private static final String OUTPUT_FILE = "LoginData.xlsx";
    /** Run main() to generate LoginData.xlsx under src/test/resources/testdata/ for editing. */
    public static final String RESOURCES_TESTDATA = "src/test/resources/testdata";

    private ExcelTestDataBuilder() {
    }

    /** Generate Excel in src/test/resources/testdata/ (for version control / editing). Run as Java main. */
    public static void main(String[] args) {
        Path dir = Paths.get(RESOURCES_TESTDATA);
        Path file = dir.resolve(OUTPUT_FILE);
        try {
            Files.createDirectories(dir);
            try (var wb = new XSSFWorkbook()) {
                createValidLoginSheet(wb);
                createInvalidLoginSheet(wb);
                createLoginFormSheet(wb);
                try (var out = Files.newOutputStream(file)) {
                    wb.write(out);
                }
            }
            System.out.println("Created: " + file.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createLoginDataFileIfMissing() {
        Path dir = Paths.get(OUTPUT_DIR);
        Path file = dir.resolve(OUTPUT_FILE);
        try {
            if (Files.exists(file) && Files.size(file) > 0) {
                return;
            }
        } catch (IOException ignored) {
            // recreate on error
        }
        try {
            Files.createDirectories(dir);
            try (Workbook wb = new XSSFWorkbook()) {
                createValidLoginSheet(wb);
                createInvalidLoginSheet(wb);
                createLoginFormSheet(wb);
                try (var out = Files.newOutputStream(file)) {
                    wb.write(out);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create " + file + ": " + e.getMessage(), e);
        }
    }

    private static void createValidLoginSheet(Workbook wb) {
        Sheet sheet = wb.createSheet("ValidLogin");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("username");
        header.createCell(1).setCellValue("password");
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("tomsmith");
        row1.createCell(1).setCellValue("SuperSecretPassword!");
    }

    private static void createInvalidLoginSheet(Workbook wb) {
        Sheet sheet = wb.createSheet("InvalidLogin");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("username");
        header.createCell(1).setCellValue("password");
        header.createCell(2).setCellValue("expectedMessagePart");
        String[][] data = {
                {"wronguser", "SuperSecretPassword!", "Your username is invalid!"},
                {"tomsmith", "wrongpass", "Your password is invalid!"},
                {"", "SuperSecretPassword!", "Your username is invalid!"},
                {"tomsmith", "", "Your password is invalid!"},
                {"", "", "Your username is invalid!"}
        };
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                row.createCell(j).setCellValue(data[i][j]);
            }
        }
    }

    private static void createLoginFormSheet(Workbook wb) {
        Sheet sheet = wb.createSheet("LoginForm");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("username");
        header.createCell(1).setCellValue("password");
        String[][] data = {
                {"user1", "pass1"},
                {"admin", "admin123"},
                {"test", "test"}
        };
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
        }
    }
}
