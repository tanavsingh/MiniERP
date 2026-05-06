package com.minierp.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.minierp.services.AttendanceService;
import com.minierp.utils.DateUtils;
import com.minierp.utils.PDFGenerator;

public class AttendanceReport {
    private final AttendanceService service = new AttendanceService();

    public void generate(int studentId, String studentName, String outputPath) {
        Map<String, double[]> summary = service.getStudentAttendanceSummary(studentId);
        List<String[]> data = new ArrayList<>();
        for (Map.Entry<String, double[]> entry : summary.entrySet()) {
            data.add(new String[]{
                entry.getKey(),
                String.valueOf((int) entry.getValue()[0]),
                String.valueOf((int) entry.getValue()[1]),
                String.format("%.1f%%", entry.getValue()[2]),
                entry.getValue()[2] >= 75 ? "OK" : "LOW"
            });
        }
String[] headersArr = {"Subject", "Present", "Total", "Percentage", "Status"}; List<String[]> headers = new java.util.ArrayList<>(); headers.add(headersArr);
        String title = "Attendance Report - " + studentName + " | Generated: " + DateUtils.today();
        PDFGenerator.generateReport(title, headers, data, outputPath);
    }

    public void generateClassReport(double threshold, String outputPath) {
        List<Map<String, Object>> lowList = service.getLowAttendanceStudents();
        List<String[]> data = new ArrayList<>();
        for (Map<String, Object> row : lowList) {
            data.add(new String[]{
                String.valueOf(row.get("name")),
                String.valueOf(row.get("subject_name")),
                String.valueOf(row.get("present")),
                String.valueOf(row.get("total")),
                String.valueOf(row.get("percentage")) + "%"
            });
        }
String[] headersArr2 = {"Student", "Subject", "Present", "Total", "Percentage"}; List<String[]> headers = new java.util.ArrayList<>(); headers.add(headersArr2);
        String title = "Low Attendance Report (Below " + threshold + "%) | " + DateUtils.today();
        PDFGenerator.generateReport(title, headers, data, outputPath);
    }
}
