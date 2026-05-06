package com.minierp.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.minierp.ai.StudyRecommendationAI;
import com.minierp.models.Result;
import com.minierp.services.AttendanceService;
import com.minierp.services.ResultService;
import com.minierp.utils.DateUtils;
import com.minierp.utils.PDFGenerator;

public class StudentPerformanceReport {
    private final AttendanceService attendanceService = new AttendanceService();
    private final ResultService resultService = new ResultService();
    private final StudyRecommendationAI ai = new StudyRecommendationAI();

    public void generate(int studentId, String studentName, String outputPath) {
        List<String[]> data = new ArrayList<>();

        // Attendance section
        data.add(new String[]{"=== ATTENDANCE SUMMARY ==="});
        Map<String, double[]> attendance = attendanceService.getStudentAttendanceSummary(studentId);
        for (Map.Entry<String, double[]> entry : attendance.entrySet()) {
            data.add(new String[]{entry.getKey(),
                String.format("%.0f/%.0f", entry.getValue()[0], entry.getValue()[1]),
                String.format("%.1f%%", entry.getValue()[2]),
                entry.getValue()[2] >= 75 ? "Good" : "WARNING"});
        }

        // Results section
        data.add(new String[]{"=== EXAM RESULTS ==="});
        List<Result> results = resultService.getByStudent(studentId);
        for (Result r : results) {
            double pct = r.getMaxMarks() > 0 ? (r.getMarksObtained() / r.getMaxMarks()) * 100 : 0;
            data.add(new String[]{r.getExamTitle(), String.valueOf(r.getMarksObtained()),
                String.format("%.1f%%", pct), r.getGrade()});
        }

        // AI Recommendations
        data.add(new String[]{"=== AI RECOMMENDATIONS ==="});
        List<String> recs = ai.getRecommendations(studentId);
        for (String rec : recs) data.add(new String[]{rec});

String[] headersArr = {"Category/Subject", "Value", "Percentage/Score", "Status"}; List<String[]> headers = new java.util.ArrayList<>(); headers.add(headersArr);
        String title = "Student Performance Report - " + studentName + " | " + DateUtils.today();
        PDFGenerator.generateReport(title, headers, data, outputPath);
    }
}
