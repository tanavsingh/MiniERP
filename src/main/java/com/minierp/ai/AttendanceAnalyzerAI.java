package com.minierp.ai;

import com.minierp.services.AttendanceService;
import com.minierp.models.Student;
import java.util.*;

public class AttendanceAnalyzerAI {
    private final AttendanceService service = new AttendanceService();

    public List<String> analyzeStudent(int studentId) {
        List<String> warnings = new ArrayList<>();
        Map<String, double[]> summary = service.getStudentAttendanceSummary(studentId);
        for (Map.Entry<String, double[]> entry : summary.entrySet()) {
            double pct = entry.getValue()[2];
            if (pct < 50) {
                warnings.add("CRITICAL: " + entry.getKey() + " attendance is " + String.format("%.1f%%", pct) + " — Detention risk!");
            } else if (pct < 75) {
                warnings.add("WARNING: " + entry.getKey() + " attendance is " + String.format("%.1f%%", pct) + " — Below minimum threshold.");
            }
        }
        if (warnings.isEmpty()) warnings.add("All attendance records look good!");
        return warnings;
    }

    public List<String> getClassInsights(int subjectId) {
        List<Map<String, Object>> low = service.getLowAttendanceStudents();
        List<String> insights = new ArrayList<>();
        int count = 0;
        for (Map<String, Object> row : low) {
            Object subName = row.get("subject_name");
            if (subName != null) count++;
        }
        insights.add("Students below 75% threshold: " + count);
        insights.add("Recommendation: Send attendance warning notifications to affected students.");
        return insights;
    }
}
