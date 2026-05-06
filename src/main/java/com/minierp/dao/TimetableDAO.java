package com.minierp.dao;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Timetable;
import java.util.*;

public class TimetableDAO {

    public List<Timetable> findAll() {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT t.*, sub.name AS subject_name, f.name AS faculty_name, c.name AS course_name " +
            "FROM timetable t LEFT JOIN subjects sub ON t.subject_id = sub.id " +
            "LEFT JOIN faculty f ON t.faculty_id = f.id LEFT JOIN courses c ON t.course_id = c.id " +
            "ORDER BY t.day_of_week, t.start_time");
        return mapList(rows);
    }

    public List<Timetable> findBySemesterAndCourse(int semester, int courseId) {
        List<Map<String, Object>> rows = QueryExecutor.executeQuery(
            "SELECT t.*, sub.name AS subject_name, f.name AS faculty_name, c.name AS course_name " +
            "FROM timetable t LEFT JOIN subjects sub ON t.subject_id = sub.id " +
            "LEFT JOIN faculty f ON t.faculty_id = f.id LEFT JOIN courses c ON t.course_id = c.id " +
            "WHERE t.semester = ? AND t.course_id = ? ORDER BY t.day_of_week, t.start_time",
            semester, courseId);
        return mapList(rows);
    }

    public long insert(Timetable t) {
        return QueryExecutor.executeInsert(
            "INSERT INTO timetable (subject_id, faculty_id, day_of_week, start_time, end_time, room, semester, course_id) VALUES (?,?,?,?,?,?,?,?)",
            t.getSubjectId(), t.getFacultyId(), t.getDayOfWeek(), t.getStartTime(),
            t.getEndTime(), t.getRoom(), t.getSemester(), t.getCourseId());
    }

    public int delete(int id) {
        return QueryExecutor.executeUpdate("DELETE FROM timetable WHERE id=?", id);
    }

    public int deleteAll() {
        return QueryExecutor.executeUpdate("DELETE FROM timetable");
    }

    private List<Timetable> mapList(List<Map<String, Object>> rows) {
        List<Timetable> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Timetable t = new Timetable();
            t.setId(toInt(row.get("id")));
            t.setSubjectId(toInt(row.get("subject_id")));
            t.setFacultyId(toInt(row.get("faculty_id")));
            t.setSemester(toInt(row.get("semester")));
            t.setCourseId(toInt(row.get("course_id")));
            t.setDayOfWeek(str(row.get("day_of_week")));
            t.setStartTime(str(row.get("start_time")));
            t.setEndTime(str(row.get("end_time")));
            t.setRoom(str(row.get("room")));
            t.setSubjectName(str(row.get("subject_name")));
            t.setFacultyName(str(row.get("faculty_name")));
            t.setCourseName(str(row.get("course_name")));
            list.add(t);
        }
        return list;
    }

    private int toInt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
