package com.tic.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for capturing screenshots during test execution.
 */
public final class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "test-output/screenshots";

    private ScreenshotUtils() {
    }

    /** Captures screenshot to file and returns absolute path. */
    public static String capture(WebDriver driver, String name) {
        if (driver == null) {
            return null;
        }
        try {
            Path dir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = name + "_" + timestamp + ".png";
            File dest = dir.resolve(fileName).toFile();
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dest.toPath());
            return dest.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot: " + e.getMessage());
        }
    }

    /** Returns base64 string for embedding in Extent Report. */
    public static String captureBase64(WebDriver driver) {
        if (driver == null) {
            return null;
        }
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    public static String capture(WebDriver driver) {
        return capture(driver, "screenshot");
    }
}
