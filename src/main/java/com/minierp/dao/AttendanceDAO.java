package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Attendance;
import java.util.*;

public class AttendanceDAO {

    public List<Attendance> findBySubjectAndDate(int subjectId, String date) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT a.*, s.name AS student_name, sub.name AS subject_name " +
            "FROM attendance a JOIN students s ON a.student_id = s.id " +
            "JOIN subjects sub ON a.subject_id = sub.id " +
            "WHERE a.subject_id = ? AND a.date = ?", subjectId, date);
        return mapList(rows);
    }

    public List<Attendance> findByStudent(int studentId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT a.*, s.name AS student_name, sub.name AS subject_name " +
            "FROM attendance a JOIN students s ON a.student_id = s.id " +
            "JOIN subjects sub ON a.subject_id = sub.id " +
            "WHERE a.student_id = ? ORDER BY a.date DESC", studentId);
        return mapList(rows);
    }

    public Map<String, double[]> getAttendanceSummaryByStudent(int studentId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT sub.name AS subject_name, " +
            "COUNT(*) AS total, SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END) AS present " +
            "FROM attendance a JOIN subjects sub ON a.subject_id = sub.id " +
            "WHERE a.student_id = ? GROUP BY a.subject_id", studentId);
        Map<String, double[]> summary = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            double total = toDouble(row.get("total"));
            double present = toDouble(row.get("present"));
            double pct = total > 0 ? (present / total) * 100 : 0;
            summary.put(str(row.get("subject_name")), new double[]{present, total, pct});
        }
        return summary;
    }

    public long insert(Attendance a) {
        return QueryExecutor.executeInsert(
            "INSERT OR REPLACE INTO attendance (student_id, subject_id, date, status, marked_by, remarks) VALUES (?,?,?,?,?,?)",
            a.getStudentId(), a.getSubjectId(), a.getDate(), a.getStatus(), a.getMarkedBy(), a.getRemarks());
    }

    public void insertBatch(List<Attendance> list) {
        for (Attendance a : list) insert(a);
    }

    public List<Map<String, Object>> getLowAttendanceStudents(double threshold) {
        return QueryExecutor.executeQuery(
            "SELECT s.name, s.student_id, sub.name AS subject_name, " +
            "COUNT(*) AS total, SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END) AS present, " +
            "ROUND(SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END)*100.0/COUNT(*),1) AS percentage " +
            "FROM attendance a JOIN students s ON a.student_id = s.id " +
            "JOIN subjects sub ON a.subject_id = sub.id " +
            "GROUP BY a.student_id, a.subject_id " +
            "HAVING percentage < ?", threshold);
    }

    private List<Attendance> mapList(List<Map<String, Object>> rows) {
        List<Attendance> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Attendance a = new Attendance();
            a.setId(toInt(row.get("id")));
            a.setStudentId(toInt(row.get("student_id")));
            a.setSubjectId(toInt(row.get("subject_id")));
            a.setDate(str(row.get("date")));
            a.setStatus(str(row.get("status")));
            a.setMarkedBy(toInt(row.get("marked_by")));
            a.setRemarks(str(row.get("remarks")));
            a.setStudentName(str(row.get("student_name")));
            a.setSubjectName(str(row.get("subject_name")));
            list.add(a);
        }
        return list;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private double toDouble(Object o) { return o == null ? 0.0 : ((Number) o).doubleValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
