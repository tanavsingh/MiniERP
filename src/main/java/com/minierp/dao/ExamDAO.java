package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Exam;
import java.util.*;

public class ExamDAO {

    public List<Exam> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT e.*, sub.name AS subject_name FROM exams e " +
            "LEFT JOIN subjects sub ON e.subject_id = sub.id ORDER BY e.exam_date DESC");
        return mapList(rows);
    }

    public Exam findById(int id) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT e.*, sub.name AS subject_name FROM exams e " +
            "LEFT JOIN subjects sub ON e.subject_id = sub.id WHERE e.id=?", id);
        return rows.isEmpty() ? null : map(rows.get(0));
    }

    public List<Exam> findUpcoming() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT e.*, sub.name AS subject_name FROM exams e " +
            "LEFT JOIN subjects sub ON e.subject_id = sub.id " +
            "WHERE e.exam_date >= date('now') ORDER BY e.exam_date");
        return mapList(rows);
    }

    public long insert(Exam e) {
        return QueryExecutor.executeInsert(
            "INSERT INTO exams (title, subject_id, exam_type, exam_date, start_time, end_time, venue, max_marks, instructions, created_by) VALUES (?,?,?,?,?,?,?,?,?,?)",
            e.getTitle(), e.getSubjectId(), e.getExamType(), e.getExamDate(),
            e.getStartTime(), e.getEndTime(), e.getVenue(), e.getMaxMarks(),
            e.getInstructions(), e.getCreatedBy());
    }

    public int update(Exam e) {
        return QueryExecutor.executeUpdate(
            "UPDATE exams SET title=?, exam_type=?, exam_date=?, start_time=?, end_time=?, venue=?, max_marks=?, instructions=? WHERE id=?",
            e.getTitle(), e.getExamType(), e.getExamDate(), e.getStartTime(),
            e.getEndTime(), e.getVenue(), e.getMaxMarks(), e.getInstructions(), e.getId());
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM exams WHERE id=?", id);
    }

    public int count() {
        Object r = QueryExecutor.executeScalar("SELECT COUNT(*) FROM exams");
        return r == null ? 0 : ((Number) r).intValue();
    }

    private List<Exam> mapList(List<Map<String, Object>> rows) {
        List<Exam> list = new ArrayList<>();
        for (Map<String, Object> row : rows) list.add(map(row));
        return list;
    }

    private Exam map(Map<String, Object> row) {
        Exam e = new Exam();
        e.setId(toInt(row.get("id")));
        e.setSubjectId(toInt(row.get("subject_id")));
        e.setMaxMarks(toInt(row.get("max_marks")));
        e.setCreatedBy(toInt(row.get("created_by")));
        e.setTitle(str(row.get("title")));
        e.setExamType(str(row.get("exam_type")));
        e.setExamDate(str(row.get("exam_date")));
        e.setStartTime(str(row.get("start_time")));
        e.setEndTime(str(row.get("end_time")));
        e.setVenue(str(row.get("venue")));
        e.setInstructions(str(row.get("instructions")));
        e.setCreatedAt(str(row.get("created_at")));
        e.setSubjectName(str(row.get("subject_name")));
        return e;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
