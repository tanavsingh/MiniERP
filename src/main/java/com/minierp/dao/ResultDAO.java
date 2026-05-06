package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Result;
import java.util.*;

public class ResultDAO {

    public List<Result> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT r.*, s.name AS student_name, e.title AS exam_title, e.max_marks, sub.name AS subject_name " +
            "FROM results r JOIN students s ON r.student_id = s.id " +
            "JOIN exams e ON r.exam_id = e.id JOIN subjects sub ON e.subject_id = sub.id " +
            "ORDER BY r.entered_at DESC");
        return mapList(rows);
    }

    public List<Result> findByStudent(int studentId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT r.*, s.name AS student_name, e.title AS exam_title, e.max_marks, sub.name AS subject_name " +
            "FROM results r JOIN students s ON r.student_id = s.id " +
            "JOIN exams e ON r.exam_id = e.id JOIN subjects sub ON e.subject_id = sub.id " +
            "WHERE r.student_id = ? ORDER BY r.entered_at DESC", studentId);
        return mapList(rows);
    }

    public List<Result> findByExam(int examId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT r.*, s.name AS student_name, e.title AS exam_title, e.max_marks, sub.name AS subject_name " +
            "FROM results r JOIN students s ON r.student_id = s.id " +
            "JOIN exams e ON r.exam_id = e.id JOIN subjects sub ON e.subject_id = sub.id " +
            "WHERE r.exam_id = ? ORDER BY r.marks_obtained DESC", examId);
        return mapList(rows);
    }

    public long insert(Result r) {
        return QueryExecutor.executeInsert(
            "INSERT OR REPLACE INTO results (student_id, exam_id, marks_obtained, grade, remarks, entered_by) VALUES (?,?,?,?,?,?)",
            r.getStudentId(), r.getExamId(), r.getMarksObtained(), r.getGrade(), r.getRemarks(), r.getEnteredBy());
    }

    public int update(Result r) {
        return QueryExecutor.executeUpdate(
            "UPDATE results SET marks_obtained=?, grade=?, remarks=? WHERE id=?",
            r.getMarksObtained(), r.getGrade(), r.getRemarks(), r.getId());
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM results WHERE id=?", id);
    }

    private List<Result> mapList(List<Map<String, Object>> rows) {
        List<Result> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Result r = new Result();
            r.setId(toInt(row.get("id")));
            r.setStudentId(toInt(row.get("student_id")));
            r.setExamId(toInt(row.get("exam_id")));
            r.setEnteredBy(toInt(row.get("entered_by")));
            r.setMaxMarks(toInt(row.get("max_marks")));
            r.setMarksObtained(toDouble(row.get("marks_obtained")));
            r.setGrade(str(row.get("grade")));
            r.setRemarks(str(row.get("remarks")));
            r.setEnteredAt(str(row.get("entered_at")));
            r.setStudentName(str(row.get("student_name")));
            r.setExamTitle(str(row.get("exam_title")));
            r.setSubjectName(str(row.get("subject_name")));
            list.add(r);
        }
        return list;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private double toDouble(Object o) { return o == null ? 0.0 : ((Number) o).doubleValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
