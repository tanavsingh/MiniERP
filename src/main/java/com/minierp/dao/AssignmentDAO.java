package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Assignment;
import java.util.*;

public class AssignmentDAO {

    public List<Assignment> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT a.*, sub.name AS subject_name FROM assignments a " +
            "LEFT JOIN subjects sub ON a.subject_id = sub.id ORDER BY a.due_date DESC");
        return mapList(rows);
    }

    public List<Assignment> findByFaculty(int facultyId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT a.*, sub.name AS subject_name FROM assignments a " +
            "LEFT JOIN subjects sub ON a.subject_id = sub.id " +
            "WHERE a.faculty_id = ? ORDER BY a.due_date DESC", facultyId);
        return mapList(rows);
    }

    public List<Assignment> findByStudentWithStatus(int studentId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT a.*, sub.name AS subject_name, " +
            "sub2.status, sub2.marks_obtained, sub2.feedback, sub2.id AS submission_id " +
            "FROM assignments a LEFT JOIN subjects sub ON a.subject_id = sub.id " +
            "LEFT JOIN assignment_submissions sub2 ON a.id = sub2.assignment_id AND sub2.student_id = ? " +
            "ORDER BY a.due_date DESC", studentId);
        return mapList(rows);
    }

    public long insert(Assignment a) {
        return QueryExecutor.executeInsert(
            "INSERT INTO assignments (title, description, subject_id, faculty_id, due_date, max_marks) VALUES (?,?,?,?,?,?)",
            a.getTitle(), a.getDescription(), a.getSubjectId(), a.getFacultyId(), a.getDueDate(), a.getMaxMarks());
    }

    public int update(Assignment a) {
        return QueryExecutor.executeUpdate(
            "UPDATE assignments SET title=?, description=?, due_date=?, max_marks=? WHERE id=?",
            a.getTitle(), a.getDescription(), a.getDueDate(), a.getMaxMarks(), a.getId());
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM assignments WHERE id=?", id);
    }

    public long submitAssignment(int assignmentId, int studentId, String text) {
        return QueryExecutor.executeInsert(
            "INSERT OR REPLACE INTO assignment_submissions (assignment_id, student_id, submission_text, status) VALUES (?,?,?,'SUBMITTED')",
            assignmentId, studentId, text);
    }

    public int gradeSubmission(int submissionId, double marks, String feedback) {
        return QueryExecutor.executeUpdate(
            "UPDATE assignment_submissions SET marks_obtained=?, feedback=?, status='GRADED' WHERE id=?",
            marks, feedback, submissionId);
    }

    public List<Map<String, Object>> getSubmissions(int assignmentId) {
        return QueryExecutor.executeQuery(
            "SELECT sub.*, s.name AS student_name, s.student_id AS sid " +
            "FROM assignment_submissions sub JOIN students s ON sub.student_id = s.id " +
            "WHERE sub.assignment_id = ?", assignmentId);
    }

    private List<Assignment> mapList(List<Map<String, Object>> rows) {
        List<Assignment> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Assignment a = new Assignment();
            a.setId(toInt(row.get("id")));
            a.setSubjectId(toInt(row.get("subject_id")));
            a.setFacultyId(toInt(row.get("faculty_id")));
            a.setMaxMarks(toInt(row.get("max_marks")));
            a.setTitle(str(row.get("title")));
            a.setDescription(str(row.get("description")));
            a.setDueDate(str(row.get("due_date")));
            a.setCreatedAt(str(row.get("created_at")));
            a.setSubjectName(str(row.get("subject_name")));
            a.setStatus(str(row.get("status")));
            a.setFeedback(str(row.get("feedback")));
            a.setSubmissionId(toInt(row.get("submission_id")));
            Object m = row.get("marks_obtained");
            if (m != null) a.setMarksObtained(((Number) m).doubleValue());
            list.add(a);
        }
        return list;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
