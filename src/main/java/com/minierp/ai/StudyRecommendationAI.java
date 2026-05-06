package com.minierp.ai;

import com.minierp.database.QueryExecutor;
import com.minierp.services.ResultService;
import com.minierp.services.AttendanceService;
import java.util.*;

public class StudyRecommendationAI {
    private final AttendanceService attendanceService = new AttendanceService();

    public List<String> getRecommendations(int studentId) {
        List<String> recs = new ArrayList<>();
        Map<String, double[]> attendance = attendanceService.getStudentAttendanceSummary(studentId);

        List<Map<String, Object>> results = QueryExecutor.executeQuery(
            "SELECT r.marks_obtained, r.grade, e.max_marks, sub.name AS subject_name " +
            "FROM results r JOIN exams e ON r.exam_id = e.id JOIN subjects sub ON e.subject_id = sub.id " +
            "WHERE r.student_id = ? ORDER BY r.entered_at DESC", studentId);

        // Analyze performance by subject
        Map<String, Double> subjectPerf = new LinkedHashMap<>();
        for (Map<String, Object> row : results) {
            String sub = row.get("subject_name") != null ? row.get("subject_name").toString() : "Unknown";
            double marks = row.get("marks_obtained") != null ? ((Number) row.get("marks_obtained")).doubleValue() : 0;
            int max = row.get("max_marks") != null ? ((Number) row.get("max_marks")).intValue() : 100;
            double pct = max > 0 ? (marks / max) * 100 : 0;
            subjectPerf.merge(sub, pct, (a, b) -> (a + b) / 2);
        }

        recs.add("=== AI Study Recommendations ===");
        if (subjectPerf.isEmpty()) {
            recs.add("No result data available yet. Focus on regular attendance and assignment completion.");
        } else {
            for (Map.Entry<String, Double> entry : subjectPerf.entrySet()) {
                double pct = entry.getValue();
                if (pct < 50) {
                    recs.add("🔴 " + entry.getKey() + ": URGENT - Seek extra help, join study groups, review fundamentals.");
                } else if (pct < 65) {
                    recs.add("🟡 " + entry.getKey() + ": Practice more past papers and solve additional exercises.");
                } else if (pct < 80) {
                    recs.add("🟢 " + entry.getKey() + ": Good progress! Focus on advanced topics and exam preparation.");
                } else {
                    recs.add("⭐ " + entry.getKey() + ": Excellent! Maintain this pace and help peers.");
                }
            }
        }

        // Attendance-based suggestions
        for (Map.Entry<String, double[]> entry : attendance.entrySet()) {
            if (entry.getValue()[2] < 75) {
                recs.add("⚠️ Attendance Alert: Improve attendance in " + entry.getKey() + " to avoid exam bar.");
            }
        }

        recs.add("");
        recs.add("General Tips:");
        recs.add("• Study 2-3 hours daily in focused sessions");
        recs.add("• Use the Pomodoro technique (25 min study, 5 min break)");
        recs.add("• Review lecture notes within 24 hours");
        recs.add("• Form study groups for difficult subjects");
        return recs;
    }
}
