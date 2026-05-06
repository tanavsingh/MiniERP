package com.minierp.ai;

import com.minierp.database.QueryExecutor;
import com.minierp.models.Timetable;
import com.minierp.config.Constants;
import java.util.*;

public class TimetableGeneratorAI {

    public List<Timetable> generate(int courseId, int semester) {
        List<Map<String, Object>> subjects = QueryExecutor.executeQuery(
            "SELECT s.*, f.id AS fid FROM subjects s LEFT JOIN faculty f ON s.faculty_id = f.id " +
            "WHERE s.course_id = ? AND s.semester = ?", courseId, semester);

        List<Timetable> schedule = new ArrayList<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] slots = {"09:00", "10:00", "11:00", "13:00", "14:00", "15:00"};
        String[] rooms = {"Room 101", "Room 102", "Room 103", "Lab 01", "Hall A"};

        int dayIdx = 0, slotIdx = 0;
        for (Map<String, Object> sub : subjects) {
            if (dayIdx >= days.length) { dayIdx = 0; slotIdx++; }
            if (slotIdx >= slots.length - 1) slotIdx = 0;

            Timetable t = new Timetable();
            t.setSubjectId(((Number) sub.get("id")).intValue());
            t.setFacultyId(sub.get("fid") != null ? ((Number) sub.get("fid")).intValue() : 1);
            t.setDayOfWeek(days[dayIdx]);
            t.setStartTime(slots[slotIdx]);
            t.setEndTime(slots[slotIdx + 1]);
            t.setRoom(rooms[dayIdx % rooms.length]);
            t.setSemester(semester);
            t.setCourseId(courseId);
            t.setSubjectName(sub.get("name") != null ? sub.get("name").toString() : "");
            schedule.add(t);
            dayIdx++;
        }
        return schedule;
    }
}
