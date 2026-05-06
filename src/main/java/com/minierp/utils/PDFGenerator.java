package com.minierp.utils;

import java.io.*;
import java.util.List;

/**
 * Simple PDF text report generator using basic text output.
 * Replace with iText calls for rich formatting.
 */
public class PDFGenerator {
    public static void generateReport(String title, List<String[]> headers, List<String[]> data, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("=".repeat(80));
            pw.println("  " + title);
            pw.println("  Generated: " + DateUtils.now());
            pw.println("=".repeat(80));
            pw.println();
            if (headers != null && !headers.isEmpty()) {
                pw.println(String.join(" | ", headers.get(0)));
                pw.println("-".repeat(80));
            }
            for (String[] row : data) {
                pw.println(String.join(" | ", row));
            }
            pw.println();
            pw.println("=".repeat(80));
            System.out.println("[PDF] Report saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("[PDF] Error: " + e.getMessage());
        }
    }

    private PDFGenerator() {}
}
