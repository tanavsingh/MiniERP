package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentDAO {

    public List<Student> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT s.*, u.username FROM students s LEFT JOIN users u ON s.user_id = u.id ORDER BY s.name");
        List<Student> list = new ArrayList<>();
        for (Map<String, Object> row : rows) list.add(map(row));
        return list;
    }

    public Student findById(int id) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT * FROM students WHERE id = ?", id);
        return rows.isEmpty() ? null : map(rows.get(0));
    }

    public Student findByUserId(int userId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT * FROM students WHERE user_id = ?", userId);
        return rows.isEmpty() ? null : map(rows.get(0));
    }

    public List<Student> findByCourse(String course, int semester) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT * FROM students WHERE course = ? AND semester = ? ORDER BY name", course, semester);
        List<Student> list = new ArrayList<>();
        for (Map<String, Object> row : rows) list.add(map(row));
        return list;
    }

    public long insert(Student s, String username, String password) {
        long userId = QueryExecutor.executeInsert(
            "INSERT INTO users (username, password, role) VALUES (?, ?, 'STUDENT')",
            username, password);
        if (userId < 0) return -1;
        return QueryExecutor.executeInsert(
            "INSERT INTO students (user_id, student_id, name, email, phone, course, semester, address, dob, gender, cgpa) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
            userId, s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(),
            s.getCourse(), s.getSemester(), s.getAddress(), s.getDob(), s.getGender(), s.getCgpa());
    }

    public int update(Student s) {
        return QueryExecutor.executeUpdate(
            "UPDATE students SET name=?, email=?, phone=?, course=?, semester=?, address=?, dob=?, gender=?, cgpa=? WHERE id=?",
            s.getName(), s.getEmail(), s.getPhone(), s.getCourse(), s.getSemester(),
            s.getAddress(), s.getDob(), s.getGender(), s.getCgpa(), s.getId());
    }

    public int delete(int id) {
        Student s = findById(id);
        if (s != null) QueryExecutor.executeUpdate("DELETE FROM users WHERE id = ?", s.getUserId());
        return QueryExecutor.executeUpdate("DELETE FROM students WHERE id = ?", id);
    }

    public int count() {
        Object r = QueryExecutor.executeScalar("SELECT COUNT(*) FROM students");
        return r == null ? 0 : ((Number) r).intValue();
    }

    private Student map(Map<String, Object> row) {
        Student s = new Student();
        s.setId(toInt(row.get("id")));
        s.setUserId(toInt(row.get("user_id")));
        s.setStudentId(str(row.get("student_id")));
        s.setName(str(row.get("name")));
        s.setEmail(str(row.get("email")));
        s.setPhone(str(row.get("phone")));
        s.setCourse(str(row.get("course")));
        s.setSemester(toInt(row.get("semester")));
        s.setAddress(str(row.get("address")));
        s.setDob(str(row.get("dob")));
        s.setGender(str(row.get("gender")));
        s.setCgpa(toDouble(row.get("cgpa")));
        s.setCreatedAt(str(row.get("created_at")));
        return s;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private double toDouble(Object o) { return o == null ? 0.0 : ((Number) o).doubleValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
